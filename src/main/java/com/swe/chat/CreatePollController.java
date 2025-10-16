package com.swe.chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class CreatePollController {
    @FXML private TextField questionField;
    @FXML private VBox optionsContainer;

    private PollManager pollManager;

    // Called after FXML loading to pass in the PollManager
    public void initData(PollManager pollManager) {
        this.pollManager = pollManager;
        // Start with two empty option fields
        addOptionField();
        addOptionField();
    }

    @FXML
    private void addOptionField() {
        if (optionsContainer.getChildren().size() < 5) { // Limit to 5 options
            optionsContainer.getChildren().add(new TextField());
        }
    }

    @FXML
    private void createPoll() {
        String question = questionField.getText();
        List<String> options = optionsContainer.getChildren().stream()
                .map(node -> ((TextField) node).getText())
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.toList());

        if (!question.trim().isEmpty() && options.size() >= 2) {
            pollManager.createPoll(question, options);
            closeStage();
        }
    }

    @FXML
    private void cancel() {
        closeStage();
    }

    private void closeStage() {
        ((Stage) questionField.getScene().getWindow()).close();
    }
}