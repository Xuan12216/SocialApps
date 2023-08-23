package com.example.socialapps.bottomMenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.socialapps.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class homeSelectFragment extends Fragment {
    private DatabaseReference databaseReference;
    homeSelectAdapter adapter;
    ArrayList<String> text = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> video = new ArrayList<>();
    ArrayList<String> userProPic = new ArrayList<>();
    ArrayList<String> userNameList = new ArrayList<>();
    RecyclerView recyclerView;
    ArrayList<Post> posts = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_select, container, false);

        recyclerView = view.findViewById(R.id.selectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new homeSelectAdapter(getContext(),posts);
        recyclerView.setAdapter(adapter);
        // 初始化 Firebase Database 引用
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        // 讀取資料並添加至 ArrayList
        readDataFromFirebase();

        return view;
    }

    private void readDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // 讀取 post 資料
                    String postText = postSnapshot.child("text").getValue(String.class);
                    String postUserID = postSnapshot.child("userId").getValue(String.class);

                    // 讀取 images 資料
                    ArrayList<String> postImages = new ArrayList<>();
                    for (DataSnapshot imageSnapshot : postSnapshot.child("images").getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        postImages.add(imageUrl != null ? imageUrl : "");
                    }

                    // 讀取 users 資料
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(postUserID);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userSnapshot) {
                            String profilePic = userSnapshot.child("profilePic").getValue(String.class);
                            String userName = userSnapshot.child("userName").getValue(String.class);

                            // 創建新的 Post 對象並添加到 posts ArrayList
                            Post post = new Post(postText, postImages, profilePic, userName);
                            posts.add(post);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 處理數據讀取錯誤
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 處理數據讀取錯誤
            }
        });
    }
}