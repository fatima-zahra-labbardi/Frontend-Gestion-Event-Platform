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

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history);

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

        // Setup RecyclerView
        RecyclerView rvEvents = findViewById(R.id.rv_events);
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            
            List<Event> events = new ArrayList<>();
            events.add(new Event("Football Game : 24 July", "Uni Stadium"));
            events.add(new Event("Table Tennis Game : 15 May", "Uni Stadium"));
            events.add(new Event("Concert : 10 April", "ENSA"));
            
            EventsAdapter adapter = new EventsAdapter(events);
            rvEvents.setAdapter(adapter);
        }
        
        NavigationUtils.setupNavigation(this, 2); // 2 = Profile
    }

    // Simple Event Model
    private static class Event {
        String title;
        String location;

        Event(String title, String location) {
            this.title = title;
            this.location = location;
        }
    }

    // Adapter
    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

        private final List<Event> eventList;

        EventsAdapter(List<Event> eventList) {
            this.eventList = eventList;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.tvTitle.setText(event.title);
            holder.tvLocation.setText(event.location);
            
            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), EventDetailsActivity.class);
                intent.putExtra("EXTRA_TITLE", event.title);
                intent.putExtra("EXTRA_LOCATION", event.location);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        static class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            TextView tvLocation;

            EventViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_event_title);
                tvLocation = itemView.findViewById(R.id.tv_event_location);
            }
        }
    }
}
