package com.example.mall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.Timestamp.now;

public class RedeemDetailActivity extends AppCompatActivity {
    private final String TAG = "Test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvGPoint, tvName, tvStock;
    private ImageView ivGIcon;
    private Context mCtx;
    private int stock = 0, myPoint = 0, giftPoint = 0;
    private String giftName, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_detail);
        mCtx = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Redeem Gift Detail");

        tvGPoint = findViewById(R.id.ivGiftPoint_D);
        tvName = findViewById(R.id.ivGiftName_D);
        ivGIcon = findViewById(R.id.ivGiftIcon_D);
        tvStock = findViewById(R.id.ivGiftStock_D);

        SharedPreferences pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final String id = pref.getString("uid", "");
        getPoint(id);

        final String docID = getIntent().getExtras().getString("docID", "");
        getGiftInformation(docID);

        findViewById(R.id.btnRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gift stock
                if (stock > 0) {
                    Log.d(TAG, "Redeem: enough stock");
                    //point
                    if (myPoint >= giftPoint) {
                        //create gift record
                        Map<String, Object> giftRecord = new HashMap<>();
                        giftRecord.put("userID", id);
                        giftRecord.put("giftID", docID);
                        giftRecord.put("giftName", giftName);
                        giftRecord.put("url", url);
                        giftRecord.put("qty", 1);
                        giftRecord.put("receive", false);
                        giftRecord.put("Created", now());

                        db.collection("redeemRecord")
                                .add(giftRecord)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                        //modify point(user)
                                        DocumentReference washingtonRef = db.collection("users").document(id);
                                        washingtonRef
                                                .update("Point", (myPoint-giftPoint))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                                                        //modify stock(gift)
                                                        DocumentReference washingtonRef = db.collection("gift").document(docID);
                                                        washingtonRef
                                                                .update("stock", (stock-1))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                        Toast.makeText(mCtx, "Redeem success", Toast.LENGTH_LONG).show();
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error updating document", e);
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    } else {
                        Toast.makeText(mCtx, "Sorry, you do not have enough points", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mCtx, "Sorry, we do not have enough stock", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void getGiftInformation(String docID) {
        DocumentReference docRef = db.collection("gift").document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        stock = document.getLong("stock").intValue();
                        giftPoint = document.getLong("point").intValue();
                        url = document.getString("url");
                        giftName = document.getString("name");
                        tvGPoint.setText(document.getLong("point") + "Pts");
                        tvName.setText(document.getString("name"));
                        tvStock.setText("Stock: " + document.getLong("stock"));
                        Glide.with(mCtx).load(document.getString("url")).into(ivGIcon);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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
                        myPoint = document.getLong("Point").intValue();
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
