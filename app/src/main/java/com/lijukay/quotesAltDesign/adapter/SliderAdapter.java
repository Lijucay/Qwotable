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

    Context context;
    LayoutInflater layoutInflater;


    public SliderAdapter(Context context){
        this.context = context;
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


    public String[] slide_title = {
            "Welcome",
            "No ads!",
            "Made for you to relax",
            "Many Qwotables",
            "Author Pages",
            "Information",
            "That's it!"
    };

    public String[] slide_message = {
            "...to Qwotable. In the following, you will see features and instructions of Qwotable.",
            "Qwotable is an ad free application for you to relax and focuse on Qwotable's content!",
            "Qwotable's content are quotes and wisdom. It is made for you to relax and fill your brain with deep sentences.",
            "Qwotable has a lot of quotable stuff that will be updated from time to time. You will recognize it, because the newer the Qwotable, the more on the top it will be!",
            "Besides the quotes and wisdom pages, Qwotable offers a page with the quotes and wisdom, based on the author. You simply have to tap on the authors name to get there!",
            "Get the latest information of Qwotable in the Information page. For that, all you need to do is to swipe down to the bottom of the Homepage and click on 'Information'",
            "Have fun using Qwotable!"
    };

    public String[] slide_button_message = {
            "Simply click on the button below on or swipe to go on. You can also press the skip button in the bottom left corner",
            "",
            "",
            "",
            "",
            "",
            "Tap the button below to use Qwotable"
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

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.pages, container, false);


        TextView title = view.findViewById(R.id.titleOnBoarding);
        TextView message = view.findViewById(R.id.messageOnBoarding);
        LottieAnimationView lottie = view.findViewById(R.id.lottieOnBoarding);
        ImageView imageView = view.findViewById(R.id.imageOnBoardingScreen);
        TextView bottomMessage = view.findViewById(R.id.buttonInfo);

        title.setText(slide_title[position]);
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
        bottomMessage.setText(slide_button_message[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }



}
