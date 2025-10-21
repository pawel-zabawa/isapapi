package com.example.isap.ui;

import com.example.isap.AppConfig;
import com.example.isap.model.Act;
import com.example.isap.service.ActContentService;
import com.example.isap.service.ActFeedService;
import com.example.isap.service.IsapApiClient;
import com.example.isap.service.OpenAiSummaryService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App extends Application {
    private static final String ALL_TOPICS_OPTION = "Wszystkie tematy";

    private final AppConfig config = new AppConfig();
    private final IsapApiClient isapApiClient = new IsapApiClient();
    private final ActContentService contentService = new ActContentService();
    private final ActFeedService feedService = new ActFeedService(isapApiClient, contentService);
    private final OpenAiSummaryService summaryService = new OpenAiSummaryService(config.getOpenAiApiKey(), config.getOpenAiModel());

    private ComboBox<String> topicComboBox;
    private Label connectionLabel;
    private Label headerLabel;
    private Label statusLabel;
    private TextArea summaryArea;
    private ProgressIndicator summaryProgress;
    private Label counterLabel;

    private int currentIndex = 0;
    private String currentTopic = null;

    @Override
    public void start(Stage stage) {
        stage.setTitle("ISAP Legislacja - Przegląd projektów");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        root.setTop(buildTopBar());
        root.setCenter(buildContentArea());
        root.setBottom(buildActionBar());

        Scene scene = new Scene(root, 720, 960);
        stage.setScene(scene);
        stage.show();

        runConnectionTest();
        loadActs();
    }

    private VBox buildContentArea() {
        connectionLabel = new Label("Trwa test połączenia z ISAP...");
        connectionLabel.setWrapText(true);
        connectionLabel.setStyle("-fx-text-fill: #555;");

        headerLabel = new Label("Ładuję projekty ustaw...");
        headerLabel.setWrapText(true);
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #555;");

        summaryArea = new TextArea();
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        summaryArea.setPrefRowCount(10);
        summaryArea.setStyle("-fx-font-size: 16px;");

        summaryProgress = new ProgressIndicator();
        summaryProgress.setVisible(false);

        VBox contentBox = new VBox(12, connectionLabel, headerLabel, statusLabel, new Separator());
        contentBox.getChildren().add(summaryProgress);

        ScrollPane scrollPane = new ScrollPane(summaryArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        contentBox.getChildren().add(scrollPane);

        return contentBox;
    }

    private HBox buildTopBar() {
        Label topicLabel = new Label("Temat:");
        topicComboBox = new ComboBox<>();
        topicComboBox.setPromptText(ALL_TOPICS_OPTION);
        topicComboBox.setOnAction(event -> {
            currentTopic = topicComboBox.getSelectionModel().getSelectedItem();
            currentIndex = 0;
            displayCurrentAct();
        });

        counterLabel = new Label("0 / 0");
        HBox topBar = new HBox(8, topicLabel, topicComboBox, counterLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 16, 0));
        return topBar;
    }

    private VBox buildActionBar() {
        Button goodButton = new Button("Dobre");
        Button badButton = new Button("Złe");
        Button unsureButton = new Button("Nie wiem, nie znam się");

        goodButton.setMaxWidth(Double.MAX_VALUE);
        badButton.setMaxWidth(Double.MAX_VALUE);
        unsureButton.setMaxWidth(Double.MAX_VALUE);

        goodButton.setOnAction(event -> moveToNextAct());
        badButton.setOnAction(event -> moveToNextAct());
        unsureButton.setOnAction(event -> moveToNextAct());

        VBox actionBar = new VBox(12, goodButton, badButton, unsureButton);
        actionBar.setAlignment(Pos.CENTER);
        return actionBar;
    }

    private void moveToNextAct() {
        currentIndex++;
        displayCurrentAct();
    }

    private void loadActs() {
        summaryArea.setText("Pobieram listę projektów z ISAP...");
        Task<List<String>> loadTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                feedService.refresh(config.getPublisher(), config.getYearOrDefault(), config.getLimit());
                List<String> topics = new ArrayList<>();
                topics.add(ALL_TOPICS_OPTION);
                topics.addAll(feedService.availableTopics());
                return topics;
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<String> topics = loadTask.getValue();
            topicComboBox.getItems().setAll(topics);
            topicComboBox.getSelectionModel().selectFirst();
            currentTopic = topicComboBox.getSelectionModel().getSelectedItem();
            currentIndex = 0;
            displayCurrentAct();
        });

        loadTask.setOnFailed(event -> {
            Throwable error = loadTask.getException();
            summaryArea.setText("Nie udało się pobrać danych z ISAP: " + (error != null ? error.getMessage() : "nieznany błąd"));
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void runConnectionTest() {
        if (connectionLabel != null) {
            connectionLabel.setText("Trwa test połączenia z ISAP...");
            connectionLabel.setStyle("-fx-text-fill: #555;");
        }
        Task<IsapApiClient.ConnectionTestResult> testTask = new Task<>() {
            @Override
            protected IsapApiClient.ConnectionTestResult call() {
                return isapApiClient.performConnectionTest();
            }
        };

        testTask.setOnSucceeded(event -> {
            IsapApiClient.ConnectionTestResult result = testTask.getValue();
            if (connectionLabel != null) {
                connectionLabel.setStyle(result.success() ? "-fx-text-fill: #2f7d32;" : "-fx-text-fill: #c62828;");
                connectionLabel.setText(result.message());
            }
        });

        testTask.setOnFailed(event -> {
            if (connectionLabel != null) {
                connectionLabel.setStyle("-fx-text-fill: #c62828;");
                Throwable error = testTask.getException();
                connectionLabel.setText("Test połączenia nie powiódł się: " + (error != null ? error.getMessage() : "nieznany błąd"));
            }
        });

        Thread thread = new Thread(testTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayCurrentAct() {
        String selectedTopic = currentTopic;
        String keyword = (selectedTopic == null || ALL_TOPICS_OPTION.equals(selectedTopic)) ? null : selectedTopic;
        List<Act> acts = feedService.filterByKeyword(keyword);
        if (acts.isEmpty()) {
            headerLabel.setText("Brak projektów spełniających kryteria.");
            statusLabel.setText("Spróbuj zmienić temat lub zwiększyć limit w konfiguracji.");
            summaryArea.setText("");
            summaryProgress.setVisible(false);
            counterLabel.setText("0 / 0");
            return;
        }
        if (currentIndex >= acts.size()) {
            currentIndex = 0;
        }
        Act act = acts.get(currentIndex);
        counterLabel.setText((currentIndex + 1) + " / " + acts.size());
        headerLabel.setText(act.getTitle());
        statusLabel.setText(String.format("Typ: %s | Status: %s | Data: %s", act.getType(), act.getStatus(),
                act.getPromulgationDate() != null ? act.getPromulgationDate() : "brak danych"));
        summaryArea.setText("Generuję streszczenie...");
        summaryProgress.setVisible(true);

        Task<String> summaryTask = new Task<>() {
            @Override
            protected String call() {
                String text = feedService.loadActText(act);
                Optional<String> summary = summaryService.summarizeAct(act, text);
                return summary.orElse("Brak streszczenia dla tego dokumentu.");
            }
        };

        summaryTask.setOnSucceeded(event -> {
            summaryProgress.setVisible(false);
            summaryArea.setText(summaryTask.getValue());
        });

        summaryTask.setOnFailed(event -> {
            summaryProgress.setVisible(false);
            Throwable error = summaryTask.getException();
            summaryArea.setText("Nie udało się przygotować streszczenia: " + (error != null ? error.getMessage() : "nieznany błąd"));
        });

        Thread thread = new Thread(summaryTask);
        thread.setDaemon(true);
        thread.start();
    }

}
