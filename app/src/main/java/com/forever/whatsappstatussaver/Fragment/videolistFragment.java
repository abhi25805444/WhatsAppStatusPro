package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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
    ArrayList<File> arrayofVideo = new ArrayList<>();
    FloatingActionButton btnRefresh;
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
        btnRefresh = root.findViewById(R.id.btn_refresh);
        progressBar = root.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        Drawable icon = btnRefresh.getDrawable();
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
        btnRefresh.setImageDrawable(icon);

        new MyAsyncTask().execute();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshVideoList();
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            progressBar.setVisibility(View.GONE);
            videoRecylerviewAdapter = new VideoRecylerviewAdapter(getActivity(), ar);
            recyclerView.setAdapter(videoRecylerviewAdapter);
            sizeofArray = executeNew().size();
            updateEmptyViewVisibility();
        }
    }

    private void refreshVideoList() {
        new AsyncTask<Void, Void, ArrayList<DocumentFile>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<DocumentFile> doInBackground(Void... voids) {
                return executeNew();
            }

            @Override
            protected void onPostExecute(ArrayList<DocumentFile> newAr) {
                super.onPostExecute(newAr);
                progressBar.setVisibility(View.GONE);
                if (newAr != null) {
                    if (!areArrayListsEqual(ar, newAr)) { // Check if the new list is different
                        ar.clear();
                        ar.addAll(newAr);
                        videoRecylerviewAdapter.notifyDataSetChanged();
                        sizeofArray = ar.size();
                        updateEmptyViewVisibility();
                    } else {
                        Toast.makeText(getActivity(), "List is already up to date", Toast.LENGTH_SHORT).show();
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