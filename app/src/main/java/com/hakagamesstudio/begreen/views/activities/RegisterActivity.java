package com.hakagamesstudio.begreen.views.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.customs.DialogLoader;
import com.hakagamesstudio.begreen.pojos.user_model.UserData;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.btn_save_register)
    Button signupBtn;
    @BindView(R.id.btn_cancel_register)
    Button cancel;
    @BindView(R.id.editName_register)
    AppCompatEditText txtName;
    @BindView(R.id.edit_address_register)
    AppCompatEditText txtAddress;
    @BindView(R.id.edit_email_register)
    AppCompatEditText txtEmail;
    @BindView(R.id.edit_password_register)
    AppCompatEditText txtPassword;

    private View parentView;
    private DialogLoader dialogLoader;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        dialogLoader = new DialogLoader(this);
        signupBtn.setOnClickListener(v -> {
            boolean isValidData = validateForm();
            if (isValidData) {
                parentView = v;
                processRegistration();
            }
        });

        cancel.setOnClickListener(v -> finish());
    }

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(txtName.getText().toString().trim())) {
            txtName.setError("Nombre invalido");
            return false;
        } else if (!ValidateInputs.isValidInput(txtAddress.getText().toString().trim())) {
            txtAddress.setError("dirección invalida");
            return false;
        } else if (!ValidateInputs.isValidEmail(txtEmail.getText().toString().trim())) {
            txtEmail.setError("Correo invalido");
            return false;
        } else if (!ValidateInputs.isValidPassword(txtPassword.getText().toString().trim())) {
            txtPassword.setError("Contraseña invalida");
            return false;
        } else {
            return true;
        }
    }

    private void processRegistration() {
        dialogLoader.showProgressDialog();
        Call<UserData> call = mCall.processRegistration(
                txtName.getText().toString().trim(),
                "",
                txtEmail.getText().toString().trim(),
                txtPassword.getText().toString().trim(),
                "",
                null);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull retrofit2.Response<UserData> response) {
                dialogLoader.hideProgressDialog();
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            finish();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                            editor = sharedPreferences.edit();
                            editor.putString("DireccionUser", txtAddress.getText().toString());
                            editor.apply();
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            // Get the Error Message from Response
                            String message = response.body().getMessage();
                            Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();

                        } else {
                            // Unable to get Success status
                            Toast.makeText(RegisterActivity.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Show the Error Message
                        Toast.makeText(RegisterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(RegisterActivity.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}
