package com.nnguye51.androidnotes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notes implements Serializable {
    private String title;
    private String body;
    private String time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notes(String title, String body, String time) {
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }


    /*public String getTime() {
        return time;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTime() {
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter tempFormatter = DateTimeFormatter.ofPattern("E MMM dd, h:mm a");
        time = current.format(tempFormatter);

        return time;
    }

    public String toString() {
        return this.title + "\n" + this.time + "\n" + this.body;
    }
}