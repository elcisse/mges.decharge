package com.mges.decharge.controller;

import com.mges.decharge.dao.DechargeDAO;
import com.mges.decharge.model.Decharge;
import com.mges.decharge.service.PdfGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ListController {

    @FXML private TextField searchField;
    @FXML private TableView<Decharge> table;
    @FXML private TableColumn<Decharge, String> numeroColumn;
    @FXML private TableColumn<Decharge, String> beneficiaireColumn;
    @FXML private TableColumn<Decharge, Long> montantColumn;
    @FXML private TableColumn<Decharge, String> motifColumn;
    @FXML private TableColumn<Decharge, String> dateColumn;

    private final DechargeDAO dao = new DechargeDAO();
    private final PdfGenerator pdfGenerator = new PdfGenerator();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));
        beneficiaireColumn.setCellValueFactory(new PropertyValueFactory<>("beneficiaire"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        motifColumn.setCellValueFactory(new PropertyValueFactory<>("motif"));
        dateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateDecharge().format(DATE_FMT)));
        refresh();
    }

    public void refresh() {
        List<Decharge> decharges = dao.findAll();
        ObservableList<Decharge> items = FXCollections.observableArrayList(decharges);
        table.setItems(items);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isBlank()) {
            refresh();
            return;
        }
        table.setItems(FXCollections.observableArrayList(dao.search(keyword.trim())));
    }

    @FXML
    private void handleReimprimer() {
        Decharge selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une décharge à réimprimer.");
            return;
        }
        File outputDir = new File(System.getProperty("user.home") + File.separator + "MGES" + File.separator + "decharges");
        outputDir.mkdirs();
        File pdfFile = new File(outputDir, selected.getNumero() + ".pdf");
        try {
            pdfGenerator.generate(selected, pdfFile);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", "Impossible de générer/ouvrir le PDF : " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Decharge selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une décharge à supprimer.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer la décharge " + selected.getNumero() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirmation");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            dao.delete(selected.getId());
            refresh();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
