package com.example.socialapps.StartupPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.socialapps.R;

import pl.droidsonroids.gif.GifImageView;

//在這個PagerAdapter中，使用LottieAnimationView類別來顯示幻燈片的動畫，並使用TextView來顯示標題和描述。
//通過在sliderAllImages、sliderAllTitle和sliderAllDesc數組中定義相應的資源，我們可以在ViewPager中動態顯示多個幻燈片頁面。
public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    int sliderAllImages[] = { R.raw.page1, R.raw.page2, R.raw.page3 };

    int sliderAllTitle[] = { R.string.screen1, R.string.screen2, R.string.screen3 };

    int sliderAllDesc[] = { R.string.screen1desc, R.string.screen2desc, R.string.screen3desc };

    //這是該類別的建構子，用於初始化 ViewPagerAdapter 物件。
    //傳入的 context 會被存儲在成員變數 context 中，以便在需要時可以在整個類別內部使用。
    public ViewPagerAdapter(Context context){ this.context = context; }

    //此方法返回 ViewPager 中包含的幻燈片數量，通常等於所顯示的頁面數量。
    @Override
    public int getCount() {
        return sliderAllTitle.length;
    }

    //此方法判斷給定的View是否來自給定的Object。
    //在這個例子中，它檢查給定的View是否是由instantiateItem方法所返回的View，如果是返回true，否則返回false。
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    //在這個方法中，我們根據當前的位置(position)創建幻燈片的View，並將它加入到ViewPager的容器中。
    //在這個例子中，我們使用LayoutInflater從slider_screen.xml佈局中填充 View，然後設置該幻燈片的標題、描述和圖片。
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_screen,container,false);

        LottieAnimationView sliderImage = view.findViewById(R.id.sliderImage);
        TextView sliderTitle = view.findViewById(R.id.sliderTitle);
        TextView sliderDesc = view.findViewById(R.id.sliderDesc);

        sliderImage.setAnimation(this.sliderAllImages[position]);
        sliderTitle.setText(this.sliderAllTitle[position]);
        sliderDesc.setText(this.sliderAllDesc[position]);

        container.addView(view);

        return view;
    }

    //這個方法用於在銷毀幻燈片時從ViewPager的容器中刪除它。
    //這樣可以釋放資源，以避免過多的佈局在內存中佔用空間。
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}