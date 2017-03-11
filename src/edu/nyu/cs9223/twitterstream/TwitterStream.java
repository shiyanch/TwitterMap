package edu.nyu.cs9223.twitterstream;

import com.google.gson.Gson;
import edu.nyu.cs9223.bean.Tweet;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitterStream implements Runnable{
    private static final String ES_URL = "https://search-cloud-computing-cl3869-mzhj7m6rkltbbqt3zdva34st7e.us-east-1.es.amazonaws.com/test/test-twitter-data/";
    private final twitter4j.TwitterStream stream;
    private final TwitterStatusListener listener;

    public TwitterStream() {
        this.listener = new TwitterStatusListener() {
            @Override
            public void onStatus(Status status) {
                if (status.getGeoLocation() != null) {
                    Tweet tweet = new Tweet(status.getId(), status.getUser().getScreenName(),
                            status.getText(), status.getCreatedAt(), status.getGeoLocation());
                    sendToES(new Gson().toJson(tweet));
                }
            }
        };
        this.stream = new TwitterStreamFactory().getInstance();
        this.stream.addListener(listener);
    }

    @Override
    public void run() {
        stream.sample("en");
    }

    private void sendToES(String json) {
        try {
            URL url = new URL(ES_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Encoding", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while((temp = reader.readLine()) != null) {
                sb.append(temp).append(" ");
            }
            System.out.println(sb.toString());
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
