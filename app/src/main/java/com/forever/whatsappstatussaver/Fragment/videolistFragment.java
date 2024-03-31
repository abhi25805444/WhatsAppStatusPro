package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.Adapters.VideoRecylerviewAdapter;
import com.forever.whatsappstatussaver.Model.Status;
import com.forever.whatsappstatussaver.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class videolistFragment extends Fragment {

 RecyclerView recyclerView;

 ArrayList<File>arrayofVideo=new ArrayList<>();
 FloatingActionButton btnRefresh;
 int sizeofArray;

    ArrayList<DocumentFile> ar = new ArrayList();
    VideoRecylerviewAdapter videoRecylerviewAdapter;
    View view;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_videolist, container, false);


        view=root.findViewById(R.id.emptyviewofvideo);
        recyclerView=root.findViewById(R.id.videoRecyclerView);
        btnRefresh=root.findViewById(R.id.btn_refresh);
        progressBar=root.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        new MyAsyncTask().execute();

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

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ar.clear();
            ar = executeNew();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            videoRecylerviewAdapter=new VideoRecylerviewAdapter(getActivity(),ar);
            recyclerView.setAdapter(videoRecylerviewAdapter);
            sizeofArray=executeNew().size();

            if(videoRecylerviewAdapter.getItemCount()==0)
            {
                view.setVisibility(View.VISIBLE);
            }
            else {
                view.setVisibility(View.GONE);
            }

        }
    }

    private ArrayList<DocumentFile> executeNew() {

        final ArrayList<DocumentFile> imagesList = new ArrayList<>();

        List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

        DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());


        DocumentFile[] statusFiles = file.listFiles();


        for (DocumentFile documentFile : statusFiles) {

            if(documentFile!=null)
            {
                Log.d(TAG, "executeNew: file name " + documentFile.getName());

                if(isVideo(documentFile,getContext()))
                {
                    imagesList.add(documentFile);
                }
                Log.d(TAG, "executeNew: file name " + documentFile.getName());
            }
        }
        return imagesList;
    }

    private static boolean isVideo(DocumentFile file, Context context) {
        ; // Replace YourApplication with your application class
        String mimeType = context.getContentResolver().getType(file.getUri());
        return mimeType != null && mimeType.startsWith("video/");
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