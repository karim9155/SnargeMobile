package com.example.snargemobile.ui.artists;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.snargemobile.R;

public class ArtistsFragment extends Fragment {

    private ArtistsViewModel artistsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize ViewModel
        artistsViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_artists, container, false);

        // Example usage of the ViewModel data in the fragment
        final TextView textView = root.findViewById(R.id.text_artists);
        artistsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}
