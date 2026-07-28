package com.mges.decharge.model;

import java.time.LocalDate;

public class Decharge {

    private int id;
    private String numero;
    private String beneficiaire;
    private String cniNumero;
    private LocalDate cniDate;
    private long montant;
    private String motif;
    private String lieu;
    private LocalDate dateDecharge;

    public Decharge() {
    }

    public Decharge(String numero, String beneficiaire, String cniNumero, LocalDate cniDate,
                     long montant, String motif, String lieu, LocalDate dateDecharge) {
        this.numero = numero;
        this.beneficiaire = beneficiaire;
        this.cniNumero = cniNumero;
        this.cniDate = cniDate;
        this.montant = montant;
        this.motif = motif;
        this.lieu = lieu;
        this.dateDecharge = dateDecharge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(String beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public String getCniNumero() {
        return cniNumero;
    }

    public void setCniNumero(String cniNumero) {
        this.cniNumero = cniNumero;
    }

    public LocalDate getCniDate() {
        return cniDate;
    }

    public void setCniDate(LocalDate cniDate) {
        this.cniDate = cniDate;
    }

    public long getMontant() {
        return montant;
    }

    public void setMontant(long montant) {
        this.montant = montant;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDate getDateDecharge() {
        return dateDecharge;
    }

    public void setDateDecharge(LocalDate dateDecharge) {
        this.dateDecharge = dateDecharge;
    }
}
