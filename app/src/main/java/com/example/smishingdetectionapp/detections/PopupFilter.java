package com.example.smishingdetectionapp.detections;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupFilter extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_filter,
                container, false);


        return v;
    }
}
