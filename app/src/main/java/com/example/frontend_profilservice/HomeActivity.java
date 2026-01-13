package com.example.frontend_profilservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.EventResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout llPastEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        llPastEvents = findViewById(R.id.ll_past_events_container);

        // Retrieve user role from SharedPreferences
        android.content.SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String role = preferences.getString("user_role", "STUDENT");
        boolean isOrganizer = "ORGANIZER".equalsIgnoreCase(role);

        // Header Logic
        android.widget.ImageView ivAddEvent = findViewById(R.id.iv_add_event);
        ImageView ivNotification = findViewById(R.id.iv_notification);
        
        ivNotification.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        
        if (isOrganizer) {
            ivAddEvent.setVisibility(View.VISIBLE);
            ivAddEvent.setOnClickListener(v -> startActivity(new Intent(this, CreateEventActivity.class)));
        } else {
            ivAddEvent.setVisibility(View.GONE);
        }

        // Action Button Logic
        LinearLayout llMainAction = findViewById(R.id.ll_main_action);
        TextView tvActionLabel = findViewById(R.id.tv_action_label);

        if (isOrganizer) {
            tvActionLabel.setText("Create An Event");
            llMainAction.setOnClickListener(v -> startActivity(new Intent(this, CreateEventActivity.class)));
        } else {
            tvActionLabel.setText("Register In An Event");
            llMainAction.setOnClickListener(v -> startActivity(new Intent(this, ServicesActivity.class)));
        }

        // Clubs Carousel (Keep static for now as requested or until service is ready)
        RecyclerView rvClubs = findViewById(R.id.rv_clubs);
        rvClubs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        List<Club> clubs = new ArrayList<>();
        clubs.add(new Club("AFAAQ Club", "A Humanitarian Club", R.drawable.ic_club_afaaq_v2));
        clubs.add(new Club("Anaruz", "A Humanitarian Club", R.drawable.ic_club_anaruz_v2));
        clubs.add(new Club("Green Invest", "A Green Investment Club", R.drawable.ic_club_green_invest));
        clubs.add(new Club("Mécatronique", "A Specialized Engineering & Robotics Club", R.drawable.ic_club_mecatronique_v2));
        clubs.add(new Club("Google Developer Groups", "A Global Community for Developers", R.drawable.ic_club_gdg));
        clubs.add(new Club("Enactus", "A Social Entrepreneurship & Action Club", R.drawable.ic_club_enactus));
        
        rvClubs.setAdapter(new ClubsAdapter(clubs));

        // 1. Restore Static Past Events
        addPastEventView(llPastEvents, "L'Ingénieur Citoyen", "17 December, 2025", "ENSAK", R.drawable.ic_past_event_citoyen);
        addPastEventView(llPastEvents, "Tournoi Inter-Filières", "13 December, 2025", "Spot Club, Kenitra", R.drawable.ic_past_event_tournoi);
        addPastEventView(llPastEvents, "Billard Tournament", "11 December, 2025", "University fields", R.drawable.ic_past_event_billard);

        // 2. Fetch Real Upcoming Events (will be added to the top)
        fetchUpcomingEvents();

        NavigationUtils.setupNavigation(this, 0); 
    }

    private void fetchUpcomingEvents() {
        RetrofitClient.getApiService().getUpcomingEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // On ne supprime plus tout (removeAllViews), on AJOUTE au début
                    for (EventResponse e : response.body()) {
                        addEventView(llPastEvents, e, true); // true = au début
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
            }
        });
    }

    private void addEventView(LinearLayout container, EventResponse e, boolean atTop) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_past_event, container, false);
        
        ((TextView) v.findViewById(R.id.tv_event_title)).setText(e.getTitle());
        ((TextView) v.findViewById(R.id.tv_event_date)).setText(e.getEventDate());
        ((TextView) v.findViewById(R.id.tv_event_location)).setText(e.getLocation());
        ((ImageView) v.findViewById(R.id.iv_event)).setImageResource(R.drawable.ic_event_default);
        
        long currentUserId = getSharedPreferences("EventHubPrefs", MODE_PRIVATE).getLong("user_id", -1L);

        v.setOnClickListener(view -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("EXTRA_TITLE", e.getTitle());
            intent.putExtra("EXTRA_LOCATION", e.getLocation());
            intent.putExtra("EXTRA_DATE", e.getEventDate());
            intent.putExtra("EXTRA_DESCRIPTION", e.getDescription());
            intent.putExtra("EXTRA_EVENT_ID", e.getId());
            intent.putExtra("EXTRA_MAX_PARTICIPANTS", e.getMaxParticipants());
            intent.putExtra("EXTRA_IS_CREATOR", e.getCreatedBy() != null && e.getCreatedBy() == currentUserId);
            startActivity(intent);
        });
        
        if (atTop) container.addView(v, 0);
        else container.addView(v);
    }

    private void addPastEventView(LinearLayout container, String title, String date, String location, int imgRes) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_past_event, container, false);
        
        ((TextView) v.findViewById(R.id.tv_event_title)).setText(title);
        ((TextView) v.findViewById(R.id.tv_event_date)).setText(date);
        ((TextView) v.findViewById(R.id.tv_event_location)).setText(location);
        ((ImageView) v.findViewById(R.id.iv_event)).setImageResource(imgRes);
        
        v.setOnClickListener(view -> {
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("EXTRA_TITLE", title);
            intent.putExtra("EXTRA_LOCATION", location);
            intent.putExtra("EXTRA_DATE", date);
            intent.putExtra("EXTRA_STATIC", true);
            startActivity(intent);
        });
        
        container.addView(v);
    }

    // Keep inner classes for Clubs for now
    private static class Club {
        String name, type;
        int iconRes;
        Club(String n, String t, int i) { name = n; type = t; iconRes = i; }
    }

    private static class ClubsAdapter extends RecyclerView.Adapter<ClubsAdapter.ClubViewHolder> {
        private final List<Club> list;
        ClubsAdapter(List<Club> l) { list = l; }

        @NonNull @Override
        public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ClubViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_club, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ClubViewHolder h, int pos) {
            Club c = list.get(pos);
            h.name.setText(c.name);
            h.type.setText(c.type);
            h.logo.setImageResource(c.iconRes);
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ClubViewHolder extends RecyclerView.ViewHolder {
            TextView name, type;
            ImageView logo;
            ClubViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_club_name);
                type = v.findViewById(R.id.tv_club_type);
                logo = v.findViewById(R.id.iv_club_logo);
            }
        }
    }
}
