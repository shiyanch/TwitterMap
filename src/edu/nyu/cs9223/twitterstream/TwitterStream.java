package edu.nyu.cs9223.twitterstream;

import com.google.gson.Gson;
import edu.nyu.cs9223.bean.Tweet;
import edu.nyu.cs9223.elasticsearch.ElasticSearch;
import edu.nyu.cs9223.util.DateConvetor;
import twitter4j.*;

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
                    sendToES(new Gson().toJson(tweet));
                    System.out.println(new Gson().toJson(tweet));
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
        ElasticSearch.indexToElasticSearch(json);
    }
}
