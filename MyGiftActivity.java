package com.example.mall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyGiftActivity extends AppCompatActivity {
    private final String TAG = "Test";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MyGiftInfoAdapter adapter;

    private RecyclerView recyclerView;
    private TextView tvStatus;
    private Context mCtx;
    private String uid;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift);

        mCtx = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Gift");

        recyclerView = findViewById(R.id.recyclerView);
//        tvStatus = findViewById(R.id.tvStatus);

        SharedPreferences pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        uid = pref.getString("uid","");


        setUpRecyclerView();
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

    private void setUpRecyclerView() {
        Log.d(TAG,"Start setUpRecyclerView");

        Query query = db.collection("redeemRecord")
                .whereEqualTo("userID", uid)
                .orderBy("receive");

        FirestoreRecyclerOptions<MyGiftInfo> options = new FirestoreRecyclerOptions.Builder<MyGiftInfo>()
                .setQuery(query, MyGiftInfo.class)
                .build();

        adapter = new MyGiftInfoAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(adapter != null) {
            adapter.stopListening();
        }
    }
}