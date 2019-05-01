package com.hakagamesstudio.begreen.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.User_Info_DB;
import com.hakagamesstudio.begreen.pojos.user_model.UserData;
import com.hakagamesstudio.begreen.pojos.user_model.UserDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.support.ValidateInputs;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edit_user)
    AppCompatEditText editUser;
    @BindView(R.id.edit_password)
    AppCompatEditText editPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_suscribe)
    Button btnSusbcribe;

    private MaterialDialog mProgressDialog;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private User_Info_DB userInfoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btnLogin.setOnClickListener(this);
        btnSusbcribe.setOnClickListener(this);

        userInfoDB = new User_Info_DB();
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editUser.setText(sharedPreferences.getString("userEmail", null));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                boolean isValidData = validateLogin();
                if (isValidData) {
                    processLogin();
                }
                break;

            case R.id.btn_suscribe:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private boolean validateLogin() {
        if (!ValidateInputs.isValidEmail(Objects.requireNonNull(editUser.getText()).toString().trim())) {
            editUser.setError("Usuario incorecto");
            return false;
        } else if (!ValidateInputs.isValidPassword(editPassword.getText().toString().trim())) {
            editPassword.setError("Contraseña incorecto");
            return false;
        } else {
            return true;
        }
    }

    private void processLogin() {
        mProgressDialog = BeGreenSupport.getInstance().createProgressDialog("Iniciando sesión", this);
        mProgressDialog.show();
        Call<UserData> call = mCall.processLogin(editUser.getText().toString().trim(), editPassword.getText().toString().trim());
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull Response<UserData> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1") || response.body().getSuccess().equalsIgnoreCase("2")) {
                        // Get the User Details from Response
                        UserDetails userDetails = response.body().getData().get(0);

                        // Save User Data to Local Databases
                        if (userInfoDB.getUserData(userDetails.getCustomersId()) != null) {
                            // User already exists
                            userInfoDB.updateUserData(userDetails);
                        } else {
                            // Insert Details of New User
                            userInfoDB.insertUserData(userDetails);
                        }

                        // Save necessary details in SharedPrefs
                        editor = sharedPreferences.edit();
                        editor.putString("userID", userDetails.getCustomersId());
                        editor.putString("userEmail", userDetails.getCustomersEmailAddress());
                        editor.putString("userName", userDetails.getCustomersFirstname());
                        editor.putString("apellido", userDetails.getCustomersLastname());
                        editor.putString("userDefaultAddressID", userDetails.getCustomersDefaultAddressId());
                        editor.putBoolean("isLogged_in", true);
                        editor.apply();

                        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(LoginActivity.this);
                        myAppPrefsManager.setUserLoggedIn(true);

                        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
                        Intent i = new Intent(LoginActivity.this, Menu_NavDrawer_Acititty.class);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ocurrio un error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Navigate back to MainActivity
        startActivity(new Intent(LoginActivity.this, Menu_NavDrawer_Acititty.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }
}
