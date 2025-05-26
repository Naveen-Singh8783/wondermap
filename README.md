WonderMap 🌍🖼️

A country-themed image search engine combining computer vision and information retrieval.

WonderMap is a lightweight, end-to-end image search engine focused on tourism-related images from countries around the world. Built with Java, Spark, and Maven, it crawls Wikipedia for images, enriches them with visual metadata using OpenCV and YOLO, and allows users to search via a simple web interface.

✨ Features
🔍 Web Crawling: Collects over 1,000+ country images from Wikipedia using JSoup.

🧠 Metadata Enrichment: Adds color profiles and object labels using OpenCV and YOLOv3.

📦 Efficient Indexing: Uses a custom-built inverted index and BM25 ranking model.

🌐 Web Interface: Built with Spark Java and Velocity templates for easy querying and display.

🚀 Deployment: Dockerized and hosted on Render for public access.

🛠 Technologies Used
Java (Maven)

Spark Java Framework

JSoup (Web scraping)

OpenCV & YOLO (Computer vision)

Apache Velocity (Templating)

Docker (Containerization)

Render (Deployment)

📸 Example Use Cases
Search for “Eiffel Tower”, “elephant”, or “flag of Brazil” and get relevant images with context.

Discover places visually even when the text doesn’t directly mention them, thanks to object detection.

📂 Dataset
Images sourced from Wikipedia’s “Tourism in [Country]” pages.

Metadata stored in a structured JSON file including country name, caption, alt text, object labels, and color info.

🚀 Live Demo
Check out the live application here:
👉 https://wondermap.onrender.com

📁 Repository
Explore the full source code and documentation:
👉 https://github.com/Naveen-Singh8783/wondermap

📌 Future Improvements
Scale dataset with more sources and smarter models.

Add advanced filters, autocomplete, and map-based exploration.

Optimize performance for larger-scale deployments.
