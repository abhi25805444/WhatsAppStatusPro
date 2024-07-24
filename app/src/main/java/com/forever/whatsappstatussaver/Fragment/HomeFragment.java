package com.forever.whatsappstatussaver.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.Interface.VideoRefreshInterface;
import com.forever.whatsappstatussaver.MainActivity;
import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.viewpagerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class HomeFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    viewpagerAdapter viewpagerAdapter;
    Spinner spinner;
    LinearLayout linearLayout;
    ImageView btnNoAds;
    FloatingActionButton btn_refresh;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    RefreshInterface refreshInterface;
    VideoRefreshInterface videoRefreshInterface;
    private int WHATSAPP=0;
    private  int WHATSAPPBUSINES=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.isFirsttime = false;
        spinner = root.findViewById(R.id.spinner);
        tabLayout = root.findViewById(R.id.tabLayout);
        linearLayout = root.findViewById(R.id.adView);
        btnNoAds = root.findViewById(R.id.noadsicon);
        btn_refresh = root.findViewById(R.id.btn_refresh);
        AdView adView = new AdView(getActivity());
        adView.setAdSize(getAdSize());
        adView.setAdUnitId(getString(R.string.banneradunit));
        linearLayout.removeAllViews();
        linearLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        char c = '1';
        Log.d(TAG, "onCreateView: Tag of frag " + getTag());


        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = viewpagerAdapter.getCurrentFrag();

                Log.d(TAG, "onClick: (((((((((((((((((( 1");
                if (refreshInterface != null) {
                    Log.d(TAG, "onClick: (((((((((((((((((( 1");
                    refreshInterface.onRefreshImage();
                }

                if (videoRefreshInterface != null) {
                    videoRefreshInterface.onRefreshVideo();
                }


            }
        });


        String[] options = {"WHATSAPP", "WHATSAPP BUSINESS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.plus);
        btnNoAds.startAnimation(animation);


        spinner.setAdapter(adapter);



        if (spinner!=null)
        {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==WHATSAPPBUSINES)
                    {
                        showBottomSheetDialog();
                        boolean isWhatsAppBusinessInstalled = isAppInstalled(getContext(), "com.whatsapp.w4b");
                        if (isWhatsAppBusinessInstalled) {

                            // WhatsApp Business is installed
                        } else {
                            Toast.makeText(getActivity(), "Please Install WhatsApp Business App ", Toast.LENGTH_SHORT).show();
                            spinner.setSelection(0);
                            // WhatsApp Business is not installed
                        }
                    } else if (position==WHATSAPP) {
                        
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(TAG, "onAdFailedToLoad: " + loadAdError.toString());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
        });
        viewPager = root.findViewById(R.id.viewPager);
        viewpagerAdapter = new viewpagerAdapter(getActivity().getSupportFragmentManager());

        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        /*if (viewpagerAdapter.getItem(1) instanceof imagelistFragment) {
            ((imagelistFragment) viewpagerAdapter.getItem(1)).setInterface(this);
        }*/
        // Inflate the layout for this fragment
        return root;
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.SheetDialog);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.wb_storage_permission, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior behavior = bottomSheetDialog.getBehavior();
        behavior.setDraggable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = linearLayout.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getActivity(), adWidth);
    }

    public void setRefreshInterface(RefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void setVideoRefreshInterface(VideoRefreshInterface videoRefreshInterface) {
        this.videoRefreshInterface = videoRefreshInterface;
    }
}
