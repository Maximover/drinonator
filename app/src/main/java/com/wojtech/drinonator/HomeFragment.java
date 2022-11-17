package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
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
        ExecutorService main_executor = Executors.newSingleThreadExecutor();
        ApiHandler api = new ApiHandler(ApiHandler.SEARCH_ALCOHOLIC, "alcoholic", latch);
        try {
            main_executor.execute(api);
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            main_executor.shutdown();
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
                entry_layout.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);
                entry_layout.setPadding(0, 5, 0, 5);

                ImageView thumbnail = new ImageView(getContext());
                thumbnail.setMinimumWidth(300);
                thumbnail.setMinimumHeight(300);
                thumbnail.setPadding(10,0, 10, 0);
                WebImageRenderer renderer = new WebImageRenderer(getActivity(), drink.get("strDrinkThumb").toString(), thumbnail);
                executor.execute(renderer);
                entry_layout.addView(thumbnail);

                LinearLayout desc_layout = new LinearLayout(getContext());
                desc_layout.setOrientation(LinearLayout.VERTICAL);
                TextView name = new TextView(getContext());
                name.setText(drink.get("strDrink").toString());
                name.setTextColor(getResources().getColor(R.color.white, null));
                TextView desc = new TextView(getContext());
                desc.setText(drink.get("strDrink").toString());
                desc.setTextColor(getResources().getColor(R.color.white, null));
                desc_layout.addView(name);
                desc_layout.addView(desc);
                entry_layout.addView(desc_layout);
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