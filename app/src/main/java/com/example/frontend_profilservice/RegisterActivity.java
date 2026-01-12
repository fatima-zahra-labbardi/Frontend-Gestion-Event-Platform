package com.example.frontend_profilservice;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPass, etConfirmPass;
    private EditText etStudentSchool, etStudentMajor;
    private EditText etNomOrg, etTypeOrg;

    private RadioGroup rgRole;
    private LinearLayout layoutStudent, layoutOrganizer;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPassword);

        rgRole = findViewById(R.id.rgRole);
        layoutStudent = findViewById(R.id.layoutStudentFields);
        layoutOrganizer = findViewById(R.id.layoutOrganizerFields);

        etStudentSchool = findViewById(R.id.etStudentSchool);
        etStudentMajor = findViewById(R.id.etStudentMajor);
        etNomOrg = findViewById(R.id.etNomEtablissement);
        etTypeOrg = findViewById(R.id.etTypeOrg);

        btnRegister = findViewById(R.id.btnRegister);

        rgRole.clearCheck();
        layoutStudent.setVisibility(View.GONE);
        layoutOrganizer.setVisibility(View.GONE);

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStudent) {
                layoutStudent.setVisibility(View.VISIBLE);
                layoutOrganizer.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbOrganizer) {
                layoutStudent.setVisibility(View.GONE);
                layoutOrganizer.setVisibility(View.VISIBLE);
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());

        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgRole.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a role (Student or Organizer)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(etConfirmPass.getText().toString())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPassword(password);

        Call<String> call;

        if (rgRole.getCheckedRadioButtonId() == R.id.rbStudent) {
            request.setRole("STUDENT");
            request.setNomEtablissement(etStudentSchool.getText().toString());
            request.setFiliere(etStudentMajor.getText().toString());
            call = RetrofitClient.getApiService().registerStudent(request);
        } else {
            request.setRole("ORGANIZER");
            request.setNomEtablissement(etNomOrg.getText().toString());
            request.setTypeOrganisateur(etTypeOrg.getText().toString());
            call = RetrofitClient.getApiService().registerOrganizer(request);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
