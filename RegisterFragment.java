package com.example.mall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.text.TextUtils.isEmpty;
import static com.google.firebase.Timestamp.now;


public class RegisterFragment extends Fragment {

    Button btnregister;
    String sname, spassword, sphonenum, sbirthday, scity, scountry;
    int i = 0;
    EditText name, password, phonenum, birthday, city, country;
    TextView tvlogin;
    RadioGroup radioGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);


        btnregister = (Button) root.findViewById(R.id.btnRegister);
        tvlogin = (TextView) root.findViewById(R.id.tvlogin);


        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new LoginFragment());
                ft.commit();
            }
        });


        btnregister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                name = (EditText) getView().findViewById(R.id.eName);
                password = (EditText) getView().findViewById(R.id.ePassword);
                phonenum = (EditText) getView().findViewById(R.id.ePhoneNumber);
                birthday = (EditText) getView().findViewById(R.id.eBirthday);
                country = (EditText) getView().findViewById(R.id.eCountry);
                radioGroup = (RadioGroup) getView().findViewById(R.id.radio);


                sname = name.getText().toString();
                spassword = password.getText().toString();
                sphonenum = phonenum.getText().toString();
                sbirthday = birthday.getText().toString();
                scountry = country.getText().toString();


                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (isEmpty(phonenum.getText().toString()) || isEmpty(name.getText().toString()) || isEmpty(password.getText().toString())
                        || isEmpty(birthday.getText().toString()) || isEmpty(country.getText().toString())|| radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getActivity(), "Please input all information", Toast.LENGTH_SHORT).show();
                } else if (sphonenum.length() != 8) {
                    Toast.makeText(getActivity(), "Please input proper phone number example: 6262 6262", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("users").document(sphonenum).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //get birthday
                            String[] n = sbirthday.split("/");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    Toast.makeText(getActivity(), "User already registered", Toast.LENGTH_SHORT).show();
                                } else if (n[0].length() != 2 || n[1].length() != 2 || n[2].length() != 4) {
                                    Toast.makeText(getActivity(), "Please input proper birthday example: dd/mm/yyyy 01/01/1999", Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(n[0]) > 31 || Integer.parseInt(n[1]) > 12 || Integer.parseInt(n[2]) > Calendar.getInstance().get(Calendar.YEAR)) {
                                    Toast.makeText(getActivity(), "Please input valid day", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Create a new user with a first and last name
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("UserID", sphonenum);
                                    user.put("Name", sname);
                                    user.put("Password", spassword);
                                    user.put("Phonenum", sphonenum);
                                    user.put("AccountType", "Cus");
                                    user.put("Birthday", sbirthday);
                                    user.put("Country", scountry);
                                    user.put("Point", 0);
                                    user.put("d", n[0]);
                                    user.put("m", n[1]);
                                    user.put("y", n[2]);
                                    user.put("Lock", false);
                                    // get selected radio button from radioGroup
                                    int selectedId = radioGroup.getCheckedRadioButtonId();
                                    // find the radiobutton by returned id
                                    RadioButton radioButton;
                                    // find the radiobutton by returned id
                                    radioButton = (RadioButton) getView().findViewById(selectedId);
                                    user.put("gender", radioButton.getText());
                                    user.put("Createat", now());

                                    db.collection("users").document(phonenum.getText().toString()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            Toast.makeText(getActivity(), "Register success", Toast.LENGTH_SHORT).show();
                                            //change page
                                            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MemberFragment());
                                            ft.commit();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                }
                            }
                        }
                    });

                }
            }
        });
        return root;
    }
}
