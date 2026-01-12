package com.example.frontend_profilservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateEventActivity extends AppCompatActivity {

    private TextView tvImagePath;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // UI Elements
        tvImagePath = findViewById(R.id.tv_image_path);
        
        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    tvImagePath.setText("Image Selected: " + uri.getLastPathSegment());
                }
            }
        );

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
                Toast.makeText(this, "Event Updated Successfully!", Toast.LENGTH_SHORT).show();
                finish(); 
            });
        }
        
        // Image Picker Trigger
        findViewById(R.id.ll_image_picker).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        
        NavigationUtils.setupNavigation(this, 1); 
    }
}
