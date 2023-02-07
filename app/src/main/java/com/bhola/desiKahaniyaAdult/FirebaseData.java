package com.bhola.desiKahaniyaAdult;

public class FirebaseData {
int id;
  String Date,Heading,Title;

    public FirebaseData() {
    }

    public FirebaseData(int id, String date, String heading, String title) {
        this.id = id;
        Date = date;
        Heading = heading;
        Title = title;
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

    public void setDate(String date) {
        Date = date;
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
}
