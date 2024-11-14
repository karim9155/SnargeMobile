package com.example.snargemobile.ui.events;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snargemobile.R;
import com.example.snargemobile.databinding.FragmentEventsBinding;
import com.example.snargemobile.models.Event;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel eventsViewModel;
    private EventsAdapter adapter;
    private PaymentSheet paymentSheet;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize PaymentSheet here with the fragment's lifecycle
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            handlePaymentResult(paymentSheetResult);
        });
        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerViewEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventsAdapter(this::showEditDeleteDialog);
        recyclerView.setAdapter(adapter);

        // Observe events from the ViewModel
        eventsViewModel.getEvents().observe(getViewLifecycleOwner(), adapter::setEvents);

        // Handle add event button click
        Button addEventButton = binding.buttonAddEvent;
        addEventButton.setOnClickListener(v -> showAddEventDialog());

        return root;
    }
    private void handlePaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "Payment successful. Confirmation SMS sent!", Toast.LENGTH_SHORT).show();
            // Additional logic to mark the event as paid, etc., if needed
        } else {
            Toast.makeText(getContext(), "Payment failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showAddEventDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_event);

        // Set dialog width to take up most of the screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initialize input fields
        EditText nameInput = dialog.findViewById(R.id.editTextEventName);
        EditText descriptionInput = dialog.findViewById(R.id.editTextEventDescription);
        EditText dateInput = dialog.findViewById(R.id.editTextEventDate);
        EditText priceInput = dialog.findViewById(R.id.editTextEventPrice);
        Button saveButton = dialog.findViewById(R.id.buttonSaveEvent);

        saveButton.setOnClickListener(v -> {
            // Validate and get input values
            Double price = validateAndGetPrice(nameInput, descriptionInput, dateInput, priceInput);
            if (price == null) return;

            // Create a new Event object
            Event newEvent = new Event(0, nameInput.getText().toString(),
                    descriptionInput.getText().toString(), dateInput.getText().toString(), price);
            eventsViewModel.addEvent(newEvent);

            dialog.dismiss();
            Toast.makeText(getContext(), "Event added", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void showEditDeleteDialog(Event event) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_edit_event);

        // Set dialog width to take up most of the screen
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initialize input fields with existing event data
        EditText nameInput = dialog.findViewById(R.id.editTextEventName);
        EditText descriptionInput = dialog.findViewById(R.id.editTextEventDescription);
        EditText dateInput = dialog.findViewById(R.id.editTextEventDate);
        EditText priceInput = dialog.findViewById(R.id.editTextEventPrice);
        Button updateButton = dialog.findViewById(R.id.buttonUpdateEvent);
        Button deleteButton = dialog.findViewById(R.id.buttonDeleteEvent);
        Button payButton = dialog.findViewById(R.id.buttonPayEvent); // "Mark as Paid" button

        // Set existing event data
        nameInput.setText(event.getName());
        descriptionInput.setText(event.getDescription());
        dateInput.setText(event.getDate());
        priceInput.setText(String.valueOf(event.getPrice()));

        updateButton.setOnClickListener(v -> {
            // Validate and get input values
            Double price = validateAndGetPrice(nameInput, descriptionInput, dateInput, priceInput);
            if (price == null) return;

            // Update event properties
            event.setName(nameInput.getText().toString());
            event.setDescription(descriptionInput.getText().toString());
            event.setDate(dateInput.getText().toString());
            event.setPrice(price);

            // Update the event in the ViewModel
            eventsViewModel.updateEvent(event);
            dialog.dismiss();
            Toast.makeText(getContext(), "Event updated", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            eventsViewModel.deleteEvent(event.getId());
            dialog.dismiss();
            Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
        });

        // Set the click listener for the payment button
        payButton.setOnClickListener(v -> {
            if (!event.isPaymentStatus()) {
                eventsViewModel.initiatePayment(event);  // Trigger the ViewModel to set the client secret

                // Observe client secret from ViewModel and present PaymentSheet if available
                eventsViewModel.getPaymentIntentClientSecret().observe(getViewLifecycleOwner(), clientSecret -> {
                    if (clientSecret != null) {
                        // Present the Payment Sheet with the client secret
                        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Your Business Name");
                        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
                    } else {
                        Toast.makeText(getContext(), "Failed to retrieve client secret", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Event already marked as paid", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });



        dialog.show();
    }

    private Double validateAndGetPrice(EditText nameInput, EditText descriptionInput, EditText dateInput, EditText priceInput) {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || date.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            return Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}