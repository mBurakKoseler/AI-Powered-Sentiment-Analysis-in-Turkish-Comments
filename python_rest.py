#!!!! BU KODU AYRI BİR REST API İÇİNDE ÇALIŞACAKTIR ÖRNEK KOD OLARAK BURAYA EKLENMİŞTİR !!!!
from flask import Flask, request, jsonify
from flask_cors import CORS
from transformers import pipeline
import logging

app = Flask(__name__)
CORS(app)  # CORS desteği ekle

# Logging ayarları
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Etiket eşlemesi
label_map = {
    "LABEL_0": "Neutral",
    "LABEL_1": "Positive",
    "LABEL_2": "Negative"
}

# Modeli yükle (bir kere)
try:
    pipe = pipeline("text-classification", model="saribasmetehan/bert-base-turkish-sentiment-analysis")
    logger.info("Model başarıyla yüklendi")
except Exception as e:
    logger.error(f"Model yüklenirken hata: {e}")
    pipe = None


@app.route("/predict", methods=["POST"])
def predict():
    try:
        if not request.is_json:
            logger.error("Request JSON formatında değil")
            return jsonify({"error": "Request must be JSON"}), 400

        data = request.get_json()
        logger.info(f" Gelen data: {data}")

        if not data or "text" not in data:
            return jsonify({"error": "Missing 'text' field in request"}), 400

        text = data["text"].strip()
        if not text:
            return jsonify({"error": "Text cannot be empty"}), 400

        if pipe is None:
            logger.error("Model yüklü değil")
            return jsonify({"error": "Model not loaded"}), 500

        # En yüksek skoru veren sonucu al (default olarak zaten top-1 döner)
        result = pipe(text)[0]  # {'label': 'LABEL_2', 'score': 0.9987, ...}

        response = {
            "input": text,
            "label": result["label"],
            "sentiment": label_map.get(result["label"], result["label"]),
            "score": round(result["score"], 4)
        }

        logger.info(f"Response: {response}")
        return jsonify(response)

    except Exception as e:
        logger.error(f"Predict endpoint hatası: {e}")
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "healthy", "model_loaded": pipe is not None})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
