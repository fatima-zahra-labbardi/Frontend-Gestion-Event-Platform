package com.example.frontend_profilservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend_profilservice.api.RetrofitClient;
import com.example.frontend_profilservice.models.CreateEventRequest;
import com.example.frontend_profilservice.models.EventResponse;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import android.net.Uri;

public class UpdateEventActivity extends AppCompatActivity {

    private EditText etTitle, etTime, etLocation, etDescription;
    private TextView tvImagePath;
    private ActivityResultLauncher<String> galleryLauncher;
    private long eventId;
    private Uri selectedImageUri;
    private String rawEventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        // UI Elements
        etTitle = findViewById(R.id.et_event_title);
        etTime = findViewById(R.id.et_event_time);
        etLocation = findViewById(R.id.et_event_location);
        etDescription = findViewById(R.id.et_event_description);
        tvImagePath = findViewById(R.id.tv_image_path);
        
        // Data from Intent
        eventId = getIntent().getLongExtra("EXTRA_EVENT_ID", -1L);
        etTitle.setText(getIntent().getStringExtra("EXTRA_TITLE"));
        etLocation.setText(getIntent().getStringExtra("EXTRA_LOCATION"));
        etTime.setText(getIntent().getStringExtra("EXTRA_DATE"));
        etDescription.setText(getIntent().getStringExtra("EXTRA_DESCRIPTION"));
        rawEventDate = getIntent().getStringExtra("EXTRA_DATE_RAW");

        // Disable direct typing on time and use picker
        etTime.setFocusable(false);
        etTime.setClickable(true);
        etTime.setOnClickListener(v -> showDateTimePicker());

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    tvImagePath.setText("Image Selected: " + uri.getLastPathSegment());
                }
            }
        );

        // Back Button
        ImageView ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Close Button
        Button btnClose = findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        // Update Button
        Button btnUpdate = findViewById(R.id.btn_update);
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> updateEvent());
        }
        
        // Image Picker Trigger
        findViewById(R.id.ll_image_picker).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        
        NavigationUtils.setupNavigation(this, 1); 
    }

    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String displayTime = etTime.getText().toString().trim(); 
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || displayTime.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // If the date field contains 'T', it's already an ISO string from the picker.
        // Otherwise, it's the formatted display string, so we use the raw ISO date string.
        String finalDate = displayTime.contains("T") ? displayTime : rawEventDate;

        if (finalDate == null || finalDate.isEmpty()) {
            finalDate = displayTime; // Last-ditch effort fallback
        }

        // 1. Prepare Request Object
        CreateEventRequest eventRequest = new CreateEventRequest(title, description, finalDate, location, 100);
        String json = new Gson().toJson(eventRequest);
        RequestBody eventPart = RequestBody.create(MediaType.parse("application/json"), json);

        // 2. Prepare Image Part
        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = getBytes(inputStream);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), bytes);
                imagePart = MultipartBody.Part.createFormData("image", "event_image.jpg", requestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Get User ID
        long userId = getSharedPreferences("EventHubPrefs", MODE_PRIVATE).getLong("user_id", -1L);

        // 4. API Call
        RetrofitClient.getApiService().updateEvent(eventId, eventPart, imagePart, userId).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateEventActivity.this, "Événement mis à jour avec succès !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateEventActivity.this, "Erreur lors de la mise à jour : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(UpdateEventActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void showDateTimePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    final int selectedYear = year1;
                    final int selectedMonth = monthOfYear + 1;
                    final int selectedDay = dayOfMonth;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                String formattedTime = String.format(Locale.getDefault(), 
                                    "%04d-%02d-%02dT%02d:%02d:00", 
                                    selectedYear, selectedMonth, selectedDay, hourOfDay, minute);
                                etTime.setText(formattedTime);
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }
}
