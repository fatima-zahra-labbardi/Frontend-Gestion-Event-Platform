package com.example.frontend_profilservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    
    private EditText etTitle, etTime, etLocation, etDescription;
    private TextView tvImagePath;
    private ActivityResultLauncher<String> galleryLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // UI Elements
        etTitle = findViewById(R.id.et_event_title);
        etTime = findViewById(R.id.et_event_time);
        etLocation = findViewById(R.id.et_event_location);
        etDescription = findViewById(R.id.et_event_description);
        tvImagePath = findViewById(R.id.tv_image_path);
        
        // Picker Listener
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
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Create Button
        Button btnCreate = findViewById(R.id.btn_create);
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> createEvent());
        }
        
        // Image Picker Trigger
        findViewById(R.id.ll_image_picker).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        
        NavigationUtils.setupNavigation(this, 1);
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
    
    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim(); // Format attendu: 2026-01-12T10:00:00
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || timeStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Prepare Request Object
        CreateEventRequest eventRequest = new CreateEventRequest(title, description, timeStr, location, 100);
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
        long userId = getSharedPreferences("EventHubPrefs", MODE_PRIVATE).getLong("user_id", 1L);

        // 4. API Call
        RetrofitClient.getApiService().createEvent(eventPart, imagePart, userId).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Événement créé avec succès !", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Erreur " + response.code();
                    try {
                        String errorBody = response.errorBody().string();
                        android.util.Log.e("API_ERROR", "Code: " + response.code() + ", Body: " + errorBody);
                        if (errorBody.contains("organisateur") || errorBody.contains("rôle inconnu")) {
                            errorMsg = "Accès refusé : Seuls les organisateurs (ou compte non synchronisé)";
                        }
                    } catch (Exception e) {}
                    Toast.makeText(CreateEventActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
