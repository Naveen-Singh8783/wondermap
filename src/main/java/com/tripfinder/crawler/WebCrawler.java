package com.tripfinder.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URL;
import java.util.*;

public class WebCrawler {

    private static final String TOURISM_BASE_URL = "https://en.wikipedia.org/wiki/Tourism_in_";
    private static final String COUNTRY_LIST_URL = "https://en.wikipedia.org/wiki/List_of_sovereign_states";
    private static final String IMAGE_SAVE_DIR = "downloaded_images/";
    private static final String METADATA_FILE = "image_metadata.json";
    private static final int IMAGES_PER_COUNTRY = 20;

    private static final List<ImageMetadata> metadataList = new ArrayList<>();

    public static void main(String[] args) {
        new File(IMAGE_SAVE_DIR).mkdirs();

        List<String> countries = fetchAllCountries();

        int totalDownloaded = 0;
        for (String country : countries) {
            int downloaded = downloadImagesForCountry(country, IMAGES_PER_COUNTRY);
            if (downloaded > 0) {
                totalDownloaded += downloaded;
                System.out.println("[" + country + "] Downloaded: " + downloaded + " images ✅");
            } else {
                System.out.println("[" + country + "] Skipped ❌ (no images or page not found)");
            }
        }

        writeMetadataToJson();
        System.out.println("🎉 Done. Total images downloaded: " + totalDownloaded);
    }

    private static List<String> fetchAllCountries() {
        List<String> countries = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(COUNTRY_LIST_URL).userAgent("Mozilla/5.0").get();
            Elements links = doc.select("table.wikitable tbody tr td:first-child a");

            for (Element link : links) {
                String title = link.attr("title");
                if (title != null && !title.contains("(") && !title.toLowerCase().contains("territory")) {
                    countries.add(title.replace(" ", "_"));
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to fetch country list: " + e.getMessage());
        }

        return countries;
    }

    private static int downloadImagesForCountry(String country, int maxImages) {
        int downloaded = 0;

        try {
            String pageUrl = TOURISM_BASE_URL + country;
            Document doc = Jsoup.connect(pageUrl).userAgent("Mozilla/5.0").get();
            Elements imgs = doc.select("img");

            String pageTitle = doc.title();

            for (Element img : imgs) {
                if (downloaded >= maxImages) break;
            
                String imgUrl = img.absUrl("src");
                if (!imgUrl.startsWith("https")) continue;
                if (imgUrl.contains("icon") || imgUrl.contains("logo") || imgUrl.contains("wikimedia-button")) continue;
                if (imgUrl.endsWith(".svg")) continue;
            
                int width = 0, height = 0;
                try {
                    width = Integer.parseInt(img.attr("width"));
                    height = Integer.parseInt(img.attr("height"));
                } catch (NumberFormatException ignored) {}
            
                // Skip small images
                if (width < 100 || height < 100) continue;
            
                try {
                    String urlPath = new URL(imgUrl).getPath();
                    if (!urlPath.contains(".")) continue;
            
                    String rawFileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);
                    String originalName = rawFileName;
            
                    String nameOnly = rawFileName.contains(".") ?
                            rawFileName.substring(0, rawFileName.lastIndexOf(".")) : rawFileName;
                    String extension = rawFileName.contains(".") ?
                            rawFileName.substring(rawFileName.lastIndexOf(".")) : ".jpg";
            
                    // Sanitize filename
                    nameOnly = nameOnly.replaceAll("[^a-zA-Z0-9\\-_]", "_");
                    String finalFileName = country + "_" + nameOnly + "_" + (downloaded + 1) + extension;
            
                    saveImage(imgUrl, finalFileName);
            
                    String alt = img.attr("alt");
            
                    String caption = "", paragraph = "";
                    Element figure = img.closest("figure");
                    if (figure != null) {
                        Element figcaption = figure.selectFirst("figcaption");
                        if (figcaption != null) caption = figcaption.text();
                    }
                    Element para = img.closest("p");
                    if (para != null) paragraph = para.text();
            
                    ImageMetadata meta = new ImageMetadata(
                            country,
                            imgUrl,
                            finalFileName,
                            originalName,
                            alt,
                            caption,
                            pageTitle,
                            paragraph,
                            pageUrl
                    );
                    metadataList.add(meta);
                    downloaded++;
            
                } catch (IOException e) {
                    System.err.println("⚠️ Failed to save image: " + imgUrl);
                }
            }            

            Thread.sleep(800);

        } catch (IOException e) {
            System.err.println("❌ Tourism page not found: " + country);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        return downloaded;
    }

    private static void saveImage(String imageUrl, String fileName) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream();
             OutputStream out = new FileOutputStream(IMAGE_SAVE_DIR + fileName)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }
    }

    private static void writeMetadataToJson() {
        try (Writer writer = new FileWriter(METADATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(metadataList, writer);
        } catch (IOException e) {
            System.err.println("❌ Could not write metadata file: " + e.getMessage());
        }
    }

    // Extended metadata class
    static class ImageMetadata {
        String country;
        String imageUrl;
        String filename;
        String originalFilename; // New field
        String alt;
        String caption;
        String pageTitle;
        String paragraph;
        String sourceUrl;
    
        public ImageMetadata(String country, String imageUrl, String filename, String originalFilename,
                             String alt, String caption, String pageTitle, String paragraph, String sourceUrl) {
            this.country = country;
            this.imageUrl = imageUrl;
            this.filename = filename;
            this.originalFilename = originalFilename;
            this.alt = alt;
            this.caption = caption;
            this.pageTitle = pageTitle;
            this.paragraph = paragraph;
            this.sourceUrl = sourceUrl;
        }
    }    
}
