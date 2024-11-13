package com.example.snargemobile.ui.events;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.snargemobile.data.DatabaseHelper;
import com.example.snargemobile.models.Event;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import java.util.List;
import okhttp3.*;
import org.json.JSONObject;

public class EventsViewModel extends AndroidViewModel {

    private static final String ACCOUNT_SID = "AC9e957c700b9133d465d30d24f77b0423"; // Replace with your Twilio Account SID
    private static final String AUTH_TOKEN = "01a68116d2e0756bc5d32127ed7d64dd";   // Replace with your Twilio Auth Token
    private static final String TWILIO_PHONE_NUMBER = "+1 443 251 3881"; // Replace with your Twilio Phone Number

    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<Event>> events;

    public EventsViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        events = new MutableLiveData<>();

        // Initialize Twilio SDK with Account SID and Auth Token
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        loadEvents();
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    // Load all events from the database asynchronously
    public void loadEvents() {
        AsyncTask.execute(() -> events.postValue(dbHelper.getAllEvents()));
    }

    // Add a new event to the database asynchronously and reload the list
    public void addEvent(Event event) {
        AsyncTask.execute(() -> {
            dbHelper.insertEvent(event);
            loadEvents();
        });
    }

    // Update an existing event in the database asynchronously and reload the list
    public void updateEvent(Event event) {
        AsyncTask.execute(() -> {
            dbHelper.updateEvent(event);
            loadEvents();
        });
    }

    // Delete an event from the database asynchronously by its ID and reload the list
    public void deleteEvent(long eventId) {
        AsyncTask.execute(() -> {
            dbHelper.deleteEvent(eventId);
            loadEvents();
        });
    }

    // Mark an event as paid and send a confirmation message
    public void markEventAsPaid(Event event) {
        event.setPaymentStatus(true); // Mark the event as paid

        AsyncTask.execute(() -> {
            dbHelper.updateEvent(event); // Update the event in the database
            loadEvents(); // Reload the events to reflect the change
            sendPaymentConfirmation(event); // Send confirmation message
        });
    }

    // Send payment confirmation SMS using Twilio
    public void sendPaymentConfirmation(Event event) {
        OkHttpClient client = new OkHttpClient();
        String messageBody = "Payment confirmed for event: " + event.getName() + ". Thank you!";

        String authToken = ACCOUNT_SID + ":" + AUTH_TOKEN;
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";

        RequestBody body = new FormBody.Builder()
                .add("To", "+21626620734") // Replace with the recipient's phone number
                .add("From", TWILIO_PHONE_NUMBER)
                .add("Body", messageBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + Base64.encodeToString(authToken.getBytes(), Base64.NO_WRAP))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Twilio", "Failed to send message: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Twilio", "Message sent successfully: " + response.body().string());
                } else {
                    Log.e("Twilio", "Failed to send message: " + response.body().string());
                }
            }
        });

}}
