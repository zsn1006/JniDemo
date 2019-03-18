package com.dc.myjni;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dc.myjni.serialport.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zsn10
 */
public class MainActivity extends AppCompatActivity {
    private SerialPort mSerialPort;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private ReadThread mReadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //series
        try {
            /* 打开串口 */
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
            mOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
            mInputStream = (FileInputStream) mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[512];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        ComBean ComRecData = new ComBean("/dev/ttyS1", buffer, size);
                        onDataReceived(ComRecData);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    protected void onDataReceived(final ComBean var1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String receiveData = FuncUtil.ByteArrToHex(var1.bRec);
                String result = receiveData.substring(6, 8);
                    String scanData = receiveData.substring(12, receiveData.length());
                    Log.i("zsn", scanData);
                    String printStr = ConvertUtil.hexStr2Str(scanData);
                    Log.i("zsn", printStr);
            }
        });
    }
}
