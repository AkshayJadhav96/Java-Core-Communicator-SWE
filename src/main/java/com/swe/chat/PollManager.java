package com.swe.chat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PollManager {
    private final Map<String, Poll> activePolls = new ConcurrentHashMap<>();
    private Consumer<Poll> onPollUpdateListener;

    public void setOnPollUpdateListener(Consumer<Poll> listener) {
        this.onPollUpdateListener = listener;
    }

    // Creates a new poll and notifies the listener
    public Poll createPoll(String question, List<String> options) {
        String pollId = UUID.randomUUID().toString();
        Poll poll = new Poll(pollId, question, options);
        activePolls.put(pollId, poll);

        if (onPollUpdateListener != null) {
            onPollUpdateListener.accept(poll);
        }
        return poll;
    }

    // Adds a vote to a poll and notifies the listener of the update
    public void vote(String pollId, String option) {
        Poll poll = activePolls.get(pollId);
        if (poll != null) {
            poll.addVote(option);
            System.out.println("Voted for '" + option + "' in poll: " + poll.getQuestion());
            if (onPollUpdateListener != null) {
                onPollUpdateListener.accept(poll);
            }
        }
    }
}