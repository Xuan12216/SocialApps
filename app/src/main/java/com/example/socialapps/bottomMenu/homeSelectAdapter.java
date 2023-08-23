package com.example.socialapps.bottomMenu;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapps.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;

public class homeSelectAdapter extends RecyclerView.Adapter<homeSelectAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;
    private Executor executor = Executors.newFixedThreadPool(3);

    public homeSelectAdapter(Context context, ArrayList<Post> posts) {
        this.layoutInflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView home_nameTextView;
        TextView home_textView;
        ShapeableImageView homeProfilePic;
        LinearLayout home_imageContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            home_nameTextView = itemView.findViewById(R.id.home_nameTextView);
            home_textView = itemView.findViewById(R.id.home_textView);
            homeProfilePic = itemView.findViewById(R.id.homeProfilePic);
            home_imageContainer = itemView.findViewById(R.id.home_imageContainer);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 在点击事件中处理点击逻辑
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 获取点击的目标项的数据
                        //String selectedItem = text.get(position);
                        // 更新textView2的文本
                        //textView2.setText(String.valueOf(selectedItem));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.home_custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.home_textView.setText(post.getText());
        holder.home_nameTextView.setText(post.getUserName());
        Picasso.get().load(post.getUserProPic()).into(holder.homeProfilePic);

        // 清除舊的 ImageView
        holder.home_imageContainer.removeAllViews();

        // 填充新的 ImageView
        for (int i = 0; i < post.getImages().size(); i++) {
            String imageUrl = post.getImages().get(i);
            // 預加載圖片
            executor.execute(() -> {
                Picasso.get().load(imageUrl).fetch();
            });

            int marginLeft = i == 0 ? 100 : 0;
            int marginRight = 30;
            int marginBottom = 10;
            // 創建 ImageView 並設置圖片
            View itemView = layoutInflater.inflate(R.layout.image_layout, holder.home_imageContainer, false);
            ShapeableImageView imageView = itemView.findViewById(R.id.imageView);
            ShapeableImageView closeButton = itemView.findViewById(R.id.homeProfilePic);

            closeButton.setVisibility(View.GONE);
            // 設置圖片的 margin
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            layoutParams.setMargins(marginLeft, 0, marginRight, marginBottom);
            imageView.setLayoutParams(layoutParams);

            // 載入圖片
            Picasso.get().load(imageUrl).into(imageView);
            holder.home_imageContainer.addView(itemView);
        }

        // 填充新的 TextureView
        for (int i = 0; i < post.getVideo().size(); i++) {
            String videoUrl = post.getVideo().get(i);

            // 創建 TextureView 並設置影片
            View itemView = layoutInflater.inflate(R.layout.video_layout, holder.home_imageContainer, false);
            TextureView videoView = itemView.findViewById(R.id.videoView);
            ShapeableImageView closeButton = itemView.findViewById(R.id.homeProfilePic);
            ShapeableImageView playButton = itemView.findViewById(R.id.playButton);
            MediaPlayer mediaPlayer = new MediaPlayer();

            closeButton.setVisibility(View.GONE);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.start();
                    playButton.setVisibility(View.GONE);
                }
            });
            // 設置TextureView的監聽器
            videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    mediaPlayer.setSurface(new Surface(surfaceTexture));

                    try {
                        mediaPlayer.setDataSource(videoUrl);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                // 影片準備好後開始播放
                                mediaPlayer.start();
                                playButton.setVisibility(View.GONE);
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                playButton.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                    // 需要時調整影片尺寸
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    // 釋放MediaPlayer資源
                    mediaPlayer.release();
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    // 監聽TextureView的更新
                }
            });

            // Add the itemView to the container
            holder.home_imageContainer.addView(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
