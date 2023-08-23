package com.example.socialapps.bottomMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.socialapps.LoginAndRegister.ViewPagerAdapterSignInSignUp;
import com.example.socialapps.R;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {
    private ImageView home_imageView;
    private TabLayout home_tabLayout;
    private ViewPager2 home_viewPager2;
    private boolean isOnClicked = false;
    private ViewPagerAdapterHome adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        home_imageView = view.findViewById(R.id.home_imageView);
        home_tabLayout = view.findViewById(R.id.home_tab_layout);
        home_viewPager2 = view.findViewById(R.id.home_viewPager2);

        home_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnClicked) {
                    isOnClicked = false;
                    home_tabLayout.setVisibility(View.GONE);
                }
                else {
                    isOnClicked = true;
                    home_tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        //給viewPager2實現顯示adapter的方法，具體實現方法在ViewPagerAdapterSignInSignUp的func中
        FragmentManager fragmentManager = getChildFragmentManager();
        adapter = new ViewPagerAdapterHome(fragmentManager, getLifecycle());
        home_viewPager2.setAdapter(adapter);

        // 關閉滑動切換頁面功能
        home_viewPager2.setUserInputEnabled(false);
        //處理tabLayout的點擊事件
        home_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                home_viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        home_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                home_tabLayout.selectTab(home_tabLayout.getTabAt(position));
            }
        });

        return view;
    }
}