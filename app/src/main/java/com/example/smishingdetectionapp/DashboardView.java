package com.example.smishingdetectionapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DashboardView extends View {

    private Paint barPaint;
    private Paint textPaint;
    private Paint axisPaint;
    private int[] values = new int[7];
    private String[] labels = new String[7];

    public DashboardView(Context context) {
        super(context);
        init();
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.parseColor("#189AB4"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#333333"));
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.parseColor("#CCCCCC"));
        axisPaint.setStrokeWidth(2f);
    }

    public void setData(int[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int paddingLeft = 20;
        int paddingRight = 20;
        int paddingTop = 20;
        int paddingBottom = 50;

        int chartWidth = width - paddingLeft - paddingRight;
        int chartHeight = height - paddingTop - paddingBottom;

        // Draw baseline
        canvas.drawLine(paddingLeft, height - paddingBottom,
                width - paddingRight, height - paddingBottom, axisPaint);

        // Find max value
        int maxVal = 1;
        for (int v : values) if (v > maxVal) maxVal = v;

        int barCount = values.length;
        float barWidth = (chartWidth / (float) barCount) * 0.6f;
        float gap = (chartWidth / (float) barCount) * 0.4f;

        for (int i = 0; i < barCount; i++) {
            float barHeight = (values[i] / (float) maxVal) * chartHeight;
            float left = paddingLeft + i * (barWidth + gap) + gap / 2;
            float right = left + barWidth;
            float top = height - paddingBottom - barHeight;
            float bottom = height - paddingBottom;

            // Colour based on value
            if (values[i] == 0) {
                barPaint.setColor(Color.parseColor("#E0E0E0"));
            } else if (values[i] >= maxVal * 0.7) {
                barPaint.setColor(Color.parseColor("#E53935")); // red - high
            } else if (values[i] >= maxVal * 0.3) {
                barPaint.setColor(Color.parseColor("#FFA500")); // orange - medium
            } else {
                barPaint.setColor(Color.parseColor("#4CAF50")); // green - low
            }

            canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, barPaint);

            // Count label above bar
            if (values[i] > 0) {
                textPaint.setTextSize(24f);
                canvas.drawText(String.valueOf(values[i]),
                        left + barWidth / 2, top - 8, textPaint);
            }

            // Day label below baseline
            textPaint.setTextSize(26f);
            textPaint.setColor(Color.parseColor("#666666"));
            canvas.drawText(labels[i],
                    left + barWidth / 2, height - paddingBottom + 36, textPaint);
            textPaint.setColor(Color.parseColor("#333333"));
        }
    }
}
