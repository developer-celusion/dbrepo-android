package com.mobiliteam.dbrepoexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mobiliteam.dbrepo.IDatabaseRepository;
import com.mobiliteam.dbrepoexample.databinding.ActivityAddUserBinding;
import com.mobiliteam.dbrepoexample.model.User;


public class AddUserActivity extends AppCompatActivity {

    private IDatabaseRepository iDatabaseRepository;
    private User user;
    private ActivityAddUserBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AddUserActivity.this,
                R.layout.activity_add_user);
        iDatabaseRepository = ApplicationEx.getInstance().getDbRepo();
        initUser();
        binding.setModel(user);

        setTitle();

        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setTitle(){
        binding.topAppBar.setTitle(user.getId() == 0 ? "Create User" : "Update User");
        binding.topAppBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
    }

    private void initUser(){
        if(getIntent().hasExtra("UserID")){
            int userId = getIntent().getIntExtra("UserID", -1);
            user = iDatabaseRepository.getById(User.class, userId);
            binding.buttonSave.setText(getString(R.string.update_user));
            return;
        }
        user = new User();
        binding.buttonSave.setText(getString(R.string.save_user));
    }

    private void setListener() {

        binding.topAppBar.setNavigationOnClickListener(view -> {
            finish();
        });

        binding.textName.addTextChangedListener(inputTextWatcher);
        binding.textMobile.addTextChangedListener(inputTextWatcher);
        binding.textEmail.addTextChangedListener(inputTextWatcher);

        binding.buttonSave.setOnClickListener(view -> {
            //Check if user came to update record
            if (user != null && user.getId() > 0){
                iDatabaseRepository.update(User.class, user);
                sendBackResult(user.getId(), MainActivity.REQUEST_UPDATE_RECORD);
            }else{
                User createdUser = iDatabaseRepository.add(User.class, user);
                sendBackResult(createdUser.getId(), MainActivity.REQUEST_CREATE_RECORD);
            }
        });
    }

    public void sendBackResult(int userID, int requestCode){
        Intent intent = new Intent();
        intent.putExtra("UserID", userID);
        setResult(requestCode, intent);
        finish();
    }

    private final TextWatcher inputTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (binding.textName.getText() != null && binding.textMobile.getText() != null
                    && binding.textEmail.getText() != null) {

                if (binding.textName.length() > 2) {

                    binding.nameTextInputLayout.setErrorEnabled(false);
                    if (binding.textMobile.length() > 0
                            && binding.textMobile.length() == 10) {

                        binding.mobileNumberTextInputLayout.setErrorEnabled(false);
                        if (binding.textEmail.length() > 0 && isEmailValid(binding.textEmail.getText().toString())) {
                            binding.emailTextInputLayout.setErrorEnabled(false);
                            enableButton();

                        } else {
                            setInputError(binding.emailTextInputLayout, getString(R.string.invalid_email));
                        }
                    } else {
                        setInputError(binding.mobileNumberTextInputLayout, getString(R.string.invalid_mobile_number));
                    }
                } else {
                    setInputError(binding.nameTextInputLayout, getString(R.string.required));
                }
            } else {
                disableButton();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void enableButton() {
        binding.buttonSave.setEnabled(true);
    }

    private void disableButton() {
        binding.buttonSave.setEnabled(false);
    }

    private void setInputError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
        textInputLayout.setErrorEnabled(true);
        disableButton();
    }

    private boolean isEmailValid(String email) {

        // Android offers the inbuilt patterns which the entered
        // data from the EditText field needs to be compared with
        // In this case the the entered data needs to compared with
        // the EMAIL_ADDRESS, which is implemented same below
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
