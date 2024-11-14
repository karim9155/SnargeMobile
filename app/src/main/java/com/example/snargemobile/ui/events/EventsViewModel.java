package com.example.snargemobile.ui.events;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.snargemobile.data.DatabaseHelper;
import com.example.snargemobile.models.Event;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.*;
import org.json.JSONObject;

public class EventsViewModel extends AndroidViewModel {

    private static final String ACCOUNT_SID = "AC9e957c700b9133d465d30d24f77b0423"; // Replace with your Twilio Account SID
    private static final String AUTH_TOKEN = "908945c809391d569f7bfd34bc3e240c";   // Replace with your Twilio Auth Token
    private static final String TWILIO_PHONE_NUMBER = "+1 443 251 3881"; // Replace with your Twilio Phone Number

    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<Event>> events;

    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> paymentIntentClientSecret = new MutableLiveData<>();


    public EventsViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        events = new MutableLiveData<>();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        loadEvents();
        // Initialize Stripe with your publishable key
        PaymentConfiguration.init(application, "pk_test_51QKmVZG79d8RuUpSUKugPLlWX4TMvmRQuoIQwzVlAD3eyqFJV8wpKWPhSEYLhsjynNKdEYopBRwWGmzzcIR2dm9b00IyzEZjAI"); // Replace with your Stripe Publishable Key
    }
    public LiveData<String> getPaymentIntentClientSecret() {
        return paymentIntentClientSecret;
    }

    public void initiatePayment(Event event) {
        // This client secret should be generated on your server or retrieved from Stripe Dashboard for testing
        String testClientSecret = "pi_3QKqbOG79d8RuUpS09lFHmSR_secret_RHCZLTUV4AqK5RHJeyGWEnuNf"; // Replace this with an actual test client secret
        paymentIntentClientSecret.setValue(testClientSecret);
    }




    private void onPaymentResult(PaymentSheetResult paymentSheetResult, Event event) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            markEventAsPaid(event); // Mark event as paid and trigger SMS
            paymentSuccess.setValue(true);
        } else {
            paymentSuccess.setValue(false);
            Log.e("Payment", "Payment not completed.");
        }
    }

    private void fetchPaymentIntentClientSecret(Event event, Consumer<String> onClientSecretReceived) {
        // This should make an API call to your backend to get the PaymentIntent client secret
        // Example implementation (replace with actual network call to your server)
        String clientSecret = "client_secret_from_backend";
        onClientSecretReceived.accept(clientSecret);
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
    // Send payment confirmation SMS using Twilio
    public void sendPaymentConfirmation(Event event) {
        OkHttpClient client = new OkHttpClient();
        String messageBody = "Payment confirmed for event: " + event.getName() + ". Thank you!";
        String authToken = ACCOUNT_SID + ":" + AUTH_TOKEN;
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";

        // Log the URL and authorization details for debugging (do NOT log authToken in production)
        Log.d("TwilioDebug", "Twilio URL: " + url);
        Log.d("TwilioDebug", "Authorization header: " + Base64.encodeToString(authToken.getBytes(), Base64.NO_WRAP));

        // Construct the request body
        RequestBody body = new FormBody.Builder()
                .add("To", "+21626620734") // Replace with recipient's phone number
                .add("From", TWILIO_PHONE_NUMBER)
                .add("Body", messageBody)
                .build();

        // Log the message body details
        Log.d("TwilioDebug", "Message Body: " + messageBody);
        Log.d("TwilioDebug", "To: " + "+21626620734");
        Log.d("TwilioDebug", "From: " + TWILIO_PHONE_NUMBER);

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + Base64.encodeToString(authToken.getBytes(), Base64.NO_WRAP))
                .post(body)
                .build();

        // Execute the Twilio request asynchronously
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
                    Log.e("Twilio", "Failed to send message: HTTP " + response.code() + " - " + response.body().string());
                }
            }
        });
    }

}
