package com.example.smishingdetectionapp.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.Nullable;


import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupPW extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_password,
                container, false);

        Button changePW = v.findViewById(R.id.change_pwBtn);
        changePW.setOnClickListener(v1 -> {
            //TODO: Add password change functionality.
            dismiss();
        });

        return v;
    }

}