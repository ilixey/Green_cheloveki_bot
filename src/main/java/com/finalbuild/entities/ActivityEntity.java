package com.finalbuild.entities;

import java.sql.Date;

public class ActivityEntity {
    private long id;
    private String name;
    private String surname;
    private String activity;
    private double duration;
    private Date date;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getActivity() {
        return activity;
    }

    public double getDuration() {
        return duration;
    }

    public Date getDate() {
        return date;
    }

    public ActivityEntity(long id, String name, String surname, String activity, double duration, Date date) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.activity = activity;
        this.duration = duration;
        this.date = date;
    }
}
