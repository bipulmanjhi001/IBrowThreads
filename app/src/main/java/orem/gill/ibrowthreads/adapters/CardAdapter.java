package orem.gill.ibrowthreads.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.activities.LoyalityCardActivity;
import orem.gill.ibrowthreads.pojoclasses.CardPOJO;

/**
 * Created by Dawinder on 16/11/2016.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ChatHolder> {

    private Context mContext;
    private ArrayList<CardPOJO> list;
    int stamp=0;

    public CardAdapter(Context mContext, ArrayList<CardPOJO> list,int stamp) {
        this.mContext = mContext;
        this.list = list;
        this.stamp = stamp;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loyality_card, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(list.get(holder.getAdapterPosition()).getId())==(stamp+1)){
                    ((LoyalityCardActivity)mContext).showSecurityCodeDialog();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {
        CardPOJO item=list.get(position);
        holder.tvStamp.setText(item.getValue());
        if(Integer.parseInt(item.getId())<=stamp){
            holder.tvStamp.setBackgroundResource(R.drawable.custom_card_select);
            holder.tvStamp.setTextColor(ContextCompat.getColor(mContext,R.color.colorWhite));
        }else if(Integer.parseInt(item.getId())==(stamp+1)){
            holder.tvStamp.setBackgroundResource(R.drawable.custom_card_unselect);
            holder.tvStamp.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));
        }else{
            holder.tvStamp.setBackgroundResource(R.drawable.custom_card_disabled);
            holder.tvStamp.setTextColor(ContextCompat.getColor(mContext,android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView tvStamp;

        public ChatHolder(View itemView) {
            super(itemView);
            tvStamp = (TextView) itemView.findViewById(R.id.tvStamp);
        }
    }
}
