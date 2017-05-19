package com.example.vibs.newsnow;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by vibhakar.sarswat on 5/19/2017.
 */

public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static String webTitle;
    private static String newsWebUrl;
    private static String sectionName;

    /**
     * Creating a private constructor to avoid object creation of this class.
     */
    private QueryUtils() {
    }

    /**
     * Query the GUARDIAN dataset and return an {@link News} ArrayList to represent a single news.
     */
    public static ArrayList<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request,", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<News> newses = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return newses;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /*
            If the request was successful (response code 200),
            then read the input stream and parse the response.
            */
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                //handle exception
            }
        } catch (IOException e) {
            //handle exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractFeatureFromJson(String jsonResponse) {

        // Create an empty ArrayList that we can start adding newses to
        ArrayList<News> newses = new ArrayList<>();

        /*
        Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        is formatted, a JSONException exception object will be thrown.
        Catch the exception so the app doesn't crash, and handle exception.
        */
        try {
            JSONObject news_json_response = new JSONObject(jsonResponse);
            if (news_json_response.has("response")) {
                JSONObject response = news_json_response.getJSONObject("response");
                if (response.has("results")) {
                    JSONArray resultsArray = response.getJSONArray("results");


                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject resultDetails = resultsArray.getJSONObject(i);
                        if (resultDetails.has("webTitle")) {
                            webTitle = resultDetails.getString("webTitle");
                            sectionName = resultDetails.getString("sectionName");
                        }
                        if (resultDetails.has("webUrl")) {
                            newsWebUrl = resultDetails.getString("webUrl");
                        }
                        newses.add(new News(webTitle, sectionName, newsWebUrl));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        }

        // Return the list of newses
        return newses;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        return url;
    }
}
