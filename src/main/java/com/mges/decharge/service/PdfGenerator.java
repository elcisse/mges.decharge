package com.mges.decharge.service;

import com.mges.decharge.model.Decharge;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reconstruit la mise en page du modèle Word "DECHARGE MGES" en PDF.
 */
public class PdfGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float MARGIN = 60;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    private final PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public void generate(Decharge d, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float topY = PDRectangle.A4.getHeight() - MARGIN;
                float y = topY;

                float logoBottom = topY;
                PDImageXObject logo = loadLogo(document);
                if (logo != null) {
                    float logoWidth = 60;
                    float logoHeight = logoWidth * logo.getHeight() / (float) logo.getWidth();
                    float logoY = topY - logoHeight;
                    cs.drawImage(logo, MARGIN, logoY, logoWidth, logoHeight);
                    logoBottom = logoY;
                }

                y = drawCentered(cs, "MINISTERE DE L'EDUCATION NATIONALE", bold, 12, y);
                y -= 16;
                y = drawCentered(cs, "Mutuelle Générale de l'Éducation", bold, 11, y);
                y -= 14;
                y = drawCentered(cs, "du Sénégal (M.G.E.S.)", bold, 11, y);
                y -= 30;
                y = Math.min(y, logoBottom - 10);

                String dateEnTete = "Dakar, le " + d.getDateDecharge().format(DATE_FMT);
                y = drawRight(cs, dateEnTete, regular, 11, y);
                y -= 10;
                y = drawRight(cs, "N° " + d.getNumero(), regular, 9, y);
                y -= 40;

                y = drawCentered(cs, "DECHARGE", bold, 16, y);
                y -= 40;

                y = drawLine(cs, "Je soussigné(e) M. " + safe(d.getBeneficiaire()), regular, 11, y);
                y -= 26;

                String cniLine = "C.N.I. N° " + safe(d.getCniNumero())
                        + (d.getCniDate() != null ? "  du " + d.getCniDate().format(DATE_FMT) : "");
                y = drawLine(cs, cniLine, regular, 11, y);
                y -= 26;

                y = drawWrapped(cs, "atteste avoir reçu de Monsieur Souleymane BA, Trésorier Général "
                        + "National de la M.G.E.S.", regular, 11, y);
                y -= 6;
                y = drawLine(cs, "la somme de :", regular, 11, y);
                y -= 26;

                y = drawLine(cs, "(En chiffres)  " + formatMontant(d.getMontant()) + " F CFA", regular, 11, y);
                y -= 26;

                y = drawWrapped(cs, "(En lettres)  " + NumberToWordsFr.montantEnLettres(d.getMontant()), regular, 11, y);
                y -= 30;

                y = drawLine(cs, "Constituant : " + safe(d.getMotif()), regular, 11, y);
                y -= 50;

                y = drawWrapped(cs, "En foi de quoi, je lui délivre cette présente, pour servir et valoir "
                        + "ce que de droit.", regular, 11, y);
                y -= 40;

                y = drawLine(cs, "Fait à " + safe(d.getLieu()) + ", le " + d.getDateDecharge().format(DATE_FMT), regular, 11, y);
                y -= 60;

                drawRight(cs, "M. ......................................................", regular, 11, y);
            }

            document.save(outputFile);
        }
    }

    private PDImageXObject loadLogo(PDDocument document) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/images/logo.png")) {
            if (is == null) {
                return null;
            }
            return PDImageXObject.createFromByteArray(document, is.readAllBytes(), "logo");
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formatMontant(long montant) {
        String s = Long.toString(montant);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.insert(0, s.charAt(i));
            count++;
            if (count % 3 == 0 && i > 0) {
                sb.insert(0, ' ');
            }
        }
        return sb.toString();
    }

    private float drawLine(PDPageContentStream cs, String text, PDFont font, int size, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y;
    }

    private float drawCentered(PDPageContentStream cs, String text, PDFont font, int size, float y) throws IOException {
        float width = font.getStringWidth(text) / 1000 * size;
        float x = (PAGE_WIDTH - width) / 2;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y;
    }

    private float drawRight(PDPageContentStream cs, String text, PDFont font, int size, float y) throws IOException {
        float width = font.getStringWidth(text) / 1000 * size;
        float x = PAGE_WIDTH - MARGIN - width;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y;
    }

    private float drawWrapped(PDPageContentStream cs, String text, PDFont font, int size, float y) throws IOException {
        List<String> lines = wrap(text, font, size, CONTENT_WIDTH);
        for (String line : lines) {
            drawLine(cs, line, font, size, y);
            y -= (size + 6);
        }
        return y;
    }

    private List<String> wrap(String text, PDFont font, int size, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            float width = font.getStringWidth(candidate) / 1000 * size;
            if (width > maxWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }
}
