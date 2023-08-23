package com.example.socialapps.bottomMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.socialapps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class bottom_menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private boolean isVideo = false;
    private boolean isPicture = false;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private static final int PICK_IMAGES_REQUEST = 2;
    private static final int PICK_VIDEOS_REQUEST = 3;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<Uri> selectedVideoUris = new ArrayList<>();
    private Executor executor = Executors.newFixedThreadPool(3);
    LinearLayout imageContainer ;
    private String errorMessage,errorMessage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);
        replaceFragment(new HomeFragment());//预设每次开启都是为home fragment

        bottomNavigationView= findViewById(R.id.bottomNavigationView);
        floatingActionButton=findViewById(R.id.floatActionButton);
        //指定视图（在这种情况下是 bottomNavigationView）的背景设置为 null，即没有背景，从而实现透明或无背景的效果。
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.Search:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.follower:
                    replaceFragment(new followerFragment());
                    break;
                case R.id.Profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frm,fragment);
        fragmentTransaction.commit();
    }
    private void showBottomDialog() {
        selectedImageUris.clear();
        selectedVideoUris.clear();
        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_context_sheeting);

        TextInputEditText add_edittext = dialog.findViewById(R.id.add_edittext);
        LinearLayout AddVideoLayout = dialog.findViewById(R.id.AddVideoLayout);
        LinearLayout AddPictureLayout = dialog.findViewById(R.id.AddPictureLayout);
        imageContainer = dialog.findViewById(R.id.imageContainer);
        Button postButton = dialog.findViewById(R.id.postButton);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        AddVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedVideoUris.size() >= 5)
                    Toast.makeText(getApplicationContext(), "You can select up to 5 videos.", Toast.LENGTH_SHORT).show();
                else {
                    // Open video picker and handle selected video
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, PICK_VIDEOS_REQUEST);
                }
            }
        });
        AddPictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUris.size() >= 5)
                    Toast.makeText(getApplicationContext(), "You can select up to 5 images.", Toast.LENGTH_SHORT).show();
                else {
                    // Open image picker and handle selected images
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMAGES_REQUEST);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss();}
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_edittext.getText().toString().isEmpty() && !isVideo && !isPicture)
                    Toast.makeText(getApplicationContext(),"No data to post",Toast.LENGTH_LONG).show();
                else {
                    DatabaseReference postsRef = database.getReference("posts"); // Change this to the appropriate reference
                    DatabaseReference newPostRef = postsRef.push(); // Create a new unique key,生成新帖子的唯一key，每個可以都不會重複。

                    newPostRef.child("text").setValue(add_edittext.getText().toString()); // Add the text data to Firebase
                    newPostRef.child("userId").setValue(user.getUid()); // Add the user ID to the specific post

                    if (isPicture){
                        // Upload selected images to Firebase Storage
                        for (Uri imageUri : selectedImageUris) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference imageRef = storageReference.child("images/" + newPostRef.getKey() + "/" + System.currentTimeMillis() + ".jpg");

                            imageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the download URL of the uploaded image
                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Save the image URL to the post in the database
                                            DatabaseReference postImageRef = database.getReference("posts").child(newPostRef.getKey()).child("images");
                                            postImageRef.push().setValue(uri.toString());
                                        });
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Handle the failure
                                        Toast.makeText(getApplicationContext(),"Image upload failed",Toast.LENGTH_LONG).show();
                                    });
                        }
                        isPicture = false;
                    }
                    if (isVideo){
                        // Upload selected videos to Firebase Storage
                        for (Uri videoUri : selectedVideoUris) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference videoRef = storageReference.child("videos/" + newPostRef.getKey() + "/" + System.currentTimeMillis() + ".mp4");

                            videoRef.putFile(videoUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the download URL of the uploaded video
                                        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            // Save the video URL to the post in the database
                                            DatabaseReference postVideoRef = database.getReference("posts").child(newPostRef.getKey()).child("videos");
                                            postVideoRef.push().setValue(uri.toString());
                                        });
                                    })
                                    .addOnFailureListener(exception -> {
                                        // Handle the failure
                                        Toast.makeText(getApplicationContext(), "Video upload failed", Toast.LENGTH_LONG).show();
                                    });
                        }
                        isVideo = false;
                    }
                    selectedImageUris.clear();
                    selectedVideoUris.clear();
                    Toast.makeText(getApplicationContext(),"Data posted successfully",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                    }
                }
                else {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                }

                try {updateImageContainer();}// Update the image container with selected images
                catch (IOException e) {throw new RuntimeException(e);}
            }
        }
        else if (requestCode == PICK_VIDEOS_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri videoUri = clipData.getItemAt(i).getUri();
                        selectedVideoUris.add(videoUri);
                    }
                }
                else {
                    Uri videoUri = data.getData();
                    selectedVideoUris.add(videoUri);
                }

                try {updateImageContainer();}// Update the image container with selected images
                catch (IOException e) {throw new RuntimeException(e);}
            }
        }
    }

    private void updateImageContainer() throws IOException {
        imageContainer.removeAllViews();

        if (selectedImageUris.size()>0){isPicture = true;}
        if (selectedVideoUris.size()>0){isVideo = true;}

        if (selectedImageUris.size()>5){
            Toast.makeText(getApplicationContext(), "You can select up to 5 images.", Toast.LENGTH_SHORT).show();
            selectedImageUris.subList(5,selectedImageUris.size()).clear();
        }
        if (selectedVideoUris.size()>5){
            Toast.makeText(getApplicationContext(), "You can select up to 5 videos.", Toast.LENGTH_SHORT).show();
            selectedVideoUris.subList(5,selectedVideoUris.size()).clear();
        }
        // Execute the AsyncTask for images
        new LoadImagesAsyncTask().execute(selectedImageUris.toArray(new Uri[0]));

        // Execute the AsyncTask for videos
        new LoadVideosAsyncTask().execute(selectedVideoUris.toArray(new Uri[0]));
    }
    //======================ImageTask=====================
    private class LoadImagesAsyncTask extends AsyncTask<Uri, Void, Void> {
        @Override
        protected Void doInBackground(Uri... uris) {
            for (Uri imageUri : selectedImageUris) {
                errorMessage = null;
                // 檢查圖片大小是否超過6MB
                if (getImageFileSize(imageUri) > 6 * 1024 * 1024) {
                    errorMessage = "Image size exceeds 6MB limit.";
                    selectedImageUris.remove(imageUri);
                    continue;
                }
                int desiredWidth, desiredHeight;
                // Open an InputStream to read the image
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    // Load the image dimensions using BitmapFactory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    // Close the InputStream
                    inputStream.close();
                    // Calculate the aspect ratio of the image
                    float imageAspectRatio = (float) options.outWidth / (float) options.outHeight;
                    // Define the desired dimensions for both orientations
                    // Set the desired height for vertical images
                    if (imageAspectRatio > 1) {desiredWidth = 850;}// Horizontal image
                    else {desiredWidth = 500;}// Vertical image
                    desiredHeight = 700;
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Inflate the image_layout.xml
                View imageLayout = getLayoutInflater().inflate(R.layout.image_layout, null);
                // Find views within the inflated layout
                ShapeableImageView imageView = imageLayout.findViewById(R.id.imageView);
                ShapeableImageView closeButton = imageLayout.findViewById(R.id.homeProfilePic);

                imageView.setPadding(0,0,30,10);
                imageView.setLayoutParams(new ConstraintLayout.LayoutParams(desiredWidth,desiredHeight));
                // Set data and listeners for views
                imageView.setImageURI(imageUri);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Remove the corresponding ConstraintLayout from the imageContainer
                        imageContainer.removeView(imageLayout);
                        // Remove the corresponding imageUri from selectedImageUris list
                        selectedImageUris.remove(imageUri);
                    }
                });
                // runOnMainThread and Add the inflated layout to the imageContainer
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {imageContainer.addView(imageLayout);}
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (errorMessage != null) Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
    //======================VideoTask=====================
    private class LoadVideosAsyncTask extends AsyncTask<Uri, Void, Void> {
        @Override
        protected Void doInBackground(Uri... uris) {
            for (Uri videoUri : selectedVideoUris) {
                errorMessage1 = null;
                // 檢查影片時長是否超過2分鐘
                if (getVideoDuration(videoUri) > 2 * 60 * 1000) { // 2分鐘轉換為毫秒
                    errorMessage1 = "Video duration exceeds 2-minute limit.";
                    selectedVideoUris.remove(videoUri);
                    continue;
                }
                // Inflate the video_layout.xml
                View videoLayout = getLayoutInflater().inflate(R.layout.video_layout, null);
                // Find views within the inflated layout
                TextureView videoView = videoLayout.findViewById(R.id.videoView);
                ShapeableImageView closeButton = videoLayout.findViewById(R.id.homeProfilePic);
                ShapeableImageView playButton = videoLayout.findViewById(R.id.playButton);

                videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        executor.execute(() -> {
                            try {
                                mediaPlayer.setDataSource(getApplicationContext(), videoUri);
                                mediaPlayer.setSurface(new Surface(surfaceTexture));
                                mediaPlayer.prepareAsync();
                            }
                            catch (IOException e) {e.printStackTrace();}
                        });

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {playButton.setVisibility(View.VISIBLE);}
                        });
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                int videoWidth = mediaPlayer.getVideoWidth();
                                int videoHeight = mediaPlayer.getVideoHeight();
                                float videoAspectRatio = (float) videoWidth / (float) videoHeight;

                                // Define the desired dimensions for both orientations
                                int desiredWidth, desiredHeight;
                                // Set the desired height for vertical videos
                                if (videoAspectRatio > 1) {desiredWidth = 830;}// Horizontal video
                                else {desiredWidth = 480; }// Vertical video
                                desiredHeight = 690;

                                // Set the layout parameters for the TextureView
                                videoView.setLayoutParams(new ConstraintLayout.LayoutParams(desiredWidth, desiredHeight));
                            }
                        });

                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playButton.setVisibility(View.GONE);
                                mediaPlayer.start();
                            }
                        });
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Remove the corresponding ConstraintLayout from the imageContainer
                                imageContainer.removeView(videoLayout);
                                // Remove the corresponding videoUri from selectedVideoUris list
                                selectedVideoUris.remove(videoUri);
                                // Release the MediaPlayer
                                mediaPlayer.release();
                            }
                        });
                    }
                    // 其他SurfaceTextureListener方法
                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {return false;}
                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
                });
                // runOnUiThread and Add the inflated layout to the imageContainer
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {imageContainer.addView(videoLayout);}
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (errorMessage1!=null) Toast.makeText(getApplicationContext(), errorMessage1, Toast.LENGTH_SHORT).show();
        }
    }
    private long getVideoDuration(Uri videoUri) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), videoUri);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            return duration;
        }
        catch (IOException e) {e.printStackTrace();}
        return 0;
    }

    private long getImageFileSize(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                long size = inputStream.available();
                inputStream.close();
                return size;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return 0;
    }
}