package edu.nyu.cs9223.twitterstream;

import edu.nyu.cs9223.bean.Twitt;
import twitter4j.*;

public class TwitterStream extends Thread {
    private final twitter4j.TwitterStream stream;
    private final TwitterStatusListener listener;

    public TwitterStream() {
        this.listener = new TwitterStatusListener() {
            @Override
            public void onStatus(Status status) {
                Twitt twitt = new Twitt(status.getId(), status.getUser().getScreenName(),
                        status.getText(), status.getCreatedAt(), status.getGeoLocation());
                // push it to elastic search
            }
        };
        this.stream = new TwitterStreamFactory().getInstance();
        this.stream.addListener(listener);
    }

    @Override
    public void run() {
        super.run();

    }

    public void main(String[] args) {
        twitter4j.TwitterStream stream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                //Do nothing
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };

        stream.addListener(listener);
        stream.sample("en");
    }


}
