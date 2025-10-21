package com.example.isap;

import com.example.isap.ui.App;
import javafx.application.Application;

/**
 * Entry point used by IDE run configurations to launch the JavaFX application.
 */
public final class Main {
    private Main() {
        // Utility class
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
