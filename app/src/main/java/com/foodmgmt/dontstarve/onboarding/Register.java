package com.foodmgmt.dontstarve.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.foodmgmt.dontstarve.R;
import com.google.android.material.textfield.TextInputLayout;

public class Register extends Fragment {

    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutRegNo;
    private EditText inputName, inputEmail, inputRegNo;
    private ImageButton button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        inputLayoutName = (TextInputLayout) v.findViewById(R.id.usernameWrapper);
        inputLayoutEmail = (TextInputLayout) v.findViewById(R.id.emailWrapper);
        inputLayoutRegNo = (TextInputLayout) v.findViewById(R.id.regNoWrapper);
        inputName = (EditText) v.findViewById(R.id.username);
        inputEmail = (EditText) v.findViewById(R.id.email);
        inputRegNo = (EditText) v.findViewById(R.id.regNo);
        button = (ImageButton) v.findViewById(R.id.button);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputRegNo.addTextChangedListener(new MyTextWatcher(inputRegNo));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm(v);

            }
        });

        return v;

    }

    private void submitForm(View v) {
        if (!validateName())
            return;
        if (!validateEmail())
            return;
        if (!validateRegNo())
            return;

        Navigation.findNavController(v).navigate(R.id.action_register_to_verify);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Enter Name");
            requestFocus(inputName);
            return false;
        }
        else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError("Enter valid Email ID");
            requestFocus(inputEmail);
            return false;
        }
        else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateRegNo() {
        String num = inputRegNo.getText().toString().trim();
        if (num.isEmpty() || !isValidRegNo(num)) {
            inputLayoutRegNo.setError("Enter valid Reg No.");
            requestFocus(inputRegNo);
            return false;
        }
        else {
            inputLayoutRegNo.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidRegNo(String num) {
        return !TextUtils.isEmpty(num) && TextUtils.isDigitsOnly(num) && num.length()==9;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.username:
                    validateName();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.regNo:
                    validateRegNo();
                    break;
            }
        }
    }
}
