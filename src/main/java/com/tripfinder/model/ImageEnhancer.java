// package com.tripfinder.model;

// import com.google.gson.*;
// import com.google.gson.reflect.TypeToken;
// import org.opencv.core.*;
// import org.opencv.dnn.*;
// import org.opencv.imgcodecs.Imgcodecs;

// import java.io.*;
// import java.lang.reflect.Type;
// import java.nio.file.Paths;
// import java.util.*;

// public class ImageEnhancer {

//     static {
//         System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//     }

//     private static final String IMAGE_DIR = "src/main/resources/public/downloaded_images";
//     private static final String METADATA_FILE = "image_metadata.json";
//     private static final String YOLO_CFG = "src/main/resources/models/yolo/yolov3.cfg";
//     private static final String YOLO_WEIGHTS = "src/main/resources/models/yolo/yolov3.weights";
//     private static final String YOLO_NAMES = "src/main/resources/models/yolo/coco.names";

//     private static Net yoloNet;
//     private static List<String> classNames;

//     static class ImageMetadata {
//         String country;
//         String imageUrl;
//         String filename;
//         String alt;
//         String caption;
//         String pageTitle;
//         String paragraph;
//         String sourceUrl;

//         int width;
//         int height;
//         ColorProfile color_profile;
//         List<String> detected_objects;
//     }

//     static class ColorProfile {
//         double avg_red, avg_green, avg_blue;
//         public ColorProfile(double r, double g, double b) {
//             this.avg_red = r;
//             this.avg_green = g;
//             this.avg_blue = b;
//         }
//     }

//     public static void main(String[] args) {
//         try {
//             initYOLO();

//             Gson gson = new GsonBuilder().setPrettyPrinting().create();
//             Reader reader = new FileReader(METADATA_FILE);
//             Type listType = new TypeToken<List<ImageMetadata>>() {}.getType();
//             List<ImageMetadata> metadataList = gson.fromJson(reader, listType);
//             reader.close();

//             for (ImageMetadata meta : metadataList) {
//                 String imagePath = Paths.get(IMAGE_DIR, meta.filename).toString();
//                 Mat image = Imgcodecs.imread(imagePath);

//                 if (image.empty()) {
//                     System.err.println("⚠️ Could not read image: " + imagePath);
//                     continue;
//                 }

//                 meta.width = image.cols();
//                 meta.height = image.rows();

//                 List<Mat> channels = new ArrayList<>();
//                 Core.split(image, channels);
//                 meta.color_profile = new ColorProfile(
//                         Core.mean(channels.get(2)).val[0], // R
//                         Core.mean(channels.get(1)).val[0], // G
//                         Core.mean(channels.get(0)).val[0]  // B
//                 );

//                 meta.detected_objects = detectObjects(image);
//             }

//             Writer writer = new FileWriter(METADATA_FILE);
//             gson.toJson(metadataList, writer);
//             writer.close();

//             System.out.println("✅ Metadata enriched with OpenCV visual features + YOLO object detection.");

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private static void initYOLO() throws IOException {
//         yoloNet = Dnn.readNetFromDarknet(YOLO_CFG, YOLO_WEIGHTS);
//         yoloNet.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
//         yoloNet.setPreferableTarget(Dnn.DNN_TARGET_CPU);

//         classNames = new ArrayList<>();
//         try (BufferedReader br = new BufferedReader(new FileReader(YOLO_NAMES))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 classNames.add(line);
//             }
//         }
//     }

//     private static List<String> detectObjects(Mat image) {
//         List<String> labels = new ArrayList<>();
//         Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(416, 416), new Scalar(0), true, false);
//         yoloNet.setInput(blob);

//         List<Mat> outputs = new ArrayList<>();
//         yoloNet.forward(outputs, yoloNet.getUnconnectedOutLayersNames());

//         float confThreshold = 0.5f;
//         for (Mat result : outputs) {
//             for (int i = 0; i < result.rows(); i++) {
//                 Mat row = result.row(i);
//                 Mat scores = row.colRange(5, result.cols());
//                 Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
//                 float confidence = (float) mm.maxVal;
//                 int classId = (int) mm.maxLoc.x;

//                 if (confidence > confThreshold && classId < classNames.size()) {
//                     String label = classNames.get(classId);
//                     if (!labels.contains(label)) {
//                         labels.add(label);
//                     }
//                 }
//             }
//         }
//         return labels;
//     }
// }
