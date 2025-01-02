package com.xcheng.usbcommunicatehost.sub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.xccommunication.IXcUsbListener;
import android.xccommunication.XcUsbManager;

import androidx.annotation.Nullable;

import com.xcheng.usbcommunicatehost.CommunicateManager;
import com.xcheng.usbcommunicatehost.R;

public class SubInfoActivity extends Activity {

    private static final String TAG = SubInfoActivity.class.getSimpleName();
    private CommunicateManager manager;

    private TextView mTvBuildNumber;
    private TextView mTvSubModel;
    private TextView mTvSubSn;
    private TextView mTvSubFwVersion;
    private TextView mTvSubSpFwVersion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_info);

        manager = new CommunicateManager(this);
        initView();
        initData();

        XcUsbManager usbManager = (XcUsbManager) getSystemService(Context.XC_USB_SERVICE);
        usbManager.setReceiveListener(new IXcUsbListener.Stub() {
            @Override
            public void onReceiveData(byte[] data) throws RemoteException {
                Log.d(TAG, "onReceiveData: ----" + new String(data));
            }
        });

    }

    private void initView() {
        mTvBuildNumber = findViewById(R.id.sub_build_number);
        mTvSubModel = findViewById(R.id.sub_model);
        mTvSubFwVersion = findViewById(R.id.sub_fw_version);
        mTvSubSpFwVersion = findViewById(R.id.sub_sp_version);
        mTvSubSn = findViewById(R.id.sub_sn);
    }

    private void initData() {
        mTvBuildNumber.setText(getString(R.string.sub_build_number, manager.getSubBuildNumber()));
        mTvSubModel.setText(getString(R.string.sub_model, manager.getSubModel()));
        mTvSubSn.setText(getString(R.string.sub_sn, manager.getSubSn()));
        mTvSubFwVersion.setText(getString(R.string.sub_firmware_version, manager.getSubFwVersion()));
        mTvSubSpFwVersion.setText(getString(R.string.sub_sp_version, manager.getSubSpFwVersion()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
