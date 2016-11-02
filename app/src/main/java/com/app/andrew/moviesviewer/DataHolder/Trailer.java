package com.app.andrew.moviesviewer.DataHolder;

import java.io.Serializable;

/**
 * Created by Andrew on 10/20/2016.
 */

public class Trailer implements Serializable {
    private String url;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
