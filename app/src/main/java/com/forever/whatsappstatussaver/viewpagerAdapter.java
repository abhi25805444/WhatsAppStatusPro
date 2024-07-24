package com.forever.whatsappstatussaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.forever.whatsappstatussaver.Fragment.imagelistFragment;
import com.forever.whatsappstatussaver.Fragment.videolistFragment;

public class viewpagerAdapter extends FragmentPagerAdapter {


    Fragment currentFrag;
    public viewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            currentFrag=new imagelistFragment();
            return currentFrag;
        } else {
            currentFrag=new videolistFragment();
            return currentFrag;
        }

    }

    public Fragment getCurrentFrag()
    {
        return currentFrag;
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
