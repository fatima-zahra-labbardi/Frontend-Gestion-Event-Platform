package com.example.frontend_profilservice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchEventsActivity extends AppCompatActivity {

    private EventsAdapter adapter;
    private List<Event> allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Search Input
        EditText etSearch = findViewById(R.id.et_search);
        
        // Debug Toast
        android.widget.Toast.makeText(this, "Welcome to Search Page", android.widget.Toast.LENGTH_SHORT).show();

        // Mock Data (Same as ServicesActivity)
        allEvents = new ArrayList<>();
        allEvents.add(new Event("Jo Malone London's Mother's Day Presents", "Radius Gallery • Santa Cruz, CA", "Wed, Apr 28 • 5:30 PM", R.drawable.ic_event_default));
        allEvents.add(new Event("A Virtual Evening of Smooth Jazz", "Lot 13 • Oakland, CA", "Sat, May 1 • 2:00 PM", R.drawable.ic_event_default));
        allEvents.add(new Event("Women's Leadership Conference 2021", "53 Bush St • San Francisco, CA", "Sat, Apr 24 • 1:30 PM", R.drawable.ic_event_default));
        allEvents.add(new Event("International Kids Safe Parents Night Out", "Lot 13 • Oakland, CA", "Fri, Apr 23 • 6:00 PM", R.drawable.ic_event_default));
        
        // Setup RecyclerView
        RecyclerView rvResults = findViewById(R.id.rv_search_results);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(allEvents);
        rvResults.setAdapter(adapter);

        // Search Logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        NavigationUtils.setupNavigation(this, 1); // Keep Grid active or maybe none? Keeping 1 for consistency
    }

    private void filter(String text) {
        List<Event> filteredList = new ArrayList<>();
        for (Event item : allEvents) {
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
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
        private List<Event> list;

        EventsAdapter(List<Event> l) { list = l; }

        public void filterList(List<Event> filteredList) {
            list = filteredList;
            notifyDataSetChanged();
        }

        @NonNull @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            // Reusing the detailed card view
            return new EventViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_event_card, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder h, int pos) {
            Event e = list.get(pos);
            h.title.setText(e.title);
            h.location.setText(e.location);
            if (h.date != null) h.date.setText(e.date);
            
            if (e.imageResId != 0) {
                h.image.setImageResource(e.imageResId);
            } else {
                 h.image.setImageResource(R.drawable.ic_event_default);
            }
            
            h.itemView.setOnClickListener(v -> {
                android.content.Intent i = new android.content.Intent(v.getContext(), EventDetailsActivity.class);
                i.putExtra("EXTRA_TITLE", e.title);
                i.putExtra("EXTRA_LOCATION", e.location);
                i.putExtra("EXTRA_IS_CREATOR", false); // Assuming search usually shows other's events
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
