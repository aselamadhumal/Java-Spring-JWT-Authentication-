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
}*/
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
    private static final int MAX_MATCHES = 10; // Maximum allowed matches
    private final List<String> maskPatterns = new ArrayList<>();
    private Pattern multilinePattern;
    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    public String maskMessage(String message) {
        if (multilinePattern == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);

        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {

                    int startIndex = matcher.start(group);
                    int bound = matcher.end(group);

                    // Avoid full mask if card number is present
                    boolean accountNo = matcher.group().contains("number")
                            || matcher.group().contains("accountNo");

                    // Handle account number (masking part of it)
                    if (accountNo) {
                        startIndex += 6; // Start masking after the first 6 characters
                        bound -= 4; // Stop masking 4 characters before the end

                        for (int i = startIndex; i < bound; i++) {
                            sb.setCharAt(i, '*');
                            System.out.println("Masking account number: " + sb);
                        }
                    } else {
                        // Mask the entire match with "*****"
                        sb.replace(startIndex, bound, "**");
                        System.out.println("Masking with *****: " + sb);
                    }

                }
            });
        }

        return sb.toString();
    }
}
