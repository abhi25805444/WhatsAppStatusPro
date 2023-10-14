package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.forever.whatsappstatussaver.R;


public class PermisionFragment extends Fragment {
    Button btnPermision;
    private static final int REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION = 101;
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && isAllFilePermiossionEnable()) {

            showHomeFrag();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View root= inflater.inflate(R.layout.fragment_permision, container, false);
       btnPermision=root.findViewById(R.id.btnpermision);
       btnPermision.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && isAllFilePermiossionEnable()) {

                       showHomeFrag();

               } else {
                   if(!isAllFilePermiossionEnable())
                   {
                       requestAllFilesAccessPermission();
                   }
                   requestPermistion();
               }

           }
       });

       return root;
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


    public void showHomeFrag()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        Fragment Homefrag=new HomeFragment();
        ft.replace(R.id.container,Homefrag);
        ft.commit();
        Log.d(TAG, "showHomeFrag: ");
    }
    public void requestPermistion()
    {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_STORAGE);
    }
    public void requestAllFilesAccessPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            }
        }
    }
}