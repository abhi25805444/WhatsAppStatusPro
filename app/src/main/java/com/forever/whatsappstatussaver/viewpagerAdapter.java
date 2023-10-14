package com.forever.whatsappstatussaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.forever.whatsappstatussaver.Fragment.imagelistFragment;
import com.forever.whatsappstatussaver.Fragment.videolistFragment;

public class viewpagerAdapter extends FragmentPagerAdapter {
    public viewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            return new imagelistFragment();
        } else {
            return new videolistFragment();
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
        {
           return "Images";
        } else {
            return "Videos";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
