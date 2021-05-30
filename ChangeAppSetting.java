package com.example.mall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ChangeAppSetting extends Fragment {
    private final String TAG = "Test";
    private Button btnon, btnoff;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_change_app_setting, container, false);
        final SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        btnon = root.findViewById(R.id.On);
        btnoff = root.findViewById(R.id.Off);

        btnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("ad", "on").apply();
                String alertOn= getResources().getString(R.string.alertOn);
                Toast.makeText(getActivity(),alertOn , Toast.LENGTH_SHORT).show();
                Intent i = getActivity().getBaseContext().getPackageManager().
                        getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        btnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("ad", "off").apply();
                String alertOff= getResources().getString(R.string.alertOff);
                Toast.makeText(getActivity(), alertOff, Toast.LENGTH_SHORT).show();
                Intent i = getActivity().getBaseContext().getPackageManager().
                        getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        return root;
    }
}
