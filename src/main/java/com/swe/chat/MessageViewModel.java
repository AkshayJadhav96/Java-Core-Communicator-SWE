package com.swe.chat;

import java.time.format.DateTimeFormatter;

/**
 * ViewModel for a single message. Prepares data from the ChatMessage model for display.
 */
public class MessageViewModel implements DisplayableItem {
    private final ChatMessage message;
    private final boolean isSent;
    private final String quotedContent;

    public MessageViewModel(ChatMessage message, String currentUserId, String quotedContent) {
        this.message = message;
        this.isSent = message.getUserId().equals(currentUserId);
        this.quotedContent = quotedContent;
    }

    public String getUsername() {
        return isSent ? "You" : message.getUserId();
    }

    public String getContent() {
        return message.getContent();
    }

    public String getFormattedTimestamp() {
        return message.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public boolean isSent() {
        return isSent;
    }

    public String getMessageId() {
        return message.getMessageId();
    }

    public String getQuotedContent() {
        return quotedContent;
    }
}