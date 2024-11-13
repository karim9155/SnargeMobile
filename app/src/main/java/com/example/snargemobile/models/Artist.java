package com.example.snargemobile.models;

public class Artist {
    private long id;
    private String name;
    private String genre;
    private String track; // New field for the SoundCloud track URL

    // Constructor with trackUrl
    public Artist(long id, String name, String genre, String track) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.track = track;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
