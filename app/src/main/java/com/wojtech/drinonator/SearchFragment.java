package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.CountDownLatch;

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

        TextView tv = new TextView(getContext());
        tv.setTextColor(getResources().getColor(R.color.white, null));
        tv.setText("sample text");
        container.addView(tv);
        try{
            ImageView iv = new ImageView(getContext());
            WebImageRenderer renderer = new WebImageRenderer(getActivity(), "https://www.thecocktaildb.com/images/media/drink/yyrwty1468877498.jpg", iv);
            Thread renderer_thread = new Thread(renderer);
            renderer_thread.start();
            container.addView(iv);
        } catch (Exception e){
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}