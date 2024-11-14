package com.example.snargemobile.ui.artists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.snargemobile.data.DatabaseHelper;
import com.example.snargemobile.models.Artist;

import java.util.List;

public class ArtistsViewModel extends AndroidViewModel {

    private final DatabaseHelper databaseHelper;
    private final MutableLiveData<List<Artist>> artistsLiveData;

    public ArtistsViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        artistsLiveData = new MutableLiveData<>();
        loadArtists();
    }

    public LiveData<List<Artist>> getArtists() {
        return artistsLiveData;
    }

    private void loadArtists() {
        List<Artist> artistList = databaseHelper.getAllArtists();
        artistsLiveData.setValue(artistList);
    }

    public void addArtist(Artist artist) {
        databaseHelper.addArtist(artist);
        loadArtists();  // Refresh the list
    }

    public void updateArtist(Artist artist) {
        databaseHelper.updateArtist(artist);
        loadArtists();  // Refresh the list
    }

    public void deleteArtist(long id) {
        databaseHelper.deleteArtist(id);
        loadArtists();  // Refresh the list
    }
}
