package com.example.frontend_profilservice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_profilservice.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.frontend_profilservice.models.EventResponse;
import com.example.frontend_profilservice.models.ProfileResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventDetailsActivity extends AppCompatActivity {

    private boolean isRegistered = false;
    private long userId;
    private long eventId;
    private Button btnRegister;
    private TextView tvTitle, tvLocation, tvDate, tvDescription, tvOrganizerName;
    private TextView tvTimeRange, tvLocationAddress;
    private ImageView ivBanner;
    private String rawEventDate; // ISO format from backend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        
        // Navigation Utilities
        NavigationUtils.setupNavigation(this, 1); 

        // Data from Intent
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String location = getIntent().getStringExtra("EXTRA_LOCATION");
        String date = getIntent().getStringExtra("EXTRA_DATE");
        String description = getIntent().getStringExtra("EXTRA_DESCRIPTION");
        boolean isCreator = getIntent().getBooleanExtra("EXTRA_IS_CREATOR", false);
        boolean isStatic = getIntent().getBooleanExtra("EXTRA_STATIC", false);
        eventId = getIntent().getLongExtra("EXTRA_EVENT_ID", -1L);
        int maxParticipants = getIntent().getIntExtra("EXTRA_MAX_PARTICIPANTS", 0);

        // Retrieve UserId from SharedPreferences
        userId = getSharedPreferences("EventHubPrefs", MODE_PRIVATE).getLong("user_id", -1L);

        // UI Header
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // UI Content - Initialize fields
        tvTitle = findViewById(R.id.tv_event_title);
        tvLocation = findViewById(R.id.tv_location_name);
        tvDate = findViewById(R.id.tv_event_date);
        tvDescription = findViewById(R.id.tv_event_description);
        tvOrganizerName = findViewById(R.id.tv_organizer_name);
        tvTimeRange = findViewById(R.id.tv_event_time_range);
        tvLocationAddress = findViewById(R.id.tv_location_address);
        ivBanner = findViewById(R.id.iv_event_banner);
        btnRegister = findViewById(R.id.btn_register_event);

        // Set initial data if available (optional, will be overwritten by API)
        if (tvTitle != null && title != null) tvTitle.setText(title);
        if (tvLocation != null && location != null) tvLocation.setText(location);
        if (tvDate != null && date != null) tvDate.setText(date);
        if (tvDescription != null && description != null) tvDescription.setText(description);

        // If we have an eventId, fetch FRESH data from server
        if (eventId != -1L) {
            fetchEventDetails(eventId);
        }

        if (userId != -1L) {
            loadProfileData(); // Get role info if needed (optional)
        }

        // Setup Buttons Listeners
        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteEvent());
        findViewById(R.id.btn_update).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, UpdateEventActivity.class);
            intent.putExtra("EXTRA_EVENT_ID", eventId);
            if (tvTitle != null) intent.putExtra("EXTRA_TITLE", tvTitle.getText().toString());
            if (tvLocation != null) intent.putExtra("EXTRA_LOCATION", tvLocation.getText().toString());
            if (tvDate != null) intent.putExtra("EXTRA_DATE", tvDate.getText().toString());
            if (tvDescription != null) intent.putExtra("EXTRA_DESCRIPTION", tvDescription.getText().toString());
            intent.putExtra("EXTRA_DATE_RAW", rawEventDate);
            int maxPart = getIntent().getIntExtra("EXTRA_MAX_PART_INT", 
                          getIntent().getIntExtra("EXTRA_MAX_PARTICIPANTS", 0));
            intent.putExtra("EXTRA_MAX_PARTICIPANTS", maxPart);
            startActivity(intent);
        });
        findViewById(R.id.btn_registered_list).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ParticipantsListActivity.class);
            intent.putExtra("EXTRA_EVENT_ID", eventId);
            startActivity(intent);
        });

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                if (userId == -1L) {
                    Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isRegistered) unregisterFromEvent();
                else registerToEvent();
            });
        }

        // Initialize based on Intent (fast UI)
        if (isCreator) {
            showCreatorUI();
        } else {
            showStudentUI();
        }

        // Fetch fresh data
        if (eventId != -1L) {
            fetchEventDetails(eventId);
        }
    }

    private void showCreatorUI() {
        LinearLayout llOrganizerInfo = findViewById(R.id.ll_organizer_info);
        LinearLayout llCreatorButtons = findViewById(R.id.ll_creator_buttons);
        if (llCreatorButtons != null) llCreatorButtons.setVisibility(View.VISIBLE);
        if (llOrganizerInfo != null) llOrganizerInfo.setVisibility(View.GONE);
        if (btnRegister != null) btnRegister.setVisibility(View.GONE);
        fetchRegistrationCount();
    }

    private void showStudentUI() {
        LinearLayout llOrganizerInfo = findViewById(R.id.ll_organizer_info);
        LinearLayout llCreatorButtons = findViewById(R.id.ll_creator_buttons);
        if (llCreatorButtons != null) llCreatorButtons.setVisibility(View.GONE);
        if (llOrganizerInfo != null) llOrganizerInfo.setVisibility(View.VISIBLE);
        if (btnRegister != null && !getIntent().getBooleanExtra("EXTRA_STATIC", false)) {
            btnRegister.setVisibility(View.VISIBLE);
            checkRegistrationStatus();
        }
    }

    private void loadProfileData() {
        // Just used to refresh local role if needed, currently we use EXTRA_IS_CREATOR or API createdBy
    }

    private void fetchEventDetails(long id) {
        RetrofitClient.getApiService().getEventById(id).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventResponse event = response.body();
                    tvTitle.setText(event.getTitle());
                    tvLocation.setText(event.getLocation());
                    if (tvLocationAddress != null) tvLocationAddress.setText(event.getLocation());
                    
                    // Format Date and Time
                    rawEventDate = event.getEventDate();
                    formatAndSetDateTime(rawEventDate);
                    
                    tvDescription.setText(event.getDescription());
                    
                    if (event.getCreatedBy() != null) {
                        fetchOrganizerProfile(event.getCreatedBy());
                        
                        // Dynamic Creator Check
                        if (event.getCreatedBy().equals(userId)) {
                            showCreatorUI();
                        } else {
                            showStudentUI();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Error loading event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOrganizerProfile(long organizerId) {
        RetrofitClient.getApiService().getProfileByUserId(organizerId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (tvOrganizerName != null) {
                        tvOrganizerName.setText(response.body().getFullName());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }

    private void checkRegistrationStatus() {
        if (userId == -1L) return;

        RetrofitClient.getApiService().getRegistrationStatus(eventId, userId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isRegistered = response.body();
                    updateButtonUI();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Keep default state
            }
        });
    }

    private void registerToEvent() {
        RetrofitClient.getApiService().registerToEvent(eventId, userId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    isRegistered = true;
                    updateButtonUI();
                    Toast.makeText(EventDetailsActivity.this, "INSCRIPTION RÉUSSIE !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventDetailsActivity.this, "ÉCHEC INSCRIPTION Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "ERREUR RÉSEAU : Inscription impossible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unregisterFromEvent() {
        RetrofitClient.getApiService().unregisterFromEvent(eventId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isRegistered = false;
                    updateButtonUI();
                    Toast.makeText(EventDetailsActivity.this, "DÉSINCRIPTION RÉUSSIE !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventDetailsActivity.this, "ÉCHEC DÉSINCRIPTION Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "ERREUR RÉSEAU : Désinscription impossible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEvent() {
        RetrofitClient.getApiService().deleteEvent(eventId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EventDetailsActivity.this, "Événement supprimé avec succès !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Échec de la suppression : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRegistrationCount() {
        RetrofitClient.getApiService().countEventRegistrations(eventId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Button btnList = findViewById(R.id.btn_registered_list);
                    if (btnList != null) {
                        btnList.setText("Registered (" + response.body() + ")");
                    }
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
            }
        });
    }

    private void formatAndSetDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return;
        try {
            // ISO format from backend: 2026-01-15T03:00:00
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(dateStr);

            if (date != null) {
                SimpleDateFormat dateOnly = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
                SimpleDateFormat timeOnly = new SimpleDateFormat("EEEE, hh:mm a", Locale.getDefault());
                
                if (tvDate != null) tvDate.setText(dateOnly.format(date));
                if (tvTimeRange != null) tvTimeRange.setText(timeOnly.format(date));
            }
        } catch (Exception e) {
            if (tvDate != null) tvDate.setText(dateStr);
            if (tvTimeRange != null) tvTimeRange.setText("");
        }
    }

    private void updateButtonUI() {
        if (isRegistered) {
            btnRegister.setText("Unregister Event");
            btnRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.light_purple)));
            btnRegister.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnRegister.setText("Register Event");
            btnRegister.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.purple_custom)));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
