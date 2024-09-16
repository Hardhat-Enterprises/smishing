package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupSO extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_signout,
                container, false);

        Button sign_outYes = v.findViewById(R.id.confirmYesBtn);
        sign_outYes.setOnClickListener(v1 -> {
            //TODO: Add sign out functionality.
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            dismiss();
            Toast.makeText(getContext(), "You have been signed out.", Toast.LENGTH_LONG).show();
        });

        Button sign_outNo = v.findViewById(R.id.confirmNoBtn);
        sign_outNo.setOnClickListener(v1 -> {
            dismiss();
        });

        return v;
    }
}
