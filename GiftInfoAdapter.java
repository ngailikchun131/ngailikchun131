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

public class GiftInfoAdapter extends FirestoreRecyclerAdapter<GiftInfo, GiftInfoAdapter.GiftInfoHolder> {
    private OnItemClickListener listener;


    public GiftInfoAdapter(@NonNull FirestoreRecyclerOptions<GiftInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GiftInfoHolder giftInfoHolder, int i, @NonNull GiftInfo giftInfo) {
        giftInfoHolder.tvName.setText(giftInfo.getName());
        giftInfoHolder.tvPoint.setText(Integer.toString(giftInfo.getPoint()) + " point" );

        Glide.with(giftInfoHolder.ivIcon.getContext()).load(giftInfo.getUrl()).into(giftInfoHolder.ivIcon);
    }

    @NonNull
    @Override
    public GiftInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_item,parent,false);

        return new GiftInfoHolder(v);
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class GiftInfoHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvPoint;
        ImageView ivIcon;

        public GiftInfoHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGiftName);
            tvPoint = itemView.findViewById(R.id.tvGiftPoint);
            ivIcon = itemView.findViewById(R.id.ivGiftPic);

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
