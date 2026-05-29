package com.mosquizto.api.util.matching;

import java.util.HashMap;
import java.util.Map;

public final class BagOfWordsCosineMatcher implements TextMatcher {

    @Override
    public TextMatcherType type() {
        return TextMatcherType.BAG_OF_WORDS_COSINE;
    }

    @Override
    public double match(String left, String right) {
        String normalizedLeft = TextNormalizer.normalize(left);
        String normalizedRight = TextNormalizer.normalize(right);

        if (normalizedLeft.equals(normalizedRight)) {
            return 1d;
        }

        if (normalizedLeft.isBlank() || normalizedRight.isBlank()) {
            return 0d;
        }

        Map<String, Integer> leftFrequencies = termFrequencies(normalizedLeft);
        Map<String, Integer> rightFrequencies = termFrequencies(normalizedRight);

        double dotProduct = 0d;
        double leftMagnitude = 0d;
        double rightMagnitude = 0d;

        for (Map.Entry<String, Integer> entry : leftFrequencies.entrySet()) {
            int leftFrequency = entry.getValue();
            leftMagnitude += (double) leftFrequency * leftFrequency;
            dotProduct += (double) leftFrequency * rightFrequencies.getOrDefault(entry.getKey(), 0);
        }

        for (int rightFrequency : rightFrequencies.values()) {
            rightMagnitude += (double) rightFrequency * rightFrequency;
        }

        if (leftMagnitude == 0d || rightMagnitude == 0d) {
            return 0d;
        }

        return dotProduct / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }

    private Map<String, Integer> termFrequencies(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        for (String token : text.split("\\s+")) {
            frequencies.merge(token, 1, Integer::sum);
        }
        return frequencies;
    }
}
