package com.example.eb03_tp_dimmer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BTManager extends Transceiver {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mAdapter;

    private BluetoothSocket mSocket = null;

    private ConnectThread mConnectThread = null;
    private WritingThread mWritingThread = null;

    @Override
    public void connect(String id) {
        disconnect();
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(id);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectThread = new ConnectThread(device);
        setState(STATE_CONNECTING);
        mFrameProcessor = new FrameProcessor();
        mConnectThread.start();
    }


    @Override
    public void disconnect() {
        if((getState() == STATE_CONNECTED)||(getState() == STATE_CONNECTING)){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void send(byte[] b) {
        byte[] new_frame;
        new_frame = mFrameProcessor.toFrame(FrameProcessor.SET_CALIBRATION_DUTY_CYCLE,b);
        mWritingThread.mByteRingBuffer.put(new_frame);
    }

    /**
     * Classe du Thread de connexion au device
     */
    private class ConnectThread extends Thread{


        public ConnectThread(BluetoothDevice device) {
            //BluetoothSocket socket = null;

            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            mAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException e) {
                disconnect();
            }
            mConnectThread = null;
            startReadWriteThreads();

        }
    }


    private void startReadWriteThreads(){
        // instanciation d'un thread de lecture

        mWritingThread = new WritingThread(mSocket);
        Log.i("ConnectThread","Thread WritingThread lancé");
        mWritingThread.start();
        setState(STATE_CONNECTED);
    }

    /**
     * Classe du Thread d'écriture vers le device
     */
    private class WritingThread extends Thread{
        private OutputStream mOutStream;
        public ByteRingBuffer mByteRingBuffer;

        public WritingThread(BluetoothSocket mSocket) {
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mByteRingBuffer = new ByteRingBuffer(4096);
        }

        @Override
        public void run() {
            while(mSocket != null){
                if(mByteRingBuffer.bytesToRead() > 0){
                    try {
                        mOutStream.write(mByteRingBuffer.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
