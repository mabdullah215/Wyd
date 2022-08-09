package com.mobileapp.wyd.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobileapp.wyd.R;
import com.mobileapp.wyd.model.Event;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>
{
    private ArrayList<Event> dataList;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    Picasso client;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public EventListAdapter(Context context, ArrayList<Event> eventList) {
        this.dataList = eventList;
        mContext=context;
        client=Picasso.get();
    }

    public void setDataList(ArrayList<Event> dataList) {
        this.dataList = dataList;
    }


    public ArrayList<Event> getDataList() {
        return dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_event_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public int getItemCount()
    {
        return dataList == null? 0: dataList.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        final Event item = dataList.get(position);
        holder.setDetails(item,position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mCategory,mTitle,mAddress,mParticipents;
        private TextView mDate;
        private ImageView eventImage;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mCategory=itemView.findViewById(R.id.tv_category);
            mTitle=itemView.findViewById(R.id.tv_name);
            mAddress=itemView.findViewById(R.id.tv_address);
            mDate=itemView.findViewById(R.id.tv_date);
            mParticipents=itemView.findViewById(R.id.tv_participents);
            eventImage=itemView.findViewById(R.id.img_source);
            eventImage.setClipToOutline(true);
            itemView.setOnClickListener(this);
        }

         public void setDetails(final Event item, final int pos)
         {
            client.load(item.getPhotos().get(0)).fit().into(eventImage);
            mCategory.setText(item.getCategory());
            mTitle.setText(item.getTitle());
            String address = "<b>Address: </b>" +item.getAddress();
            mAddress.setText(Html.fromHtml(address));
            String dateString = formatter.format(new Date(item.getTimestamp()));
            mDate.setText(dateString);
            String participents = "<b>Participents: </b>" +item.getParticipents().size();
            mParticipents.setText(Html.fromHtml(participents));

         }

        @Override
        public void onClick(View v)
        {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

}


