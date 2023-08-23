package com.example.socialapps.bottomMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapps.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class homeSelectAdapter extends RecyclerView.Adapter<homeSelectAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    private ArrayList<Post> posts;

    public homeSelectAdapter(Context context, ArrayList<Post> posts) {
        this.layoutInflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView home_nameTextView;
        TextView home_textView;
        ShapeableImageView homeProfilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            home_nameTextView = itemView.findViewById(R.id.home_nameTextView);
            home_textView = itemView.findViewById(R.id.home_textView);
            homeProfilePic = itemView.findViewById(R.id.homeProfilePic);
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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
