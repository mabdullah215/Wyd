package com.mobileapp.wyd;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {

    View progressOverlayView;
    RelativeLayout screenRootView;

    public void showToast(String msg)
    {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout,msg, Snackbar.LENGTH_INDEFINITE).setAction("Ignore", new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                    }
        }).setActionTextColor(getResources().getColor(android.R.color.holo_red_light )).show();
    }

    @Override
    public void setContentView(int layoutResID)
    {
        screenRootView = new RelativeLayout(this);
        screenRootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screenView=inflater.inflate(layoutResID,null);
        screenView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        progressOverlayView = inflater.inflate(R.layout.progress_bar_view, null);
        progressOverlayView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        screenRootView.addView(screenView);
        super.setContentView(screenRootView);
    }

    public void showLoading()
    {
        if(progressOverlayView!=null)
        {
            screenRootView.addView(progressOverlayView);
            screenRootView.getChildAt(0).setAlpha(0.7f);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void hideLoading()
    {
        if(progressOverlayView!=null)
        {
            screenRootView.removeView(progressOverlayView);
            screenRootView.getChildAt(0).setAlpha(1f);
        }
    }
}