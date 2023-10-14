package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.Adapters.VideoRecylerviewAdapter;
import com.forever.whatsappstatussaver.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class videolistFragment extends Fragment {

 RecyclerView recyclerView;

 ArrayList<File>arrayofVideo=new ArrayList<>();
 FloatingActionButton btnRefresh;
 int sizeofArray;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_videolist, container, false);
        sizeofArray=retriveVideoeFromStorage().size();

        View view=root.findViewById(R.id.emptyviewofvideo);
        recyclerView=root.findViewById(R.id.videoRecyclerView);
        btnRefresh=root.findViewById(R.id.btn_refresh);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        VideoRecylerviewAdapter videoRecylerviewAdapter=new VideoRecylerviewAdapter(getActivity(),retriveVideoeFromStorage());
        recyclerView.setAdapter(videoRecylerviewAdapter);

        if(videoRecylerviewAdapter.getItemCount()==0)
        {
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retriveVideoeFromStorage();
                if(sizeofArray!=retriveVideoeFromStorage().size())
                {
                    if(videoRecylerviewAdapter.getItemCount()==0)
                    {
                        view.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.setVisibility(View.GONE);
                    }
                    videoRecylerviewAdapter.notifyDataChanges();
                    sizeofArray=retriveVideoeFromStorage().size();
                }
            }
        });



        return root;

    }
    private ArrayList<File> retriveVideoeFromStorage() {
        String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
        Log.d(TAG, "listMediaFiles: " + directoryPath);
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            FilenameFilter mediaFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    return lowercaseName.endsWith(".mp4");
                }
            };

            File[] mediaFiles = directory.listFiles(mediaFilter);

            if (mediaFiles != null) {
                for (File file : mediaFiles) {
                    if(!arrayofVideo.contains(file))
                    {
                        arrayofVideo.add(file);
                    }

                }
                Log.d(TAG, "listvideoFiles: " + arrayofVideo);
            }
        } else {
            Toast.makeText(getActivity(), "getting Error To Retrive ", Toast.LENGTH_SHORT).show();
        }
        return arrayofVideo;
    }
}