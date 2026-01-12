package com.example.frontend_profilservice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // Back Button - Goes to Home Activity
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                startActivity(new android.content.Intent(ServicesActivity.this, HomeActivity.class));
                finish();
            });
        }

        // Notification Icon
        ImageView ivNotification = findViewById(R.id.iv_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new android.content.Intent(this, NotificationActivity.class)));
        }

        // Search Icon
        ImageView ivSearch = findViewById(R.id.iv_search);
        if (ivSearch != null) {
            ivSearch.setOnClickListener(v -> {
                startActivity(new android.content.Intent(ServicesActivity.this, SearchEventsActivity.class));
            });
        }
        
        // Role Logic for Add Event
        ImageView ivAddEvent = findViewById(R.id.iv_add_event);
        
        // Retrieve user role from SharedPreferences
        android.content.SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String role = preferences.getString("user_role", "STUDENT");
        boolean isOrganizer = "ORGANIZER".equalsIgnoreCase(role);
        
        if (ivAddEvent != null) {
            if (isOrganizer) {
                ivAddEvent.setVisibility(View.VISIBLE);
                ivAddEvent.setOnClickListener(v -> {
                     startActivity(new android.content.Intent(ServicesActivity.this, CreateEventActivity.class));
                });
            } else {
                ivAddEvent.setVisibility(View.GONE);
            }
        }

        // Events List Setup
        RecyclerView rvEvents = findViewById(R.id.rv_events);
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            
            List<Event> events = new ArrayList<>();
            // Adding events with simulated image resources or logic to mock "no photo"
            events.add(new Event("Jo Malone London's Mother's Day Presents", "Radius Gallery • Santa Cruz, CA", "Wed, Apr 28 • 5:30 PM", R.drawable.ic_event_default));
            events.add(new Event("A Virtual Evening of Smooth Jazz", "Lot 13 • Oakland, CA", "Sat, May 1 • 2:00 PM", R.drawable.ic_event_default));
            events.add(new Event("Women's Leadership Conference 2021", "53 Bush St • San Francisco, CA", "Sat, Apr 24 • 1:30 PM", R.drawable.ic_event_default));
            
            EventsAdapter adapter = new EventsAdapter(events);
            rvEvents.setAdapter(adapter);
        }

        NavigationUtils.setupNavigation(this, 1); // 1 = Services/Grid
    }
    
    // Inner models and adapters
    private static class Event {
        String title, location, date;
        int imageResId;
        
        Event(String t, String l, String d, int img) { 
            title=t; location=l; date=d; imageResId=img; 
        }
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private final List<Event> list;
        EventsAdapter(List<Event> l) { list = l; }

        @NonNull @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new EventViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_event_card, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder h, int pos) {
            Event e = list.get(pos);
            h.title.setText(e.title);
            h.location.setText(e.location);
            if (h.date != null) h.date.setText(e.date);
            
            // Set image or default
            if (e.imageResId != 0) {
                h.image.setImageResource(e.imageResId);
            } else {
                 h.image.setImageResource(R.drawable.ic_event_default);
            }
            
            h.itemView.setOnClickListener(v -> {
                android.content.Intent i = new android.content.Intent(v.getContext(), EventDetailsActivity.class);
                i.putExtra("EXTRA_TITLE", e.title);
                i.putExtra("EXTRA_LOCATION", e.location);
                // For demonstration: First item is "mine" (I am creator), others are "student view"
                i.putExtra("EXTRA_IS_CREATOR", pos == 0);
                v.getContext().startActivity(i);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class EventViewHolder extends RecyclerView.ViewHolder {
            TextView title, location, date;
            ImageView image;
            
            EventViewHolder(View v) {
                super(v);
                title = v.findViewById(R.id.tv_event_title);
                location = v.findViewById(R.id.tv_event_location);
                date = v.findViewById(R.id.tv_event_date);
                image = v.findViewById(R.id.iv_event);
            }
        }
    }
}
