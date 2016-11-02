package com.app.andrew.moviesviewer.DataHolder;

import java.io.Serializable;

/**
 * Created by andrew on 10/19/16.
 */

public class Review implements Serializable {
    private String author;
    private String comment;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
