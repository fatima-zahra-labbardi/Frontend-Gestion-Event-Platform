package com.example.frontend_profilservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import android.content.SharedPreferences;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvHello, tvLoginAs, tvBioBox;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Header Views
        tvHello = findViewById(R.id.tv_hello);
        tvLoginAs = findViewById(R.id.tv_login_as);
        tvBioBox = findViewById(R.id.tv_bio_box);
        ivProfile = findViewById(R.id.iv_profile);

        // Load Data from SharedPreferences and API
        SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        Long userId = prefs.getLong("user_id", -1L);
        String savedRole = prefs.getString("user_role", "STUDENT");

        // Initial Role Setup (Fallback)
        if (tvLoginAs != null && savedRole != null && !savedRole.isEmpty()) {
            String formattedRole = savedRole.substring(0, 1).toUpperCase() + savedRole.substring(1).toLowerCase();
            tvLoginAs.setText("Login as " + formattedRole);
        }
        
        

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
            // Clear session data
            android.content.SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_remembered", false);
            editor.putString("user_email", null);
            editor.putString("user_role", null);
            editor.apply();

            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            // Clear back stack so user can't go back to dashboard
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Notification Icon
        android.widget.ImageView ivNotification = findViewById(R.id.iv_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, NotificationActivity.class)));
        }

        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        Long userId = prefs.getLong("user_id", -1L);
        if (userId != -1L) {
            loadProfileData(userId);
        }
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

    private void loadProfileData(Long userId) {
        RetrofitClient.getApiService().getProfileByUserId(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    
                    if (tvHello != null) {
                        String fullName = profile.getFullName();
                        if (fullName != null && !fullName.isEmpty()) {
                            tvHello.setText("Hello " + fullName + ",");
                        } else {
                            tvHello.setText("Hello,");
                        }
                    }
                    
                    if (tvLoginAs != null) {
                        String role = profile.getUserType(); // "STUDENT" ou "ORGANIZER"
                        if (role != null && !role.isEmpty()) {
                            String formattedRole = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                            tvLoginAs.setText("Login as " + formattedRole);
                        }
                    }
                    
                    if (tvBioBox != null) {
                        String bio = profile.getBio();
                        if (bio != null && !bio.isEmpty()) {
                            tvBioBox.setText(bio);
                        } else {
                            tvBioBox.setText("No bio added yet.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // Silent failure for dashboard header
            }
        });
    }
}
