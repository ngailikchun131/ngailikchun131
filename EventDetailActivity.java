package com.example.mall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class EventDetailActivity extends AppCompatActivity {
    ImageView ivIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ivIcon = findViewById(R.id.ivEventDetail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int img = bundle.getInt("img");
            ivIcon.setImageResource(img);
        }
    }
}
