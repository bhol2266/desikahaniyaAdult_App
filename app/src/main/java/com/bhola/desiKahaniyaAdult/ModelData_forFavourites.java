package com.bhola.desiKahaniyaAdult;

public class ModelData_forFavourites {
    int id;
    String Date,Heading,Title;
    int Liked;

    public ModelData_forFavourites() {
    }

    public ModelData_forFavourites(int id, String data, String heading, String title, int liked) {
        this.id = id;
        Date = data;
        Heading = heading;
        Title = title;
        Liked = liked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String data) {
        Date = data;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getLiked() {
        return Liked;
    }

    public void setLiked(int liked) {
        Liked = liked;
    }
}
