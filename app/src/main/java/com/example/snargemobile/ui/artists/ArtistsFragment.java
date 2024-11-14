package com.example.snargemobile.ui.artists;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snargemobile.R;
import com.example.snargemobile.models.Artist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ArtistsFragment extends Fragment implements ArtistsAdapter.OnArtistActionListener {

    private ArtistsViewModel artistsViewModel;
    private ArtistsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize ViewModel
        artistsViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_artists, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recycler_artists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtistsAdapter(this);
        recyclerView.setAdapter(adapter);

        // Observe the artists LiveData
        artistsViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            adapter.setArtists(artists);
        });

        // Set up FAB to add a new artist
        FloatingActionButton fabAdd = root.findViewById(R.id.fab_add_artist);
        fabAdd.setOnClickListener(v -> showAddArtistDialog());

        return root;
    }

    private void showAddArtistDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_artist, null);

        // Set up dialog components
        EditText editName = dialogView.findViewById(R.id.edit_artist_name);
        EditText editGenre = dialogView.findViewById(R.id.edit_artist_genre);
        EditText editTrack = dialogView.findViewById(R.id.edit_artist_track);  // Use this for track URL
        Button buttonAdd = dialogView.findViewById(R.id.button_add_artist);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        buttonAdd.setText("Add Artist");

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        buttonAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String genre = editGenre.getText().toString().trim();
            String trackUrl = editTrack.getText().toString().trim();

            if (name.isEmpty() || genre.isEmpty() || trackUrl.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Artist newArtist = new Artist(0, name, genre, trackUrl);
                artistsViewModel.addArtist(newArtist);
                dialog.dismiss();
                Toast.makeText(getContext(), "Artist Added", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showUpdateArtistDialog(Artist artist) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_artist, null);

        EditText editName = dialogView.findViewById(R.id.edit_artist_name);
        EditText editGenre = dialogView.findViewById(R.id.edit_artist_genre);
        EditText editTrack = dialogView.findViewById(R.id.edit_artist_track);
        Button buttonAdd = dialogView.findViewById(R.id.button_add_artist);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        // Set existing data
        editName.setText(artist.getName());
        editGenre.setText(artist.getGenre());
        editTrack.setText(artist.getTrack());

        // Change button text to "Update"
        buttonAdd.setText("Update");

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        buttonAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String genre = editGenre.getText().toString().trim();
            String track = editTrack.getText().toString().trim();

            if (name.isEmpty() || genre.isEmpty() || track.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                artist.setName(name);
                artist.setGenre(genre);
                artist.setTrack(track);
                artistsViewModel.updateArtist(artist);
                dialog.dismiss();
                Toast.makeText(getContext(), "Artist Updated", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmationDialog(Artist artist) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Artist")
                .setMessage("Are you sure you want to delete this artist?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    artistsViewModel.deleteArtist(artist.getId());
                    Toast.makeText(getContext(), "Artist Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onUpdateArtist(Artist artist) {
        showUpdateArtistDialog(artist);
    }

    @Override
    public void onDeleteArtist(Artist artist) {
        showDeleteConfirmationDialog(artist);
    }
}
