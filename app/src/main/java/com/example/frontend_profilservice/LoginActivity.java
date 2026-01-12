package com.example.frontend_profilservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.LoginRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private SwitchCompat switchRememberMe;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        switchRememberMe = findViewById(R.id.switchRememberMe);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;
            String deviceType = "ANDROID";
            String fcmToken = "token_local_" + Build.ID;

            LoginRequest loginRequest = new LoginRequest(email, password, fcmToken, deviceType, deviceInfo);

            RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<com.example.frontend_profilservice.models.JwtResponse>() {
                @Override
                public void onResponse(Call<com.example.frontend_profilservice.models.JwtResponse> call, Response<com.example.frontend_profilservice.models.JwtResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.frontend_profilservice.models.JwtResponse jwtResponse = response.body();
                        
                        SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        
                        if (switchRememberMe.isChecked()) {
                            editor.putBoolean("is_remembered", true);
                        }
                        
                        editor.putString("user_email", email);
                        editor.putString("user_role", jwtResponse.getRole());
                        editor.putLong("user_id", jwtResponse.getId());
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.frontend_profilservice.models.JwtResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
