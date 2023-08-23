package com.example.socialapps.StartupPage;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.socialapps.LoginAndRegister.LoginRegisterActivity;
import com.example.socialapps.MainActivity;
import com.example.socialapps.R;

public class NavigationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton, skipButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    //切換頁面時，改變dot目前顯示的func
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {

            //顯示dot的func，position就是目前的頁面所在
            setDotIndicator(position);

            //position大於0才顯示backButton
            if (position > 0)
                backButton.setVisibility(View.VISIBLE);
            else
                backButton.setVisibility(View.INVISIBLE);

            //position==2/不等於2時，skipButton顯示/消失，nextButton setText
            if (position == 2){
                skipButton.setVisibility(View.INVISIBLE);
                nextButton.setText("Get Started");
            } else {
                nextButton.setText("Next");
                skipButton.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // 檢查權限並請求
        if (!arePermissionsGranted()) {
            requestPermissions();
        }

        //進入onCreate時會檢查sharedPreferences，如果isMainPageSet true的話，直接進入MainActivity
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isMainPageSet = sharedPreferences.getBoolean("isMainPageSet", false);

        //如果isMainPageSet true的話，直接進入MainActivity
        if (isMainPageSet) {
            // 如果 MainActivity 被設置為主頁，直接跳轉到 MainActivity
            startActivity(new Intent(NavigationActivity.this, LoginRegisterActivity.class));
            finish(); // 結束 MainActivity，避免返回到它
        }

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);
        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);

        //呼叫ViewPagerAdapter，實作滑動顯示不同的圖片的func
        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);

        //dot的func，默認顯示第0頁
        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);

        //backButton onclick
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果大於0，返回前一頁
                if (getItem(0) > 0)
                    slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        //nextButton onclick
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果小於2，顯示下一頁
                if (getItem(0) < 2)
                    slideViewPager.setCurrentItem(getItem(1), true);
                else {
                    //跳轉頁面去MainActivity
                    startActivity(new Intent(NavigationActivity.this, LoginRegisterActivity.class));
                    finish();

                    // 使用 SharedPreferences 將 MainActivity 設置為主頁，下一次進入App時不會再進來這個Activity
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isMainPageSet", true);
                    editor.apply();
                }
            }
        });

        //skipButton onclick
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳轉頁面去MainActivity
                startActivity(new Intent(NavigationActivity.this,LoginRegisterActivity.class));
                finish();

                // 使用 SharedPreferences 將 MainActivity 設置為主頁，下一次進入App時不會再進來這個Activity
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isMainPageSet", true);
                editor.apply();
            }
        });
    }

    // 處理權限請求的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (arePermissionsGranted()) {
                // 已獲得權限
                Toast.makeText(this, "權限已獲取", Toast.LENGTH_SHORT).show();
            } else {
                // 拒絕了權限
                Toast.makeText(this, "未獲取權限，某些功能可能無法使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //顯示dot的func
    public void setDotIndicator(int position) {

        dots = new TextView[3];//以textView方式製造dot，這裡可以修改dot的數量
        dotIndicator.removeAllViews();//先清除之前顯示的dot

        //先顯示i個灰色的dot
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //&#8226 是html的unicode，代表的是dot
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            //設定dot的大小
            dots[i].setTextSize(35);
            //給dot上顏色，這裡上的顏色是灰色
            dots[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme()));
            //顯示dot
            dotIndicator.addView(dots[i]);
        }
        //再顯示特別顏色的dot。特別顏色是當前頁面的dot
        dots[position].setTextColor(getResources().getColor(R.color.lavender, getApplicationContext().getTheme()));
    }

    //計算 ViewPager 當前顯示的頁面索引加上一個偏移量
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}