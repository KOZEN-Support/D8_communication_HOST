package com.xcheng.usbcommunicatehost;

import android.content.Context;
import android.xccommunication.IXcUsbListener;
import android.xccommunication.XcUsbManager;

public class CommunicateManager {

    private XcUsbManager mXcUsbManager;
    private IReceiveListener mReceiveListener;

    public CommunicateManager(Context context) {
        mXcUsbManager = (XcUsbManager) context.getSystemService(Context.XC_USB_SERVICE);
    }

    public int getConnectionState() {
        return mXcUsbManager.getConnectionState();
    }

    public String getSubBuildNumber() {
        return mXcUsbManager.getSubBuildNumber();
    }

    public String getSubModel() {
        return mXcUsbManager.getSubModel();
    }

    public String getSubSn() {
        return mXcUsbManager.getSubSn();
    }

    public String getSubFwVersion() {
        return mXcUsbManager.getSubFwVersion();
    }

    public String getSubSpFwVersion() {
        return mXcUsbManager.getSubSpFwVersion();
    }

    public void subPowerOff() {
        mXcUsbManager.subPowerOff();
    }

    public void subPowerOn() {
        mXcUsbManager.subPowerOn();
    }

    public void subReboot() {
        mXcUsbManager.subReboot();
    }

    public void syncSubDateTime() {
        mXcUsbManager.setSubDateTime();
    }

    public boolean sendData(byte[] data) {
        try {
            return mXcUsbManager.sendData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setReceiveListener(IReceiveListener listener) {
        mReceiveListener = listener;
        mXcUsbManager.setReceiveListener(new IXcUsbListener.Stub() {
            @Override
            public void onReceiveData(byte[] data) {
                mReceiveListener.onReceiveData(data);
            }
        });
    }

}
