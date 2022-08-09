package com.mobileapp.wyd.utils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.mobileapp.wyd.R;
import java.util.ArrayList;


public class CustomerMenu extends RelativeLayout
{
    ImageView imgHome,imgNotifications,imgProfile;
    ArrayList<ImageView>imgList=new ArrayList<>();
    PositionUpdateListener updateListener;
    int selected=0;
    int type=0;
    Context mContext;

    public interface PositionUpdateListener
    {
        void onPositionUpdate(int pos);
    }

    public CustomerMenu(Context context)
    {
        super(context);
        mContext=context;
        initView();
    }


    public void setType(int type)
    {
        this.type = type;
        selectOption(0);
    }

    public CustomerMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView();
    }

    public CustomerMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext=context;
        initView();
    }

    public CustomerMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initView();
    }
    private void initView()
    {
        inflate(getContext(), R.layout.menu_customer, this);
        imgHome=findViewById(R.id.img_home);
        imgNotifications=findViewById(R.id.img_notification);
        imgProfile=findViewById(R.id.img_profile);
        imgProfile.setClipToOutline(true);
        imgList.add(imgHome);
        imgList.add(imgNotifications);
        imgList.add(imgProfile);
        selected=0;

        imgHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selected=0;
                selectOption(selected);
            }
        });


        imgNotifications.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {   selected=1;
                selectOption(selected);
            }
        });


        imgProfile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selected=2;
                selectOption(selected);
            }
        });

        selectOption(selected);
    }


    public void setUpdateListener(PositionUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void selectOption(int num)
    {
        if(updateListener!=null)
        {
            updateListener.onPositionUpdate(num);
        }
    }

    public int getSelected() {
        return selected;
    }
}
