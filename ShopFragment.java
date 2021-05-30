package com.example.mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShopFragment extends Fragment {

    private final String TAG = "Test";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ShopInfoAdapter adapter;

    RecyclerView recyclerView;
    private Context mCtx;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);
//        startActivity(new Intent(getActivity(),ShopActivity.class));


        // 1. get a reference to recyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        initView(view);
        return view;
    }

    private void initView(View view) {

//        recyclerView.setHasFixedSize(true);

        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Query query = db.collection("shop");
        FirestoreRecyclerOptions<ShopInfo> options = new FirestoreRecyclerOptions.Builder<ShopInfo>()
                .setQuery(query, ShopInfo.class)
                .build();


        // 3. create an adapter
        adapter = new ShopInfoAdapter(options);

        // 4. set adapter
        recyclerView.setAdapter(adapter);


        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(new ShopInfoAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ShopInfo shop = documentSnapshot.toObject(ShopInfo.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(getContext(), activity_shop_details.class);
                intent.putExtra("docID", id);
                startActivity(intent);
            }
        });
    }

    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}