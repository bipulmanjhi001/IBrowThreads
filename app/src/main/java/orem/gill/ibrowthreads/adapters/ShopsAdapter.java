package orem.gill.ibrowthreads.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.activities.ShopDetailActivity;
import orem.gill.ibrowthreads.pojoclasses.ShopsPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;

/**
 * Created by Dawinder on 14/11/2016.
 */

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ChatHolder> {

    private Context mContext;
    private ArrayList<ShopsPOJO> list;

    public ShopsAdapter(Context mContext, ArrayList<ShopsPOJO> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shops, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TinyDB(mContext).putString(GeneralValues.SELECTED_SHOP,new Gson().toJson(list.get(holder.getAdapterPosition())));
                ((Activity)mContext).startActivity(new Intent(mContext, ShopDetailActivity.class));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {
        ShopsPOJO item=list.get(position);
        holder.tvTitle.setText(item.getName());
        holder.tvDes.setText(item.getDes());

        Utils.loadImage(mContext,item.getImage(),holder.ivImage,R.drawable.ic_placeholder,R.drawable.ic_placeholder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView tvTitle,tvDes;
        ImageView ivImage;

        public ChatHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDes = (TextView) itemView.findViewById(R.id.tvDes);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
}
