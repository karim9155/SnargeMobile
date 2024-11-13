package com.example.snargemobile.ui.artists;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArtistsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ArtistsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Artists Fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
