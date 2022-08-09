package com.mobileapp.wyd.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobileapp.wyd.R;



public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder>
{
    int selected=0;
    Context mContext;
    private OnItemClickListener onItemClickListener;
    String[] dataList={"Show All","Bars","Clubs","Church","Special Events","Educational"};

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FilterListAdapter(Context context)
    {
        selected=0;
        mContext=context;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public int getItemCount()
    {
        return dataList == null? 0: dataList.length;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        final String item = dataList[position];
        holder.setDetails(item,position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mName;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mName=itemView.findViewById(R.id.tv_filter);
            itemView.setOnClickListener(this);
        }

         public void setDetails(final String item,int position)
         {
             mName.setText(item);
             if(position==selected)
             {
                 mName.getBackground().setTint(mContext.getColor(R.color.purple_200));
                 mName.setTextColor(mContext.getColor(R.color.text_color_highlight));
             }
             else
             {
                 mName.getBackground().setTint(mContext.getColor(R.color.box_color_default));
                 mName.setTextColor(mContext.getColor(R.color.text_color_default));
             }
         }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(getAdapterPosition());
        }
    }


}


