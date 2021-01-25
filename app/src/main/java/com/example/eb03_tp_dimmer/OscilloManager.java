package com.example.eb03_tp_dimmer;

public class OscilloManager {

    /**Listener du Transceiver vers le device*/
    private Transceiver.TransceiverListener mTransceiverListener;
    /**Référence vers le Transceiver utilisé vers le device*/
    private Transceiver mTransceiver;
    /**Référence vers l'instance unique de la classe (Singleton)*/
    private static OscilloManager mOscilloManager;

    /**
     * Fonction implémentant le singleton.
     * @return L'instance unique de la classe, ou alors l'instance créée de la classe
     */
    public static OscilloManager getInstance(){
        if(mOscilloManager == null){
            mOscilloManager = new OscilloManager();
            return mOscilloManager;
        }
        else{
            return mOscilloManager;
        }
    }

    /**
     * Fonction de paramétrage de la calibration de la led de l'oscilloscope
     * @param duty_cycle Rapport cyclique voulu. Entre 0 et 100
     */
    public void setCalibrationDutyCycle(int duty_cycle){
        byte[] tosend = {(byte)duty_cycle};
        mTransceiver.send(tosend);
    }

    /**
     * Fonction de verification du statut de connexion
     * @return l'état du Transceiver (Constantes dans Transceiver : 0=not connected, connecting = 1, connected = 2)
     *
     */
    public int getStatus(){
        if(mTransceiver == null){
            return Transceiver.STATE_NOT_CONNECTED;
        }else{
            return mTransceiver.getState();
        }
    }

    /**
     * Permet d'ajouter la référence à un Transciever dans la variable mTransciever, permettant l'utilisation des fonctions d'envoi des messages vers l'Oscilloscope
     * @param tr Transciever référencé
     */
    public void attachTransceiver(Transceiver tr){
        mTransceiver = tr;
    }

    /**
     * Pour détacher la référence du Transceiver, dans le cas d'un deconnexion
     */
    public void detachTransceiver(){
        mTransceiver.disconnect();
        mTransceiver = null;
    }
}
