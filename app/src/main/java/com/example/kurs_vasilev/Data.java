package com.example.kurs_vasilev;

import android.util.Log;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Data {
    static private final String server = "185.221.215.59";
    static private final String database = "Curs2";
    static private final int port = 5432;
    static private final String user = "postgres";
    static private final String pass = "sqlMaxSql";
    static private final String driver = "org.postgresql.Driver";
    static private final String url = String.format("jdbc:postgresql://%s:%d/%s", server, port, database);

    static Connection connection = null;
    static public boolean conStatus;
    static public Connect connectionToDataBase = new Connect();

    public static class Connect extends Thread {
        public void run() {
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, pass);
                conStatus = true;
                Log.i("DB", "connectionToDataBase: connection true");
            } catch (SQLException e) {
                Log.e("DB", "connectionToDataBase: connection false");
                e.printStackTrace();
                conStatus = false;
            } catch (ClassNotFoundException e) {
                Log.e("DB", "connectionToDataBase: connection false");
                e.printStackTrace();
                conStatus = false;
            }
            connectionToDataBase = new Connect();
        }
    }

    public static abstract class Query extends Thread{
        protected String sql = null;
        protected boolean queryStatus;
        protected ResultSet rs;
        protected abstract void pre();
        protected abstract void logic() throws SQLException;
        protected abstract void end();
        public void run() {
            if (!conStatus) {
                return;
            }
                pre();
            if (sql == null){
                Log.e("DB", "run: sql query is null");
                return;
            }
            try(Statement stmt = connection.createStatement() ) {
                Log.i("DB", "run: \n" + sql);
                rs = stmt.executeQuery(sql);
                this.logic();
                queryStatus = true;
            } catch (SQLException e) {
                queryStatus = false;
                Log.e("DB", "query: \n" + e);
            }
            this.end();
        }
    }
    public static ArrayList<Cabinets> cabinets = new ArrayList<>();
    public static GetCabinets getCabinets = new GetCabinets();
    public static class GetCabinets extends Query {
        public GetCabinets(){
            sql = "SELECT * FROM cabinets";
        }
        @Override
        protected void pre() {

        }
        @Override
        protected void end(){
            getCabinets = new GetCabinets();
        }
        @Override
        protected void logic() throws SQLException {
            cabinets.ensureCapacity(rs.getFetchSize());

            while (rs.next()) {
                cabinets.add(new Cabinets(rs.getInt("id"), rs.getString("number"), rs.getString("description"), null));
            }
        }
    }
    public static ArrayList<News> news = new ArrayList<>();
    public static GetNews getNews = new GetNews();
    public static class GetNews extends Query {
        public GetNews(){
            sql = "SELECT * FROM news";
        }
        @Override
        protected void pre() {

        }
        @Override
        protected void end(){
            getNews = new GetNews();
        }
        @Override
        protected void logic() throws SQLException {
            news.ensureCapacity(rs.getFetchSize());

            while (rs.next()) {
                int t1 = rs.getInt("id");
                String t2 =rs.getString("title");
                String t3 = rs.getString("url");
                News n = new News(t1, t2, t3);
               news.add(n);
            }
        }
    }

    public static ArrayList<Schedule> schedules = new ArrayList<>();
    public static GetSchedule getSchedule = new GetSchedule();

    public static class GetSchedule extends Query {
        private String cabinetNumber;
        private String tempSql;
        public void setCabinetNumber(String cabinetNumber){
            this.cabinetNumber = cabinetNumber;
        }
        public GetSchedule() {
            tempSql = "SELECT " +
                    "schedule.id, " +
                    "schedule.date, " +
                    "schedule.day_of_week, " +
                    "timeslots.time_start || ' - ' || timeslots.time_end AS timeslot, " +
                    "subject.name AS subject_name, " +
                    "groupss.number AS group_number, " +
                    "teachers.full_name AS teacher_name, " +
                    "cabinets.number AS cabinet_number " +
                    "FROM public.schedule " +
                    "JOIN public.timeslots ON schedule.id_timeslot = timeslots.id " +
                    "JOIN public.subject ON schedule.id_subject = subject.id " +
                    "JOIN public.groupss ON schedule.id_group = groupss.id " +
                    "JOIN public.teachers ON schedule.id_teachers = teachers.id " +
                    "JOIN public.cabinets ON schedule.id_cabinet = cabinets.id ";
        }
        @Override
        protected void pre() {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            sql = tempSql +
                    "WHERE cabinets.number = \'" + cabinetNumber + "\' AND schedule.date = \'" + today + "\'" +
                    "ORDER BY schedule.date";
        }
        @Override
        protected void end() {
            getSchedule = new GetSchedule();
        }

        @Override
        protected void logic() throws SQLException {
            schedules = new ArrayList<Schedule>();
            schedules.ensureCapacity(rs.getFetchSize());

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("date");
                String dayOfWeek = rs.getString("day_of_week");
                String timeslot = rs.getString("timeslot");
                String subjectName = rs.getString("subject_name");
                String groupNumber = rs.getString("group_number");
                String teacherName = rs.getString("teacher_name");
                String cabinetNumber = rs.getString("cabinet_number");

                Schedule schedule = new Schedule(id, date, dayOfWeek, timeslot, subjectName, groupNumber, teacherName, cabinetNumber);
                schedules.add(schedule);
            }
        }
    }
}
