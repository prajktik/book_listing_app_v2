package com.example.udacity.booklisting;

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

import static com.example.udacity.booklisting.BookListActivity.LOG_TAG;


public final class QueryUtils{


    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int CONNECT_SUCCESS = 200;

    private QueryUtils(){
    }

    public static ArrayList<Book> fetchBookListing(String baseUrl, String searchKey){

        if(searchKey == null || searchKey.isEmpty()){
            return null;
        }

        URL url = createUrl(baseUrl, searchKey);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<Book> books = extractBooks(jsonResponse);
        return books;
    }

    private static URL createUrl(String baseUrl, String searchKey){
        URL url = null;
        searchKey = searchKey.replace(" ", "+");
        String query = baseUrl + searchKey;
        try{
            url = new URL(query);
        }catch(MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{

        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == CONNECT_SUCCESS){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG, "Problem retrieving the Book List JSON results.", e);
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                    .forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private static ArrayList<Book> extractBooks(String jSonResponse){

        ArrayList<Book> books = new ArrayList<>();
        try{

            JSONObject mainObj = new JSONObject(jSonResponse);
            JSONArray itemsArray = mainObj.getJSONArray("items");
            JSONObject item;
            JSONObject volumeInfo;
            String infoUrl;
            String title;
            JSONArray arrAuthors;
            StringBuilder builder;
            String authors = "";
            JSONObject imageLinks;
            String thumbnailId = "";

            Book book;

            if(itemsArray != null){

                for(int i = 0; i < itemsArray.length(); i++){

                    item = itemsArray.getJSONObject(i);
                    volumeInfo = item.getJSONObject("volumeInfo");
                    title = volumeInfo.getString("title");

                    if(volumeInfo.has("authors")){
                        arrAuthors = volumeInfo.getJSONArray("authors");
                        if(arrAuthors != null && arrAuthors.length() > 0){
                            builder = new StringBuilder(arrAuthors.getString(0));
                            for(int j = 1; j < arrAuthors.length(); j++){
                                builder.append(", ");
                                builder.append(arrAuthors.getString(j));
                            }
                            authors = builder.toString();
                        }
                    }
                    if(volumeInfo.has("imageLinks")){
                        imageLinks = volumeInfo.getJSONObject("imageLinks");
                        thumbnailId = imageLinks.getString("thumbnail");
                    }
                    infoUrl = volumeInfo.getString("infoLink");
                    book = new Book(thumbnailId, title, authors, infoUrl);
                    books.add(book);
                }
            }

        }catch(JSONException e){
            Log.e(LOG_TAG, "Problem parsing the book search JSON results", e);
        }

        return books;
    }

}
