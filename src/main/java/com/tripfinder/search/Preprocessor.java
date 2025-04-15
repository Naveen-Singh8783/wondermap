package com.tripfinder.search;
import java.util.*;

public class Preprocessor {
    // Expanded stopword list
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "and", "or", "but", "if", "in", "on", "with", 
        "as", "by", "for", "of", "to", "at", "be", "is", "are", "was", 
        "were", "it", "that", "this", "which", "have", "has", "had", 
        "will", "would", "can", "could", "shall", "should", "may", 
        "might", "must", "about", "above", "below", "from", "up", "down", 
        "out", "over", "under", "again", "further", "then", "once", "here", 
        "there", "where", "why", "how", "all", "any", "both", "each", 
        "few", "more", "most", "other", "some", "such", "no", "nor", 
        "not", "only", "own", "same", "so", "than", "too", "very", "s", 
        "t", "don", "don't", "should've", "now", "d", "ll", "m", "o", 
        "re", "ve", "y", "ain", "aren", "aren't", "couldn", "couldn't", 
        "didn", "didn't", "doesn", "doesn't", "hadn", "hadn't", "hasn", 
        "hasn't", "haven", "haven't", "isn", "isn't", "ma", "mightn", 
        "mightn't", "mustn", "mustn't", "needn", "needn't", "shan", 
        "shan't", "shouldn", "shouldn't", "wasn", "wasn't", "weren", 
        "weren't", "won", "won't", "wouldn", "wouldn't"
    ));

    public static List<String> preprocess(String text) {
        // Tokenization
        String[] tokens = text.toLowerCase().split("\\W+");

        // Stopword removal and stemming
        List<String> processedTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!STOPWORDS.contains(token) && !token.isEmpty()) {
                String stemmedToken = stem(token);
                if (!stemmedToken.isEmpty()) {
                    processedTokens.add(stemmedToken);
                }
            }
        }

        return processedTokens;
    }

    // Stemming using a simplified Porter Stemmer
    public static String stem(String word) {
        if (word.length() < 3) return word;

        // Step 1a: Plural and past tense endings
        if (word.endsWith("sses")) {
            word = word.substring(0, word.length() - 2); // Replace "sses" with "ss"
        } else if (word.endsWith("ies")) {
            word = word.substring(0, word.length() - 2); // Replace "ies" with "i"
        } else if (word.endsWith("ss")) {
            // No change
        } else if (word.endsWith("s")) {
            word = word.substring(0, word.length() - 1); // Remove "s"
        }

        // Step 1b: -ed and -ing suffixes
        if (word.endsWith("eed")) {
            if (measure(word.substring(0, word.length() - 3)) > 0) {
                word = word.substring(0, word.length() - 1); // Replace "eed" with "ee"
            }
        } else if (word.endsWith("ed") && containsVowel(word.substring(0, word.length() - 2))) {
            word = word.substring(0, word.length() - 2); // Remove "ed"
        } else if (word.endsWith("ing") && containsVowel(word.substring(0, word.length() - 3))) {
            word = word.substring(0, word.length() - 3); // Remove "ing"
        }

        // Step 1c: Replace "y" with "i" if preceded by a vowel
        if (word.endsWith("y") && containsVowel(word.substring(0, word.length() - 1))) {
            word = word.substring(0, word.length() - 1) + "i"; // Replace "y" with "i"
        }

        // Step 2: Common suffixes
        if (word.endsWith("ational")) {
            word = word.substring(0, word.length() - 5) + "e"; // Replace "ational" with "ate"
        } else if (word.endsWith("tional")) {
            word = word.substring(0, word.length() - 2); // Replace "tional" with "tion"
        } else if (word.endsWith("izer")) {
            word = word.substring(0, word.length() - 1); // Replace "izer" with "ize"
        }

        // Step 3: Remove common suffixes
        if (word.endsWith("al")) {
            word = word.substring(0, word.length() - 2); // Remove "al"
        } else if (word.endsWith("ence")) {
            word = word.substring(0, word.length() - 4); // Remove "ence"
        }

        // Step 4: Remove -ity, -able, etc.
        if (word.endsWith("ity")) {
            word = word.substring(0, word.length() - 3); // Remove "ity"
        } else if (word.endsWith("able")) {
            word = word.substring(0, word.length() - 4); // Remove "able"
        }

        // Step 5: Remove -ant, -ence, etc.
        if (word.endsWith("ant")) {
            word = word.substring(0, word.length() - 3); // Remove "ant"
        }

        // Step 6: Remove final 'e' if necessary
        if (word.length() > 1 && word.endsWith("e")) {
            word = word.substring(0, word.length() - 1); // Remove final "e"
        }

        return word;
    }

    // Measures the number of vowel-consonant sequences (m)
    private static int measure(String word) {
        int count = 0;
        boolean vowelSeen = false;
        
        for (int i = 0; i < word.length(); i++) {
            if (isVowel(word.charAt(i))) {
                vowelSeen = true;
            } else if (vowelSeen) {
                count++;
                vowelSeen = false;
            }
        }
        return count;
    }

    // Checks if a string contains a vowel
    private static boolean containsVowel(String word) {
        for (char c : word.toCharArray()) {
            if (isVowel(c)) return true;
        }
        return false;
    }

    // Checks if a character is a vowel
    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) != -1;
    }
}