package com.example.mall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GiftActivity extends AppCompatActivity {
    private final String TAG = "Test";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GiftInfoAdapter adapter;

    RecyclerView recyclerView;
    TextView tvMyPointBal;
    private Context mCtx;
    private String id;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gift);

        mCtx = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Redeem Gift");

        recyclerView = findViewById(R.id.recyclerView);
        tvMyPointBal = findViewById(R.id.tvMyPointBal);

        SharedPreferences pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        id = pref.getString("uid","");
        getPoint(id);

        setUpRecyclerView();
    }

    private void getPoint(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "db point is: " + document.getLong("Point"));
                        tvMyPointBal.setText("My Poins Balance: " + document.getLong("Point") + " Points");
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

    private void setUpRecyclerView() {
        Query query = db.collection("gift");
        FirestoreRecyclerOptions<GiftInfo> options = new FirestoreRecyclerOptions.Builder<GiftInfo>()
                .setQuery(query, GiftInfo.class)
                .build();

        adapter = new GiftInfoAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GiftInfoAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                GiftInfo gift = documentSnapshot.toObject(GiftInfo.class);
                String id = documentSnapshot.getId();
//                Toast.makeText(GiftActivity.this, "ID: " + id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mCtx, RedeemDetailActivity.class);
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
        getPoint(id);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(adapter != null) {
            adapter.stopListening();
        }
    }
}