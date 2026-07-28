package com.mges.decharge.controller;

import javafx.fxml.FXML;

public class MainController {

    @FXML private FormController formController;
    @FXML private ListController listController;

    @FXML
    private void initialize() {
        formController.setOnSaved(listController::refresh);
    }
}
