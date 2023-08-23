package com.example.socialapps.bottomMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterHome extends FragmentStateAdapter {

    public ViewPagerAdapterHome(@NonNull FragmentManager fragmentManager,@NonNull Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position==1) return new homeSelectFragment1();
        return new homeSelectFragment();
    }

    @Override
    public int getItemCount() {return 2;}
}
