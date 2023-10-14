package com.forever.whatsappstatussaver;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Fragment.HomeFragment;
import com.forever.whatsappstatussaver.Fragment.PermisionFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION = 101;
    TabLayout tabLayout;
    ViewPager viewPager;
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    viewpagerAdapter viewpagerAdapter;
    FloatingActionButton btnRefresh;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRefresh=findViewById(R.id.btn_refresh);

        requestPermistion();
        showPermissonFrag();



           /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && isAllFilePermiossionEnable()) {
            {
                showHomeFrag();
            }
        } else {
                requestPermistion();
                showPermissonFrag();

        }*/


    }
    public boolean isAllFilePermiossionEnable()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.isExternalStorageManager()) {
               return true;
            }
            else {
                return false;
            }
        }
        return true;
    }
    public void showPermissonFrag()
    {
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        Fragment PermisionFrag=new PermisionFragment();
        ft.replace(R.id.container,PermisionFrag);
        ft.commit();
        Log.d(TAG, "showPermissonFrag: ");
    }


    public void requestPermistion()
    {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_STORAGE);
    }


}