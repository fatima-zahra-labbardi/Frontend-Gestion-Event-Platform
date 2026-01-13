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

import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.EventResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventsAdapter adapter;
    private List<EventResponse> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // UI Setup
        rvEvents = findViewById(R.id.rv_events);
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            adapter = new EventsAdapter(eventList);
            rvEvents.setAdapter(adapter);
        }

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                startActivity(new android.content.Intent(ServicesActivity.this, HomeActivity.class));
                finish();
            });
        }

        // Fetch Data
        fetchEvents();

        // Role-based Add Event button visibility
        ImageView ivAddEvent = findViewById(R.id.iv_add_event);
        android.content.SharedPreferences preferences = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String role = preferences.getString("user_role", "STUDENT");
        boolean isOrganizer = "ORGANIZER".equalsIgnoreCase(role);

        if (ivAddEvent != null) {
            if (isOrganizer) {
                ivAddEvent.setVisibility(View.VISIBLE);
                ivAddEvent.setOnClickListener(v -> startActivity(new android.content.Intent(this, CreateEventActivity.class)));
            } else {
                ivAddEvent.setVisibility(View.GONE);
            }
        }

        ImageView ivSearch = findViewById(R.id.iv_search);
        if (ivSearch != null) {
            ivSearch.setOnClickListener(v -> startActivity(new android.content.Intent(this, SearchEventsActivity.class)));
        }

        NavigationUtils.setupNavigation(this, 1);
    }

    private void fetchEvents() {
        RetrofitClient.getApiService().getAllEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ServicesActivity.this, "Erreur chargement événements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                Toast.makeText(ServicesActivity.this, "Problème réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private final List<EventResponse> list;
        EventsAdapter(List<EventResponse> l) { list = l; }

        @NonNull @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new EventViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_event_card, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder h, int pos) {
            EventResponse e = list.get(pos);
            h.title.setText(e.getTitle());
            h.location.setText(e.getLocation());
            h.date.setText(e.getEventDate());
            h.image.setImageResource(R.drawable.ic_event_default); // Image logic placeholder
            
            h.itemView.setOnClickListener(v -> {
                android.content.Intent i = new android.content.Intent(v.getContext(), EventDetailsActivity.class);
                i.putExtra("EXTRA_TITLE", e.getTitle());
                i.putExtra("EXTRA_LOCATION", e.getLocation());
                i.putExtra("EXTRA_EVENT_ID", e.getId());
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
