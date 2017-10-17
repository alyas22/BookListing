package com.example.android.booklisting;

import android.graphics.Bitmap;

/**
 * Created by Toshiba on 18/08/17.
 */

public class Book {

    private String mAuthors;
    private String mTitle;
    private Bitmap mImage;
    private String mUrl;
    public Book(String  title, String authors, Bitmap image, String url) {
        mAuthors = authors;
        mTitle = title;
        mImage = image;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthors;
    }

    public String getTitle() {
        return mTitle;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getUrl(){return mUrl;}
}
