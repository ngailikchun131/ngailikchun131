package com.example.mall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MoreFragment extends Fragment {

    private final String TAG = "Test";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_more, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String id = pref.getString("uid","");

        if(id == ""){
            //do not have login record
            root.findViewById(R.id.btnEditPersonal).setVisibility(View.GONE);
            root.findViewById(R.id.btnChangePwd).setVisibility(View.GONE);
//            root.findViewById(R.id.btnForgetPwd).setVisibility(View.GONE);
        }

        root.findViewById(R.id.btnMallInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MallInfoFragment());
                ft.commit();
            }
        });

        root.findViewById(R.id.btnEditPersonal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new PersonalFragment());
                ft.commit();
            }
        });

        root.findViewById(R.id.btnChangePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ChangePwdFragment());
                ft.commit();
            }
        });

        root.findViewById(R.id.btnSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ChangeAppSetting());
                ft.commit();
            }
        });

        root.findViewById(R.id.changeMyLang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;


    }
}

