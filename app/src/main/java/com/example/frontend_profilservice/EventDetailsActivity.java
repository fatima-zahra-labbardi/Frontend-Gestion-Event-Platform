package com.example.frontend_profilservice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        
        // Navigation Utilities
        NavigationUtils.setupNavigation(this, 1); // Grid page active

        // Data from Intent
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String location = getIntent().getStringExtra("EXTRA_LOCATION");
        boolean isCreator = getIntent().getBooleanExtra("EXTRA_IS_CREATOR", false);
        boolean isStudent = getIntent().getBooleanExtra("EXTRA_IS_STUDENT", false);

        // UI Header
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // UI Content
        TextView tvTitle = findViewById(R.id.tv_event_title);
        if (tvTitle != null && title != null) tvTitle.setText(title);

        TextView tvLocation = findViewById(R.id.tv_location_name);
        if (tvLocation != null && location != null) tvLocation.setText(location);

        // Role-Based Visibility
        LinearLayout llOrganizerInfo = findViewById(R.id.ll_organizer_info);
        LinearLayout llCreatorButtons = findViewById(R.id.ll_creator_buttons);
        Button btnRegister = findViewById(R.id.btn_register_event);

        if (isCreator) {
            llCreatorButtons.setVisibility(View.VISIBLE);
            llOrganizerInfo.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            
            // Creator Actions
            findViewById(R.id.btn_delete).setOnClickListener(v -> {
                Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
                finish();
            });
            findViewById(R.id.btn_update).setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, UpdateEventActivity.class));
            });
            findViewById(R.id.btn_registered_list).setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, ParticipantsListActivity.class));
            });
            
        } else {
            // Student or other organizer view
            llCreatorButtons.setVisibility(View.GONE);
            llOrganizerInfo.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);

            // Registration Toggle
            btnRegister.setOnClickListener(v -> {
                isRegistered = !isRegistered;
                if (isRegistered) {
                    btnRegister.setText("Unregister Event");
                    btnRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.light_purple)));
                    btnRegister.setTextColor(getResources().getColor(R.color.black));
                    Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    btnRegister.setText("Register Event");
                    btnRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.purple_custom)));
                    btnRegister.setTextColor(getResources().getColor(R.color.white));
                    Toast.makeText(this, "Unregistered Successfully!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
