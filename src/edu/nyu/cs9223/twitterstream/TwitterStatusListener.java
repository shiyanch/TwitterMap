package edu.nyu.cs9223.twitterstream;

import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public abstract class TwitterStatusListener implements StatusListener {

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        // do nothing
    }

    @Override
    public void onTrackLimitationNotice(int i) {
        // do nothing
    }

    @Override
    public void onScrubGeo(long l, long l1) {
        // do nothing
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
        // do nothing
    }

    @Override
    public void onException(Exception e) {
        // TODO: Add exception handler
    }
}
