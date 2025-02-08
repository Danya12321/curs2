package com.example.kurs_vasilev;

public class Schedule {
    private int id;
    private String date;
    private String dayOfWeek;
    private String timeslot;
    private String subjectName;
    private String groupNumber;
    private String teacherName;
    private String cabinetNumber;

    public Schedule(int id, String date, String dayOfWeek, String timeslot, String subjectName, String groupNumber, String teacherName, String cabinetNumber) {
        this.id = id;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.timeslot = timeslot;
        this.subjectName = subjectName;
        this.groupNumber = groupNumber;
        this.teacherName = teacherName;
        this.cabinetNumber = cabinetNumber;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }
    public String getFormattedSchedule() {
        return date + " " +
                dayOfWeek + " " +
                timeslot + " " +
                subjectName + " " +
                groupNumber + " " +
                teacherName + " " +
                cabinetNumber;
    }
}
