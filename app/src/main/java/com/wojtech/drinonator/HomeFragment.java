package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private final CountDownLatch latch = new CountDownLatch(1);

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert container != null;
        container.removeAllViews();
        // Inflate the layout for this fragment
        // add loading indicator ???
        // await for api response
        ApiHandler api = new ApiHandler(ApiHandler.SEARCH_ALCOHOLIC, null, latch);
        try {
            Thread api_thread = new Thread(api);
            api_thread.start();
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
        // scrollable layout setup
        ScrollView scroll_view = new ScrollView(this.getContext());
        LinearLayout main_layout = new LinearLayout(this.getContext());
        main_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_view.addView(main_layout);
        container.addView(scroll_view);
        // render elements
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            JSONArray drinks = api.getDrinkData().getJSONArray("drinks");
            for(int i=0; i<drinks.length();i++) {
                JSONObject drink = drinks.getJSONObject(i);
                LinearLayout entry_layout = new LinearLayout(getContext());
                entry_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView tv = new TextView(getContext());
                tv.setText(drink.get("strDrink").toString());
                tv.setTextColor(getResources().getColor(R.color.white, null));
                entry_layout.addView(tv);
                ImageView thumbnail = new ImageView(getContext());
                WebImageRenderer renderer = new WebImageRenderer(getActivity(), drink.get("strDrinkThumb").toString(), thumbnail);
                executor.execute(renderer);
                entry_layout.addView(thumbnail);
                main_layout.addView(entry_layout);
            }
            // remove loading indicator ???
        }catch(Exception e){
            Toast.makeText(this.getContext(), getString(R.string.error_json_parse), Toast.LENGTH_LONG).show();
            // change loading indicator to error indicator ???
        }finally {
            executor.shutdown();
        }

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}