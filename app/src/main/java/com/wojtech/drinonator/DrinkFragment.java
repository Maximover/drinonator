package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrinkFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DRINK = "drink";

    private JSONObject drink;
    private boolean parse_failed = false;

    public DrinkFragment() {
        // Required empty public constructor
    }

    /**
     * Get fragment with drink details
     *
     * @param drink json object with drink data, <br> random drink if null is provided
     * @return A new instance of fragment DetailsFragment.
     */
    public static DrinkFragment newInstance(@Nullable JSONObject drink) {
        DrinkFragment fragment = new DrinkFragment();
        Bundle args = new Bundle();
        if(drink != null) args.putString(DRINK, drink.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString(DRINK, null) != null) {
            try {
                drink = new JSONObject(getArguments().getString(DRINK));
            }catch (Exception e){
                parse_failed = true;
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert container != null;
        container.removeAllViews();
        // Inflate the layout for this fragment
        // add loading indicator ???

        // await for api response
        if(drink == null && !parse_failed) {
            CountDownLatch latch = new CountDownLatch(1);
            ExecutorService main_executor = Executors.newSingleThreadExecutor();
            ApiHandler api = new ApiHandler(ApiHandler.GET_RANDOM_DRINK, null, latch);
            try {
                main_executor.execute(api);
                latch.await();
                drink = api.getDrinkData().getJSONArray("drinks").getJSONObject(0);
            } catch (Exception e) {
                e.printStackTrace();
                drink = null;
            } finally {
                main_executor.shutdown();
            }
        }
        // layout setup
        ScrollView scroll_view = new ScrollView(this.getContext());
        container.addView(scroll_view);
        LinearLayout main_layout = new LinearLayout(this.getContext());
        main_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_view.addView(main_layout);
        if(!parse_failed) {
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
            if(getParentFragmentManager().getBackStackEntryCount() > 0) {
                Button back_button = new Button(getContext());
                back_button.setText("Back");
                back_button.setOnClickListener(v -> {
                    getParentFragmentManager().popBackStackImmediate();
                });
                main_layout.addView(back_button);
            }
            // render elements
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                assert drink != null;
                WebImageRenderer image_renderer = new WebImageRenderer(getActivity(), drink.getString("strDrinkThumb"), thumbnail_view);
                executor.execute(image_renderer);
                name_view.setText(drink.getString("strDrink"));
                category_view.setText(drink.getString("strCategory"));
                // remove loading indicator ???
            } catch (Exception e) {
                Toast.makeText(this.getContext(), getString(R.string.error_fetch_api), Toast.LENGTH_LONG).show();
                // change loading indicator to error indicator ???
            } finally {
                executor.shutdown();
            }
        } else {
            TextView no_results_view = new TextView(getContext());
            no_results_view.setTextSize(20);
            no_results_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            no_results_view.setTextColor(getResources().getColor(R.color.light_gray, null));
            no_results_view.setText(getString(R.string.error_json_parse));
            main_layout.addView(no_results_view);
        }
        return inflater.inflate(R.layout.fragment_drink, container, false);
    }
}