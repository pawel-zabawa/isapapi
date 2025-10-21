package pl.twojaFirma.ipapviewer.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import pl.twojaFirma.ipapviewer.model.Act;
import pl.twojaFirma.ipapviewer.model.Category;
import pl.twojaFirma.ipapviewer.model.Summary;
import pl.twojaFirma.ipapviewer.service.ActService;
import pl.twojaFirma.ipapviewer.service.IpapClient;
import pl.twojaFirma.ipapviewer.service.OpenAiClient;
import pl.twojaFirma.ipapviewer.service.SummaryService;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ComboBox<Category> categoryCombo;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private TextArea actContent;
    @FXML
    private TextArea summaryArea;
    @FXML
    private Label statusLabel;
    @FXML
    private SplitPane mainSplit;
    @FXML
    private BorderPane root;
    @FXML
    private TitledPane summaryPane;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ActService actService = new ActService(new IpapClient());
    private final SummaryService summaryService = new SummaryService(new OpenAiClient());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryCombo.setItems(categories);
        categoryCombo.setConverter(new CategoryStringConverter());
        loadCategories();
        nextButton.setOnAction(event -> onNext());
        previousButton.setOnAction(event -> onPrevious());
        summaryPane.textProperty().bind(new SimpleStringProperty("Podsumowanie"));
    }

    private void loadCategories() {
        List<Category> data = actService.categories();
        categories.setAll(data);
        if (!categories.isEmpty()) {
            categoryCombo.getSelectionModel().select(0);
            onNext();
        }
    }

    private void onNext() {
        Category selected = categoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Wybierz kategorię");
            return;
        }
        Act act = actService.next(selected.id());
        renderAct(act);
    }

    private void onPrevious() {
        actService.previous().ifPresent(this::renderAct);
    }

    private void renderAct(Act act) {
        actContent.setText(act.content());
        statusLabel.setText("Wyświetlany akt: " + act.title());
        Platform.runLater(() -> {
            Summary summary = summaryService.getSummary(act.id(), act.content(), Locale.forLanguageTag("pl-PL"));
            summaryArea.setText(summary.text());
        });
    }
}
