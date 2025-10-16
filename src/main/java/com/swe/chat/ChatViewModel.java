package com.swe.chat;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main ViewModel for the chat screen. Holds all UI state and logic for both messages and polls.
 */
public class ChatViewModel {
    // --- Properties the View will bind to ---
    private final StringProperty currentMessageText = new SimpleStringProperty("");
    // This list can hold any item (message or poll) that is meant for display.
    private final ObservableList<DisplayableItem> messages = FXCollections.observableArrayList();
    private final StringProperty replyQuoteText = new SimpleStringProperty("");
    private final BooleanProperty isReplying = new SimpleBooleanProperty(false);

    // --- Dependencies and State ---
    private final ChatManager chatManager;
    private final PollManager pollManager;
    private final String currentUserId = "Aditya-Chauhan";
    private final Map<String, ChatMessage> messageHistory = new ConcurrentHashMap<>();
    private String currentReplyId = null;

    /**
     * Constructor for the ViewModel.
     * @param chatManager The manager for sending/receiving chat messages.
     * @param pollManager The manager for creating/voting on polls.
     */
    public ChatViewModel(ChatManager chatManager, PollManager pollManager) {
        this.chatManager = chatManager;
        this.pollManager = pollManager;

        // Listen for incoming messages from the backend
        this.chatManager.setOnMessageReceived(this::onChatMessageReceived);
        // Listen for new or updated polls from the backend
        this.pollManager.setOnPollUpdateListener(this::onPollUpdate);
    }

    // --- Bindable Properties for the View ---
    public StringProperty currentMessageTextProperty() { return currentMessageText; }
    public ObservableList<DisplayableItem> getMessages() { return messages; }
    public StringProperty replyQuoteTextProperty() { return replyQuoteText; }
    public BooleanProperty isReplyingProperty() { return isReplying; }

    // --- Commands (Actions triggered by the View) ---
    public void sendMessage() {
        String text = currentMessageText.get();
        if (text == null || text.trim().isEmpty()) return;

        String messageId = UUID.randomUUID().toString();
        ChatMessage messageToSend = new ChatMessage(messageId, currentUserId, text, this.currentReplyId);
        chatManager.sendMessage(messageToSend);

        currentMessageText.set("");
        cancelReply();
    }

    public void startReply(MessageViewModel messageVM) {
        this.currentReplyId = messageVM.getMessageId();
        String replyToUsername = messageVM.getUsername();
        String contentSnippet = messageVM.getContent().substring(0, Math.min(messageVM.getContent().length(), 30)) + "...";
        replyQuoteText.set("Replying to " + replyToUsername + ": \"" + contentSnippet + "\"");
        isReplying.set(true);
    }

    public void cancelReply() {
        this.currentReplyId = null;
        isReplying.set(false);
    }

    public void voteOnPoll(String pollId, String option) {
        pollManager.vote(pollId, option);
    }

    public void simulateIncomingMessage() {
        String messageId = UUID.randomUUID().toString();
        ChatMessage fakeMessage = new ChatMessage(messageId, "akshay_backend", "This is a simulated message!", null);
        // Use the receive method on the manager to simulate a network event
        chatManager.receiveMessage(MessageParser.serialize(fakeMessage));
    }

    // --- Private Logic for Handling Backend Events ---

    /**
     * Called by the PollManager when a poll is created or a vote is cast.
     * @param poll The new or updated poll data.
     */
    private void onPollUpdate(Poll poll) {
        Platform.runLater(() -> {
            // Find and remove the old version of this poll to ensure the UI refreshes with new vote counts
            messages.removeIf(item -> item instanceof PollViewModel && ((PollViewModel) item).getPoll().getPollId().equals(poll.getPollId()));
            // Add the new/updated poll as a PollViewModel
            messages.add(new PollViewModel(poll));
        });
    }

    /**
     * Called by the ChatManager when a new chat message arrives.
     * @param message The incoming chat message.
     */
    private void onChatMessageReceived(ChatMessage message) {
        messageHistory.put(message.getMessageId(), message);

        String quotedContent = null;
        if (message.getReplyToMessageId() != null) {
            ChatMessage repliedTo = messageHistory.get(message.getReplyToMessageId());
            if (repliedTo != null) {
                String originalSender = repliedTo.getUserId().equals(currentUserId) ? "You" : repliedTo.getUserId();
                quotedContent = "REPLY to " + originalSender + ": " + repliedTo.getContent();
            }
        }

        MessageViewModel messageVM = new MessageViewModel(message, currentUserId, quotedContent);
        Platform.runLater(() -> messages.add(messageVM));
    }
}