package com.example.frontend_profilservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.EventResponse;
import com.example.frontend_profilservice.models.RegistrationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history);

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        RecyclerView rvEvents = findViewById(R.id.rv_events);
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            
            List<EventResponse> eventsList = new ArrayList<>();
            SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1L);
            
            EventsAdapter adapter = new EventsAdapter(eventsList, userId);
            rvEvents.setAdapter(adapter);

            if (userId != -1L) {
                loadUserEvents(userId, adapter, eventsList);
            }
        }
        
        NavigationUtils.setupNavigation(this, 1); 
    }

    private void loadUserEvents(long userId, final EventsAdapter adapter, final List<EventResponse> eventsList) {
        SharedPreferences prefs = getSharedPreferences("EventHubPrefs", MODE_PRIVATE);
        String role = prefs.getString("user_role", "STUDENT");

        if ("ORGANIZER".equalsIgnoreCase(role)) {
            loadOrganizerUnifiedEvents(userId, adapter, eventsList);
        } else {
            loadStudentEvents(userId, adapter, eventsList);
        }
    }

    private void loadStudentEvents(long userId, final EventsAdapter adapter, final List<EventResponse> eventsList) {
        RetrofitClient.getApiService().getUserRegistrations(userId).enqueue(new Callback<List<RegistrationResponse>>() {
            @Override
            public void onResponse(Call<List<RegistrationResponse>> call, Response<List<RegistrationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Long> eventIds = response.body().stream()
                            .map(RegistrationResponse::getEventId)
                            .collect(Collectors.toList());
                    if (!eventIds.isEmpty()) {
                        fetchEventDetails(eventIds, adapter, eventsList);
                    } else {
                        eventsList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<RegistrationResponse>> call, Throwable t) {
                Toast.makeText(EventsActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrganizerUnifiedEvents(long userId, final EventsAdapter adapter, final List<EventResponse> eventsList) {
        final Set<EventResponse> mergedEvents = new HashSet<>();
        
        // 1. Fetch Created Events
        RetrofitClient.getApiService().getEventsByOrganizer(userId).enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mergedEvents.addAll(response.body());
                }
                
                // 2. Fetch Registered Events
                RetrofitClient.getApiService().getUserRegistrations(userId).enqueue(new Callback<List<RegistrationResponse>>() {
                    @Override
                    public void onResponse(Call<List<RegistrationResponse>> call, Response<List<RegistrationResponse>> responseReg) {
                        if (responseReg.isSuccessful() && responseReg.body() != null) {
                            List<Long> regIds = responseReg.body().stream()
                                    .map(RegistrationResponse::getEventId)
                                    .collect(Collectors.toList());
                            
                            if (!regIds.isEmpty()) {
                                RetrofitClient.getApiService().getEventsByIds(regIds).enqueue(new Callback<List<EventResponse>>() {
                                    @Override
                                    public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> responseDetails) {
                                        if (responseDetails.isSuccessful() && responseDetails.body() != null) {
                                            mergedEvents.addAll(responseDetails.body());
                                        }
                                        updateUIWithMergedEvents(mergedEvents, adapter, eventsList);
                                    }
                                    @Override
                                    public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                                        updateUIWithMergedEvents(mergedEvents, adapter, eventsList);
                                    }
                                });
                            } else {
                                updateUIWithMergedEvents(mergedEvents, adapter, eventsList);
                            }
                        } else {
                            updateUIWithMergedEvents(mergedEvents, adapter, eventsList);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<RegistrationResponse>> call, Throwable t) {
                        updateUIWithMergedEvents(mergedEvents, adapter, eventsList);
                    }
                });
            }
            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                loadStudentEvents(userId, adapter, eventsList);
            }
        });
    }

    private void updateUIWithMergedEvents(Set<EventResponse> mergedSet, EventsAdapter adapter, List<EventResponse> eventsList) {
        if (eventsList == null || adapter == null) return;
        eventsList.clear();
        if (mergedSet != null) {
            for (EventResponse e : mergedSet) {
                if (e != null) eventsList.add(e);
            }
        }
        
        try {
            Collections.sort(eventsList, (e1, e2) -> {
                if (e1 == null && e2 == null) return 0;
                if (e1 == null) return 1;
                if (e2 == null) return -1;
                Long id1 = (e1.getId() != null) ? e1.getId() : 0L;
                Long id2 = (e2.getId() != null) ? e2.getId() : 0L;
                return id2.compareTo(id1);
            });
        } catch (Exception e) {
            Log.e("EventsActivity", "Sort error", e);
        }
        
        adapter.notifyDataSetChanged();
    }

    private void fetchEventDetails(List<Long> eventIds, final EventsAdapter adapter, final List<EventResponse> eventsList) {
        RetrofitClient.getApiService().getEventsByIds(eventIds).enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventsList.clear();
                    eventsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                Toast.makeText(EventsActivity.this, "Error loading event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter
    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

        private final List<EventResponse> eventList;
        private final long currentUserId;

        EventsAdapter(List<EventResponse> eventList, long currentUserId) {
            this.eventList = eventList;
            this.currentUserId = currentUserId;
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
            final EventResponse event = eventList.get(position);
            holder.tvTitle.setText(event.getTitle());
            holder.tvLocation.setText(event.getLocation());
            
            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), EventDetailsActivity.class);
                intent.putExtra("EXTRA_TITLE", event.getTitle());
                intent.putExtra("EXTRA_LOCATION", event.getLocation());
                intent.putExtra("EXTRA_EVENT_ID", event.getId());
                intent.putExtra("EXTRA_DATE", event.getEventDate());
                intent.putExtra("EXTRA_DESCRIPTION", event.getDescription());
                intent.putExtra("EXTRA_MAX_PARTICIPANTS", event.getMaxParticipants() != null ? event.getMaxParticipants() : 0);
                
                boolean isCreator = event.getCreatedBy() != null && event.getCreatedBy().equals(currentUserId);
                intent.putExtra("EXTRA_IS_CREATOR", isCreator);
                
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
