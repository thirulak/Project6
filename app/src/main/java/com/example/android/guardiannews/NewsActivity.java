package com.example.android.guardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsClass>> {

    // URL for article data from The Guardian
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?section=sport&from-date=2018-01-01&to-date=2018-12-31&show-tags=contributor&order-by=newest&q=Sport&api-key=";
    private static final String apiKey = BuildConfig.THE_GUARDIAN_API_KEY;
    private static final String REQUEST_URL = GUARDIAN_REQUEST_URL + apiKey;
    private static final int NEWS_LOADER_ID = 1;
    LoaderManager loaderManager;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ListView newsList = findViewById(R.id.list);
        //Create a new adapter that takes news as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsClass>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsList.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsList.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            loaderManager.initLoader(NEWS_LOADER_ID, null, NewsActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.indeterminateBar);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected News.

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current News that was clicked on
                NewsClass currentNews = mAdapter.getItem(position);
                mAdapter.getItem(position);
                assert currentNews != null;
                String cmp = currentNews.getUrl();
                // Create a new intent to view the URI
                String url = "https://www.theguardian.com/" + cmp;
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                websiteIntent.setData(Uri.parse(url));
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


    }

    @Override
    public Loader<List<NewsClass>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsClass>> loader, List<NewsClass> newsList) {
        // Hide progressbar because the data has been loaded
        View progress = findViewById(R.id.indeterminateBar);
        progress.setVisibility(View.GONE);

        // Set empty state text to display "No News found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous News data
        mAdapter.clear();
        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsClass>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}


