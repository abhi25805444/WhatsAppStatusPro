package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class imagelistFragment extends Fragment {
    RecyclerView imageRecyclerView;
    int sizeofArray;
    ArrayList<File> arrayofImages = new ArrayList<File>();
    FloatingActionButton btnRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_imagelist, container, false);
        View view=root.findViewById(R.id.emptyviewofimage);
        btnRefresh=root.findViewById(R.id.btn_refresh);
        sizeofArray=retriveImageFromStorage().size();

        imageRecyclerView=root.findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        ImageRecyclerViewAdapter imageRecyclerViewAdapter=new ImageRecyclerViewAdapter(getActivity(),retriveImageFromStorage(),false,fragmentTransaction,getActivity());
        imageRecyclerView.setAdapter(imageRecyclerViewAdapter);


        if(imageRecyclerViewAdapter.getItemCount()==0)
        {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retriveImageFromStorage();
                if (sizeofArray!=retriveImageFromStorage().size())
                {
                    if(imageRecyclerViewAdapter.getItemCount()==0)
                    {
                        view.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.setVisibility(View.GONE);
                    }
                    imageRecyclerViewAdapter.notifyDataChanges();
                }


            }
        });
        return root;

    }
    private ArrayList<File> retriveImageFromStorage() {
        String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
        Log.d(TAG, "listMediaFiles: " + directoryPath);
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            FilenameFilter mediaFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    return lowercaseName.endsWith(".jpg") ||
                            lowercaseName.endsWith(".jpeg") ||
                            lowercaseName.endsWith(".png");
                }
            };

            File[] mediaFiles = directory.listFiles(mediaFilter);

            if (mediaFiles != null) {
                for (File file : mediaFiles) {
                    if(!arrayofImages.contains(file))
                    {
                        arrayofImages.add(file);
                    }

                }
                Log.d(TAG, "listMediaFiles: " + arrayofImages);
            }
        } else {
            Toast.makeText(getActivity(), "getting Error To Retrive ", Toast.LENGTH_SHORT).show();
        }
        return arrayofImages;
    }
}