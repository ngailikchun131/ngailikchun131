package com.example.mall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MyGiftInfoAdapter extends FirestoreRecyclerAdapter<MyGiftInfo, MyGiftInfoAdapter.MyGiftInfoHolder> {
    private OnItemClickListener listener;


    public MyGiftInfoAdapter(@NonNull FirestoreRecyclerOptions<MyGiftInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyGiftInfoHolder myGiftInfoHolder, int i, @NonNull MyGiftInfo myGiftInfo) {
        myGiftInfoHolder.tvName.setText(myGiftInfo.getGiftName());
        if(myGiftInfo.isReceive() == false){
            myGiftInfoHolder.tvStatus.setText("Status: Unreceived");
        }else{
            myGiftInfoHolder.tvStatus.setText("Status: Received");
        }

        Glide.with(myGiftInfoHolder.ivIcon.getContext()).load(myGiftInfo.getUrl()).into(myGiftInfoHolder.ivIcon);
    }

    @NonNull
    @Override
    public MyGiftInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_gift_item,parent,false);

        return new MyGiftInfoHolder(v);
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class MyGiftInfoHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvStatus;
        ImageView ivIcon;

        public MyGiftInfoHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMyGiftName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivIcon = itemView.findViewById(R.id.ivMyGiftPic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }


}
