package com.mosquizto.api.util.matching;

public interface TextMatcher {

    TextMatcherType type();

    double match(String left, String right);
}
