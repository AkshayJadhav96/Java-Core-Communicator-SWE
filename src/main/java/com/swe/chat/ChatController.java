package com.swe.chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ChatController {
    @FXML private ListView<DisplayableItem> messageListView;
    @FXML private TextField messageInputField;
    @FXML private HBox replyQuoteBox;
    @FXML private Label replyQuoteLabel;

    private ChatViewModel viewModel;
    private PollManager pollManager;

    @FXML
    public void initialize() {
        // In a real app, these would be injected (Dependency Injection)
        AbstractNetworking mockNetwork = new MockNetworking();
        ChatManager chatManager = new ChatManager(mockNetwork);
        this.pollManager = new PollManager(); // Initialize PollManager here
        this.viewModel = new ChatViewModel(chatManager, this.pollManager);

        // --- BINDINGS ---
        // Bind the text field's text to the ViewModel's property
        messageInputField.textProperty().bindBidirectional(viewModel.currentMessageTextProperty());

        // Bind the ListView's items to the ViewModel's message list
        messageListView.setItems(viewModel.getMessages());

        // Bind the reply box visibility and text
        replyQuoteBox.visibleProperty().bind(viewModel.isReplyingProperty());
        replyQuoteBox.managedProperty().bind(viewModel.isReplyingProperty());
        replyQuoteLabel.textProperty().bind(viewModel.replyQuoteTextProperty());

        // Set up the custom cell factory to render our styled messages
        messageListView.setItems(viewModel.getMessages());
        messageListView.setCellFactory(new MessageCellFactory(viewModel));
    }

    // --- EVENT HANDLERS (Delegate to ViewModel) ---
    @FXML private void handleSendButtonAction() {
        viewModel.sendMessage();
        messageInputField.requestFocus();
    }

    @FXML private void simulateIncomingMessage() {
        viewModel.simulateIncomingMessage();
    }

    @FXML private void cancelReply() {
        viewModel.cancelReply();
    }

    // Add this new FXML method
    @FXML
    private void handleCreatePollAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreatePollView.fxml"));
            Parent root = loader.load();

            CreatePollController controller = loader.getController();
            controller.initData(this.pollManager); // Pass the PollManager instance

            Stage stage = new Stage();
            stage.setTitle("Create Poll");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}