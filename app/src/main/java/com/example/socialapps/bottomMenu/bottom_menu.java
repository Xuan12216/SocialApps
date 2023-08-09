package com.example.socialapps.bottomMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.socialapps.R;
import com.example.socialapps.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class bottom_menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
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
                replaceFragment(new AddFragment());
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frm,fragment);
        fragmentTransaction.commit();
    }
}