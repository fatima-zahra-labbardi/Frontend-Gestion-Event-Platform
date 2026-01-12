package com.example.frontend_profilservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Header and Bio are static for now or loaded from shared prefs/API
        // For this task, we assume static or set here.

        // Events Link
        View eventsLayout = findViewById(R.id.layout_events);
        setupMenuItem(eventsLayout, "Events", "Find your Events history", R.drawable.ic_menu_events);

        eventsLayout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, EventsActivity.class));
        });

        // Profile Link
        View profileLayout = findViewById(R.id.layout_profile);
        setupMenuItem(profileLayout, "Profile", "Manage your profile", R.drawable.ic_menu_profile);
        profileLayout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
        });

        // Support Link
        View supportLayout = findViewById(R.id.layout_support);
        setupMenuItem(supportLayout, "Support", "Help center", R.drawable.ic_menu_support);
        supportLayout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SupportActivity.class));
        });

        // Terms Link
        View termsLayout = findViewById(R.id.layout_terms);
        setupMenuItem(termsLayout, "Terms & Conditions", "", R.drawable.ic_menu_terms);
        termsLayout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TermsActivity.class));
        });
        
        // Log out
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            // Clear back stack so user can't go back to dashboard
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Notification Icon
        android.widget.ImageView ivNotification = findViewById(R.id.iv_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, NotificationActivity.class)));
        }

        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }

    private void setupMenuItem(View view, String title, String subtitle, int iconResId) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSubtitle = view.findViewById(R.id.tv_subtitle);
        ImageView ivIcon = view.findViewById(R.id.iv_icon); // If we want to change icon programmatically

        if (tvTitle != null) tvTitle.setText(title);
        if (tvSubtitle != null) {
            if (subtitle.isEmpty()) {
                tvSubtitle.setVisibility(View.GONE);
            } else {
                tvSubtitle.setText(subtitle);
            }
        }
        if (ivIcon != null) ivIcon.setImageResource(iconResId);
    }
}
