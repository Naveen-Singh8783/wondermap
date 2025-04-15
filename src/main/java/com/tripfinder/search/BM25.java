package com.tripfinder.search;

import com.tripfinder.indexer.InvertedIndex;
import java.util.List;

public class BM25 {
    private final InvertedIndex index;
    private static final double K1 = 1.2;
    private static final double B = 0.75;

    public BM25(InvertedIndex index) {
        this.index = index;
    }

    public double score(List<String> queryTerms, int docId) {
        double score = 0.0;
        double avgDocLength = index.getAverageDocumentLength();
        int totalDocs = index.getTotalDocuments();
        int docLength = index.getDocumentLength(docId);

        for (String term : queryTerms) {
            int tf = index.getTermFrequency(term, docId);
            int df = index.getDocumentFrequency(term);

            if (df == 0 || tf == 0) continue;

            double idf = Math.log((totalDocs - df + 0.5) / (df + 0.5) + 1.0);
            double tfComponent = (tf * (K1 + 1)) / (tf + K1 * (1 - B + B * (docLength / avgDocLength)));

            score += idf * tfComponent;
        }

        return score;
    }
}
