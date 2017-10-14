package com.example.udacity.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

class BookLoader extends AsyncTaskLoader<List<Book>>{

    private String mBaseUrl;
    private String mSearchKey;


    public BookLoader(Context context, String baseUrl, String searchKey){

        super(context);
        mBaseUrl = baseUrl;
        mSearchKey = searchKey;
    }

    @Override
    protected void onStartLoading(){

        forceLoad();
    }

    @Override
    public List<Book> loadInBackground(){

        return QueryUtils.fetchBookListing(mBaseUrl, mSearchKey);

    }
}
