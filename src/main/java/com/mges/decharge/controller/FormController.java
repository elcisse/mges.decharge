package com.mges.decharge.controller;

import com.mges.decharge.dao.DechargeDAO;
import com.mges.decharge.model.Decharge;
import com.mges.decharge.service.NumberToWordsFr;
import com.mges.decharge.service.NumeroGenerator;
import com.mges.decharge.service.PdfGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class FormController {

    @FXML private TextField beneficiaireField;
    @FXML private TextField cniNumeroField;
    @FXML private DatePicker cniDateField;
    @FXML private TextField montantField;
    @FXML private TextArea motifField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDechargeField;
    @FXML private Label montantLettresLabel;

    private final DechargeDAO dao = new DechargeDAO();
    private final NumeroGenerator numeroGenerator = new NumeroGenerator(dao);
    private final PdfGenerator pdfGenerator = new PdfGenerator();

    private Runnable onSaved;

    @FXML
    private void initialize() {
        lieuField.setText("Dakar");
        dateDechargeField.setValue(LocalDate.now());
        montantField.textProperty().addListener((obs, oldVal, newVal) -> updateMontantEnLettres());
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void updateMontantEnLettres() {
        try {
            long montant = Long.parseLong(montantField.getText().trim());
            montantLettresLabel.setText(NumberToWordsFr.montantEnLettres(montant));
        } catch (NumberFormatException e) {
            montantLettresLabel.setText("");
        }
    }

    @FXML
    private void handleSave() {
        String erreur = valider();
        if (erreur != null) {
            showAlert(Alert.AlertType.WARNING, "Champs invalides", erreur);
            return;
        }

        long montant = Long.parseLong(montantField.getText().trim());
        Decharge d = new Decharge(
                numeroGenerator.next(),
                beneficiaireField.getText().trim(),
                cniNumeroField.getText().trim(),
                cniDateField.getValue(),
                montant,
                motifField.getText().trim(),
                lieuField.getText().trim(),
                dateDechargeField.getValue()
        );
        dao.save(d);

        File outputDir = new File(System.getProperty("user.home") + File.separator + "MGES" + File.separator + "decharges");
        outputDir.mkdirs();
        File pdfFile = new File(outputDir, d.getNumero() + ".pdf");
        try {
            pdfGenerator.generate(d, pdfFile);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", "La décharge a été enregistrée mais le PDF n'a pas pu être généré : " + e.getMessage());
            return;
        }

        if (onSaved != null) {
            onSaved.run();
        }

        Alert confirm = new Alert(Alert.AlertType.INFORMATION,
                "Décharge " + d.getNumero() + " enregistrée.\nOuvrir le PDF généré ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Décharge enregistrée");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            openFile(pdfFile);
        }

        clearForm();
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        beneficiaireField.clear();
        cniNumeroField.clear();
        cniDateField.setValue(null);
        montantField.clear();
        motifField.clear();
        lieuField.setText("Dakar");
        dateDechargeField.setValue(LocalDate.now());
        montantLettresLabel.setText("");
    }

    private String valider() {
        if (beneficiaireField.getText() == null || beneficiaireField.getText().isBlank()) {
            return "Le nom du bénéficiaire est obligatoire.";
        }
        if (montantField.getText() == null || montantField.getText().isBlank()) {
            return "Le montant est obligatoire.";
        }
        try {
            long montant = Long.parseLong(montantField.getText().trim());
            if (montant <= 0) {
                return "Le montant doit être supérieur à zéro.";
            }
        } catch (NumberFormatException e) {
            return "Le montant doit être un nombre entier valide (ex: 10000).";
        }
        if (dateDechargeField.getValue() == null) {
            return "La date de la décharge est obligatoire.";
        }
        return null;
    }

    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
