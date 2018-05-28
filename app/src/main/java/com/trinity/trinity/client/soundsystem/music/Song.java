package com.trinity.trinity.client.soundsystem.music;

public class Song {

    private String name, artist, location;
    private long duration;

    public Song(String name, String artist, String location, long duration) {
        this.name = name;
        this.artist = artist;
        this.location = location;
        this.duration = duration;
    }

    public String getTitle() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getLocation() {
        return location;
    }

    public long getDuration() {
        return duration;
    }
}
