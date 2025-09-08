# Türkçe Yorumlarda Yapay Zeka Destekli Duygu Analizi ve Hibrit Puan Hesaplama

[![Java](https://img.shields.io/badge/Java-21+-red?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven)](https://maven.apache.org/)
[![Hugging Face](https://img.shields.io/badge/AI-Hugging%20Face-ffcc4d?logo=huggingface&logoColor=black)](https://huggingface.co/)
[![License: MIT](https://img.shields.io/badge/License-MIT-informational)](./LICENSE)

Yapay zeka destekli duygu analizi ve hibrit puanlama yaklaşımıyla ürün yorumlarını inceleyerek, kullanıcıların memnuniyet seviyesini yıldız puanlarıyla birlikte değerlendirir ve daha doğru bir hibrit skor üretir. Bu sayede modern bir e-ticaret analitik deneyimi sunar. Uygulama; cam efekti (glassmorphism) tasarımlı arayüzü, kategori bazlı içgörüler ve güçlü bir REST API ile zenginleştirilmiştir.

## ✨ Öne Çıkanlar
- **AI Duygu Analizi (TR destekli)**: Hugging Face ile yorumların pozitif/nötr/negatif analizi
- **Hibrit Puanlama**: Yıldız puanı + AI sonucu, ağırlıklı birleşim
- **Kategori Bazında Puanlama**: Kalite & Dayanıklılık, Kullanım & Performans, Hizmet & Teslimat
- **Modern UI**
- **Hızlı Arama**
- **REST API**

<img width="1901" height="905" alt="image" src="https://github.com/user-attachments/assets/82da0be3-9877-4650-8b56-927a9b7a6f3d" />


<img width="1887" height="897" alt="image" src="https://github.com/user-attachments/assets/ff56bb8f-23b5-4aad-843d-4b17e9976150" />


<img width="1907" height="857" alt="image" src="https://github.com/user-attachments/assets/25a0e5eb-2b04-4d6c-bace-278866109f92" />


<img width="1868" height="855" alt="image" src="https://github.com/user-attachments/assets/ab235b27-0384-4ede-86c5-328ba0d4541c" />



## 🧱 Mimari ve Teknolojiler
- Backend: Spring Boot, Spring Data JPA, Validation
- Frontend: HTML5, CSS3, Vanilla JS (glassmorphism UI)
- Veritabanı: MySQL 8.0
- Build: Maven
- AI: Hugging Face (Türkçe sentiment)

```
src/
  main/
    java/com/ecommerce/...     # API, servis, repo, entity
    resources/
      static/                   # index.html, styles.css, script.js
      application.properties    # yapılandırma
```

## ⚙️ Kurulum

### Gereksinimler
- Java 21+
- MySQL 8.0+
- Maven 3.6+

### Veritabanı
```sql
CREATE DATABASE ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
`src/main/resources/application.properties` içinde bağlantıyı düzenleyin:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=<kullanici>
spring.datasource.password=<sifre>
server.port=8081
```

### Model Kurulumu (Transformers) – API Anahtarı Gerekmez
Python tarafında Flask servisimiz, `transformers` kütüphanesi ile modeli ilk çalıştırmada indirip yerel cache'ten kullanır. Varsayılan kamuya açık modeller için API anahtarı gerekmez.

Notlar:
- İlk çalıştırmada internet bağlantısı gerekir; model `~/.cache/huggingface` (veya `HF_HOME`) altında saklanır.
- Özel/gated bir model kullanırsanız Hugging Face token gerekebilir (örn. `HUGGINGFACE_HUB_TOKEN` ortam değişkeni).
- Java tarafında yalnızca Python servis adresi konfigüre edilir: `python.api.url=http://localhost:5000`


## 🤖 Java + Python Mimari (Flask Entegrasyonu)

Bu proje, duygu analizi için ayrı bir Python Flask servisiyle birlikte çalışır. Java (Spring Boot) tarafı yorum metnini Flask servisine gönderir ve dönen sonucu hibrit puanlamada kullanır.

### Entegrasyon Akışı
- Kullanıcı yorum ekler → Java API alır
- Java `SentimentAnalysisService` → `POST {python.api.url}/predict` ile Flask'a metni yollar
- Flask, BERT tabanlı Türkçe modelle sınıflandırma yapar ve şu yapıda döner: `{ label, sentiment, score }`
- Java, skoru normalize eder ve yıldız puanıyla birleştirir

### Flask Servisi Nasıl Çalıştırılır
1) Python 3.9+ kurulu olmalı
2) Gerekli paketler:
```bash
pip install flask flask-cors transformers torch --upgrade
```
3) Flask uygulamasını başlatın (örnek port 5000):
```bash
python app.py
# veya
python -m flask --app app:app run --host 0.0.0.0 --port 5000
```

Örnek Flask uygulaması (özet):
```python
from flask import Flask, request, jsonify
from flask_cors import CORS
from transformers import pipeline

app = Flask(__name__)
CORS(app)
pipe = pipeline("text-classification", model="saribasmetehan/bert-base-turkish-sentiment-analysis")

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    text = (data or {}).get("text", "").strip()
    if not text:
        return jsonify({"error": "Text cannot be empty"}), 400
    result = pipe(text)[0]  # {'label': 'LABEL_2', 'score': 0.9987}
    label_map = {"LABEL_0": "Neutral", "LABEL_1": "Positive", "LABEL_2": "Negative"}
    return jsonify({
        "input": text,
        "label": result["label"],
        "sentiment": label_map.get(result["label"], result["label"]),
        "score": round(result["score"], 4)
    })

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "healthy"})
```

### Java Tarafı Konfigürasyonu
`src/main/resources/application.properties` içinde Python servisinin URL'ini tanımlayın:
```properties
python.api.url=http://localhost:5000
```
`SentimentAnalysisService` bu değeri kullanarak `/predict` endpointine istek atar.

### Endpoint Sözleşmesi (Contract)
- Request (Java → Flask)
```json
{ "text": "Ürün gerçekten çok kaliteli, tavsiye ederim" }
```
- Response (Flask → Java)
```json
{
  "input": "Ürün gerçekten çok kaliteli, tavsiye ederim",
  "label": "LABEL_1",
  "sentiment": "Positive",
  "score": 0.9876
}
```

### Hızlı Test (cURL)
```bash
curl -X POST http://localhost:5000/predict \
  -H "Content-Type: application/json" \
  -d '{"text":"Kargo çok hızlıydı, memnun kaldım"}'

curl http://localhost:5000/health
```

### Birlikte Çalıştırma
1) Önce Flask servisini 5000 portunda başlatın
2) Ardından Java uygulamasını çalıştırın (8081)
3) UI üzerinden yorum eklediğinizde, arka planda Flask servisinden duygu sonucu alınır ve hibrit puana yansır

