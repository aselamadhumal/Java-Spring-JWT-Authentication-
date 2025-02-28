package com.jwtauth.jwtauth.utils;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private final List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        // Recompile the combined pattern whenever a new pattern is added
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        // Apply message masking before logging it
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        if (multilinePattern == null || message == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);

        // Use a list to store the start and end indices of matches
        List<int[]> matches = new ArrayList<>();

        // First, find all matches and store their positions
        while (matcher.find()) {
            for (int group = 1; group <= matcher.groupCount(); group++) {
                if (matcher.group(group) != null) {
                    matches.add(new int[]{matcher.start(group), matcher.end(group)});
                }
            }
        }

        // Replace the matches from the end to the beginning to avoid index shifting
        for (int i = matches.size() - 1; i >= 0; i--) {
            int[] match = matches.get(i);
            int start = match[0];
            int end = match[1];
            sb.replace(start, end, "*****");
        }

        return sb.toString();
    }
}

/*package com.jwtauth.jwtauth.utils;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        // Recompile the combined pattern whenever a new pattern is added
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        // Apply message masking before logging it
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        if (multilinePattern == null || message == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);

        while (matcher.find()) {
            // Iterate over each matched group in the pattern
            for (int group = 1; group <= matcher.groupCount(); group++) {
                if (matcher.group(group) != null) {
                    // Replace the matched characters with '*'
                    IntStream.range(matcher.start(group), matcher.end(group))
                            .forEach(i -> sb.setCharAt(i, '*'));
                }
            }
        }

        return sb.toString();
    }
}*/