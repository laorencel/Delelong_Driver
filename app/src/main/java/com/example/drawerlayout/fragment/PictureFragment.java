package com.example.drawerlayout.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drawerlayout.R;

/**
 * Created by CLF on 2016/8/4.
 */
public class PictureFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.picture, null);
        return view;
    }
}
