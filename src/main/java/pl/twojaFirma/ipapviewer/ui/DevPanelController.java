package pl.twojaFirma.ipapviewer.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pl.twojaFirma.ipapviewer.service.HealthService;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class DevPanelController implements Initializable {

    @FXML
    private Button checkIpapButton;
    @FXML
    private Button checkOpenAiButton;
    @FXML
    private Label ipapStatusLabel;
    @FXML
    private Label openAiStatusLabel;
    @FXML
    private TextArea logsArea;

    private final HealthService healthService = new HealthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkIpapButton.setOnAction(e -> updateStatus("IPAP", ipapStatusLabel));
        checkOpenAiButton.setOnAction(e -> updateStatus("OpenAI", openAiStatusLabel));
        logsArea.setText("Panel Deweloperski gotowy.");
    }

    private void updateStatus(String name, Label label) {
        Duration elapsed = healthService.sinceLastCheck(name);
        healthService.markCheck(name);
        label.setText(name + " OK (ostatnio: " + elapsed.toSeconds() + "s temu)");
    }
}
