package com.mosquizto.api.util.matching;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextMatcherFactoryTest {

    private final TextMatcherResolver factory = new TextMatcherResolver();

    @Test
    void shouldResolveDamerauLevenshteinMatcher() {
        TextMatcher matcher = factory.get(TextMatcherType.DAMERAU_LEVENSHTEIN);

        assertInstanceOf(DamerauLevenshteinMatcher.class, matcher);
        assertEquals(1d, matcher.match("H\u1ecdc l\u1eadp tr\u00ecnh Java!", "hoc lap trinh java"), 1e-9);
        assertEquals(0.75d, matcher.match("caht", "chat"), 1e-9);
    }

    @Test
    void shouldResolveBagOfWordsCosineMatcher() {
        TextMatcher matcher = factory.get(TextMatcherType.BAG_OF_WORDS_COSINE);

        assertInstanceOf(BagOfWordsCosineMatcher.class, matcher);
        assertEquals(1d, matcher.match("java spring boot", "boot spring java"), 1e-9);
        assertEquals(0d, matcher.match("java", "football"), 1e-9);
    }

    @Test
    void shouldMatchByTypeAtRuntime() {
        double damerauLevenshteinScore = factory.match(TextMatcherType.DAMERAU_LEVENSHTEIN, "caht", "chat");
        double bagOfWordsCosineScore = factory.match(
                TextMatcherType.BAG_OF_WORDS_COSINE,
                "java spring boot",
                "boot spring java"
        );

        assertEquals(0.75d, damerauLevenshteinScore, 1e-9);
        assertEquals(1d, bagOfWordsCosineScore, 1e-9);
    }

    @Test
    void shouldReturnLowScoreForDifferentTexts() {
        double score = factory.match(TextMatcherType.DAMERAU_LEVENSHTEIN, "java", "football");

        assertTrue(score < 0.2d);
    }
}
