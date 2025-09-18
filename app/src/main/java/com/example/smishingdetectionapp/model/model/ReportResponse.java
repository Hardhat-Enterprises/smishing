package com.example.smishingdetectionapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ReportResponse {

    // ---- legacy/flat fallbacks (if server ever sends them) ----
    @SerializedName(value = "label",      alternate = {"prediction", "result", "status"})
    private String label;

    @SerializedName(value = "confidence", alternate = {"score", "probability"})
    private Double confidence; // 0..1 or 0..100 (we normalize in getters)

    @SerializedName(value = "details",    alternate = {"explanation", "reason", "advice"})
    private String details;

    // ---- preferred nested fields from the new backend ----
    @SerializedName("classification")
    private Classification classification;

    @SerializedName("analysis")
    private Analysis analysis;

    public static class Classification {
        @SerializedName("label")        public String label;
        @SerializedName("confidence")   public Double confidence;           // 0..1
        @SerializedName(value = "advice", alternate = {"details","explanation","reason"})
        public String advice;
        @SerializedName("badge")        public String badge;                // "Safe" | "Spam" | "Smishing"
        @SerializedName("probabilities")public Map<String, Double> probabilities; // optional
        @SerializedName("severity")     public String severity;             // optional
        @SerializedName("model_version")public String modelVersion;         // optional
        @SerializedName("actions")      public List<String> actions;        // optional
    }

    public static class Analysis {
        @SerializedName("riskScore") public Integer riskScore; // 0..100
        @SerializedName("tags")      public List<String> tags; // optional
    }

    // ---- convenient getters used by the Activity ----
    public String getLabel() {
        if (classification != null && classification.label != null) return classification.label;
        return label;
    }

    /** Confidence normalized to 0..1 if present. Falls back to analysis.riskScore/100. */
    public Double getConfidence() {
        if (classification != null && classification.confidence != null) {
            return clamp01(classification.confidence <= 1 ? classification.confidence : classification.confidence / 100.0);
        }
        if (confidence != null) {
            return clamp01(confidence <= 1 ? confidence : confidence / 100.0);
        }
        if (analysis != null && analysis.riskScore != null) {
            return clamp01(analysis.riskScore / 100.0);
        }
        return null;
    }

    public String getDetails() {
        if (classification != null && classification.advice != null) return classification.advice;
        return details;
    }

    public String getBadge() {
        return (classification != null) ? classification.badge : null;
    }

    // Optional helper: 0..100 percentage
    public Integer getPercent() {
        Double c = getConfidence();
        return (c == null) ? null : (int)Math.round(c * 100.0);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
