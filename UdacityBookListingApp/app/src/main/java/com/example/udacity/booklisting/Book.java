package com.example.udacity.booklisting;


public final class Book{

    String mThumbnail;
    String mTitle;
    String mAuthor;
    String mUrl;

    public Book(String thumbnail, String title, String author, String url){
        mThumbnail = thumbnail;
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public String getUrl(){
        return mUrl;
    }
}
