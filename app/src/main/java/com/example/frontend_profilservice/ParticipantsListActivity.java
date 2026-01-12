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

public class ParticipantsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        // Header
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView rvParticipants = findViewById(R.id.rv_participants);
        if (rvParticipants != null) {
            rvParticipants.setLayoutManager(new LinearLayoutManager(this));
            
            List<Participant> participants = new ArrayList<>();
            // Sample data from screenshot
            participants.add(new Participant("David Sibia", "Just now"));
            participants.add(new Participant("David Sibia", "5 min ago"));
            participants.add(new Participant("David Sibia", "20 min ago"));
            participants.add(new Participant("David Sibia", "1 hr ago"));
            participants.add(new Participant("David Sibia", "9 hr ago"));
            participants.add(new Participant("David Sibia", "Tue , 5:10 pm"));
            participants.add(new Participant("David Sibia", "Wed, 3:30 pm"));
            
            ParticipantsAdapter adapter = new ParticipantsAdapter(participants);
            rvParticipants.setAdapter(adapter);
        }

        NavigationUtils.setupNavigation(this, 1);
    }

    private static class Participant {
        String name, time;
        Participant(String n, String t) { name = n; time = t; }
    }

    private static class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder> {
        private final List<Participant> list;
        ParticipantsAdapter(List<Participant> l) { list = l; }

        @NonNull @Override
        public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ParticipantViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_participant, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ParticipantViewHolder h, int pos) {
            Participant p = list.get(pos);
            h.name.setText(p.name);
            h.time.setText(p.time);
            // Image is default for now
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ParticipantViewHolder extends RecyclerView.ViewHolder {
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
