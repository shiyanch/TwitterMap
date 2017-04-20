package edu.nyu.cs9223.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nyu.cs9223.aws.SNSMessage;
import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class SentimentHandleServlet extends HttpServlet {
    private static Logger log = Logger.getAnonymousLogger();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tweetJson = request.getParameter("tweet");
        System.out.println(tweetJson);
        ElasticSearch.indexToElasticSearch(tweetJson);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String messagetype = request.getHeader("x-amz-sns-message-type");
        System.out.println(">> Message Type: " + messagetype);
        if (messagetype == null) {
            return;
        }

        Scanner scanner = new Scanner(request.getInputStream());
        StringBuilder builder = new StringBuilder();
        while(scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        SNSMessage msg = readMessageFromJson(builder.toString());

        if (msg.getSignatureVersion().equals("1")) {
            if (isMessageSignatureValid(msg)) {
                System.out.println(">> Signature verification successed!");
            }
            else {
                System.out.println(">> Signature verification failed!");
                throw new SecurityException("Signature verification failed.");
            }
        }
        else {
            System.out.println(">> Unexpected signature version.");
            throw new SecurityException("Unexpected signature version.");
        }

        if (messagetype.equals("Notification")) {
            String logMsgAndSubject = "";
            logMsgAndSubject += msg.getMessage();
            Random r = new Random();
            System.out.print(">> Notification: ");
            System.out.println(logMsgAndSubject);
            ElasticSearch.indexToElasticSearch(logMsgAndSubject);
        }
        else if (messagetype.equals("SubscriptionConfirmation")) {
            Scanner sc = new Scanner(new URL(msg.getSubscribeURL()).openStream());
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }
            System.out.println(">> Subscription confirmation ("
                    + msg.getSubscribeURL() + ") Return value: "
                    + sb.toString());
        }
        else {
            System.out.println(">> Other Message Type: "+msg.getMessage());
        }
        System.out.println(">>Done processing message: " + msg.getMessageId());
    }

    private SNSMessage readMessageFromJson(String string) {
        ObjectMapper mapper = new ObjectMapper();
        SNSMessage message = null;
        try {
            message = mapper.readValue(string, SNSMessage.class);
        } catch (JsonParseException | JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

    private boolean isMessageSignatureValid(SNSMessage msg) {
        try {
            URL url = new URL(msg.getSigningCertUrl());
            InputStream inStream = url.openStream();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf
                    .generateCertificate(inStream);
            inStream.close();

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(cert.getPublicKey());
            sig.update(getMessageBytesToSign(msg));
            return sig.verify(Base64.decodeBase64(msg.getSignature().getBytes()));
        } catch (Exception e) {
            throw new SecurityException("Verify method failed.", e);
        }
    }

    private byte[] getMessageBytesToSign(SNSMessage msg) {
        byte[] bytesToSign = null;
        if (msg.getType().equals("Notification"))
            bytesToSign = buildNotificationStringToSign(msg).getBytes();
        else if (msg.getType().equals("SubscriptionConfirmation")
                || msg.getType().equals("UnsubscribeConfirmation"))
            bytesToSign = buildSubscriptionStringToSign(msg).getBytes();
        return bytesToSign;
    }

    private static String buildNotificationStringToSign(SNSMessage msg) {
        String stringToSign = null;

        // Build the string to sign from the values in the message.
        // Name and values separated by newline characters
        // The name value pairs are sorted by name
        // in byte sort order.
        stringToSign = "Message\n";
        stringToSign += msg.getMessage() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.getMessageId() + "\n";
        if (msg.getSubject() != null) {
            stringToSign += "Subject\n";
            stringToSign += msg.getSubject() + "\n";
        }
        stringToSign += "Timestamp\n";
        stringToSign += msg.getTimestamp() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.getType() + "\n";
        return stringToSign;
    }

    // Build the string to sign for SubscriptionConfirmation
    // and UnsubscribeConfirmation messages.
    private static String buildSubscriptionStringToSign(SNSMessage msg) {
        String stringToSign = null;
        // Build the string to sign from the values in the message.
        // Name and values separated by newline characters
        // The name value pairs are sorted by name
        // in byte sort order.
        stringToSign = "Message\n";
        stringToSign += msg.getMessage() + "\n";
        stringToSign += "MessageId\n";
        stringToSign += msg.getMessageId() + "\n";
        stringToSign += "SubscribeURL\n";
        stringToSign += msg.getSubscribeURL() + "\n";
        stringToSign += "Timestamp\n";
        stringToSign += msg.getTimestamp() + "\n";
        stringToSign += "Token\n";
        stringToSign += msg.getToken() + "\n";
        stringToSign += "TopicArn\n";
        stringToSign += msg.getTopicArn() + "\n";
        stringToSign += "Type\n";
        stringToSign += msg.getType() + "\n";
        return stringToSign;
    }
}
