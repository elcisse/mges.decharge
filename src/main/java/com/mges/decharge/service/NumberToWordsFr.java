package com.mges.decharge.service;

/**
 * Convertit un montant entier en toutes lettres françaises,
 * en respectant les règles d'accord de "vingt", "cent" et "mille".
 */
public final class NumberToWordsFr {

    private static final String[] UNITS = {
            "zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
            "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize"
    };

    private static final String[] TENS = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante"
    };

    private NumberToWordsFr() {
    }

    public static String convert(long number) {
        if (number == 0) {
            return "zéro";
        }
        if (number < 0) {
            return "moins " + convert(-number);
        }
        return convertPositive(number).trim().replaceAll(" +", " ");
    }

    /** Montant en lettres suivi de "francs CFA", ex: "Dix mille francs CFA". */
    public static String montantEnLettres(long montant) {
        String mots = convert(montant);
        String capitalized = mots.substring(0, 1).toUpperCase() + mots.substring(1);
        return capitalized + " francs CFA";
    }

    private static String convertPositive(long n) {
        if (n < 17) {
            return UNITS[(int) n];
        }
        if (n < 20) {
            return "dix-" + UNITS[(int) (n - 10)];
        }
        if (n < 100) {
            return convertTens(n);
        }
        if (n < 1000) {
            return convertHundreds(n);
        }
        if (n < 1_000_000) {
            return convertThousands(n);
        }
        if (n < 1_000_000_000) {
            return convertMillions(n);
        }
        return convertBillions(n);
    }

    private static String convertTens(long n) {
        // 70-79 : soixante + (10..19)
        if (n >= 70 && n < 80) {
            long reste = n - 60;
            if (reste == 11) {
                return "soixante et onze";
            }
            return "soixante-" + convertPositive(reste);
        }
        // 80-89 : quatre-vingt(s) + (0..9), pas de "et"
        if (n >= 80 && n < 90) {
            int unite = (int) (n - 80);
            if (unite == 0) {
                return "quatre-vingts";
            }
            return "quatre-vingt-" + UNITS[unite];
        }
        // 90-99 : quatre-vingt + (10..19), pas de "et"
        if (n >= 90 && n < 100) {
            long reste = n - 80;
            return "quatre-vingt-" + convertPositive(reste);
        }

        int dizaine = (int) (n / 10);
        int unite = (int) (n % 10);
        if (unite == 0) {
            return TENS[dizaine];
        }
        if (unite == 1) {
            return TENS[dizaine] + " et un";
        }
        return TENS[dizaine] + "-" + UNITS[unite];
    }

    private static String convertHundreds(long n) {
        int centaines = (int) (n / 100);
        long reste = n % 100;

        StringBuilder sb = new StringBuilder();
        if (centaines == 1) {
            sb.append("cent");
        } else {
            sb.append(UNITS[centaines]).append(" cent");
            if (reste == 0) {
                sb.append("s");
            }
        }
        if (reste > 0) {
            sb.append(" ").append(convertPositive(reste));
        }
        return sb.toString();
    }

    private static String convertThousands(long n) {
        long milliers = n / 1000;
        long reste = n % 1000;

        StringBuilder sb = new StringBuilder();
        if (milliers == 1) {
            sb.append("mille");
        } else {
            sb.append(convertPositive(milliers)).append(" mille");
        }
        if (reste > 0) {
            sb.append(" ").append(convertPositive(reste));
        }
        return sb.toString();
    }

    private static String convertMillions(long n) {
        long millions = n / 1_000_000;
        long reste = n % 1_000_000;

        StringBuilder sb = new StringBuilder();
        sb.append(convertPositive(millions)).append(millions > 1 ? " millions" : " million");
        if (reste > 0) {
            sb.append(" ").append(convertPositive(reste));
        }
        return sb.toString();
    }

    private static String convertBillions(long n) {
        long milliards = n / 1_000_000_000;
        long reste = n % 1_000_000_000;

        StringBuilder sb = new StringBuilder();
        sb.append(convertPositive(milliards)).append(milliards > 1 ? " milliards" : " milliard");
        if (reste > 0) {
            sb.append(" ").append(convertPositive(reste));
        }
        return sb.toString();
    }
}
