package com.example.tiagoc.relogiodecabeceira;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    private Handler mHandler = new Handler();
    private boolean mRunnableStoped = false;
    private boolean mIsBatteryOn = true;
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.mTextBatteryLevel.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mViewHolder.mTextHourMinutes = (TextView) this.findViewById(R.id.text_hour_minutes);
        this.mViewHolder.mTextSeconds = (TextView) this.findViewById(R.id.text_seconds);
        this.mViewHolder.mCheckBatterry = (CheckBox) this.findViewById(R.id.check_battery);
        this.mViewHolder.mTextBatteryLevel = (TextView) this.findViewById(R.id.text_battery_level);
        this.mViewHolder.mImageClose = (ImageView) this.findViewById(R.id.image_close);
        this.mViewHolder.mImageOption = (ImageView) this.findViewById(R.id.image_options);
        this.mViewHolder.mLinearOption = (LinearLayout) this.findViewById(R.id.linear_option);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mViewHolder.mCheckBatterry.setChecked(true);
        this.mViewHolder.mLinearOption.animate().translationY(500).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        this.setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mRunnableStoped = false;
        this.startBedSide();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mRunnableStoped = true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.check_battery){
            this.toogleCheckBattery();
        }else if(id == R.id.image_options){
            this.mViewHolder.mLinearOption.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearOption.animate().translationY(0).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            this.mViewHolder.mImageOption.setVisibility(View.GONE);
        }else if(id == R.id.image_close){
            this.mViewHolder.mImageOption.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearOption.setVisibility(View.GONE);
            this.mViewHolder.mLinearOption.animate().translationY(this.mViewHolder.mLinearOption.getMeasuredHeight()).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        }

    }

    private void toogleCheckBattery() {
        if(this.mIsBatteryOn){
            this.mIsBatteryOn = false;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.GONE);
        }else {
            this.mIsBatteryOn = true;
            this.mViewHolder.mTextBatteryLevel.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        this.mViewHolder.mCheckBatterry.setOnClickListener(this);
        this.mViewHolder.mImageClose.setOnClickListener(this);
        this.mViewHolder.mImageOption.setOnClickListener(this);
    }

    private void startBedSide() {

        final Calendar calendar = Calendar.getInstance();

        this.mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mRunnableStoped)
                    return;

                calendar.setTimeInMillis(System.currentTimeMillis());

                String hourMinutesFormat = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                String secondsFormat = String.format("%02d", calendar.get(Calendar.SECOND));

                mViewHolder.mTextHourMinutes.setText(hourMinutesFormat);
                mViewHolder.mTextSeconds.setText(secondsFormat);

                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - (now % 1000));

                mHandler.postAtTime(mRunnable, next);
            }
        };

        this.mRunnable.run();


    }

    private static class ViewHolder {
        TextView mTextHourMinutes;
        TextView mTextSeconds;
        TextView mTextBatteryLevel;
        CheckBox mCheckBatterry;
        ImageView mImageOption;
        ImageView mImageClose;
        LinearLayout mLinearOption;
    }
}
