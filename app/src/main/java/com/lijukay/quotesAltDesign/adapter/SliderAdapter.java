package com.lijukay.quotesAltDesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.lijukay.quotesAltDesign.R;

public class SliderAdapter extends PagerAdapter {

    private final Context CONTEXT;


    public SliderAdapter(Context context){
        CONTEXT = context;
    }

    public int[] slide_image = {
            0,
            0,
            0,
            0,
            R.drawable.feature1,
            R.drawable.feature2,
            0
    };

    public int[] slide_lottie = {
            R.raw.welcome,
            R.raw.no_ads,
            R.raw.relax,
            R.raw.data,
            0,
            0,
            R.raw.fun
    };


    public int[] slide_title = {
            R.string.welcome_title,
            R.string.no_ads_title,
            R.string.relax_title,
            R.string.many_qwotables_title,
            R.string.author_pages_title,
            R.string.information_title,
            R.string.thats_it_title
    };

    public int[] slide_message = {
            R.string.welcome_message,
            R.string.no_ads_message,
            R.string.relax_message,
            R.string.many_qwotables_message,
            R.string.author_pages_message,
            R.string.information_message,
            R.string.thats_it_message
    };

    public int[] slide_button_message = {
            R.string.slide_button_message,
            0,
            0,
            0,
            0,
            0,
            R.string.slide_button_end
    };


    @Override
    public int getCount(){
        return slide_title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o){
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.pages, container, false);

        TextView title = view.findViewById(R.id.titleOnBoarding);
        TextView message = view.findViewById(R.id.messageOnBoarding);
        LottieAnimationView lottie = view.findViewById(R.id.lottieOnBoarding);
        ImageView imageView = view.findViewById(R.id.imageOnBoardingScreen);
        TextView bottomMessage = view.findViewById(R.id.buttonInfo);

        title.setText(slide_title[position]);
        if (slide_button_message[position] == 0){
            bottomMessage.setText("");
        } else {
            bottomMessage.setText(slide_button_message[position]);
        }
        message.setText(slide_message[position]);
        if (slide_lottie[position] == 0){
            lottie.setVisibility(View.INVISIBLE);
            lottie.clearAnimation();
        } else {
            lottie.setVisibility(View.VISIBLE);
            lottie.setAnimation(slide_lottie[position]);
        }
        if (slide_image[position] == 0){
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(slide_image[position]);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}
