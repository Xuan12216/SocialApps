package com.example.socialapps.bottomMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.socialapps.R;
import com.example.socialapps.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class bottom_menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private boolean isCamera = false;
    private boolean isPicture = false;
    private FirebaseUser user;
    private FirebaseDatabase database;

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

        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_context_sheeting);

        TextInputEditText add_edittext = dialog.findViewById(R.id.add_edittext);
        LinearLayout cameraLayout = dialog.findViewById(R.id.cameraLayout);
        LinearLayout AddPictureLayout = dialog.findViewById(R.id.AddPictureLayout);
        Button postButton = dialog.findViewById(R.id.postButton);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

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

                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}