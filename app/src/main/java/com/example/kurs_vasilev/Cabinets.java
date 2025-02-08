package com.example.kurs_vasilev;

import java.util.ArrayList;

public class Cabinets {
    public int id;
    public String number;
    public String description;
    public ArrayList<Schedule> schedules;

    public Cabinets(int id, String number, String description, ArrayList<Schedule> schedules){
        this.id = id;
        this.number = number;
        this.description = description;
        this.schedules = schedules;
    }
}
