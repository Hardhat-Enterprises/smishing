package com.example.smishingdetectionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.IntroSlideViewHolder> {

    private Context context;
    private List<IntroSlide> slides;

    public IntroSliderAdapter(Context context, List<IntroSlide> slides) {
        this.context = context;
        this.slides = slides;
    }

    @NonNull
    @Override
    public IntroSlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntroSlideViewHolder(
                LayoutInflater.from(context).inflate(R.layout.intro_slide_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull IntroSlideViewHolder holder, int position) {
        holder.bind(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    public class IntroSlideViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleTextView;
        private TextView descriptionTextView;

        public IntroSlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.introImage);
            titleTextView = itemView.findViewById(R.id.introTitle);
            descriptionTextView = itemView.findViewById(R.id.introDescription);

        }

        void bind(IntroSlide slide) {
            imageView.setImageResource(slide.getImage());
            titleTextView.setText(slide.getTitle());
            descriptionTextView.setText(slide.getDescription());
        }
    }
}