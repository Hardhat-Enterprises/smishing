package com.example.smishingdetectionapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ProgressBarView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF rectF;
    private float progress = 0f; // Progress value (0-100)
    private float animatedProgress = 0f;

    public ProgressBarView(Context context) {
        super(context);
        init();
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Background paint for unfilled dashes
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(getResources().getColor(R.color.grey)); // Background color for unfilled dashes
        backgroundPaint.setStyle(Paint.Style.FILL);

        // Progress paint for filled dashes
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.FILL);

        // RectF for defining the bounds of the progress bar
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set bounds
        float width = getWidth();
        float height = getHeight();
        rectF.set(0, 0, width, height);

        // Number of dashes
        int dashCount = 20; // Number of dashes
        float dashWidth = width / dashCount;
        float gapWidth = dashWidth * 0.2f;

        // Total progress width based on animated progress
        float progressWidth = (animatedProgress / 100) * width;

        // Draw background dashes
        for (int i = 0; i < dashCount; i++) {
            float startX = i * dashWidth;
            float endX = startX + dashWidth - gapWidth;

            // Ensure dashes do not overlap
            if (startX >= width) break;

            canvas.drawRect(startX, 0, endX, height, backgroundPaint);
        }

        // Determine solid color for progress based on risk levels
        int progressColor;
        if (progress >= 60) {
            // High risk: Red
            progressColor = getResources().getColor(R.color.red);
        } else if (progress > 20) {
            // Medium risk: Yellow
            progressColor = getResources().getColor(R.color.dark_yellow);
        } else {
            // Low risk: Green
            progressColor = getResources().getColor(R.color.green);
        }

        // Set solid color for progress
        progressPaint.setColor(progressColor);

        // Draw progress dashes
        for (int i = 0; i < dashCount; i++) {
            float startX = i * dashWidth;
            float endX = startX + dashWidth - gapWidth;

            if (startX >= progressWidth) break;

            canvas.drawRect(startX, 0, endX, height, progressPaint);
        }
    }

    /**
     * Sets the progress value (0-100) with animation.
     */
    public void setProgressWithAnimation(float targetProgress) {
        this.progress = targetProgress;

        ValueAnimator animator = ValueAnimator.ofFloat(0, targetProgress);
        animator.setDuration(1500); // Animation duration
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate(); // Redraw the view
        });
        animator.start();
    }
}
