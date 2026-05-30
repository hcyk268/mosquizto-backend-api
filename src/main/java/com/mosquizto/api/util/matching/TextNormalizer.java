package com.mosquizto.api.util.matching;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        return applyNormalization(text);
    }


    public static String normalize(String text, int maxLength) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String truncated = text.length() > maxLength ? text.substring(0, maxLength) : text;
        return applyNormalization(truncated);
    }

    private static String applyNormalization(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{Alnum}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
