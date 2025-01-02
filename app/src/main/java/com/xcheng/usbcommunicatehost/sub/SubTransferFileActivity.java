package com.xcheng.usbcommunicatehost.sub;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xcheng.usbcommunicatehost.CommunicateManager;
import com.xcheng.usbcommunicatehost.DataAdapter;
import com.xcheng.usbcommunicatehost.IReceiveListener;
import com.xcheng.usbcommunicatehost.R;


import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class SubTransferFileActivity extends Activity implements IReceiveListener {

    private static final String TAG = SubTransferFileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_FILE = 1;
    public static final int BUFFER_SIZE_IN_BYTES_MAX = 16384;

    private static final String XC_FILE_TRANSFER_START = "xc_file_transfer_start";
    private static final String XC_SUB_FILE_TRANSFER_READY = "xc_sub_file_transfer_ready";
    private static final String XC_FILE_TRANSFER_END = "xc_file_transfer_end";

    private CommunicateManager manager;
    private RecyclerView mRvData;
    private EditText mEtData;
    private Button mBtnSendData;
    private Button mBtnTransferFile;
    private int dataLen;
    private DataAdapter dataAdapter;
    private Uri mFileUri;
    private InputStream inputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_transfer_file);

        manager = new CommunicateManager(this);
        manager.setReceiveListener(this);

        mBtnTransferFile = findViewById(R.id.btn_transfer_file);
        mRvData = findViewById(R.id.rv_data);
        mEtData = findViewById(R.id.et_send);
        mBtnSendData = findViewById(R.id.btn_send);

        dataAdapter = new DataAdapter(new ArrayList<>(), this);
        mRvData.setLayoutManager(new LinearLayoutManager(this));
        mRvData.setAdapter(dataAdapter);

        mBtnTransferFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        mBtnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = mEtData.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(SubTransferFileActivity.this, getString(R.string.et_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                manager.sendData(data.getBytes());
                updateData(getString(R.string.host_prompt) + data);
                mEtData.setText("");
            }
        });

    }

    private void updateData(String line) {
        dataAdapter.update(line);
        mRvData.smoothScrollToPosition(dataAdapter.getItemCount() - 1);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                mFileUri = data.getData();
                if (mFileUri != null) {
                    try {
                        inputStream = getContentResolver().openInputStream(mFileUri);
                        dataLen = inputStream.available();
                        String fileName = getFileName(mFileUri);
                        Log.d(TAG, "onActivityResult: file name=" + fileName + ",file length=" + dataLen);
                        String start = XC_FILE_TRANSFER_START + "," + dataLen + "," + fileName;
                        manager.sendData(start.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }

    private void sendFile() {
        try {
            byte[] bytes = new byte[BUFFER_SIZE_IN_BYTES_MAX];
            int offset = 0;
            int len;
            int targetPro = 0;
            Log.d(TAG, "transfer begin");
            while ((len = inputStream.read(bytes)) != -1) {
                while (!manager.sendData(Arrays.copyOf(bytes, len))) {
                    Log.d(TAG, "send error, retrying...");
                }
                offset = offset + len;
                float progress = ((float) offset / dataLen) * 100;
                if (targetPro != (int) progress) {
                    targetPro = (int) progress;
                    updateData("total length =" + dataLen + " ,transfer length=" + offset + " ,Length of each pass =" + len);
                    updateData("The transmission progress is：" + format(progress) + "%");
                }
                if ((int) progress == 100) {
                    manager.sendData(XC_FILE_TRANSFER_END.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {
        runOnUiThread(() -> {
            String receiveData = new String(data);
            if (XC_SUB_FILE_TRANSFER_READY.equals(receiveData)) {
                // file transfer
                updateData("================File transfer start=============");
                sendFile();
                return;
            }
            if (XC_FILE_TRANSFER_END.equals(receiveData)) {
                try {
                    dataLen = 0;
                    mFileUri = null;
                    inputStream.close();
                    updateData("================File transfer completed================");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updateData(getString(R.string.device_prompt) + receiveData);
        });
    }

    public String format(double progress) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(progress);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}