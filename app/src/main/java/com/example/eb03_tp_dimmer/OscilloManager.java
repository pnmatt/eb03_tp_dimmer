package com.example.eb03_tp_dimmer;

public class OscilloManager {

    private Transceiver.TransceiverListener mTransceiverListener;
    private Transceiver mTransceiver;
    private static OscilloManager mOscilloManager;

    public static OscilloManager getInstance(){
        if(mOscilloManager == null){
            mOscilloManager = new OscilloManager();
            return mOscilloManager;
        }
        else{
            return mOscilloManager;
        }
    }

    public void setCalibrationDutyCycle(int duty_cycle){
        byte[] tosend = {(byte)duty_cycle};
        mTransceiver.send(tosend);
    }

    public int getStatus(){
        if(mTransceiver == null){
            return Transceiver.STATE_NOT_CONNECTED;
        }else{
            return mTransceiver.getState();
        }
    }

    public void attachTransceiver(Transceiver tr){
        mTransceiver = tr;
    }

    public void detachTransceiver(){
        mTransceiver.disconnect();
        mTransceiver = null;
    }
}
