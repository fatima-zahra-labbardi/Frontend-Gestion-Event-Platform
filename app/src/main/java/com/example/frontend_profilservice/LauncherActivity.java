package com.example.frontend_profilservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LauncherActivity extends AppCompatActivity {

    private TextView tvAnimatedText;
    private String fullText = "ventHub";
    private int index = 0;
    private long delay = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        tvAnimatedText = findViewById(R.id.tvAnimatedText);
        handler.postDelayed(characterAdder, delay);
    }

    private final Handler handler = new Handler();
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            if (index <= fullText.length()) {
                String currentString = fullText.substring(0, index);
                SpannableStringBuilder spannable = new SpannableStringBuilder(currentString);

                int endVent = Math.min(currentString.length(), 4);
                if (endVent > 0) {
                    spannable.setSpan(
                            new ForegroundColorSpan(ContextCompat.getColor(LauncherActivity.this, R.color.event_purple)),
                            0, endVent, 0
                    );
                }

                if (currentString.length() > 4) {
                    spannable.setSpan(
                            new ForegroundColorSpan(ContextCompat.getColor(LauncherActivity.this, R.color.hub_pink)),
                            4, currentString.length(), 0
                    );
                }

                tvAnimatedText.setText(spannable);
                index++;
                handler.postDelayed(this, delay);
            } else {
                new Handler().postDelayed(() -> {
                    SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
                    boolean isRemembered = preferences.getBoolean("is_remembered", false);

                    Intent nextScreen;
                    if (isRemembered) {
                        nextScreen = new Intent(LauncherActivity.this, DashboardActivity.class);
                    } else {
                        nextScreen = new Intent(LauncherActivity.this, LoginActivity.class);
                    }

                    startActivity(nextScreen);
                    finish();
                }, 1000);
            }
        }
    };
}
