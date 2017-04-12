package edu.nyu.cs9223.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;

public class SQSQueue {
    private static final AWSCredentials credentials;
    private static final AmazonSQS sqs;
    private static final Region region;
    private static String queueURL = "";

    static {
        credentials = new BasicAWSCredentials("AKIAITPI6U23VQLLC7ZQ", "5KZyajmwiTQUFzuMH/PekHBkkKTcCKa1dNwggAa4");
        sqs = new AmazonSQSClient(credentials);
        region = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(region);
    }

    private static void createMessageQueue() {
        System.out.println("Creating a new SQS called TweetMQ.");
        CreateQueueRequest createQueueRequest = new CreateQueueRequest("TweetMQ");
        queueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
        System.out.println("Created a new SQS: " + queueURL);
    }

    private static void fetchMessageQueue() {
        System.out.println("Fetch a SQS queue if exists, otherwise create a new one.");
        while (queueURL.equals("")) {
            List<String> queueURLs = sqs.listQueues("TweetMQ").getQueueUrls();
            if (queueURLs.isEmpty()) {
                createMessageQueue();
            }
            else {
                queueURL = queueURLs.get(0);
            }
        }
        System.out.println("SQS: " + queueURL);
    }

    public static void sendMessage(String json) {
        if (queueURL.equals("")) {
            fetchMessageQueue();
        }
        System.out.println("Sending a message to TweetQueue.");
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueURL, json);
        sqs.sendMessage(sendMessageRequest);
    }
}
