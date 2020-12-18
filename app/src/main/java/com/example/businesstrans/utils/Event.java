package com.example.businesstrans.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String id;
    private String name_of_event;
    private String date_of_event;
    private String venue_of_event;

    public  Event(){

    }

    public Event(String id, String name_of_event, String date_of_event, String venue_of_event) {
        this.id = id;
        this.name_of_event = name_of_event;
        this.date_of_event = date_of_event;
        this.venue_of_event = venue_of_event;
    }

    protected Event(Parcel in) {
        id = in.readString();
        name_of_event = in.readString();
        date_of_event = in.readString();
        venue_of_event = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_of_event() {
        return name_of_event;
    }

    public void setName_of_event(String name_of_event) {
        this.name_of_event = name_of_event;
    }

    public String getDate_of_event() {
        return date_of_event;
    }

    public void setDate_of_event(String date_of_event) {
        this.date_of_event = date_of_event;
    }

    public String getVenue_of_event() {
        return venue_of_event;
    }

    public void setVenue_of_event(String venue_of_event) {
        this.venue_of_event = venue_of_event;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name_of_event);
        parcel.writeString(date_of_event);
        parcel.writeString(venue_of_event);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name_of_event='" + name_of_event + '\'' +
                ", date_of_event='" + date_of_event + '\'' +
                ", venue_of_event='" + venue_of_event + '\'' +
                '}';
    }
}
