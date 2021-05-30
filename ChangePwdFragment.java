package com.example.mall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;

public class ChangePwdFragment extends Fragment {
    private final String TAG = "Test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvPhoneNumber;
    EditText etCurrPwd, etNewPwd, etRePwd;
    String pwd;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_change_pwd, container, false);


        //Get the local record
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final String id = pref.getString("uid", "");

        tvPhoneNumber = root.findViewById(R.id.ePhoneNumber);
        etCurrPwd = root.findViewById(R.id.eCurrPassword);
        etNewPwd = root.findViewById(R.id.eNewPassword);
        etRePwd = root.findViewById(R.id.eRepeatPassword);

        getInfo(id);

        root.findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty(etCurrPwd.getText().toString()) || isEmpty(etNewPwd.getText().toString()) || isEmpty(etRePwd.getText().toString())) {
                    Toast.makeText(getActivity(), "Please input all information", Toast.LENGTH_SHORT).show();
                }else if(!(etCurrPwd.getText().toString()).equals(pwd)) {
                    Toast.makeText(getActivity(), "Current Password is not correct", Toast.LENGTH_SHORT).show();
                }else if(!(etNewPwd.getText().toString()).equals(etRePwd.getText().toString())){
                    Toast.makeText(getActivity(), "New Password and Repeat Password are not same", Toast.LENGTH_SHORT).show();
                }else{
                    DocumentReference washingtonRef = db.collection("users").document(id);
                    washingtonRef
                            .update(
                                    "Password", etNewPwd.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    Toast.makeText(getActivity(), "Change Password success", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }
        });
        return root;
    }

    private void getInfo(String userID) {
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //phone number
                        Log.d(TAG, "Phonr Number is: " + document.getString("Phonenum"));
                        tvPhoneNumber.setText(document.getString("Phonenum"));

                        Log.d(TAG, "Password is: " + document.getString("Password"));
                        pwd = document.getString("Password");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
