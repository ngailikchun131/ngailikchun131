package com.example.mall;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private final String TAG = "Test";
    View view;
    private Banner banner;
    List<Integer> images = new ArrayList<Integer>();
    ImageView ivShop1, ivShop2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);


        ivShop1 = view.findViewById(R.id.ivShop1);
        ivShop2 = view.findViewById(R.id.ivShop2);

//        Slide Show
        initSlider();

        view.findViewById(R.id.ivEvent1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),EventDetailActivity.class);
                intent.putExtra("img", R.drawable.event1);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.ivEvent2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),EventDetailActivity.class);
                intent.putExtra("img", R.drawable.event2);
                startActivity(intent);
            }
        });

        getPic();

        return view;
    }

    private void getPic() {
        //New Shop 1
        String url="https://firebasestorage.googleapis.com/v0/b/fypfirebaseproject-93ae0.appspot.com/o/Shop%2Fshop1.png?alt=media&token=8d41eec0-a94e-49fe-83ca-1cfdc8df43f2";//Retrieved url as mentioned above
        Glide.with(getActivity().getApplicationContext()).load(url).into(ivShop1);
        //New Shop 2
        url="https://firebasestorage.googleapis.com/v0/b/fypfirebaseproject-93ae0.appspot.com/o/Shop%2Fshop2.png?alt=media&token=dccb79ea-c212-45bf-9878-293b46ef02c8";
        Glide.with(getActivity().getApplicationContext()).load(url).into(ivShop2);
    }

    private void initSlider() {
        images.add(R.drawable.banner1);
        images.add(R.drawable.banner2);
        images.add(R.drawable.banner3);
        banner = view.findViewById(R.id.banner);
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setDelayTime(5000);
        banner.start();
    }

}