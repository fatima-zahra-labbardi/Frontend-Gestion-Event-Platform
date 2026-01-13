package com.example.frontend_profilservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private ActivityResultLauncher<String> imagePickerLauncher;
    
    private LinearLayout layoutStudent, layoutOrganizer;
    private TextView tvSubtitle;
    private EditText etName, etBio, etInstitution, etMajor, etOrgName, etOrgType;
    private Long currentProfileId;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Views
        ivProfile = findViewById(R.id.iv_profile);
        ImageView ivEditIcon = findViewById(R.id.iv_edit_icon);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        
        layoutStudent = findViewById(R.id.layout_student_params);
        layoutOrganizer = findViewById(R.id.layout_organizer_params);
        
        etName = findViewById(R.id.et_name);
        etBio = findViewById(R.id.et_bio);
        etInstitution = findViewById(R.id.et_institution);
        etMajor = findViewById(R.id.et_major);
        etOrgName = findViewById(R.id.et_org_name);
        etOrgType = findViewById(R.id.et_org_type);

        // Load User Role and Set Visibility
        SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String role = prefs.getString("user_role", "STUDENT"); // Default to STUDENT if not found
        Long userId = prefs.getLong("user_id", -1L);

        if ("STUDENT".equalsIgnoreCase(role)) {
            tvSubtitle.setText("Login as Student");
            layoutStudent.setVisibility(View.VISIBLE);
            layoutOrganizer.setVisibility(View.GONE);
        } else {
            tvSubtitle.setText("Login as Organizer");
            layoutStudent.setVisibility(View.GONE);
            layoutOrganizer.setVisibility(View.VISIBLE);
        }

        if (userId != -1L) {
            loadProfile(userId);
        }

        // Modern Image Picker Contract
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && ivProfile != null) {
                    ivProfile.setImageURI(uri);
                }
            }
        );

        // Click listeners for photo modification
        if (ivEditIcon != null) {
            ivEditIcon.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Close Button
        Button btnClose = findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        // Update Button
        Button btnUpdate = findViewById(R.id.btn_update);
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> saveProfile());
        }
        
        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }

    private void loadProfile(Long userId) {
        RetrofitClient.getApiService().getProfileByUserId(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    currentProfileId = profile.getId();
                    Log.d("EditProfile", "Profile loaded. ID: " + currentProfileId);
                    
                    etName.setText(profile.getFullName());
                    etBio.setText(profile.getBio());
                    etInstitution.setText(profile.getInstitution());
                    etMajor.setText(profile.getMajor());
                    etOrgName.setText(profile.getOrganizationName());
                    etOrgType.setText(profile.getOrganizationType());
                } else {
                    Log.e("EditProfile", "Load failed. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentProfileId == null) {
            Toast.makeText(this, "Impossible de mettre à jour : ID manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        ProfileResponse updatedProfile = new ProfileResponse();
        updatedProfile.setFullName(etName.getText().toString());
        updatedProfile.setBio(etBio.getText().toString());
        
        SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String currentRole = prefs.getString("user_role", "STUDENT");

        if ("STUDENT".equalsIgnoreCase(currentRole)) {
            updatedProfile.setInstitution(etInstitution.getText().toString());
            updatedProfile.setMajor(etMajor.getText().toString());
        } else {
            updatedProfile.setOrganizationName(etOrgName.getText().toString());
            updatedProfile.setOrganizationType(etOrgType.getText().toString());
        }

        Log.d("EditProfile", "Updating Profile ID: " + currentProfileId + " with Name: " + updatedProfile.getFullName());

        RetrofitClient.getApiService().updateProfile(currentProfileId, updatedProfile).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("EditProfile", "Update successful!");
                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("EditProfile", "Update failed. Code: " + response.code());
                    Toast.makeText(EditProfileActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("EditProfile", "Update error", t);
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
