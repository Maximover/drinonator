package com.wojtech.drinonator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert container != null;
        container.removeAllViews();

        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }
}