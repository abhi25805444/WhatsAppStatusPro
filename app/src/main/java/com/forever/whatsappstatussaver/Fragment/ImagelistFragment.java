package com.forever.whatsappstatussaver.Fragment;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forever.whatsappstatussaver.Adapters.ImageRecyclerViewAdapter;
import com.forever.whatsappstatussaver.Interface.RefreshInterface;
import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.SessionManger;

import java.io.File;

import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImagelistFragment extends Fragment implements RefreshInterface {
    private static final String TAG = "imagelistFragment";
    private RecyclerView imageRecyclerView;
    private ArrayList<File> arrayofImages = new ArrayList<File>();
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    FragmentTransaction fragmentTransaction;
    ArrayList<DocumentFile> statusList = new ArrayList();
    public View emptyView;
    public boolean isProcessRunning = false;
    int TYPE;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View root = inflater.inflate(R.layout.fragment_imagelist, container, false);
        emptyView = root.findViewById(R.id.emptyviewofimage);
        imageRecyclerView = root.findViewById(R.id.imageRecyclerView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onViewCreated: ");
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeColors(
                    Color.RED,
                    Color.parseColor("#20C062"),
                    Color.BLACK
            );
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getImageStatus();
                }
            });
        }


        if (imageRecyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            imageRecyclerView.setLayoutManager(layoutManager);
            imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), statusList, false, fragmentTransaction, getActivity());
            imageRecyclerView.setAdapter(imageRecyclerViewAdapter);
        }
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.container);
        if (homeFragment != null) {
            homeFragment.setRefreshInterface(this);
        }
        TYPE = SessionManger.getInstance().getSelectionType();
//        new GetImageStatus().execute();
        getImageStatus();
    }

    public void getImageStatus() {

        if (isProcessRunning) {
            Log.d(TAG, "getImageStatus: isProcessRunning ");
            return;
        }
        isProcessRunning = true;

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        // Show progress bar and hide view
       /* if(imageRecyclerView!=null){
            imageRecyclerView.setVisibility(View.INVISIBLE);
        }*/

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }

        executor.execute(() -> {
            // Background execution
            Log.d(TAG, "doInBackground: ");
            if (statusList != null) {
                statusList.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    statusList.addAll(executeNew(TYPE));
                } else {
                    statusList.addAll(executeOld(TYPE));
                }
            }

            // Update UI on the main thread
            mainHandler.post(() -> {
                Log.d(TAG, "onPostExecute: ");
                if (statusList != null && !statusList.isEmpty()) {
                    if (imageRecyclerViewAdapter != null && imageRecyclerViewAdapter.getItemCount() > 0) {
                        imageRecyclerViewAdapter.notifyDataSetChanged();
                        if (imageRecyclerView != null) {
                            imageRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (emptyView != null) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        if (imageRecyclerView != null) {
                            imageRecyclerView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (imageRecyclerView != null) {
                        imageRecyclerView.setVisibility(View.GONE);
                    }
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                isProcessRunning = false;
            });
        });
        executor.shutdown();
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
    public void onExecuteNew(int TYPE) {
        Log.d(TAG, "onExecuteNew: TYPE " + TYPE);
        this.TYPE = TYPE;
        getImageStatus();
    }


    private void updateEmptyViewVisibility() {
        if (imageRecyclerViewAdapter.getItemCount() == 0) {
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (imageRecyclerView != null) {
                imageRecyclerView.setVisibility(View.VISIBLE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
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

        Log.d(TAG, "executeNew: list " + list);

        if (list.isEmpty()) {
            Log.e(TAG, "No persisted URI permissions found.");
            return imagesList;
        }

        DocumentFile rootDir = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());

        Log.d(TAG, "executeNew: list.get(0).getUri() " + list.get(0).getUri());
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
                if (getContext() != null) {
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