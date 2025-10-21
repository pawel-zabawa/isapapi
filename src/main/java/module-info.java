module com.example.isap {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    exports com.example.isap;
    exports com.example.isap.model;
    exports com.example.isap.service;
    exports com.example.isap.ui;
}
