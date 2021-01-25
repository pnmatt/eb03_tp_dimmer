package com.example.eb03_tp_dimmer;

public abstract class Transceiver {

    /**Constante de Statut : Transceiver non connecté*/
    public static final int STATE_NOT_CONNECTED = 0; // non connecté
    /**Constante de Statut : connexion en cours*/
    public static final int STATE_CONNECTING = 1;    // connexion en cours
    /**Constante de Statut : Transceiver connecté*/
    public static final int STATE_CONNECTED = 2;     // connecté


    /**Statut de connexion*/
    private int mState;
    /**Référence vers le Listener, pour la réception*/
    private TransceiverListener mTransceiverListener;
    /**Référence vers un FrameProcessor, pour la création de frames*/
    public FrameProcessor mFrameProcessor;


    /**
     * Pour ajouter un Listener
     * @param transceiverListener
     */
    public void setTransceiverListener(TransceiverListener transceiverListener){
        mTransceiverListener = transceiverListener;
    }

    /**
     * Pour retirer le Listener
     * @param frameProcessor
     */
    public void attachFrameProcessor(FrameProcessor frameProcessor){
        mFrameProcessor = frameProcessor;
    }

    /**
     * Pour détacher le FrameProcessor
     */
    public void detachFrameProcessor(){
        mFrameProcessor = null;
    }


    /**
     * Fonction pour avoir l'état du Transceiver
     * @return mState, état du Transceiver
     */
    public int getState() {
        return mState;
    }

    /**
     * Changer l'état du Transceiver
     * @param state Nouvel état
     */
    public void setState(int state) {
        mState = state;
        if(mTransceiverListener != null){
            mTransceiverListener.onTransceiverStateChanged(state);
        }
    }

    /*********************************************************************************************
     *
     *                                         INTERFACES
     *
     ********************************************************************************************/

    /**
     * Interface du Listener, permettant la réception des données
     */
    public interface TransceiverListener{
        void onTransceiverDataReceived();
        void onTransceiverStateChanged(int state);
        void onTransceiverConnectionLost();
        void onTransceiverUnableToConnect();
    }


    /*********************************************************************************************
     *
     *                                         METHODES ABSTRAITES
     *
     ********************************************************************************************/

    /**
     * Connexion avec le périphérique bluetooth
     * @param id Addresse du périphérique
     */
    public abstract void connect(String id);

    /**
     * Déconnexion avec le périphérique bluetooth
     */
    public abstract void disconnect();

    /**
     * Envoi des données vers le périphérique
     * @param b
     */
    public abstract void send(byte[] b);
}
