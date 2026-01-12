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
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private ActivityResultLauncher<String> imagePickerLauncher;
    
    private LinearLayout layoutStudent, layoutOrganizer;
    private TextView tvSubtitle;
    private EditText etName, etBio, etInstitution, etMajor, etOrgName, etOrgType;

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

        if ("STUDENT".equalsIgnoreCase(role)) {
            tvSubtitle.setText("Login as Student");
            layoutStudent.setVisibility(View.VISIBLE);
            layoutOrganizer.setVisibility(View.GONE);
        } else {
            tvSubtitle.setText("Login as Organizer");
            layoutStudent.setVisibility(View.GONE);
            layoutOrganizer.setVisibility(View.VISIBLE);
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
            btnUpdate.setOnClickListener(v -> {
                // Handle update logic here - you can send data to your update API
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
        
        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }
}
