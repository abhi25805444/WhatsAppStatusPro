package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class imagelistFragment extends Fragment implements RefreshInterface {
    RecyclerView imageRecyclerView;
    int sizeofArray;
    ArrayList<File> arrayofImages = new ArrayList<File>();

    ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    FragmentTransaction fragmentTransaction;
    ArrayList<DocumentFile> ar = new ArrayList();
    View view;
    private ProgressBar progressBar;

    static boolean isRefreshClick = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_imagelist, container, false);
        view = root.findViewById(R.id.emptyviewofimage);
        sizeofArray = ar.size();
        progressBar = root.findViewById(R.id.progressBar);
        imageRecyclerView = root.findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.container);
        homeFragment.setRefreshInterface(this);

        new getStatus().execute();
        return root;

    }

    @Override
    public void onRefreshImage() {
        if (!isRefreshClick) {
            new refresh().execute();
        }
        Log.d(TAG, "onRefresh: ");
    }

    public void showToast() {
        Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
    }


    private class getStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            ar.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ar = executeNew();
            } else {
                ar = executeOld();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressBar!=null)
            {
                progressBar.setVisibility(View.GONE);
            }
            imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), ar, false, fragmentTransaction, getActivity());
            imageRecyclerView.setAdapter(imageRecyclerViewAdapter);

            if (imageRecyclerViewAdapter.getItemCount() == 0) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }

        }
    }

    public class refresh extends AsyncTask<Void, Void, ArrayList<DocumentFile>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRefreshClick = true;
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.GONE);
            }
            if(progressBar!=null)
            {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<DocumentFile> doInBackground(Void... voids) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return executeNew();
            } else {
                return executeOld();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<DocumentFile> newAr) {
            super.onPostExecute(newAr);
            if(progressBar!=null)
            {
                progressBar.setVisibility(View.GONE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.VISIBLE);
            }

            if (newAr != null && !areArrayListsEqual(ar, newAr)) { // Check if the new list is different
                ar.clear(); // Clear existing data
                ar.addAll(newAr); // Update with new data
                imageRecyclerViewAdapter.notifyDataSetChanged(); // Notify adapter
                updateEmptyViewVisibility();
            }

            isRefreshClick = false; // Reset refresh state
        }
    }

    private void updateEmptyViewVisibility() {
        if (imageRecyclerViewAdapter.getItemCount() == 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
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

        final ArrayList<DocumentFile> imagesList = new ArrayList<>();

        List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

        DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());

        DocumentFile[] statusFiles = file.listFiles();

        for (DocumentFile documentFile : statusFiles) {
            if (documentFile != null) {
                Log.d(TAG, "executeNew: file name " + documentFile.getName());
                if (isImage(documentFile, getContext())) {
                    imagesList.add(documentFile);
                }
                Log.d(TAG, "executeNew: file name " + documentFile.getName());
            }

        }


        return imagesList;
    }

    private ArrayList<DocumentFile> executeOld() {

        final ArrayList<DocumentFile> imagesList = new ArrayList<>();

        File[] statusFiles;
        statusFiles = new File(Environment.getExternalStorageDirectory() +
                File.separator + "WhatsApp/Media/.Statuses").listFiles();
        ;
        imagesList.clear();

        if (statusFiles != null && statusFiles.length > 0) {

            Arrays.sort(statusFiles);
            for (File file : statusFiles) {
                if (file.getName().contains(".nomedia"))
                    continue;

                if (file.getName().contains(".jpg")) {
                    imagesList.add(DocumentFile.fromFile(file));
                }
                Log.d(TAG, "executeOld: " + file.getName());

            }
        }
        return imagesList;

    }


    private static boolean isImage(DocumentFile file, Context context) {
        String mimeType = context.getContentResolver().getType(file.getUri());
        return mimeType != null && mimeType.startsWith("image/");
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
                    if (!arrayofImages.contains(file)) {
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