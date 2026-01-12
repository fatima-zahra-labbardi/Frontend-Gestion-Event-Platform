package com.example.frontend_profilservice;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Notification Icon
        ImageView ivNotification = findViewById(R.id.iv_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new android.content.Intent(this, NotificationActivity.class)));
        }
        
        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }
}
