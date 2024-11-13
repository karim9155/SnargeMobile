package com.example.snargemobile.ui.artists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
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

        // Configure WebView to display Spotify track
        String trackUrl = artist.getTrack();
        if (trackUrl != null && !trackUrl.isEmpty()) {
            String trackId = extractTrackId(trackUrl);
            if (trackId != null) {
                String embedUrl = "https://open.spotify.com/embed/track/" + trackId;
                holder.trackWebView.loadData(
                        "<iframe style=\"border-radius:12px\" src=\"" + embedUrl +
                                "\" width=\"100%\" height=\"80\" frameBorder=\"0\" " +
                                "allow=\"autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture\" " +
                                "loading=\"lazy\"></iframe>",
                        "text/html", "utf-8");
            }
        } else {
            holder.trackWebView.loadData("<p>No track available</p>", "text/html", "utf-8");
        }

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

    private String extractTrackId(String url) {
        // Extract the track ID from the Spotify URL
        // Spotify URL format: https://open.spotify.com/track/{trackId}?{parameters}
        String[] parts = url.split("/track/");
        if (parts.length > 1) {
            String trackPart = parts[1];
            return trackPart.contains("?") ? trackPart.split("\\?")[0] : trackPart;
        }
        return null;
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, genreTextView;
        WebView trackWebView;
        ImageButton updateButton;  // Declare as ImageButton
        ImageButton deleteButton;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_artist_name);
            genreTextView = itemView.findViewById(R.id.text_artist_genre);
            trackWebView = itemView.findViewById(R.id.webview_artist_track);
            updateButton = itemView.findViewById(R.id.button_update_artist);
            deleteButton = itemView.findViewById(R.id.button_delete_artist);

            // Configure WebView settings
            WebSettings webSettings = trackWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);  // Enable JavaScript for iframe support
        }
    }

    // Interface for handling artist actions
    public interface OnArtistActionListener {
        void onUpdateArtist(Artist artist);
        void onDeleteArtist(Artist artist);
    }
}
