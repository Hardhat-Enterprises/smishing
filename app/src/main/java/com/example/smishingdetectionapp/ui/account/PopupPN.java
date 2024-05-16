package com.example.smishingdetectionapp.ui.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupPN extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_phone_number,
                container, false);

        Button changePnBtn = v.findViewById(R.id.pn_changeBtn);
        changePnBtn.setOnClickListener(v1 -> {
            //TODO: Add phone number change functionality. Make it work with database, OTP, etc.
            dismiss();
        });

        final EditText changePN = v.findViewById(R.id.change_accPN);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String userPN = changePN.getText().toString();
                changePnBtn.setEnabled(!userPN.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        changePN.addTextChangedListener(afterTextChangedListener);

        return v;
    }
}
