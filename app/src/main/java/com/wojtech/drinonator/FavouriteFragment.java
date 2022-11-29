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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouriteFragment extends Fragment {

    public FavouriteFragment() {
        // Required empty public constructor
    }

    /**
     * Get fragment with drinks added as favourite
     *
     * @return A new instance of fragment FavouriteFragment.
     */
    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();
        Bundle args = new Bundle();
        //
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert container != null;
        container.removeAllViews();

        LinearLayout content_layout = new LinearLayout(this.getContext());
        content_layout.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll_view = new ScrollView(getContext());
        scroll_view.addView(content_layout);
        container.addView(scroll_view);
        content_layout.removeAllViews();
        Database db = new Database(getContext());
        ArrayList<HashMap<String, String>> drinks = db.getFavourites();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try{
            for(HashMap<String, String> drink : drinks) {
                LinearLayout entry_layout = new LinearLayout(getContext());
                entry_layout.setOrientation(LinearLayout.HORIZONTAL);
                entry_layout.setVerticalGravity(View.TEXT_ALIGNMENT_CENTER);
                entry_layout.setPadding(0, 5, 0, 5);
                ImageView thumbnail = new ImageView(getContext());
                thumbnail.setMinimumWidth(300);
                thumbnail.setMinimumHeight(300);
                thumbnail.setPadding(10,0, 10, 0);
                WebImageRenderer renderer = new WebImageRenderer(getActivity(), drink.get(Database.FAVOURITES_THUMBNAIL_URL), thumbnail);
                executor.execute(renderer);
                entry_layout.addView(thumbnail);

                LinearLayout desc_layout = new LinearLayout(getContext());
                desc_layout.setOrientation(LinearLayout.VERTICAL);
                TextView name = new TextView(getContext());
                name.setText(drink.get(Database.FAVOURITES_NAME));
                name.setTextColor(getResources().getColor(R.color.white, null));
                desc_layout.addView(name);
                TextView desc = new TextView(getContext());
                desc.setText(drink.get(Database.FAVOURITES_CATEGORY));
                desc.setTextColor(getResources().getColor(R.color.light_gray, null));
                desc_layout.addView(desc);
                entry_layout.addView(desc_layout);
                entry_layout.setOnClickListener(v -> getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, DrinkFragment.newInstance(db.getDrink(Integer.parseInt(Objects.requireNonNull(drink.get(Database.FAVOURITES_ID))))), "DrinkDetailsFragment")
                        .addToBackStack("DrinkDetailsFragment")
                        .commit()
                );
                content_layout.addView(entry_layout);
            }
        } catch(Exception e){
            e.printStackTrace();
            TextView no_results_view = new TextView(getContext());
            no_results_view.setTextSize(20);
            no_results_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            no_results_view.setTextColor(getResources().getColor(R.color.light_gray, null));
            no_results_view.setText(getString(R.string.error_no_results));
            content_layout.addView(no_results_view);
        } finally {
            executor.shutdown();
            db.close();
        }


        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }
}