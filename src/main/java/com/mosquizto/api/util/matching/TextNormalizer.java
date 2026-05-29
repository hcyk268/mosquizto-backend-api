package com.mosquizto.api.util.matching;

import java.text.Normalizer;
import java.util.Locale;

final class TextNormalizer {

    private TextNormalizer() {
    }

    static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{Alnum}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
