package com.example.smishingdetectionapp;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PieChartView extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint outerRingPaint; // Outer ring for highlighting
    private RectF rectF;
    private float progress = 0f; // Current progress (0% initially)
    private float animatedProgress = 0f; // Progress used for animation

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the remaining part (green background)
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(getResources().getColor(R.color.green)); // Always green
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(50f); // Thicker arc for better visibility

        // Paint for the progress (colored part)
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(50f); // Thicker arc for progress
        progressPaint.setShadowLayer(15f, 5f, 5f, getResources().getColor(R.color.black)); // Subtle shadow

        // Paint for the outer ring
        outerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerRingPaint.setColor(getResources().getColor(R.color.white)); // Bright white outer ring
        outerRingPaint.setStyle(Paint.Style.STROKE);
        outerRingPaint.setStrokeWidth(8f); // Thin highlighting ring

        // RectF for defining the pie bounds
        rectF = new RectF();

        // Enable hardware acceleration for shadow rendering
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Set bounds for the pie chart
        float size = Math.min(getWidth(), getHeight());
        float padding = 50f; // Padding inside the view
        rectF.set(padding, padding, size - padding, size - padding);

        // Draw the outer ring to highlight the chart
        canvas.drawOval(rectF, outerRingPaint);

        // Determine color for progress
        int startColor, endColor;
        if (progress <= 10) {
            // Blue for low risk
            startColor = getResources().getColor(R.color.navy_blue);
            endColor = getResources().getColor(R.color.dark_blue_grotto);
        } else if (progress >= 60) {
            // Red for high risk
            startColor = getResources().getColor(R.color.red);
            endColor = getResources().getColor(R.color.dark_red);
        } else {
            // Yellow for moderate risk
            startColor = getResources().getColor(R.color.yellow);
            endColor = getResources().getColor(R.color.dark_yellow);
        }

        // Apply gradient to the progress arc
        @SuppressLint("DrawAllocation") LinearGradient gradient = new LinearGradient(
                rectF.left, rectF.top, rectF.right, rectF.bottom,
                startColor, endColor, Shader.TileMode.CLAMP
        );
        progressPaint.setShader(gradient);

        // Calculate the sweep angle for the progress arc
        float sweepAngle = (animatedProgress / 100) * 360; // Positive for clockwise animation

        // Draw the remaining arc (always green part, starting where the progress ends)
        canvas.drawArc(rectF, -90 + sweepAngle, 360 - sweepAngle, false, backgroundPaint);

        // Draw the progress arc (colored part, starting at 12 o’clock)
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
    }

    /**
     * Set the progress value (0-100) and animate it.
     */
    public void setProgressWithAnimation(float targetProgress) {
        this.progress = targetProgress;

        ValueAnimator animator = ValueAnimator.ofFloat(0, targetProgress);
        animator.setDuration(1500); // Animation duration in milliseconds
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator()); // Smooth easing
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate(); // Redraw the view
        });
        animator.start();
    }
}
