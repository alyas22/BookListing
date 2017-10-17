package com.example.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String book_url = " https://www.googleapis.com/books/v1/volumes?q=search+";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ListView mListView;
    private  adapter mAdapter;
    ImageButton mSearchButton;
    ImageView mNoConnection;
    EditText mEditText;
    ProgressBar mProgressBar;
    private TextView mEmptyStateTextView, mTextProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "TEST MainActivity onCreat()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mNoConnection = (ImageView) findViewById(R.id.no_connection);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mEditText = (EditText) findViewById(R.id.edit_view);
        mTextProgress = (TextView) findViewById(R.id.myTextProgress);
        mListView = (ListView) findViewById(R.id.book_list_item);

        mAdapter = new adapter(this, new ArrayList<Book>());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Book currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                 mAdapter.clear();
                if (networkInfo != null && networkInfo.isConnected()){
                    String text = mEditText.getText().toString();
                    if (text.trim().matches("")) {
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        mEmptyStateTextView.setText(R.string.no_enter);
                }else{
                        mEmptyStateTextView.setVisibility(View.INVISIBLE);
                        String updateURL = text.trim().replaceAll("\\s+", "+");
                        String newUrl = book_url + updateURL;

                         BookAsyncTask task = new BookAsyncTask();
                         task.execute(newUrl);}}
                else {
                    mNoConnection.setVisibility(View.VISIBLE);
                    mNoConnection.setImageResource(R.drawable.no_connection);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_connection);
                }}
        });
    }
    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNoConnection.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTextProgress.setVisibility(View.VISIBLE);
            mTextProgress.setText(R.string.wait);
        }

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                    return null;
                }
                ArrayList<Book> result = Utils.fetchBookData(urls[0]);
                return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
           super.onPostExecute(books);

            mProgressBar.setVisibility(View.INVISIBLE);
            mTextProgress.setVisibility(View.INVISIBLE);
            mNoConnection.setVisibility(View.INVISIBLE);
            mAdapter.clear();
               if (books != null && !books.isEmpty()) {
                   mAdapter.addAll(books);

            } else {
                   mEmptyStateTextView.setVisibility(View.VISIBLE);
                   mEmptyStateTextView.setText(R.string.no_books);
               }
            }}

}


