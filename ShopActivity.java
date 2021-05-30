package com.example.mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShopActivity extends AppCompatActivity {
    private final String TAG = "Test";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ShopInfoAdapter adapter;

    RecyclerView recyclerView;
    private Context mCtx;
    private String id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shop);

        mCtx = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Shop");

        recyclerView = findViewById(R.id.recyclerView);

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
        Query query = db.collection("shop");
        FirestoreRecyclerOptions<ShopInfo> options = new FirestoreRecyclerOptions.Builder<ShopInfo>()
                .setQuery(query, ShopInfo.class)
                .build();

        adapter = new ShopInfoAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ShopInfoAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ShopInfo shop = documentSnapshot.toObject(ShopInfo.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(mCtx, activity_shop_details.class);
                intent.putExtra("docID", id);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
