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

import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.EventResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchEventsActivity extends AppCompatActivity {

    private EventsAdapter adapter;
    private List<EventResponse> allEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        RecyclerView rvResults = findViewById(R.id.rv_search_results);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(allEvents);
        rvResults.setAdapter(adapter);

        // Fetch Data
        fetchEvents();

        // Search Input
        EditText etSearch = findViewById(R.id.et_search);
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
        
        NavigationUtils.setupNavigation(this, 1);
    }

    private void fetchEvents() {
        RetrofitClient.getApiService().getAllEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEvents.clear();
                    allEvents.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                Toast.makeText(SearchEventsActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        List<EventResponse> filteredList = new ArrayList<>();
        for (EventResponse item : allEvents) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private List<EventResponse> list;
        EventsAdapter(List<EventResponse> l) { list = l; }

        public void filterList(List<EventResponse> filteredList) {
            list = filteredList;
            notifyDataSetChanged();
        }

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
            h.image.setImageResource(R.drawable.ic_event_default);
            
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
