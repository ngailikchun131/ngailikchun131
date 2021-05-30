package com.example.mall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "Test";
    private TextView tvpoint,tvName, tvID;
    private Button btnlogout;
    private String id;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_member, container, false);

        tvpoint = root.findViewById(R.id.tvPoint);
        tvName = root.findViewById(R.id.tvUserName);
        tvID = root.findViewById(R.id.tvUserID);
        btnlogout = root.findViewById(R.id.btnlogout);


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                pref.edit().clear().commit();
                //change page
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MemberFragment());
                ft.commit();
            }
        });

        root.findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UploadActivity.class);
                intent.putExtra("img", R.drawable.ic_file_upload_black_24dp);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btnBirthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BirthdayActivity.class);
                intent.putExtra("img", R.drawable.birthday);
                startActivity(intent);
            }
        });

        root.findViewById(R.id.btnGift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new GiftActivity());
//                ft.commit();
                startActivity(new Intent(getActivity(),GiftActivity.class));
            }
        });

        root.findViewById(R.id.btnMyGift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new GiftActivity());
//                ft.commit();
                startActivity(new Intent(getActivity(),MyGiftActivity.class));
            }
        });


        //Get the local record
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        id = pref.getString("uid","");

        if(id != ""){
            //have login record
            String memberIDAS= getResources().getString(R.string.memberIDAS);
            getUserName(id);     //set the username
            getPoint(id);    //set the point of the user
            tvID.setText( memberIDAS+ id);
        }else{
            //do not have login record
            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new LoginFragment());
            ft.commit();
        }
        return root;
    }


    private void getPoint(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String myPoint= getResources().getString(R.string.myPoint);
                        String PointVa= getResources().getString(R.string.PointVa);
                        Log.d(TAG, "db point is: " + document.getLong("Point"));
                        tvpoint.setText(myPoint+"\n " + document.getLong("Point") + PointVa);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void getUserName(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "UserName is: " + document.getString("Name"));
                        tvName.setText(document.getString("Name"));
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
    public void onStart() {
        super.onStart();
        //getPoint(id);
    }
}