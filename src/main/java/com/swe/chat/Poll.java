package com.swe.chat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Poll {
    private final String pollId;
    private final String question;
    private final List<String> options;
    private final Map<String, Integer> votes; // Option -> Vote Count

    public Poll(String pollId, String question, List<String> options) {
        this.pollId = pollId;
        this.question = question;
        this.options = options;
        this.votes = new ConcurrentHashMap<>();
        // Initialize all options with 0 votes
        for (String option : options) {
            votes.put(option, 0);
        }
    }

    public String getPollId() { return pollId; }
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public Map<String, Integer> getVotes() { return votes; }

    public void addVote(String option) {
        votes.computeIfPresent(option, (key, count) -> count + 1);
    }
}