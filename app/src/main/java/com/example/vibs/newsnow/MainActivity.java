package com.example.vibs.newsnow;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {
    private static final String GUARDIAN_API_URL = "http://content.guardianapis.com/search?";
    private static final int NEWS_LOADER_ID = 1;
    String searchQuery;
    ListView newsListView;
    TextView mEmptyStateTextView;
    NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting the view of the launch screen
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchBoxAfterClick);
        linearLayout.setVisibility(View.GONE);
        Button searchButtonBeforeFirstSearch = (Button) findViewById(R.id.buttonBeforeSearch);

        //Set click Listener on Search Button Click
        searchButtonBeforeFirstSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Find the edit text's actual text and make it compatible for a url search query
                String searchQueried = ((EditText) findViewById(R.id.editTextBeforeClick)).getText().toString();

                //Check if user input is empty or it contains some query text
                if (searchQueried.isEmpty()) {
                    searchQueriedIsEmpty();
                } else {
                    //setting the view of the launch screen
                    LinearLayout searchBoxBeforeClick = (LinearLayout) findViewById(R.id.searchBoxBeforeClick);
                    searchBoxBeforeClick.setVisibility(View.GONE);
                    LinearLayout searchBoxAfterClick = (LinearLayout) findViewById(R.id.searchBoxAfterClick);
                    searchBoxAfterClick.setVisibility(View.VISIBLE);
                    EditText editTextAfterClick = (EditText) findViewById(R.id.editTextAfterClick);
                    editTextAfterClick.setText(searchQueried);

                    //know what was the status of the app, i.e, was it the first click or not
                    int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK = 1;

                    //Handle the loader manager as per the the button and view selected
                    clickHandle(searchQueried, SEARCH_BEFORE_OR_AFTER_FIRST_CLICK);
                }

            }
        });

        //setting button click after first search
        Button searchButtonAfterFirstSearch = (Button) findViewById(R.id.buttonAfterSearch);

        //Set click Listener on Search Button Click
        searchButtonAfterFirstSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Find the edit text's actual text and make it compatible for a url search query
                String searchQueried = ((EditText) findViewById(R.id.editTextAfterClick)).getText().toString();

                //Check if user input is empty or it contains some query text
                if (searchQueried.isEmpty()) {
                    searchQueriedIsEmpty();
                } else {
                    //know what was the status of the app, i.e, was it the second or later click or not
                    int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK = 2;
                    clickHandle(searchQueried, SEARCH_BEFORE_OR_AFTER_FIRST_CLICK);
                }
            }
        });
    }

    private void searchQueriedIsEmpty() {
        Context context = getApplicationContext();
        String text = "Nothing Entered in Search";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void clickHandle(String searchQueried, int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK) {
        TextView searchQueriedFor = (TextView) findViewById(R.id.searchQueriedFor);
        searchQueriedFor.setText(searchQueried);
        searchQuery = searchQueried.replace(" ", "%20");

        //First of all check if network is connected or not then only start the laoder
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

          /* fetch data. Get a reference to the LoaderManager, in order to interact with loaders. */
            if (SEARCH_BEFORE_OR_AFTER_FIRST_CLICK == 1) {
                startLoaderManager();
            } else {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                reStartLoaderManager();
            }

        } else {
            // display error
            setEmptyView();
        }
    }

    private void setEmptyView() {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // display error
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mEmptyStateTextView.setText(R.string.no_internet);
    }


    private void startLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    private void reStartLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(GUARDIAN_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("api-key", "0a397f99-4b95-416f-9c51-34c711f0069a");
        uriBuilder.appendQueryParameter("show-tags", "contributor");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, final ArrayList<News> newses) {
        /* Clear the search box*/
        EditText editText = (EditText) findViewById(R.id.editTextAfterClick);
        EditText editText1 = (EditText) findViewById(R.id.editTextBeforeClick);
        editText.setText("");
        editText1.setText("");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        /*
       If no news is found then simply show an empty view
        */

        if (newses.isEmpty()) {
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_news);
            return;
        }
        // Create a new {@link ArrayAdapter} of news
        newsAdapter = new NewsAdapter(MainActivity.this, newses);

        // Find a reference to the {@link ListView} in the layout
        ListView news_list_view = (ListView) findViewById(R.id.news_list_view);
        /*
        Set the adapter on the {@link ListView}
        so the list can be populated in the user interface
        */
        news_list_view.setAdapter(newsAdapter);

        news_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News news = newses.get(position);
                Intent goToUrl = new Intent(Intent.ACTION_VIEW);
                goToUrl.setData(Uri.parse(news.getWebUrl()));
                startActivity(goToUrl);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        newsAdapter.clear();
        newsListView.setVisibility(View.GONE);
    }
}