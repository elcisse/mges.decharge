package com.mges.decharge;

import com.mges.decharge.service.NumberToWordsFr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberToWordsFrTest {

    @Test
    void basicNumbers() {
        assertEquals("zéro", NumberToWordsFr.convert(0));
        assertEquals("un", NumberToWordsFr.convert(1));
        assertEquals("seize", NumberToWordsFr.convert(16));
        assertEquals("dix-sept", NumberToWordsFr.convert(17));
        assertEquals("dix-neuf", NumberToWordsFr.convert(19));
    }

    @Test
    void tens() {
        assertEquals("vingt", NumberToWordsFr.convert(20));
        assertEquals("vingt et un", NumberToWordsFr.convert(21));
        assertEquals("vingt-deux", NumberToWordsFr.convert(22));
        assertEquals("soixante-dix", NumberToWordsFr.convert(70));
        assertEquals("soixante et onze", NumberToWordsFr.convert(71));
        assertEquals("soixante-douze", NumberToWordsFr.convert(72));
        assertEquals("soixante-dix-neuf", NumberToWordsFr.convert(79));
        assertEquals("quatre-vingts", NumberToWordsFr.convert(80));
        assertEquals("quatre-vingt-un", NumberToWordsFr.convert(81));
        assertEquals("quatre-vingt-dix", NumberToWordsFr.convert(90));
        assertEquals("quatre-vingt-onze", NumberToWordsFr.convert(91));
        assertEquals("quatre-vingt-dix-neuf", NumberToWordsFr.convert(99));
    }

    @Test
    void hundreds() {
        assertEquals("cent", NumberToWordsFr.convert(100));
        assertEquals("cent un", NumberToWordsFr.convert(101));
        assertEquals("deux cents", NumberToWordsFr.convert(200));
        assertEquals("deux cent un", NumberToWordsFr.convert(201));
        assertEquals("neuf cent quatre-vingt-dix-neuf", NumberToWordsFr.convert(999));
    }

    @Test
    void thousands() {
        assertEquals("mille", NumberToWordsFr.convert(1000));
        assertEquals("deux mille", NumberToWordsFr.convert(2000));
        assertEquals("dix mille", NumberToWordsFr.convert(10000));
        assertEquals("mille cinq cents", NumberToWordsFr.convert(1500));
        assertEquals("vingt et un mille", NumberToWordsFr.convert(21000));
    }

    @Test
    void montantEnLettres() {
        assertEquals("Dix mille francs CFA", NumberToWordsFr.montantEnLettres(10000));
        assertEquals("Cent mille francs CFA", NumberToWordsFr.montantEnLettres(100000));
    }
}
