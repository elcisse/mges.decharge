package com.mges.decharge.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DB_FILE = System.getProperty("user.home")
            + File.separator + "MGES" + File.separator + "decharges.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        new File(System.getProperty("user.home") + File.separator + "MGES").mkdirs();
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS decharge (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero TEXT NOT NULL UNIQUE,
                    beneficiaire TEXT NOT NULL,
                    cni_numero TEXT,
                    cni_date TEXT,
                    montant INTEGER NOT NULL,
                    motif TEXT,
                    lieu TEXT,
                    date_decharge TEXT NOT NULL
                )
                """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Impossible d'initialiser la base de données", e);
        }
    }
}
