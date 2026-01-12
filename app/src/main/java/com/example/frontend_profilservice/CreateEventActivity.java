package com.example.frontend_profilservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class CreateEventActivity extends AppCompatActivity {
    
    private TextView tvImagePath;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // UI Elements
        tvImagePath = findViewById(R.id.tv_image_path);
        
        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Update text to show something was selected
                    // In a real app we might show the file name or a preview
                    tvImagePath.setText("Image Selected: " + uri.getLastPathSegment());
                }
            }
        );

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Cancel Button
        Button btnCancel = findViewById(R.id.btn_cancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        // Create Button
        Button btnCreate = findViewById(R.id.btn_create);
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
                Toast.makeText(this, "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous screen
            });
        }
        
        // Image Picker Trigger (Text or Icon)
        ImageView ivCameraTrigger = findViewById(R.id.iv_camera_trigger);
        if (ivCameraTrigger != null) {
            ivCameraTrigger.setOnClickListener(v -> openGallery());
        }
        
        // Also allow clicking the text area
        findViewById(R.id.ll_image_picker).setOnClickListener(v -> openGallery());
        
        NavigationUtils.setupNavigation(this, 1); // Grid/Services active
    }
    
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }
}
