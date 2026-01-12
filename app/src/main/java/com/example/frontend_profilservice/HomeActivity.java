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

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        // Clubs Carousel
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

        // Past Events Implementation
        LinearLayout llPastEvents = findViewById(R.id.ll_past_events_container);
        
        addPastEventView(llPastEvents, "L'Ingénieur Citoyen", "17 December, 2025", "ENSAK", R.drawable.ic_past_event_citoyen);
        addPastEventView(llPastEvents, "Tournoi Inter-Filières", "13 December, 2025", "Spot Club, Kenitra", R.drawable.ic_past_event_tournoi);
        addPastEventView(llPastEvents, "Billard Tournament", "11 December, 2025", "University fields", R.drawable.ic_past_event_billard);

        NavigationUtils.setupNavigation(this, 0); // 0 = Home
    }

    private void addPastEventView(LinearLayout container, String title, String date, String location, int imgRes) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_past_event, container, false);
        
        ((TextView) v.findViewById(R.id.tv_event_title)).setText(title);
        ((TextView) v.findViewById(R.id.tv_event_date)).setText(date);
        ((TextView) v.findViewById(R.id.tv_event_location)).setText(location);
        ((ImageView) v.findViewById(R.id.iv_event)).setImageResource(imgRes);
        
        // Ensure no click listener as requested
        v.setClickable(false);
        v.setFocusable(false);
        
        container.addView(v);
    }

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
