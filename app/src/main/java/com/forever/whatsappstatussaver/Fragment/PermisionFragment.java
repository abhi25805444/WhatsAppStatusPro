package com.forever.whatsappstatussaver.Fragment;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.service.controls.ControlsProviderService.TAG;

import static androidx.core.app.ActivityCompat.recreate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.forever.whatsappstatussaver.R;

import java.util.Objects;


public class PermisionFragment extends Fragment {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Button btnPermision;

    private static final int PERMISSION_REQUEST_STORAGE = 1;

    private static final int REQUEST_PERMISSIONS = 1234;

    Context context;

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

           // showHomeFrag();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissionQ() {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

        String scheme = uri.toString();
        scheme = scheme.replace("/root/", "/document/");
        scheme += "%3A" + startDir;

        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);


        activityResultLauncher.launch(intent);
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                ((ActivityManager) Objects.requireNonNull(getActivity().getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                getActivity().recreate();
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        View root = inflater.inflate(R.layout.fragment_permision, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

            // If Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionQ();
            }

            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        } else {
            showHomeFrag();
        }


        btnPermision = root.findViewById(R.id.btnpermision);
        btnPermision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

                    // If Android 10+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestPermissionQ();
                    }

                    requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                } else {
                    showHomeFrag();
                }

            }
        });

        return root;
    }



    public void showHomeFrag() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment Homefrag = new HomeFragment();
        ft.replace(R.id.container, Homefrag);
        ft.commit();
        Log.d(TAG, "showHomeFrag: ");
    }

    public void requestPermistion() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_STORAGE);
    }

    private boolean arePermissionDenied() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getActivity().getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permissions : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    assert data != null;

                    Log.d("HEY: ", data.getData().toString());

                    context.getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    showHomeFrag();

                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                }
            }
    );

}