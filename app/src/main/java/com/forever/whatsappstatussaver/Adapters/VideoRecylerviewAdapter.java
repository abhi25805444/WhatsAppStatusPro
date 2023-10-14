package com.forever.whatsappstatussaver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.forever.whatsappstatussaver.R;
import com.forever.whatsappstatussaver.ViewImages;
import com.forever.whatsappstatussaver.ViewVideos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoRecylerviewAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<File> fileArrayList;
    ArrayList<Bitmap> videoThumblist=new ArrayList<>();
    public VideoRecylerviewAdapter(Context context, ArrayList<File> arrayList)
    {
        this.context=context;
        this.fileArrayList =arrayList;
        try {
            videoThumblist=getAllvideoimagearray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_recycleview,parent,false);
        return new videoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((videoViewHolder) holder).imageView.setImageBitmap(videoThumblist.get(holder.getAdapterPosition()));
        ((videoViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewVideos.class);
                intent.putExtra("seletedfile",(fileArrayList.get(holder.getAdapterPosition())).toString());
                intent.putStringArrayListExtra("arraylistofvideos",getStringArrayList());
                intent.putExtra("postionofvideo",holder.getAdapterPosition());
    
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }
    public class videoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public videoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img1);
        }
    }
    private Bitmap getVideoThumbnail(String videoPath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);

        long timeInMicroseconds = 1000000;
        Bitmap thumbnail = retriever.getFrameAtTime(timeInMicroseconds);

        retriever.release();

        return thumbnail;
    }
    private ArrayList<Bitmap> getAllvideoimagearray() throws IOException {
        ArrayList<Bitmap> bitmapArrayList=new ArrayList<>();
        for(int i=0;i<fileArrayList.size();i++)
        {
            bitmapArrayList.add(getVideoThumbnail(fileArrayList.get(i).toString()));

        }
        return bitmapArrayList;
    }
    public ArrayList<String> getStringArrayList() {
        ArrayList<String> stringArrayList=new ArrayList<>();
        for (int i=0;i<fileArrayList.size();i++)
        {
            stringArrayList.add(fileArrayList.get(i).toString());
        }
        return stringArrayList;
    }
    public void notifyDataChanges()
    {
        try {
            videoThumblist=getAllvideoimagearray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        notifyDataSetChanged();
    }
}
