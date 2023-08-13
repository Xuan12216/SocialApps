package com.example.socialapps.bottomMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.socialapps.R;
import com.example.socialapps.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class bottom_menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private boolean isCamera = false;
    private boolean isPicture = false;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private static final int PICK_IMAGES_REQUEST = 2;
    private List<Uri> selectedImageUris = new ArrayList<>();
    LinearLayout imageContainer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);
        //预设每次开启都是为home fragment
        replaceFragment(new HomeFragment());

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
        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_context_sheeting);

        TextInputEditText add_edittext = dialog.findViewById(R.id.add_edittext);
        LinearLayout videoLayout = dialog.findViewById(R.id.videoLayout);
        LinearLayout AddPictureLayout = dialog.findViewById(R.id.AddPictureLayout);
        imageContainer = dialog.findViewById(R.id.imageContainer);
        Button postButton = dialog.findViewById(R.id.postButton);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        AddPictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUris.size() >= 5) {
                    Toast.makeText(getApplicationContext(), "You can select up to 5 images.", Toast.LENGTH_SHORT).show();
                } else {
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
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_edittext.getText().toString().isEmpty() && !isCamera && !isPicture){
                    Toast.makeText(getApplicationContext(),"No data to post",Toast.LENGTH_LONG).show();
                }
                else {
                    DatabaseReference postsRef = database.getReference("posts"); // Change this to the appropriate reference
                    DatabaseReference newPostRef = postsRef.push(); // Create a new unique key,生成新帖子的唯一key，每個可以都不會重複。

                    newPostRef.child("text").setValue(add_edittext.getText().toString()); // Add the text data to Firebase
                    newPostRef.child("userId").setValue(user.getUid()); // Add the user ID to the specific post

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

                    selectedImageUris.clear();

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

                    if (clipData.getItemCount() > 5) {
                        Toast.makeText(getApplicationContext(), "You can select up to 5 images.", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed with uploading
                    }

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                    }
                } else {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                }

                updateImageContainer(); // Update the image container with selected images
            }
        }
    }

    private void updateImageContainer() {
        imageContainer.removeAllViews();

        for (Uri imageUri : selectedImageUris) {
            // Create a new LinearLayout to hold the ShapeableImageView and the close button
            LinearLayout imageLayout = new LinearLayout(getApplicationContext());
            imageLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageLayout.setLayoutParams(imageLayoutParams);
            imageLayout.setGravity(Gravity.CENTER); // Center the contents vertically

            // Add the ShapeableImageView to the new LinearLayout
            ShapeableImageView imageView = new ShapeableImageView(getApplicationContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(600, 800));
            imageView.setPadding(15,0,15,10);
            imageView.setAdjustViewBounds(true);
            imageView.setImageURI(imageUri);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ShapeAppearanceModel shapeAppearanceModel = imageView.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, getResources().getDimension(R.dimen.cornerSize))
                    .build();
            imageView.setShapeAppearanceModel(shapeAppearanceModel);
            imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            imageLayout.addView(imageView);

            // Create and configure the close button
            ImageButton closeButton = new ImageButton(getApplicationContext());
            closeButton.setImageResource(R.drawable.ic_baseline_clear_24); // Set your close icon drawable
            LinearLayout.LayoutParams closeButtonLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            closeButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL; // Center the button horizontally
            closeButton.setLayoutParams(closeButtonLayoutParams);

            // Set a click listener for the close button
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the corresponding LinearLayout from the imageContainer
                    imageContainer.removeView(imageLayout);

                    // Remove the corresponding imageUri from selectedImageUris list
                    selectedImageUris.remove(imageUri);
                }
            });

            // Add the close button to the new LinearLayout
            imageLayout.addView(closeButton);

            // Add the new LinearLayout to the imageContainer
            imageContainer.addView(imageLayout);

        }
        isPicture=true;
    }
}