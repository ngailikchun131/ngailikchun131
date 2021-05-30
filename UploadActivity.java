package com.example.mall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.text.TextUtils.isEmpty;

public class UploadActivity extends AppCompatActivity {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button btnChoose, btnUpload, btnCheck;
    private ImageView imageView;

    private EditText date, amount, invno;
    private Spinner shopName;
    private TextView tvshopName;

    private Uri filePath;
    private String imgPath, sdate, sid, imghtmluri;
    private boolean n = false;
    private int f = 0;
    private boolean birB = false;
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);

        imageView = findViewById(R.id.imgView);
        invno = findViewById(R.id.invno);

        shopName = findViewById(R.id.ShopName);
        date = findViewById(R.id.Date);
        amount = findViewById(R.id.Amount);

        tvshopName = findViewById(R.id.tvshopname);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Upload Receipt");

        //add shop into drop down box
        //create a list of items for the spinner.
        final ArrayList<String> items = new ArrayList<String>();


        //get shop name
        db.collection("shop")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(document.get("name").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //???
        items.add("");

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        //set text color
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

//        //update data
//        adapter.notifyDataSetChanged();
        //set the spinners adapter to the previously created one.
        shopName.setAdapter(adapter);

        shopName.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UploadActivity.this, "You selected " + shopName.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                tvshopName.setText(shopName.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() == null) {
                    Toast.makeText(UploadActivity.this, "Please upload an image first", Toast.LENGTH_SHORT).show();
                } else {
                    sdate = date.getText().toString();
                    //get date
                    String[] n = sdate.split("/");
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    if (shopName.getSelectedItem() == null || isEmpty(date.getText().toString()) || isEmpty(amount.getText().toString())) {
                        Toast.makeText(UploadActivity.this, "Please input all information", Toast.LENGTH_SHORT).show();
                    } else if (amount.getText().toString().matches("[0-9]+") == false) {
                        Toast.makeText(UploadActivity.this, "Please input correct amount format example:100", Toast.LENGTH_SHORT).show();
                    } else if (n[0].length() != 2 || n[1].length() != 2 || n[2].length() != 4) {
                        Toast.makeText(UploadActivity.this, "Please input proper date example: dd/mm/yyyy 01/01/1999", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(n[0]) > 31 || Integer.parseInt(n[1]) > 12 || Integer.parseInt(n[2]) > Calendar.getInstance().get(Calendar.YEAR)) {
                        Toast.makeText(UploadActivity.this, "Please input valid day", Toast.LENGTH_SHORT).show();
                    } else {
                        checking();
                    }
                }
            }
        });
    }



    private void uploadImage() {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading " + (int) progress + "%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String gslink = taskSnapshot.getStorage().toString();
                    StorageMetadata SnapshotMetadata = taskSnapshot.getMetadata();
                    Task<Uri> downloadUrl = ref.getDownloadUrl();
                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imghtmluri = uri.toString();
                            uploaddata(gslink, imghtmluri);
                            Log.d(TAG, "FUCK "+imghtmluri);
                        }
                    });
                    //change page?
////                    uploaddata(taskSnapshot.getStorage().toString(), imghtmluri
//                        );

                }
            });
        }


//    else{
//        Toast.makeText(UploadActivity.this, "You already uploaded this receipt", Toast.LENGTH_LONG).show();
//    }



    private void checking() {
        //get shop id
        db.collection("shop")
                .whereEqualTo("name", shopName.getSelectedItem().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sid = document.getId();
                            }
                            //check receipt
                            db.collection("shopReceipts")
                                    .whereEqualTo("shopID", sid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                f = 0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, "fuck" + document.getId() + " => " + document.getData());
//                                                    if (document.get("invNo").equals(invno.getText().toString()) == false ) {
////                                                    Toast.makeText(UploadActivity.this, "Invoice haven't registered", Toast.LENGTH_LONG).show();
//                                                        Log.d(TAG, "fuck go");
//                                                        Log.d(TAG, Integer.toString(f));
                                                    if (document.get("invNo").equals(invno.getText().toString())) {
                                                        f++;
                                                        Log.d(TAG, "fuck stop");
                                                        Log.d(TAG, Integer.toString(f));
                                                    }
                                                }
                                                Log.d(TAG, "fuck last "+ f);
                                                if(f <= 0){
                                                    uploadImage();
                                                }else{
                                                    Toast.makeText(UploadActivity.this, "Invoice Number already registered", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Error getting shop id: ", task.getException());
                        }
                    }
                });

    }


    private void uploaddata(String uri, String htmluri) {
        //sharepreference
        SharedPreferences pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        //store data
        Map<String, Object> data = new HashMap<>();

        //check Birthday
        String[] n = sdate.split("/");
        if (n[0] == pref.getString("userbird", "") && n[1] == pref.getString("userbirm", "")) {
            birB = true;
        }
        //Get the local record

        String id = pref.getString("uid", "");

        data.put("uid", id);
        data.put("ShopName", shopName.getSelectedItem().toString());
        data.put("Shopid", sid);
        data.put("ReceiptID", invno.getText().toString());
        data.put("Date", date.getText().toString());
        data.put("status", "Waiting for approve");
        data.put("isBirthday", birB);
        data.put("Point", amount.getText().toString());
        data.put("gsUrl", uri);
        data.put("htmlUrl", htmluri);
        data.put("d", n[0]);
        data.put("m", n[1]);
        data.put("y", n[2]);

        db.collection("receipts").document(UUID.randomUUID().toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filePath = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

