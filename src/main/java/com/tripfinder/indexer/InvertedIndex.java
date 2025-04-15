package com.tripfinder.indexer;
import java.util.*;

public class InvertedIndex {
    // Inverted index: term -> list of postings (docId, term frequency)
    private Map<String, List<Posting>> index;
    
    // Document lengths: docId -> document length
    private Map<Integer, Integer> documentLengths;
    
    // Collection model: term -> collection frequency
    private Map<String, Integer> collectionModel;
    
    // Total number of documents
    private int totalDocuments;
    
    // Total number of terms in the collection
    private int totalTermsInCollection;

    public InvertedIndex() {
        index = new HashMap<>();
        documentLengths = new HashMap<>();
        collectionModel = new HashMap<>();
        totalDocuments = 0;
        totalTermsInCollection = 0;
    }

    // Add a document to the index
    public void addDocument(int docId, List<String> tokens) {
        // Track term frequencies in this document
        Map<String, Integer> termFrequencies = new HashMap<>();
        for (String token : tokens) {
            termFrequencies.put(token, termFrequencies.getOrDefault(token, 0) + 1);
        }

        // Update the inverted index
        for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
            String term = entry.getKey();
            int frequency = entry.getValue();
            
            // Add posting to the inverted index
            index.computeIfAbsent(term, k -> new ArrayList<>()).add(new Posting(docId, frequency));
            
            // Update collection frequency
            collectionModel.put(term, collectionModel.getOrDefault(term, 0) + frequency);
        }

        // Update document length
        documentLengths.put(docId, tokens.size());

        // Update total terms in the collection
        totalTermsInCollection += tokens.size();

        // Update total documents
        totalDocuments++;
    }

    // Search for a term and return the list of document IDs
    public List<Integer> search(String term) {
        List<Integer> docIds = new ArrayList<>();
        if (index.containsKey(term)) {
            for (Posting posting : index.get(term)) {
                docIds.add(posting.getDocId());
            }
        }
        return docIds;
    }

    // Get the term frequency in a specific document
    public int getTermFrequency(String term, int docId) {
        if (index.containsKey(term)) {
            for (Posting posting : index.get(term)) {
                if (posting.getDocId() == docId) {
                    return posting.getTermFrequency();
                }
            }
        }
        return 0;
    }

    // Get the length of a document
    public int getDocumentLength(int docId) {
        return documentLengths.getOrDefault(docId, 0);
    }

    // Get the document frequency of a term
    public int getDocumentFrequency(String term) {
        return index.getOrDefault(term, Collections.emptyList()).size();
    }

    // Get the total number of documents
    public int getTotalDocuments() {
        return totalDocuments;
    }

    // Get the average document length
    public double getAverageDocumentLength() {
        return documentLengths.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // Get the term frequencies for a specific document
    public Map<String, Integer> getDocumentModel(int docId) {
        Map<String, Integer> documentModel = new HashMap<>();
        for (Map.Entry<String, List<Posting>> entry : index.entrySet()) {
            String term = entry.getKey();
            for (Posting posting : entry.getValue()) {
                if (posting.getDocId() == docId) {
                    documentModel.put(term, posting.getTermFrequency());
                    break;
                }
            }
        }
        return documentModel;
    }

    // Get the collection model (term -> collection frequency)
    public Map<String, Integer> getCollectionModel() {
        return collectionModel;
    }

    // Get the collection frequency of a term
    public int getCollectionFrequency(String term) {
        return collectionModel.getOrDefault(term, 0);
    }

    // Get the total number of terms in the collection
    public int getTotalTermsInCollection() {
        return totalTermsInCollection;
    }

    // Inner class to represent a posting (docId, term frequency)
    private static class Posting {
        private int docId;
        private int termFrequency;

        public Posting(int docId, int termFrequency) {
            this.docId = docId;
            this.termFrequency = termFrequency;
        }

        public int getDocId() {
            return docId;
        }

        public int getTermFrequency() {
            return termFrequency;
        }
    }
}