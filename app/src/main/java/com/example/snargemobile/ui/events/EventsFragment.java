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

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel eventsViewModel;
    private EventsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
        Button payButton = dialog.findViewById(R.id.buttonPayEvent); // New "Mark as Paid" button

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

        payButton.setOnClickListener(v -> {
            if (!event.isPaymentStatus()) {
                eventsViewModel.markEventAsPaid(event); // Mark as paid and send SMS
                Toast.makeText(getContext(), "Payment marked and confirmation sent", Toast.LENGTH_SHORT).show();
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
