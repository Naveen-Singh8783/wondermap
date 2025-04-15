package com.tripfinder.parser;

import com.tripfinder.model.ImageDocument;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImageMetadataParser {
    public static List<ImageDocument> parse(String resourceName) {
        List<ImageDocument> images = new ArrayList<>();

        try {
            // Load from classpath (e.g., src/main/resources/image_metadata.json)
            InputStream in = ImageMetadataParser.class.getClassLoader().getResourceAsStream(resourceName);

            if (in == null) {
                System.err.println("❌ Could not find resource: " + resourceName);
                return images;
            }

            InputStreamReader reader = new InputStreamReader(in);
            Gson gson = new Gson();
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();

            int id = 0;
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();

                ImageDocument doc = new ImageDocument(
                        id++,
                        obj.get("filename").getAsString(),
                        obj.get("alt").getAsString(),
                        obj.get("caption").getAsString(),
                        obj.get("pageTitle").getAsString(),
                        obj.get("paragraph").getAsString(),
                        obj.get("sourceUrl").getAsString()
                );

                // ✅ Handle detected_objects if present
                if (obj.has("detected_objects") && obj.get("detected_objects").isJsonArray()) {
                    List<String> detected = gson.fromJson(
                            obj.get("detected_objects"), new TypeToken<List<String>>() {}.getType());
                    doc.setDetectedObjects(detected);
                }

                images.add(doc);
            }

        } catch (Exception e) {
            System.err.println("❌ Error parsing image metadata:");
            e.printStackTrace();
        }

        return images;
    }
}
