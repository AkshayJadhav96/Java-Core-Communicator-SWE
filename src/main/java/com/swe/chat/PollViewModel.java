package com.swe.chat;

/**
 * ViewModel for a single poll, ready for display. Implements DisplayableItem.
 */
public class PollViewModel implements DisplayableItem {
    private final Poll poll;

    public PollViewModel(Poll poll) {
        this.poll = poll;
    }

    public Poll getPoll() {
        return poll;
    }
}