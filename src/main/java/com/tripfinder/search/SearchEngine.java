package com.tripfinder.search;

import java.util.*;
import com.tripfinder.indexer.InvertedIndex;

public class SearchEngine {
    private InvertedIndex index;
    private BM25 bm25;

    public SearchEngine(InvertedIndex index) {
        this.index = index;
        this.bm25 = new BM25(index);
    }

    public List<Integer> search(List<String> queryTerms) {
        // Aggregate documents containing any query term
        Set<Integer> uniqueDocs = new LinkedHashSet<>();
        for (String term : queryTerms) {
            uniqueDocs.addAll(index.search(term));
        }

        List<Integer> docIds = new ArrayList<>(uniqueDocs);
        Map<Integer, Double> scores = computeScoresBM25(queryTerms, docIds);
        docIds.sort((docId1, docId2) -> Double.compare(scores.get(docId2), scores.get(docId1)));
        return docIds;
    }

    public Map<Integer, Double> computeScoresBM25(List<String> queryTerms, List<Integer> docIds) {
        Map<Integer, Double> scores = new HashMap<>();
        for (int docId : docIds) {
            scores.put(docId, bm25.score(queryTerms, docId));
        }
        return scores;
    }
}