## 🙏 Model ve Atıf
Bu projede Türkçe duygu analizi için Hugging Face üzerinde yayınlanan şu model kullanılmıştır:

- Model: [saribasmetehan/bert-base-turkish-sentiment-analysis](https://huggingface.co/saribasmetehan/bert-base-turkish-sentiment-analysis)
- Geliştirici/Eğitici: `saribasmetehan` (Hugging Face)

Modele ve geliştiricisine emekleri için teşekkür ederiz. Üretim kullanımında modelin lisans ve kullanım şartlarına uyduğunuzdan emin olun. Model versiyonlaması veya alternatif modeller için Hugging Face sayfasını ziyaret edebilirsiniz.

## 📚 API Hızlı Bakış
- Ürünler
  - `GET /api/products` – Liste
  - `GET /api/products/{id}` – Detay
  - `POST /api/products` – Ekle
  - `DELETE /api/products/{id}` – Sil
- Yorumlar
  - `GET /api/reviews/product/{productId}` – Ürün yorumları
  - `POST /api/reviews` – Yorum ekle
  - `DELETE /api/reviews/{reviewId}` – Yorum sil

### Hibrit Puanlama (Basit Formül)
- Ağırlıklar: yıldız=0.5, duygu=0.5 (eşit ağırlık)
- Normalize duygu: positive=x, negative=(1-x)
- Nötr durumda (neutral) yalnızca yıldız puanı kullanılır
- Sonuç: `(yildiz/5)*0.5 + (duygu)*0.5` → 0..1 (UI’da 5 üzerinden gösterilir)

 
## 📄 Lisans
MIT 
