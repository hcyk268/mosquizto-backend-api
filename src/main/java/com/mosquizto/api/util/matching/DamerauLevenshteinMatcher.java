package com.mosquizto.api.util.matching;

import java.util.HashMap;
import java.util.Map;

public final class DamerauLevenshteinMatcher implements TextMatcher {

    @Override
    public TextMatcherType type() {
        return TextMatcherType.DAMERAU_LEVENSHTEIN;
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

        int maxLength = Math.max(normalizedLeft.length(), normalizedRight.length());
        int distance = distance(normalizedLeft, normalizedRight);
        return 1d - ((double) distance / maxLength);
    }

    private int distance(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();
        int maxDistance = sourceLength + targetLength;

        int[][] distances = new int[sourceLength + 2][targetLength + 2];
        distances[0][0] = maxDistance;

        for (int sourceIndex = 0; sourceIndex <= sourceLength; sourceIndex++) {
            distances[sourceIndex + 1][1] = sourceIndex;
            distances[sourceIndex + 1][0] = maxDistance;
        }

        for (int targetIndex = 0; targetIndex <= targetLength; targetIndex++) {
            distances[1][targetIndex + 1] = targetIndex;
            distances[0][targetIndex + 1] = maxDistance;
        }

        Map<Character, Integer> lastRowByCharacter = new HashMap<>();

        for (int sourceIndex = 1; sourceIndex <= sourceLength; sourceIndex++) {
            int lastMatchingColumn = 0;

            for (int targetIndex = 1; targetIndex <= targetLength; targetIndex++) {
                char sourceCharacter = source.charAt(sourceIndex - 1);
                char targetCharacter = target.charAt(targetIndex - 1);

                int lastSourceRow = lastRowByCharacter.getOrDefault(targetCharacter, 0);
                int lastTargetColumn = lastMatchingColumn;

                int substitutionCost = 1;
                if (sourceCharacter == targetCharacter) {
                    substitutionCost = 0;
                    lastMatchingColumn = targetIndex;
                }

                distances[sourceIndex + 1][targetIndex + 1] = Math.min(
                        Math.min(
                                distances[sourceIndex][targetIndex] + substitutionCost,
                                distances[sourceIndex + 1][targetIndex] + 1
                        ),
                        Math.min(
                                distances[sourceIndex][targetIndex + 1] + 1,
                                distances[lastSourceRow][lastTargetColumn]
                                        + (sourceIndex - lastSourceRow - 1)
                                        + 1
                                        + (targetIndex - lastTargetColumn - 1)
                        )
                );
            }

            lastRowByCharacter.put(source.charAt(sourceIndex - 1), sourceIndex);
        }

        return distances[sourceLength + 1][targetLength + 1];
    }
}
