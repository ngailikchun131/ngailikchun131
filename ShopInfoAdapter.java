package com.example.mall;

import android.graphics.Color;
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

import static android.app.PendingIntent.getActivity;

public class ShopInfoAdapter extends FirestoreRecyclerAdapter<ShopInfo, ShopInfoAdapter.ShopImageHolder> {
    private OnItemClickListener listener;


    public ShopInfoAdapter(@NonNull FirestoreRecyclerOptions<ShopInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShopImageHolder shopImageHolder, int i, @NonNull ShopInfo shopInfo) {

        shopImageHolder.tvName.setText(" ShopName: "+shopInfo.getName());
        shopImageHolder.tvName.setTextColor(Color.BLACK);
        Glide.with(shopImageHolder.ivIcon.getContext()).load(shopInfo.getUrl()).into(shopImageHolder.ivIcon);
    }

    @NonNull
    @Override
    public ShopImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item,parent,false);

        return new ShopImageHolder(v);
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class ShopImageHolder extends RecyclerView.ViewHolder{
        TextView tvName ,tvhowtogo;
        ImageView ivIcon;

        public ShopImageHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGiftName);
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
