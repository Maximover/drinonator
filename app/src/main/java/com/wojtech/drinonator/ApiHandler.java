package com.wojtech.drinonator;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ApiHandler implements Runnable{
    private final String API_KEY = "1";
    private final String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/"+API_KEY+"/";
    public static final int GET_RANDOM_DRINK = 0;
    public static final int SEARCH_DRINK_BY_NAME = 1;
    public static final int SEARCH_ALCOHOLIC = 2;

    private boolean failed = false;
    private URL api_url;
    private JSONObject drink_data = null;
    private final CountDownLatch latch;

    /**
     *
     * @param type type of api method
     * @param argument argument to be passed to api
     * @param latch a CountDownLatch that will notify calling thread after fetching data from API
     */
    public ApiHandler(int type, @Nullable String argument, CountDownLatch latch){
        this.latch = latch;
        try {
            String url;
            switch (type){
                case GET_RANDOM_DRINK:
                    url = BASE_URL+"/random.php";
                    this.api_url = new URL(url);
                    break;
                case SEARCH_DRINK_BY_NAME:
                    assert argument != null;
                    url = BASE_URL+"/search.php?s=" + argument;
                    this.api_url = new URL(url);
                    break;
                case SEARCH_ALCOHOLIC:
                    url = BASE_URL+"/filter.php?a=Alcoholic";
                    this.api_url = new URL(url);
                    break;
            }
        }catch (Exception e){
            this.failed = true;
        }
    }

    /**
     * Fetch data from api and parse it to JSONObject
     * @return data fetched from api
     * @throws IOException failed preparation to fetch api
     * @throws JSONException failed to parse json
     */
    private JSONObject getData() throws IOException, JSONException {
        if(failed){
            throw new IOException();
        }
        Scanner scanner = new Scanner(api_url.openStream());
        JSONTokener json_encoder = new JSONTokener(scanner.useDelimiter("\\Z").next());
        JSONObject json = new JSONObject(json_encoder);
        scanner.close();
        return json;
    }

    /**
     * Return data fetched from api, null otherwise
     * @return json data
     */
    public JSONObject getDrinkData(){
        return this.drink_data;
    }

    @Override
    public void run() {
        try {
            this.drink_data = this.getData();
            // release calling thread lock
            latch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
