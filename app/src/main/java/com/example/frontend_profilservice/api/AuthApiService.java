package com.example.frontend_profilservice.api;

import com.example.frontend_profilservice.models.EventResponse;
import com.example.frontend_profilservice.models.JwtResponse;
import com.example.frontend_profilservice.models.RegisterRequest;
import com.example.frontend_profilservice.models.LoginRequest;
import com.example.frontend_profilservice.models.RegistrationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface AuthApiService {
    // --- AUTH SERVICE ---
    @POST("auth/api/auth/register/student")
    Call<String> registerStudent(@Body RegisterRequest request);

    @POST("auth/api/auth/register/organizer")
    Call<String> registerOrganizer(@Body RegisterRequest request);

    @POST("auth/api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest request);

    // --- REGISTRATION SERVICE (Lien avec le backend) ---
    @POST("registration/api/registrations/events/{eventId}/users/{userId}")
    Call<Object> registerToEvent(@Path("eventId") Long eventId, @Path("userId") Long userId);

    @DELETE("registration/api/registrations/events/{eventId}/users/{userId}")
    Call<Void> unregisterFromEvent(@Path("eventId") Long eventId, @Path("userId") Long userId);

    @GET("registration/api/registrations/events/{eventId}")
    Call<List<com.example.frontend_profilservice.models.RegistrationResponse>> getEventRegistrations(@Path("eventId") Long eventId);

    @GET("registration/api/registrations/events/{eventId}/count")
    Call<Long> countEventRegistrations(@Path("eventId") Long eventId);

    // --- EVENT SERVICE (Affichage des données réelles) ---
    @GET("event/api/events")
    Call<List<EventResponse>> getAllEvents();

    @GET("event/api/events/upcoming")
    Call<List<EventResponse>> getUpcomingEvents();

    @GET("event/api/events/{id}")
    Call<EventResponse> getEventById(@Path("id") Long eventId);

    @GET("event/api/events/organizer/{organizerId}")
    Call<List<EventResponse>> getEventsByOrganizer(@Path("organizerId") Long organizerId);

    @Multipart
    @POST("event/api/events")
    Call<EventResponse> createEvent(
            @Part("event") RequestBody event,
            @Part MultipartBody.Part image,
            @Header("X-User-Id") Long userId
    );

    @Multipart
    @retrofit2.http.PUT("event/api/events/{id}")
    Call<EventResponse> updateEvent(
            @Path("id") Long eventId,
            @Part("event") RequestBody event,
            @Part MultipartBody.Part image,
            @Header("X-User-Id") Long userId
    );

    @DELETE("event/api/events/{id}")
    Call<Void> deleteEvent(
            @Path("id") Long eventId,
            @Header("X-User-Id") Long userId
    );

    @GET("registration/api/registrations/events/{eventId}/users/{userId}/status")
    Call<Boolean> getRegistrationStatus(@Path("eventId") Long eventId, @Path("userId") Long userId);

    // --- PROFILE SERVICE ---
    @GET("profile/profiles/user/{userId}")
    Call<com.example.frontend_profilservice.models.ProfileResponse> getProfileByUserId(@Path("userId") Long userId);

    @GET("event/api/events/batch")
    Call<List<EventResponse>> getEventsByIds(@retrofit2.http.Query("ids") List<Long> ids);

    @retrofit2.http.PUT("profile/profiles/{id}")
    Call<com.example.frontend_profilservice.models.ProfileResponse> updateProfile(@Path("id") Long profileId, @Body com.example.frontend_profilservice.models.ProfileResponse profile);

    @GET("registration/api/registrations/users/{userId}")
    Call<List<RegistrationResponse>> getUserRegistrations(@Path("userId") Long userId);
}
