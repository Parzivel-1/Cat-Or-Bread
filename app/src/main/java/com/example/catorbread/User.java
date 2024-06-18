package com.example.catorbread;

public class User {
    String username;
    String password;
    int score;
    static String current;

    public static String getCurrent () {
        return current;
    }

    public static void setCurrent (String current) {
        User.current = current;
    }

    public User (String username , String password , int score) {
        this.username = username;
        this.password = password;
        this.score = score;
    }

    public User () {
    }

    public int getScore () {
        return score;
    }

    public void setScore (int score) {
        this.score = score;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }
}