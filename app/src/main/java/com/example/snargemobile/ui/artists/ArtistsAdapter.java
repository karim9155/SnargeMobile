package com.example.snargemobile.ui.artists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snargemobile.R;
import com.example.snargemobile.models.Artist;
import java.util.ArrayList;
import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {

    private List<Artist> artists = new ArrayList<>();
    private OnArtistActionListener listener;

    public ArtistsAdapter(OnArtistActionListener listener) {
        this.listener = listener;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.nameTextView.setText(artist.getName());
        holder.genreTextView.setText(artist.getGenre());
        holder.trackTextView.setText(artist.getTrack());

        // Handle Update button click
        holder.updateButton.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateArtist(artist);
        });

        // Handle Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteArtist(artist);
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, genreTextView, trackTextView;
        Button updateButton, deleteButton;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_artist_name);
            genreTextView = itemView.findViewById(R.id.text_artist_genre);
            trackTextView = itemView.findViewById(R.id.text_artist_track);
            updateButton = itemView.findViewById(R.id.button_update_artist);
            deleteButton = itemView.findViewById(R.id.button_delete_artist);
        }
    }

    // Interface for handling artist actions
    public interface OnArtistActionListener {
        void onUpdateArtist(Artist artist);
        void onDeleteArtist(Artist artist);
    }
}
