package com.example.vibs.newsnow;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by vibhakar.sarswat on 5/19/2017.
 */

public class NewsLoader extends AsyncTaskLoader {

    // URL to fetch results
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Performing the network request to fetch a list of news.
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
