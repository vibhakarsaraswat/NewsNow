package com.example.vibs.newsnow;

/**
 * Created by vibhakar.sarswat on 5/19/2017.
 */

public class News {
    private String mTitle;
    private String mSectionName;
    private String mWebUrl;

    public News(String title, String sectionName, String webUrl) {
        mTitle = title;
        mSectionName = sectionName;
        mWebUrl = webUrl;
    }

    public String getTitle() { return mTitle; }
    public String getSectionName() { return mSectionName; }
    public String getWebUrl() { return mWebUrl; }

}
