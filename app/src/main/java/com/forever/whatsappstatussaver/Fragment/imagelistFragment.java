package com.forever.whatsappstatussaver.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.UriPermission;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.R;

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
    public View view;
    public ProgressBar progressBar;
    static boolean isRefreshClick = false;
    int TYPE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_imagelist, container, false);
        view = root.findViewById(R.id.emptyviewofimage);
        if (ar != null && ar.size() > 0) {
            sizeofArray = ar.size();
        }
        progressBar = root.findViewById(R.id.progressBar);
        imageRecyclerView = root.findViewById(R.id.imageRecyclerView);

        if (imageRecyclerView != null) {
            imageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.container);
        if (homeFragment != null) {
            homeFragment.setRefreshInterface(this);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (imageRecyclerView != null) {
            imageRecyclerView = null;
        }
        if (imageRecyclerViewAdapter != null) {
            imageRecyclerViewAdapter = null;
        }
    }

    @Override
    public void onRefreshImage() {
        if (!isRefreshClick) {
            new refresh().execute();
        }
        Log.d(TAG, "onRefresh: ");
    }

    @Override
    public void onExecuteNew(int TYPE) {
        this.TYPE = TYPE;

        new GetImageStatus().execute();

    }

    public class GetImageStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.GONE);
            }
            if (view != null) {
                view.setVisibility(View.GONE);
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
        protected void onCancelled() {
            super.onCancelled();

            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (fragmentTransaction != null && ar != null && ar.size() > 0) {
                imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), ar, false, fragmentTransaction, getActivity());
                if (imageRecyclerViewAdapter != null) {
                    imageRecyclerView.setAdapter(imageRecyclerViewAdapter);
                    if (imageRecyclerViewAdapter.getItemCount() == 0) {
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                        }
                        if (imageRecyclerView != null) {
                            imageRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        if (imageRecyclerView != null) {
                            imageRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (view != null) {
                            view.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                if (imageRecyclerView != null) {
                    imageRecyclerView.setVisibility(View.GONE);
                }
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
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
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
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
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.VISIBLE);
            }
            if (newAr != null) {
                if (ar != null && imageRecyclerViewAdapter != null) {
                    ar.clear(); // Clear existing data
                    ar.addAll(newAr); // Update with new data
                    imageRecyclerViewAdapter.notifyDataSetChanged(); // Notify adapter
                    updateEmptyViewVisibility();
                } else {
                    ar.clear(); // Clear existing data
                    ar.addAll(newAr);
                    imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), ar, false, fragmentTransaction, getActivity());
                    imageRecyclerView.setAdapter(imageRecyclerViewAdapter);
                    updateEmptyViewVisibility();
                }
            }
            isRefreshClick = false; // Reset refresh state
        }
    }

    private void updateEmptyViewVisibility() {
        if (imageRecyclerViewAdapter.getItemCount() == 0) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.VISIBLE);
            }
            if (view != null) {
                view.setVisibility(View.GONE);
            }
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

    public ArrayList<DocumentFile> executeNew(int TYPE) {
        Log.d(TAG, "executeNew: ");
        final ArrayList<DocumentFile> imagesList = new ArrayList<>();
        List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();

        if (list.isEmpty()) {
            Log.e(TAG, "No persisted URI permissions found.");
            return imagesList;
        }

        DocumentFile rootDir = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());
        if (rootDir == null || !rootDir.isDirectory()) {
            Log.e(TAG, "Root directory is null or not a directory.");
            return imagesList;
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
                return imagesList;
            }
        } else {
            whatsappDir = rootDir.findFile("com.whatsapp.w4b");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("WhatsApp Business");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile("Media");
            if (whatsappDir != null) whatsappDir = whatsappDir.findFile(".Statuses");

            if (whatsappDir == null || !whatsappDir.isDirectory()) {
                Log.e(TAG, "WhatsApp Status directory is null or not a directory.");
                return imagesList;
            }
        }


        // List files in the WhatsApp Status directory
        DocumentFile[] statusFiles = whatsappDir.listFiles();
        for (DocumentFile documentFile : statusFiles) {
            if (documentFile != null && documentFile.isFile()) {
                Log.d(TAG, "executeNew: file name " + documentFile.getName());
                if(getContext()!=null){
                    if (isImage(documentFile, getContext())) {
                        imagesList.add(documentFile);
                    }
                }
                Log.d(TAG, "executeNew: file name " + documentFile.getName());
            }
        }

        return imagesList;
    }

    public ArrayList<DocumentFile> executeOld(int TYPE) {

        final ArrayList<DocumentFile> imagesList = new ArrayList<>();

        File[] statusFiles;
        if (TYPE == 0) {
            statusFiles = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "WhatsApp/Media/.Statuses").listFiles();
        } else {
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