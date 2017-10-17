package com.example.android.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
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
 * Created by Toshiba on 18/08/17.
 */

public class Utils {
    public static String LOG_TAG = Utils.class.getSimpleName();

    private static ArrayList<Book> extractFromJson(String bookJSON) {
        Bitmap bitmap = null;
        ArrayList<Book> book_list = new ArrayList<>();

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        try {

            JSONObject baseJsonResponse = new JSONObject(bookJSON);
          int totalItems = baseJsonResponse.getInt("totalItems");
            if (totalItems == 0) {
                return null;
            }

                JSONArray bookArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < bookArray.length(); i++) {

                    JSONObject book_item = bookArray.getJSONObject(i);
                    JSONObject volume = book_item.getJSONObject("volumeInfo");
                    String title = volume.getString("title");

                    JSONObject imageLinks = volume.optJSONObject("imageLinks");
                    if (imageLinks != null) {
                        String imageUrl = imageLinks.getString("thumbnail");
                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
                    }

                    JSONArray authorsArray = volume.getJSONArray("authors");
                    String allAuthors = allAuthors(authorsArray);

                    String url = volume.getString("infoLink");

                    Book book = new Book(title, allAuthors,bitmap,url );
                    book_list.add(book);
                }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book_list;
    }

    public static ArrayList<Book> fetchBookData(String textEntered) {

        URL url = createURL(textEntered);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<Book> books = extractFromJson(jsonResponse);
        return books;
    }

    private static URL createURL(String stringUrl) {

        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
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

    private static String allAuthors(JSONArray authorsArray) throws JSONException {

        String authorsList = "";
        if (authorsArray.length() == 0)
            authorsList = "Unknown";
        for (int i = 0; i < authorsArray.length(); i++) {
            String author = authorsArray.getString(i);
            if (i == 0)
                authorsList = author;
            else
                authorsList = authorsList + ", " + author;
        }
        return authorsList;
    }

}
