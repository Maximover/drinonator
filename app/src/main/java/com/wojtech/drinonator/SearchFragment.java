package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
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

public class SearchFragment extends Fragment {
    private final CountDownLatch latch = new CountDownLatch(1);

    public SearchFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert container != null;
        container.removeAllViews();

        // scrollable layout setup
        ScrollView scroll_view = new ScrollView(this.getContext());
//        scroll_view.setOnScrollChangeListener(
//                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//                    TranslateAnimation animate;
//                    if(scrollY-oldScrollY < 0){
//                        // show animation
//                        animate = new TranslateAnimation(
//                                0,
//                                0,
//                                ed.getHeight(),
//                                0);
//                        animate.setDuration(500);
//                        animate.setFillAfter(true);
//                        ed.startAnimation(animate);
//                    }else if (scrollY-oldScrollY > 0){
//                        // hide animation
//                        animate = new TranslateAnimation(
//                                0,
//                                0,
//                                0,
//                                ed.getHeight());
//                        animate.setDuration(500);
//                        animate.setFillAfter(true);
//                        ed.startAnimation(animate);
//                    }
//
//                });
        LinearLayout main_layout = new LinearLayout(this.getContext());
        main_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_view.addView(main_layout);
        container.addView(scroll_view);
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
                main_layout.addView(entry_layout);
            }
            // remove loading indicator ???
        }catch(Exception e){
            Toast.makeText(this.getContext(), getString(R.string.error_json_parse), Toast.LENGTH_LONG).show();
            // change loading indicator to error indicator ???
        }finally {
            executor.shutdown();
        }

        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}