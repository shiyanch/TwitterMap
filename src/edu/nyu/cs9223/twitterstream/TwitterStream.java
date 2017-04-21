package edu.nyu.cs9223.twitterstream;

import com.google.gson.Gson;
import edu.nyu.cs9223.aws.SQSQueue;
import edu.nyu.cs9223.bean.Tweet;
import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import edu.nyu.cs9223.util.DateConvetor;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStream implements Runnable{
    private final twitter4j.TwitterStream stream;
    private final TwitterStatusListener listener;

    public TwitterStream() {
        this.listener = new TwitterStatusListener() {
            @Override
            public void onStatus(Status status) {
                if (status.getGeoLocation() != null) {
                    String date = DateConvetor.convert(status.getCreatedAt());
                    Tweet tweet = new Tweet(status.getId(), status.getUser().getScreenName(),
                            status.getText(), date, status.getGeoLocation());

                    System.out.println(tweet.toString());
                    sendToSQS(new Gson().toJson(tweet));
                }
            }
        };
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setJSONStoreEnabled(true)
                .setOAuthConsumerKey("Q80weJEF41YWSEygm0GfoXvY6")
                .setOAuthConsumerSecret("hAuSwLUixguyHykLpqSRmqFeVqdAHSxejugXRXLbyx0ZoWNVnw")
                .setOAuthAccessToken("839255398418898944-KX6uOlhJvxytECkTCF4FZyI5wfYMJeY")
                .setOAuthAccessTokenSecret("RVmFnWOeMDAmSAhJcuUAXUljUEc13mhooJ5Mnp9l9ZDw4");
        this.stream = new TwitterStreamFactory(cb.build()).getInstance();
        this.stream.addListener(listener);
    }

    @Override
    public void run() {
        stream.sample("en");
    }

    private void sendToSQS(String json) {
        SQSQueue.sendMessage(json);
    }
}
