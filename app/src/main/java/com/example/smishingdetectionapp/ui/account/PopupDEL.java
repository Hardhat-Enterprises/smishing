package com.example.smishingdetectionapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.login.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PopupDEL extends BottomSheetDialogFragment {


    @Override //onCreateView defines the fragment view activity
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.popup_delete_account,
                container, false);

        //Action for when Yes to delete account is clicked
        Button del_accYes = v.findViewById(R.id.confirmDelYesBtn);//defining the button
        del_accYes.setOnClickListener(v1 -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);//switches to login page
            startActivity(intent);
            dismiss();//closes the popup page
            Toast.makeText(getContext(), "Account successfully deleted.", Toast.LENGTH_LONG).show();
        });

        //Action for cancelling the account delete
        Button del_accNo = v.findViewById(R.id.confirmDelNoBtn);
        del_accNo.setOnClickListener(v1 -> {
            dismiss();
        });

        //Listener for the text boxes. listens for whether they have contents or not.
        final EditText del_accPW = v.findViewById(R.id.del_accPW);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String userPW = del_accPW.getText().toString();
                //enables the account delete confirm button when text is entered.
                del_accYes.setEnabled(!userPW.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        del_accPW.addTextChangedListener(afterTextChangedListener);

        return v;
    }
}
