package com.example.smishingdetectionapp.ui.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
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



        });

        Button sign_outNo = v.findViewById(R.id.confirmNoBtn);
        sign_outNo.setOnClickListener(v1 -> {
            dismiss();
        });

        return v;
    }




    private void moveToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clears the activity stack
        startActivity(intent);
        requireActivity().finish();  // Close the current activity
    }


}
