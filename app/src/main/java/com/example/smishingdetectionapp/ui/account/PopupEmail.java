package com.example.smishingdetectionapp.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupEmail extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_email,
                container, false);

        //TODO: Add email change functionality.

        return v;
    }

}
