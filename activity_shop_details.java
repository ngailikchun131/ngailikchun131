package com.example.mall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_shop_details extends AppCompatActivity {

    private final String TAG = "Test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvshopno, tvtel, tvophour, tvhowtogo, tvlocation;
    private ImageView imshop;
    private Context mCtx;
    private String giftName, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        mCtx = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Shop Details");

        tvshopno = findViewById(R.id.tvshopno);
        tvtel = findViewById(R.id.tvtel);
        tvophour = findViewById(R.id.tvophour);
        imshop = findViewById(R.id.imshop);
        tvhowtogo = findViewById(R.id.tvhowtogo);
        tvlocation = findViewById(R.id.tvlocation);

        final String docID = getIntent().getExtras().getString("docID", "");
        getGiftInformation(docID);

    }

    private void getGiftInformation(String docID) {
        DocumentReference docRef = db.collection("shop").document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        url = document.getString("url");
                        tvshopno.setText("ShopName: "+ document.getString("name"));
                        tvshopno.setTextColor(Color.BLACK);
                        tvtel.setText("Tel: "+ document.getString("Tel"));
                        tvtel.setTextColor(Color.BLACK);
                        tvlocation.setText("Location: "+ document.getString("ShopNo"));
                        tvlocation.setTextColor(Color.BLACK);
                        tvophour.setText("Opening Hour: "+ document.getString("OpeningHour"));
                        tvophour.setTextColor(Color.BLACK);
                        //bluetooth
                        tvhowtogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(v.getContext(), RangingActivity.class);
                                myIntent.putExtra("SNAME", document.getString("name"));
                                startActivity(myIntent);
                            }
                        });
                        //
                        Glide.with(mCtx).load(document.getString("url")).into(imshop);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}

