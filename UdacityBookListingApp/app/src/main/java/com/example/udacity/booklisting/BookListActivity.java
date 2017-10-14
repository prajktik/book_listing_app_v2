package com.example.udacity.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<Book>>{

    public static final String LOG_TAG = BookListActivity.class.getName();
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int BOOK_LOADER_ID = 1;
    private static final String BUNDLE_KEY = "prev_key";

    private String mSearchKey;
    private SearchView mSearchView;
    private ListView mBookListView;

    private RecyclerView mRecyclerView;

    private BookAdapter mAdapter;
    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;
    private boolean mIsSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        if(savedInstanceState != null){
            mSearchKey = savedInstanceState.getString(BUNDLE_KEY);
            mIsSavedState = true;
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        if(mIsSavedState){
            initiateSearch(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        if(mSearchView != null){
            String query = mSearchView.getQuery().toString();
            mSearchKey = query;
        }

        outState.putString(BUNDLE_KEY, mSearchKey);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getColor(android.R.color.black));
        searchEditText.setHintTextColor(getColor(android.R.color.white));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                mSearchKey = query;
                initiateSearch(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                //Do Nothing
                return false;
            }
        });

        if (mSearchKey != null && !mSearchKey.isEmpty()) {
            searchItem.expandActionView();
            mSearchView.setQuery(mSearchKey, false);
            mSearchView.clearFocus();
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void initiateSearch(boolean isNewSearch){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            LoaderManager loaderManager = getLoaderManager();
           if(isNewSearch){
                loaderManager.restartLoader(BOOK_LOADER_ID, null, BookListActivity.this);
            }else{
                loaderManager.initLoader(BOOK_LOADER_ID, null, BookListActivity.this);
            }
        }else{
            mLoadingIndicator.setVisibility(View.GONE);
            mAdapter.clear();
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle){

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        return new BookLoader(this, GOOGLE_BOOKS_URL, mSearchKey);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books){

        mLoadingIndicator.setVisibility(View.GONE);
        mAdapter.clear();
        if(books != null && !books.isEmpty()){
           mAdapter.addAll(books);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.empty_view);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader){

        mAdapter.clear();
    }
}
