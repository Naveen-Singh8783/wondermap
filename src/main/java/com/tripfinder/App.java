package com.tripfinder;

import com.tripfinder.model.ImageDocument;
import com.tripfinder.parser.ImageMetadataParser;
import com.tripfinder.indexer.InvertedIndex;
import com.tripfinder.search.SearchEngine;
import com.tripfinder.search.Preprocessor;

import java.util.*;

public class App {
    public static void main(String[] args) {
        // Load image metadata from JSON
        String jsonPath = "image_metadata.json";
        List<ImageDocument> imageDocs = ImageMetadataParser.parse(jsonPath);

        // Index documents
        InvertedIndex index = new InvertedIndex();
        for (ImageDocument doc : imageDocs) {
            List<String> tokens = Preprocessor.preprocess(doc.getFullText());
            index.addDocument(doc.getId(), tokens);
        }

        // Set up search engine
        SearchEngine searchEngine = new SearchEngine(index);

        // Input loop
        Scanner scanner = new Scanner(System.in);
        System.out.println("🔍 Image Search Engine Ready. Type a query or 'exit':");

        while (true) {
            System.out.print("Query: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            List<String> queryTokens = Preprocessor.preprocess(input);
            List<Integer> results = searchEngine.search(queryTokens);

            if (results.isEmpty()) {
                System.out.println("❌ No results found.");
            } else {
                System.out.println("✅ Top results:");
                for (int docId : results.subList(0, Math.min(5, results.size()))) {
                    ImageDocument doc = imageDocs.get(docId);
                    System.out.println("- " + doc.getFilename() + " (" + doc.getAlt() + ")");
                }
            }
        }

        scanner.close();
    }
}
