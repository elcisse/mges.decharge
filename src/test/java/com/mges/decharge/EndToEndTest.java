package com.mges.decharge;

import com.mges.decharge.dao.DechargeDAO;
import com.mges.decharge.model.Decharge;
import com.mges.decharge.service.NumeroGenerator;
import com.mges.decharge.service.PdfGenerator;
import com.mges.decharge.util.Database;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndToEndTest {

    @Test
    void createDechargeAndGeneratePdf() throws IOException {
        Database.initialize();
        DechargeDAO dao = new DechargeDAO();
        NumeroGenerator numeroGenerator = new NumeroGenerator(dao);

        Decharge d = new Decharge(
                numeroGenerator.next(),
                "Amadou Fall THIAW",
                "1 255 2005 01814",
                LocalDate.of(2017, 4, 10),
                10000,
                "Frais de transport",
                "Dakar",
                LocalDate.of(2026, 7, 16)
        );

        dao.save(d);
        assertTrue(d.getId() > 0, "L'id généré par SQLite doit être positif");

        File outputDir = new File(System.getProperty("java.io.tmpdir"), "mges-test");
        outputDir.mkdirs();
        File pdfFile = new File(outputDir, d.getNumero() + "-test.pdf");

        PdfGenerator generator = new PdfGenerator();
        generator.generate(d, pdfFile);

        assertTrue(pdfFile.exists(), "Le fichier PDF doit être créé");
        assertTrue(pdfFile.length() > 0, "Le fichier PDF ne doit pas être vide");

        Decharge reloaded = dao.findAll().stream()
                .filter(x -> x.getId() == d.getId())
                .findFirst()
                .orElseThrow();
        assertEquals("Amadou Fall THIAW", reloaded.getBeneficiaire());
        assertEquals(10000, reloaded.getMontant());

        dao.delete(d.getId());
        pdfFile.deleteOnExit();
    }
}
