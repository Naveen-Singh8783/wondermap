package com.tripfinder.model;

import java.util.List;

public class ImageDocument {
    private int id;
    private String filename;
    private String alt;
    private String caption;
    private String pageTitle;
    private String paragraph;
    private String sourceUrl;

    // 🔥 Add this:
    private List<String> detected_objects;

    public ImageDocument(int id, String filename, String alt, String caption, String pageTitle, String paragraph, String sourceUrl) {
        this.id = id;
        this.filename = filename;
        this.alt = alt;
        this.caption = caption;
        this.pageTitle = pageTitle;
        this.paragraph = paragraph;
        this.sourceUrl = sourceUrl;
    }

    // 🔥 Add this setter for YOLO result injection
    public void setDetectedObjects(List<String> detected_objects) {
        this.detected_objects = detected_objects;
    }

    // 🔥 Add this getter for indexing
    public List<String> getDetectedObjects() {
        return detected_objects;
    }

    public int getId() { return id; }
    public String getFilename() { return filename; }
    public String getAlt() { return alt; }
    public String getSourceUrl() { return sourceUrl; }

    public String getFullText() {
        return String.join(" ", alt, caption, pageTitle, paragraph);
    }

    public String getCleanTitle() {
        if (filename == null || !filename.contains("px-")) return "Untitled";
    
        // Extract portion after "px-"
        String name = filename.replace(".jpg", "");
        int pxIndex = name.indexOf("px-");
    
        if (pxIndex != -1 && pxIndex + 3 < name.length()) {
            name = name.substring(pxIndex + 3);
        }
    
        // Remove long numeric or encoded suffixes like "__2854_29_1"
        name = name.replaceAll("__?\\d+.*$", ""); // removes everything after __ or _ if digits follow
    
        // Replace underscores with spaces
        name = name.replaceAll("_+", " ").trim();
    
        // Capitalize words
        if (name.isEmpty()) return "Untitled";
    
        String[] words = name.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0)))
                       .append(word.substring(1).toLowerCase())
                       .append(" ");
            }
        }
    
        return builder.toString().trim();
    }
    
}
