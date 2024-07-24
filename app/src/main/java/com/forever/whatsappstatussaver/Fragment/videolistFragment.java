package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.Interface.VideoRefreshInterface;
import com.forever.whatsappstatussaver.Model.Status;
import com.forever.whatsappstatussaver.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class videolistFragment extends Fragment implements VideoRefreshInterface {

    RecyclerView recyclerView;
    ArrayList<File> arrayofVideo = new ArrayList<>();
    int sizeofArray;

    ArrayList<DocumentFile> ar = new ArrayList();
    VideoRecylerviewAdapter videoRecylerviewAdapter;
    View view;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_videolist, container, false);

        view = root.findViewById(R.id.emptyviewofvideo);
        recyclerView = root.findViewById(R.id.videoRecyclerView);
        progressBar = root.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.container);
        homeFragment.setVideoRefreshInterface(this);


        new MyAsyncTask().execute();

        return root;
    }

    @Override
    public void onRefreshVideo() {
        refreshVideoList();
    }


    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ar.clear();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                ar = executeNew();
            }else {
                ar = executeOld();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            progressBar.setVisibility(View.GONE);
            videoRecylerviewAdapter = new VideoRecylerviewAdapter(getActivity(), ar);
            recyclerView.setAdapter(videoRecylerviewAdapter);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                sizeofArray = executeNew().size();
            }else {
                sizeofArray = executeOld().size();
            }

            updateEmptyViewVisibility();
        }
    }

    private void refreshVideoList() {
        new AsyncTask<Void, Void, ArrayList<DocumentFile>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(recyclerView!=null)
                {
                    recyclerView.setVisibility(View.GONE);
                }
                if(progressBar!=null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected ArrayList<DocumentFile> doInBackground(Void... voids) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    return executeNew();
                }else {
                    return executeOld();
                }

            }

            @Override
            protected void onPostExecute(ArrayList<DocumentFile> newAr) {
                super.onPostExecute(newAr);
                progressBar.setVisibility(View.GONE);
                if(recyclerView!=null)
                {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (newAr != null) {
                    if (!areArrayListsEqual(ar, newAr)) { // Check if the new list is different
                        ar.clear();
                        ar.addAll(newAr);
                        videoRecylerviewAdapter.notifyDataSetChanged();
                        sizeofArray = ar.size();
                        updateEmptyViewVisibility();
                    } else {

                    }
                }
            }
        }.execute();
    }

    // Method to compare two ArrayLists for equality
    private boolean areArrayListsEqual(ArrayList<DocumentFile> list1, ArrayList<DocumentFile> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<DocumentFile> executeNew() {
        final ArrayList<DocumentFile> videosList = new ArrayList<>();
        List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();
        DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());
        DocumentFile[] statusFiles = file.listFiles();
        for (DocumentFile documentFile : statusFiles) {
            if (documentFile != null && isVideo(documentFile, getContext())) {
                videosList.add(documentFile);
            }
        }
        return videosList;
    }

    private ArrayList<DocumentFile> executeOld() {

        final ArrayList<androidx.documentfile.provider.DocumentFile> imagesList = new ArrayList<>();

        File[] statusFiles;
        statusFiles = new File(Environment.getExternalStorageDirectory() +
                File.separator + "WhatsApp/Media/.Statuses").listFiles();;
        imagesList.clear();

        if (statusFiles != null && statusFiles.length > 0) {

            Arrays.sort(statusFiles);
            for (File file : statusFiles) {

                if (file.getName().contains(".nomedia"))
                    continue;
                if(file.getName().contains(".mp4"))
                {
                    imagesList.add(convertFileToDocumentFile(getContext(),file));
                }

                Log.d(TAG, "executeOld: "+file.getName());
            }
        }
        return imagesList;

    }
    public static DocumentFile convertFileToDocumentFile(Context context, File file) {
        // First, get the URI of the file
        Uri fileUri = Uri.fromFile(file);

        // Second, create a DocumentFile from the URI
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);

        return documentFile;
    }

    private static boolean isVideo(DocumentFile file, Context context) {
        String mimeType = context.getContentResolver().getType(file.getUri());
        return mimeType != null && mimeType.startsWith("video/");
    }

    private void updateEmptyViewVisibility() {
        if (videoRecylerviewAdapter.getItemCount() == 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}