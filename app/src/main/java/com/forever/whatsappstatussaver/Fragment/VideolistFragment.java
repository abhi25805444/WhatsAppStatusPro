package com.forever.whatsappstatussaver.Fragment;

import android.content.Context;
import android.content.UriPermission;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import com.forever.whatsappstatussaver.Adapters.VideoRecylerviewAdapter;
import com.forever.whatsappstatussaver.Interface.VideoRefreshInterface;
import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.SessionManger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VideolistFragment extends Fragment implements VideoRefreshInterface {

    private static final String TAG = "videolistFragment";
    private RecyclerView videoRecyclerView;
    private ArrayList<DocumentFile> statusList = new ArrayList();
    private VideoRecylerviewAdapter videoRecylerviewAdapter;
    private View emptyView;
    private boolean isProcessRunning = false;

    int TYPE = 0;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_videolist, container, false);
        emptyView = root.findViewById(R.id.emptyviewofvideo);
        videoRecyclerView = root.findViewById(R.id.videoRecyclerView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeColors(
                    Color.RED,
                    Color.parseColor("#20C062"),
                    Color.BLACK
            );
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getVideoStatus();
                }
            });
        }
        if (videoRecyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            videoRecyclerView.setLayoutManager(layoutManager);

            videoRecylerviewAdapter = new VideoRecylerviewAdapter(getActivity(), statusList);
            videoRecyclerView.setAdapter(videoRecylerviewAdapter);
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.container);
        if (homeFragment != null) {
            homeFragment.setVideoRefreshInterface(this);
        }
        TYPE = SessionManger.getInstance().getSelectionType();
        getVideoStatus();
    }


    @Override
    public void onExecuteNew(int TYPE) {
        this.TYPE = TYPE;
        getVideoStatus();
    }


    public void getVideoStatus() {

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler mainHandler = new Handler(Looper.getMainLooper());

        if (isProcessRunning) {
            Log.d(TAG, "isProcessRunning ");
            return;
        }

        isProcessRunning = true;
        // Show progress bar and hide view
        if(swipeRefreshLayout!=null){
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
                if (statusList != null && statusList.size() > 0) {
                    if (videoRecyclerView != null && videoRecylerviewAdapter != null && videoRecylerviewAdapter.getItemCount() > 0) {
                        videoRecylerviewAdapter.notifyDataSetChanged();
                        if (videoRecyclerView != null) {
                            videoRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (emptyView != null) {
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        if (emptyView != null) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        if (videoRecyclerView != null) {
                            videoRecyclerView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (videoRecyclerView != null) {
                        videoRecyclerView.setVisibility(View.GONE);
                    }
                }
                if(swipeRefreshLayout!=null){
                    swipeRefreshLayout.setRefreshing(false);
                }
                isProcessRunning = false;
            });
        });
    }


    private ArrayList<DocumentFile> executeNew(int TYPE) {
        final ArrayList<DocumentFile> videosList = new ArrayList<>();
        List<UriPermission> list = new ArrayList<>();
        if (getActivity() != null && getActivity().getContentResolver() != null) {
            list = getActivity().getContentResolver().getPersistedUriPermissions();
        }

        if (list != null && list.isEmpty()) {
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
        if (videoRecylerviewAdapter != null) {
            Log.d(TAG, "updateEmptyViewVisibility: videoRecylerviewAdapter.getItemCount() " + videoRecylerviewAdapter.getItemCount());
            if (videoRecylerviewAdapter.getItemCount() == 0) {
                if (emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                if (videoRecyclerView != null) {
                    videoRecyclerView.setVisibility(View.INVISIBLE);
                }
            } else {
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
                if (videoRecyclerView != null) {
                    videoRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}