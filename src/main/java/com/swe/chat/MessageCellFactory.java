package com.swe.chat;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * A factory for creating styled message cells in a ListView.
 */
public class MessageCellFactory implements Callback<ListView<DisplayableItem>, ListCell<DisplayableItem>> {

    private final ChatViewModel viewModel;

    public MessageCellFactory(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public ListCell<DisplayableItem> call(ListView<DisplayableItem> param) {
        return new ListCell<>() {
            // UPDATE: The method signature is now correct and uses DisplayableItem
            @Override
            protected void updateItem(DisplayableItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (item instanceof MessageViewModel) {
                    setGraphic(createMessageBubble((MessageViewModel) item));
                } else if (item instanceof PollViewModel) {
                    setGraphic(createPollBubble(((PollViewModel) item).getPoll()));
                }
            }
        };
    }

    // Add this new method to create the poll bubble
    private VBox createPollBubble(Poll poll) {
        VBox pollContainer = new VBox(5);
        pollContainer.getStyleClass().add("poll-bubble");

        Label question = new Label(poll.getQuestion());
        question.getStyleClass().add("poll-question");
        pollContainer.getChildren().add(question);

        for (String option : poll.getOptions()) {
            int voteCount = poll.getVotes().get(option);
            Button voteButton = new Button(option + " (" + voteCount + ")");
            voteButton.setOnAction(e -> viewModel.voteOnPoll(poll.getPollId(), option));
            voteButton.setMaxWidth(Double.MAX_VALUE);
            pollContainer.getChildren().add(voteButton);
        }
        return pollContainer;
    }

    private HBox createMessageBubble(MessageViewModel messageVM) {
        VBox messageBubble = new VBox();
        messageBubble.getStyleClass().add("message-bubble");

        if (messageVM.getQuotedContent() != null) {
            Label quoteLabel = new Label(messageVM.getQuotedContent());
            quoteLabel.getStyleClass().add("reply-quote-label");
            messageBubble.getChildren().add(quoteLabel);
        }

        Label usernameLabel = new Label(messageVM.getUsername());
        Label contentLabel = new Label(messageVM.getContent());
        Label timestampLabel = new Label(messageVM.getFormattedTimestamp());

        Button replyButton = new Button("Reply");
        replyButton.getStyleClass().add("reply-button");
        replyButton.setOnAction(e -> viewModel.startReply(messageVM));

        HBox bottomRow = new HBox(5, timestampLabel, replyButton);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        usernameLabel.getStyleClass().add("username-label");
        contentLabel.getStyleClass().add("message-content-label");
        timestampLabel.getStyleClass().add("timestamp-label");

        messageBubble.getChildren().addAll(usernameLabel, contentLabel, bottomRow);

        HBox wrapper = new HBox();
        if (messageVM.isSent()) {
            messageBubble.getStyleClass().add("sent-bubble");
            wrapper.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBubble.getStyleClass().add("received-bubble");
            wrapper.setAlignment(Pos.CENTER_LEFT);
        }

        wrapper.getChildren().add(messageBubble);
        return wrapper;
    }
}