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

import java.util.Calendar;

import static android.text.TextUtils.isEmpty;

public class PersonalFragment extends Fragment {
    private final String TAG = "Test";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvPhoneNumber;
    EditText etName, etBirthday, etCountry;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_personal, container, false);


        //Get the local record
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final String id = pref.getString("uid", "");

        tvPhoneNumber = root.findViewById(R.id.ePhoneNumber);
        etName = root.findViewById(R.id.eName);
        etBirthday = root.findViewById(R.id.eBirthday);
        etCountry = root.findViewById(R.id.eCountry);

        getInfo(id);

        root.findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] n = etBirthday.getText().toString().split("/");


                if (isEmpty(etName.getText().toString()) || isEmpty(etBirthday.getText().toString()) || isEmpty(etCountry.getText().toString())) {
                    Toast.makeText(getActivity(), "Please input all information", Toast.LENGTH_SHORT).show();
                } else if (n[0].length() > 2 || n[1].length() > 2 || n[2].length() != 4) {
                    Toast.makeText(getActivity(), "Please input proper birthday example: dd/mm/yyyy", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(n[0]) > 31 || Integer.parseInt(n[1]) > 12 || Integer.parseInt(n[2]) > Calendar.getInstance().get(Calendar.YEAR)) {
                    Toast.makeText(getActivity(), "Please input valid birthday example: dd/mm/yyyy", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference washingtonRef = db.collection("users").document(id);

                    washingtonRef
                            .update(
                                    "d", n[0],
                                    "m",n[1],
                                    "y",n[2],
                                    "Name",etName.getText().toString(),
                                    "Country", etCountry.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    String alertInformationSuccess= getResources().getString(R.string.alertInformationSuccess);
                                    Toast.makeText(getActivity(), alertInformationSuccess, Toast.LENGTH_SHORT).show();
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
                        Log.d(TAG, "Phone Number is: " + document.getString("Phonenum"));
                        tvPhoneNumber.setText(document.getString("Phonenum"));

                        //User Name
                        Log.d(TAG, "User Name is: " + document.getString("Name"));
                        etName.setText(document.getString("Name"));

                        //Birthday
                        Log.d(TAG, "Birthday is: " + document.getString("d") + "/" + document.getString("m") + "/" + document.getString("y"));
                        etBirthday.setText(document.getString("d") + "/" + document.getString("m") + "/" + document.getString("y"));

                        //Country
                        Log.d(TAG, "Country is: " + document.getString("Country"));
                        etCountry.setText(document.getString("Country"));
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
