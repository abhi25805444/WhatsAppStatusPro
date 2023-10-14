package com.forever.whatsappstatussaver.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.viewpagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class HomeFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    viewpagerAdapter viewpagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = root.findViewById(R.id.tabLayout);

        viewPager = root.findViewById(R.id.viewPager);
        viewpagerAdapter = new viewpagerAdapter(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return root;
    }
}