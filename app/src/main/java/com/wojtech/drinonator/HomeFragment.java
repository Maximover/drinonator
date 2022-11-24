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
import java.util.concurrent.Executor;
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
        ApiHandler api = new ApiHandler(ApiHandler.GET_RANDOM_DRINK, null, latch);
        try {
            main_executor.execute(api);
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            main_executor.shutdown();
        }
        // layout setup
        ScrollView scroll_view = new ScrollView(this.getContext());
        container.addView(scroll_view);
        LinearLayout main_layout = new LinearLayout(this.getContext());
        main_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_view.addView(main_layout);
        TextView category_view = new TextView(getContext());
        category_view.setPadding(0, 40, 0, 40);
        category_view.setTextColor(getResources().getColor(R.color.light_gray, null));
        category_view.setTextSize(20);
        category_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        main_layout.addView(category_view);
        ImageView thumbnail_view = new ImageView(getContext());
        thumbnail_view.setMinimumWidth(260);
        thumbnail_view.setMinimumHeight(260);
        main_layout.addView(thumbnail_view);
        TextView name_view = new TextView(getContext());
        name_view.setPadding(0, 20, 0, 20);
        name_view.setTextColor(getResources().getColor(R.color.white, null));
        name_view.setTextSize(30);
        name_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        main_layout.addView(name_view);
        // render elements
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            JSONObject drink = api.getDrinkData().getJSONArray("drinks").getJSONObject(0);
            WebImageRenderer image_renderer = new WebImageRenderer(getActivity(), drink.getString("strDrinkThumb"), thumbnail_view);
            executor.execute(image_renderer);
            name_view.setText(drink.getString("strDrink"));
            category_view.setText(drink.getString("strCategory"));
            // remove loading indicator ???
        } catch(Exception e){
            Toast.makeText(this.getContext(), getString(R.string.error_json_parse), Toast.LENGTH_LONG).show();
            // change loading indicator to error indicator ???
        } finally {
            executor.shutdown();
        }

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}