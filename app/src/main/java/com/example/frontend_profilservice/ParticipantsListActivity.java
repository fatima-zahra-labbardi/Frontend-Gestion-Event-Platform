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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.ProfileResponse;
import com.example.frontend_profilservice.models.RegistrationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class ParticipantsListActivity extends AppCompatActivity {

    private long eventId;
    private ParticipantsAdapter adapter;
    private List<RegistrationResponse> participantsList = new ArrayList<>();
    private Map<Long, String> userNamesCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        eventId = getIntent().getLongExtra("EXTRA_EVENT_ID", -1L);

        // Header
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView rvParticipants = findViewById(R.id.rv_participants);
        if (rvParticipants != null) {
            rvParticipants.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ParticipantsAdapter(participantsList);
            rvParticipants.setAdapter(adapter);
        }

        if (eventId != -1L) {
            fetchParticipants();
        }

        NavigationUtils.setupNavigation(this, 1);
    }

    private void fetchParticipants() {
        RetrofitClient.getApiService().getEventRegistrations(eventId).enqueue(new Callback<List<RegistrationResponse>>() {
            @Override
            public void onResponse(Call<List<RegistrationResponse>> call, Response<List<RegistrationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    participantsList.clear();
                    participantsList.addAll(response.body());
                    
                    // Trigger name fetching for each participant
                    for (RegistrationResponse reg : response.body()) {
                        fetchUserName(reg.getUserId());
                    }
                    
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ParticipantsListActivity.this, "Impossible de charger les participants", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RegistrationResponse>> call, Throwable t) {
                Toast.makeText(ParticipantsListActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserName(long userId) {
        if (userNamesCache.containsKey(userId)) return;

        // Fetch profile to get the full name
        RetrofitClient.getApiService().getProfileByUserId(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userNamesCache.put(userId, response.body().getFullName());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                // Ignore failure, will keep "User #ID"
            }
        });
    }

    private class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder> {
        private final List<RegistrationResponse> list;
        ParticipantsAdapter(List<RegistrationResponse> l) { list = l; }

        @NonNull @Override
        public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ParticipantViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_participant, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ParticipantViewHolder h, int pos) {
            RegistrationResponse p = list.get(pos);
            String name = userNamesCache.get(p.getUserId());
            
            h.name.setText(name != null ? name : "User #" + p.getUserId());
            h.time.setText("Inscrit le: " + p.getRegisteredAt());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ParticipantViewHolder extends RecyclerView.ViewHolder {
            TextView name, time;
            ImageView image;
            ParticipantViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_participant_name);
                time = v.findViewById(R.id.tv_registration_time);
                image = v.findViewById(R.id.iv_participant_avatar);
            }
        }
    }
}
