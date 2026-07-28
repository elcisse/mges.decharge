package com.mges.decharge.service;

import com.mges.decharge.dao.DechargeDAO;

import java.time.Year;

public class NumeroGenerator {

    private final DechargeDAO dao;

    public NumeroGenerator(DechargeDAO dao) {
        this.dao = dao;
    }

    /** Génère le prochain numéro au format MGES-2026-0001. */
    public String next() {
        int year = Year.now().getValue();
        int count = dao.countForYear(year) + 1;
        return "MGES-%d-%04d".formatted(year, count);
    }
}
