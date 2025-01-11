package com.forever.whatsappstatussaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.forever.whatsappstatussaver.Fragment.ImagelistFragment;
import com.forever.whatsappstatussaver.Fragment.VideolistFragment;

public class ViewpagerAdapter extends FragmentPagerAdapter {


    Fragment currentFrag;
    public ViewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            currentFrag=new ImagelistFragment();
            return currentFrag;
        } else {
            currentFrag=new VideolistFragment();
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
           return "Photos";
        } else {
            return "Videos";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
