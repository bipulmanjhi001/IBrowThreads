package orem.gill.ibrowthreads.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.pojoclasses.OffersPOJO;
import orem.gill.ibrowthreads.utils.Utils;

/**
 * Created by Dawinder on 15/11/2016.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ChatHolder> {

    private Context mContext;
    private ArrayList<OffersPOJO> list;

    public OffersAdapter(Context mContext, ArrayList<OffersPOJO> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offers, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {
        OffersPOJO item=list.get(position);
        holder.tvTitle.setText(item.getName());
        holder.tvDes.setText(item.getDes());
        holder.tvStamp.setText(mContext.getString(R.string.stamps_required)+" "+item.getStamp());

        Utils.loadImage(mContext,item.getImage(),holder.ivImage,R.drawable.ic_placeholder,R.drawable.ic_placeholder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView tvTitle,tvDes,tvStamp;
        ImageView ivImage;

        public ChatHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDes = (TextView) itemView.findViewById(R.id.tvDes);
            tvStamp = (TextView) itemView.findViewById(R.id.tvStamp);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
}
