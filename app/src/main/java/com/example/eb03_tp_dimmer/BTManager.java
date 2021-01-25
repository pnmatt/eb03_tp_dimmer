package com.example.eb03_tp_dimmer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Implementation de Transciever, permettant de gérer de la connexion Bluetooth
 */
public class BTManager extends Transceiver {

    /**
     * UUID du service voulu sur l'Oscilloscope
     */
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * Référence vers l'adaptateur Bluetooth de l'appareil
     */
    private BluetoothAdapter mAdapter;

    /**
     * Socket de connexion avec le service du périphérique
     */
    private BluetoothSocket mSocket = null;

    /**
     * Thread de connection vers le périphérique bluetooth
     */
    private ConnectThread mConnectThread = null;

    /**
     * Thread d'écriture vers le périphérique bluetooth
     */
    private WritingThread mWritingThread = null;

    /**
     * Initialisation des variables de connexion Bluetooth, et lancement du Thread de connexion.
     * @param id
     */
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

    /**
     * Déconnexion du périphérique : fermeture du Socket de connexion.
     */
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


    /**
     * Envoi d'un message vers l'Oscilloscope.
     * @param b
     */
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

        /**
         * Fonction de création de la classe (du Thread)
         * @param device Périphérique auquel on veut se connecter
         */
        public ConnectThread(BluetoothDevice device) {
            //BluetoothSocket socket = null;

            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Fonction principale du Thread, connexion du Socket au device bluetooth
         */
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

    /**
     * Instanciation du Thread d'écriture.
     */
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

        /**
         * Creation de la classe (du Thread)
         * @param mSocket Socket de conexion au device bluetooth
         */
        public WritingThread(BluetoothSocket mSocket) {
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mByteRingBuffer = new ByteRingBuffer(4096);
        }

        /**
         * Envoi permanent du contenu du RingBuffer au périphérique bluetooth
         */
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
