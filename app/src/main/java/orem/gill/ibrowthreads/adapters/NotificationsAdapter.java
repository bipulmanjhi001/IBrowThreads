package orem.gill.ibrowthreads.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.pojoclasses.NotificationsPOJO;

/**
 * Created by Dawinder on 15/11/2016.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ChatHolder> {

    private Context mContext;
    private ArrayList<NotificationsPOJO> list;

    public NotificationsAdapter(Context mContext, ArrayList<NotificationsPOJO> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);

        final ChatHolder holder = new ChatHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatHolder holder, final int position) {
        NotificationsPOJO item=list.get(position);
        holder.tvText.setText(item.getDes());
        holder.tvDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView tvText,tvDate;

        public ChatHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
