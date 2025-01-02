package com.xcheng.usbcommunicatehost.sub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.xcheng.usbcommunicatehost.CommunicateManager;
import com.xcheng.usbcommunicatehost.R;

public class SubOperateActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SubOperateActivity.class.getSimpleName();

    private CommunicateManager manager;
    private Button mBtnSyncTime;
    private Button mBtnScreenOn;
    private Button mBtnScreenOff;
    private Button mBtnReboot;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_operate);

        manager = new CommunicateManager(this);
        initView();

    }

    private void initView() {
        mBtnSyncTime = findViewById(R.id.btn_sync_time);
        mBtnScreenOn = findViewById(R.id.btn_screen_on);
        mBtnScreenOff = findViewById(R.id.btn_screen_off);
        mBtnReboot = findViewById(R.id.btn_reboot);

        mBtnSyncTime.setOnClickListener(this);
        mBtnScreenOn.setOnClickListener(this);
        mBtnScreenOff.setOnClickListener(this);
        mBtnReboot.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sync_time) {
            manager.syncSubDateTime();
        } else if (v.getId() == R.id.btn_screen_on) {
            manager.subPowerOn();
        } else if (v.getId() == R.id.btn_screen_off) {
            manager.subPowerOff();
        } else if (v.getId() == R.id.btn_reboot) {
            manager.subReboot();
        }
    }

}
