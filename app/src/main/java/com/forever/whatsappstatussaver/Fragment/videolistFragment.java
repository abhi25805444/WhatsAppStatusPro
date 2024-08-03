package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.forever.whatsappstatussaver.Adapters.VideoRecylerviewAdapter;
import com.forever.whatsappstatussaver.Interface.VideoRefreshInterface;
import com.forever.whatsappstatussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class videolistFragment extends Fragment implements VideoRefreshInterface {

    RecyclerView recyclerView;
    int sizeofArray;

    ArrayList<DocumentFile> ar = new ArrayList();
    VideoRecylerviewAdapter videoRecylerviewAdapter;
    View view;
    ProgressBar progressBar;

    int TYPE = 0;

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
        if (homeFragment != null) {
            homeFragment.setVideoRefreshInterface(this);
        }

        return root;
    }

    @Override
    public void onRefreshVideo() {
        refreshVideoList();
    }

    @Override
    public void onExecuteNew(int TYPE) {
        this.TYPE = TYPE;
        new GetStatusVideo().execute();
    }


    public class GetStatusVideo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
            if(progressBar!=null)
            {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (ar != null) {
                ar.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ar = executeNew(TYPE);
                } else {
                    ar = executeOld(TYPE);
                }
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
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (ar != null && ar.size() > 0) {
                videoRecylerviewAdapter = new VideoRecylerviewAdapter(getActivity(), ar);
                recyclerView.setAdapter(videoRecylerviewAdapter);
            }else {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sizeofArray = executeNew(TYPE).size();
            } else {
                sizeofArray = executeOld(TYPE).size();
            }
            updateEmptyViewVisibility();
        }
    }

    private void refreshVideoList() {
        new AsyncTask<Void, Void, ArrayList<DocumentFile>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
                if(progressBar!=null)
                {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            protected ArrayList<DocumentFile> doInBackground(Void... voids) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return executeNew(TYPE);
                } else {
                    return executeOld(TYPE);
                }

            }

            @Override
            protected void onPostExecute(ArrayList<DocumentFile> newAr) {
                super.onPostExecute(newAr);
                progressBar.setVisibility(View.GONE);
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (newAr != null) {
                    if (!areArrayListsEqual(ar, newAr)) { // Check if the new list is different
                        ar.clear();
                        ar.addAll(newAr);
                        if(videoRecylerviewAdapter!=null)
                        {
                            videoRecylerviewAdapter.notifyDataSetChanged();
                        }else {
                            videoRecylerviewAdapter = new VideoRecylerviewAdapter(getActivity(), ar);
                            recyclerView.setAdapter(videoRecylerviewAdapter);
                        }

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

    private ArrayList<DocumentFile> executeNew(int TYPE) {
        final ArrayList<DocumentFile> videosList = new ArrayList<>();
        List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

        if (list.isEmpty()) {
            Log.e(TAG, "No persisted URI permissions found.");
            return videosList;
        }

        DocumentFile rootDir = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());
        if (rootDir == null || !rootDir.isDirectory()) {
            Log.e(TAG, "Root directory is null or not a directory.");
            return videosList;
        }

        // Navigate to the WhatsApp Status folder
        DocumentFile whatsappDir;

        if (TYPE == 0) {
            whatsappDir = rootDir.findFile("com.whatsapp");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("WhatsApp");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("Media");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile(".Statuses");

            if (whatsappDir == null || !whatsappDir.isDirectory()) {
                Log.e(TAG, "WhatsApp Status directory is null or not a directory.");
                return videosList;
            }
        } else {
            whatsappDir = rootDir.findFile("com.whatsapp.w4b");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("WhatsApp Business");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("Media");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile(".Statuses");

            if (whatsappDir == null || !whatsappDir.isDirectory()) {
                Log.e(TAG, "WhatsApp Status directory is null or not a directory.");
                return videosList;
            }
        }


        // List files in the WhatsApp Status directory
        DocumentFile[] statusFiles = whatsappDir.listFiles();
        for (DocumentFile documentFile : statusFiles) {
            if (documentFile != null && isVideo(documentFile, getContext())) {
                videosList.add(documentFile);
            }
        }

        return videosList;
    }

    private ArrayList<DocumentFile> executeOld(int TYPE) {

        final ArrayList<androidx.documentfile.provider.DocumentFile> imagesList = new ArrayList<>();

        File[] statusFiles;

        if(TYPE==0)
        {
            statusFiles = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "WhatsApp/Media/.Statuses").listFiles();
        }else {
            statusFiles = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "WhatsApp Business/Media/.Statuses").listFiles();
        }
        ;
        imagesList.clear();

        if (statusFiles != null && statusFiles.length > 0) {

            Arrays.sort(statusFiles);
            for (File file : statusFiles) {

                if (file.getName().contains(".nomedia"))
                    continue;
                if (file.getName().contains(".mp4")) {
                    imagesList.add(convertFileToDocumentFile(getContext(), file));
                }

                Log.d(TAG, "executeOld: " + file.getName());
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
        if(videoRecylerviewAdapter!=null)
        {
            if (videoRecylerviewAdapter.getItemCount() == 0) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}