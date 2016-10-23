package com.app.andrew.moviesviewer.DataBase;

import android.provider.BaseColumns;

/**
 * Created by andrew on 10/19/16.
 */

public class DataBaseContract {

    public static final class MovieTable implements BaseColumns{
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_IMAGE_URL = "url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_OVERVIEW = "overview";
    }

    public static final class ReviewTable implements BaseColumns{
        public static final String TABLE_NAME = "review";

        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_REFERENCE = "ref";
    }
    public static final class TrailerTable implements BaseColumns{
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_URL = "url";
        public static final String COLUMN_REFERENCE = "ref";
    }
}
