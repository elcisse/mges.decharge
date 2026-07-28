package com.mges.decharge.dao;

import com.mges.decharge.model.Decharge;
import com.mges.decharge.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DechargeDAO {

    public Decharge save(Decharge d) {
        String sql = """
                INSERT INTO decharge (numero, beneficiaire, cni_numero, cni_date, montant, motif, lieu, date_decharge)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNumero());
            ps.setString(2, d.getBeneficiaire());
            ps.setString(3, d.getCniNumero());
            ps.setString(4, d.getCniDate() != null ? d.getCniDate().toString() : null);
            ps.setLong(5, d.getMontant());
            ps.setString(6, d.getMotif());
            ps.setString(7, d.getLieu());
            ps.setString(8, d.getDateDecharge().toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getInt(1));
                }
            }
            return d;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la décharge", e);
        }
    }

    public void update(Decharge d) {
        String sql = """
                UPDATE decharge SET beneficiaire = ?, cni_numero = ?, cni_date = ?, montant = ?,
                       motif = ?, lieu = ?, date_decharge = ?
                WHERE id = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getBeneficiaire());
            ps.setString(2, d.getCniNumero());
            ps.setString(3, d.getCniDate() != null ? d.getCniDate().toString() : null);
            ps.setLong(4, d.getMontant());
            ps.setString(5, d.getMotif());
            ps.setString(6, d.getLieu());
            ps.setString(7, d.getDateDecharge().toString());
            ps.setInt(8, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la décharge", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM decharge WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la décharge", e);
        }
    }

    public List<Decharge> findAll() {
        String sql = "SELECT * FROM decharge ORDER BY date_decharge DESC, id DESC";
        List<Decharge> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des décharges", e);
        }
        return result;
    }

    public List<Decharge> search(String keyword) {
        String sql = """
                SELECT * FROM decharge
                WHERE beneficiaire LIKE ? OR numero LIKE ? OR motif LIKE ?
                ORDER BY date_decharge DESC, id DESC
                """;
        List<Decharge> result = new ArrayList<>();
        String like = "%" + keyword + "%";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des décharges", e);
        }
        return result;
    }

    public int countForYear(int year) {
        String sql = "SELECT COUNT(*) FROM decharge WHERE numero LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "MGES-" + year + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des décharges", e);
        }
        return 0;
    }

    private Decharge map(ResultSet rs) throws SQLException {
        Decharge d = new Decharge();
        d.setId(rs.getInt("id"));
        d.setNumero(rs.getString("numero"));
        d.setBeneficiaire(rs.getString("beneficiaire"));
        d.setCniNumero(rs.getString("cni_numero"));
        String cniDate = rs.getString("cni_date");
        d.setCniDate(cniDate != null && !cniDate.isBlank() ? LocalDate.parse(cniDate) : null);
        d.setMontant(rs.getLong("montant"));
        d.setMotif(rs.getString("motif"));
        d.setLieu(rs.getString("lieu"));
        d.setDateDecharge(LocalDate.parse(rs.getString("date_decharge")));
        return d;
    }
}
