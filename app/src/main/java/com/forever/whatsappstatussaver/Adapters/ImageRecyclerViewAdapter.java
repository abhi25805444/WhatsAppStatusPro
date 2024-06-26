package com.forever.whatsappstatussaver.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.forever.whatsappstatussaver.MainActivity;
import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.ViewImages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter {
    Context context;
    boolean isVideoimage;
    ArrayList<DocumentFile> arrayList;
    FragmentTransaction fragmentTransaction;

    public ImageRecyclerViewAdapter(Context context, ArrayList<DocumentFile> arrayList, boolean isVideoimage, androidx.fragment.app.FragmentTransaction fragmentTransaction, Activity activity) {

        this.isVideoimage = isVideoimage;
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentTransaction = fragmentTransaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_recycleview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder && !isVideoimage) {
            Glide.with(context).load(arrayList.get(holder.getAdapterPosition()).getUri()).into(((ImageViewHolder) holder).imageView);

            ((ImageViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context,ViewImages.class);
                    intent.putExtra("seletedfile",(arrayList.get(holder.getAdapterPosition())).getUri().toString());
                    intent.putExtra("position",holder.getAdapterPosition());
                    intent.putStringArrayListExtra("arrayofstring",getStringArrayList());
                    context.startActivity(intent);


                }
            });
        } else {
            try {
                ((ImageViewHolder) holder).imageView.setImageBitmap(getVideoThumbnail(arrayList.get(position).toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public  int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
        }
    }

    private Bitmap getVideoThumbnail(String videoPath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);

        // Get the thumbnail at a specific time (e.g., 1 second into the video)
        long timeInMicroseconds = 1000000; // 1 second in microseconds
        Bitmap thumbnail = retriever.getFrameAtTime(timeInMicroseconds);

        retriever.release(); // Release the MediaMetadataRetriever

        return thumbnail;
    }

    public ArrayList<String> getStringArrayList() {
        ArrayList<String> stringArrayList=new ArrayList<>();
        for (int i=0;i<arrayList.size();i++)
        {
            stringArrayList.add(arrayList.get(i).getUri().toString());
        }
        return stringArrayList;
    }

    public void notifyDataChanges() {
        notifyDataSetChanged();
    }
}
