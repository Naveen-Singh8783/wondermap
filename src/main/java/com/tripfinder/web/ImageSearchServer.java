package com.tripfinder.web;

import com.tripfinder.indexer.InvertedIndex;
import com.tripfinder.model.ImageDocument;
import com.tripfinder.parser.ImageMetadataParser;
import com.tripfinder.search.Preprocessor;
import com.tripfinder.search.SearchEngine;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;


import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class ImageSearchServer {
    public static void main(String[] args) {
        List<ImageDocument> imageDocs = ImageMetadataParser.parse("image_metadata.json");

        InvertedIndex index = new InvertedIndex();
        for (ImageDocument doc : imageDocs) {
            List<String> tokens = Preprocessor.preprocess(doc.getFullText());
            if (doc.getDetectedObjects() != null) {
                tokens.addAll(doc.getDetectedObjects()); // raw labels like "person", "car"
            }
            index.addDocument(doc.getId(), tokens);
        }

        SearchEngine engine = new SearchEngine(index);

        staticFiles.location("/public"); // Serve CSS, images if needed

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("query", "");
            model.put("results", new ArrayList<>());
            return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/search.vm"));
        });

        get("/search", (req, res) -> {
            String query = req.queryParams("query");
            List<String> tokens = Preprocessor.preprocess(query);
            List<Integer> resultIds = engine.search(tokens);

            List<ImageDocument> topDocs = resultIds.stream()
                    .limit(10)
                    .map(imageDocs::get)
                    .collect(Collectors.toList());

            Map<String, Object> model = new HashMap<>();
            model.put("query", query);
            model.put("results", topDocs);
            return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/search.vm"));
        });
    }
}
