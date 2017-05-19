package com.example.vibs.newsnow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vibhakar.sarswat on 5/19/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Creating 'ViewHolder' class to Hold the Views that are being used in 'getView' method below.
     * This is to save multiple 'findViewById' calls made by these Views and instead to call this
     * only once.
     */
    private static class ViewHolder {
        TextView newsTitle;
        TextView newsDescription;
        TextView newsUrl;
    }

    /**
     * Constructs a new {@link NewsAdapter}.
     */
    public NewsAdapter(Context context, ArrayList<News> newsDetails) {
        super(context, 0, newsDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News currentNews = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_list_item, parent, false);
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.newsDescription = (TextView) convertView.findViewById(R.id.news_description);
            viewHolder.newsUrl = (TextView) convertView.findViewById(R.id.news_url);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String newsTitle = currentNews.getTitle();
        String newsDescription = currentNews.getSectionName();
        String newsUrl = currentNews.getWebUrl();

        viewHolder.newsTitle.setText(newsTitle);
        viewHolder.newsDescription.setText(newsDescription);
        viewHolder.newsUrl.setText(newsUrl);

        return convertView;
    }
}
