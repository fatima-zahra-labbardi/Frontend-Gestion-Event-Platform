package com.example.frontend_profilservice.api;

import com.example.frontend_profilservice.models.JwtResponse;
import com.example.frontend_profilservice.models.RegisterRequest;
import com.example.frontend_profilservice.models.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("api/auth/register/student")
    Call<String> registerStudent(@Body RegisterRequest request);

    @POST("api/auth/register/organizer")
    Call<String> registerOrganizer(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest request);
}
