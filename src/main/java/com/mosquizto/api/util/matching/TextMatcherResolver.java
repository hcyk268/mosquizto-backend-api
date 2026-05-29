package com.mosquizto.api.util.matching;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Component
public final class TextMatcherResolver {

    private final Map<TextMatcherType, TextMatcher> matchers;

    public TextMatcherResolver() {
        this(
                new DamerauLevenshteinMatcher(),
                new BagOfWordsCosineMatcher()
        );
    }

    public TextMatcherResolver(TextMatcher... matchers) {
        EnumMap<TextMatcherType, TextMatcher> matcherMap = new EnumMap<>(TextMatcherType.class);
        Arrays.stream(matchers).forEach(matcher -> matcherMap.put(matcher.type(), matcher));
        this.matchers = Map.copyOf(matcherMap);
    }

    public TextMatcher get(TextMatcherType type) {
        TextMatcher matcher = matchers.get(type);
        if (matcher == null) {
            throw new IllegalArgumentException("Unsupported matcher type: " + type);
        }

        return matcher;
    }

    public double match(TextMatcherType type, String left, String right) {
        return get(type).match(left, right);
    }
}
