package com.xcheng.usbcommunicatehost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.xccommunication.XcUsbManager;

import androidx.annotation.Nullable;

import com.xcheng.usbcommunicatehost.sub.SubInfoActivity;
import com.xcheng.usbcommunicatehost.sub.SubOperateActivity;
import com.xcheng.usbcommunicatehost.sub.SubTransferFileActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnSubInfo;
    private Button mBtnSubOperate;
    private Button mBtnSubTransferFile;
    private XcUsbManager usbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnSubInfo = findViewById(R.id.btn_sub_info);
        mBtnSubOperate = findViewById(R.id.btn_sub_operate);
        mBtnSubTransferFile = findViewById(R.id.btn_sub_transfer_file);

        mBtnSubInfo.setOnClickListener(this);
        mBtnSubOperate.setOnClickListener(this);
        mBtnSubTransferFile.setOnClickListener(this);
        usbManager = (XcUsbManager) getSystemService(Context.XC_USB_SERVICE);

    }

    @Override
    public void onClick(View v) {
        int connectionState = usbManager.getConnectionState();
        Log.d(TAG, "onClick: connectionState = " + connectionState);
        if (connectionState != 1) {
            Toast.makeText(this, "Disconnect", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.btn_sub_info) {
            startActivity(new Intent(this, SubInfoActivity.class));
        } else if (v.getId() == R.id.btn_sub_operate) {
            startActivity(new Intent(this, SubOperateActivity.class));
        } else if (v.getId() == R.id.btn_sub_transfer_file) {
            startActivity(new Intent(this, SubTransferFileActivity.class));
        }
    }
}
