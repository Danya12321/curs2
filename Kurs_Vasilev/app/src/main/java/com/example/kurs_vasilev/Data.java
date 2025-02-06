package com.example.kurs_vasilev;

import android.util.Log;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Data {
    static private final String server = "185.221.214.178";
    static private final String database = "MapInfoData";
    static private final int port = 5432    ;
    static private final String user = "postgres";
    static private final String pass = "sql@sql";
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
        protected abstract void logic() throws SQLException;
        protected abstract void end();
        public void run() {
            if (!conStatus) {
                return;
            }
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
        protected void end(){
            getCabinets = new GetCabinets();
        }
        @Override
        protected void logic() throws SQLException {
            cabinets.ensureCapacity(rs.getFetchSize());

            while (rs.next()) {
                cabinets.add(new Cabinets(rs.getInt("id"), rs.getString("number"), rs.getString("description")));
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
        protected void end(){
            getNews = new GetNews();
        }
        @Override
        protected void logic() throws SQLException {
            news.ensureCapacity(rs.getFetchSize());

            while (rs.next()) {
                news.add(new News(rs.getInt("id"), rs.getString("title"), rs.getString("url")));
            }
        }
    }
}
