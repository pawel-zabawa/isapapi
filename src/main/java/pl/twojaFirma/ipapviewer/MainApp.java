package pl.twojaFirma.ipapviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

/**
 * Entrypoint for the IPAP Viewer application.
 */
public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage stage) throws IOException {
        Locale.setDefault(Locale.forLanguageTag("pl-PL"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MainView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("IPAP Viewer");
        stage.setScene(scene);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.show();
    }

    @Override
    public void stop() {
        log.info("Shutting down IPAP Viewer");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
