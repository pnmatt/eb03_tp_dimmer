package com.example.eb03_tp_dimmer;

import java.util.ArrayList;

public class FrameProcessor {
    /**Frame créée à partir d'un message*/
    public byte[] txFrame;
    /**Data parsée d'une frame*/
    public byte[] rxData;

    /*public static final byte SET_VERTICAL_SCALE = 0x02;
    public static final byte SET_VERTICAL_OFFSET = 0x03;
    public static final byte SET_HORIZONTAL_SCALE = 0x07;
    public static final byte SET_CHANNEL = 0x0B;
    public static final byte SET_TRIGGER_CHANNEL = 0x08;
    public static final byte SET_TRIGGER_TYPE = 0x09;
    public static final byte SET_TRIGGER_LEVEL = 0x0C;
    public static final byte DATA_TRANSFERT = -113; //8F = -113, 8F pas accepté*/
    /**Commande de calibration de la led*/
    public static final byte SET_CALIBRATION_DUTY_CYCLE = 0x0A;

    /**Byte de début de la frame*/
    public static final byte HEADER = 0x05;
    /**Byte de fin de la frame*/
    public static final byte TAIL = 0x04;
    /**Byte d'échappement*/
    public static final byte ESC = 0x06;

    /**ArrayList temporaire de construction/parsage de la frame*/
    private ArrayList<Byte> temp_frame;

    /**
     * Fonction permettant le parsage d'une frame. Le message est mis dans rxData. !! Pas finie !!
     * @param received_frame Frame reçue à parser
     * @return true si la frame était valide et a été parsée
     */
    public boolean fromFrame(byte[] received_frame){
        temp_frame = new ArrayList<Byte>();
        for(byte b : received_frame){
            temp_frame.add(b);
        }
        if((temp_frame.get(0) != HEADER)||(temp_frame.get(temp_frame.size()-1) != TAIL)){
            return false;
        }
        removeEscTempFrame();
        rxData = new byte[temp_frame.size()];
        for(int i=0;i<temp_frame.size();i++){
            rxData[i] = temp_frame.get(i);
        }
        return true;
    }

    /**
     * Fonction permettant la construction d'une frame à partir d'un message et une commande spécifique
     * @param command Commande à envoyer (voir constantes dans FrameProcessor)
     * @param parameters Message à envoyer avec la commande
     * @return Tableau de bytes contentant la frame construite
     */
    public byte[] toFrame(byte command,byte[] parameters){
        temp_frame = new ArrayList<Byte>();
        temp_frame.add(HEADER);
        int msg_size = parameters.length+1;
        temp_frame.add((byte)(msg_size >> 8));
        temp_frame.add((byte)msg_size);
        temp_frame.add(command);
        for(int i=0;i<msg_size-1;i++){
            temp_frame.add((byte)parameters[i]);
        }
        temp_frame.add(calcCtrl());
        temp_frame.add(TAIL);
        addEscTempFrame();

        txFrame = new byte[temp_frame.size()];
        for(int i=0;i<temp_frame.size();i++){
            txFrame[i] = temp_frame.get(i);
        }
        return txFrame;
    }

    /**
     * Permet l'ajout des échappements dans la frame
     */
    private void addEscTempFrame(){
        int i = 1;
        int frame_size = temp_frame.size();
        for(int j=1;j<frame_size-2;j++){
            if(temp_frame.get(i) == HEADER){
                temp_frame.add(i,ESC);
                temp_frame.set(i+1,(byte)(ESC+HEADER));
                i += 1;
            }else if(temp_frame.get(i) == TAIL){
                temp_frame.add(i,ESC);
                temp_frame.set(i+1,(byte)(ESC+TAIL));
                i += 1;
            }else if(temp_frame.get(i) == ESC){
                temp_frame.add(i,ESC);
                temp_frame.set(i+1,(byte)(ESC+ESC));
                i += 1;
            }
            i += 1;

        }
    }

    /**
     * Permet la suppression des échappements dans une frame parsée
     */
    private void removeEscTempFrame(){
        System.out.println("remove");
        System.out.println();
        int i = 1;
        //int frame_size = temp_frame.size();
        for(int j=1;j<temp_frame.size()-1;j++){
            System.out.println(String.format("%02X",temp_frame.get(i)));
            if(temp_frame.get(i) == ESC){
                temp_frame.remove(i);
                switch (temp_frame.get(i)){
                    case (byte)0x0A:
                        temp_frame.set(i,TAIL);
                        break;
                    case (byte)0x0B:
                        temp_frame.set(i,HEADER);
                        break;
                    case (byte)0x0C:
                        temp_frame.set(i,ESC);
                        break;
                }
                i += 1;
            }else{
                i += 1;
            }

        }
    }

    /**
     * Calcul du byte de contrôle dans la frame
     * @return
     */
    private byte calcCtrl(){
        int ctrl = 0;
        int frame_size = temp_frame.size();
        for(int i=1;i<frame_size;i++){
            ctrl += temp_frame.get(i);
        }
        ctrl = ctrl%256;
        ctrl = -ctrl;
        return (byte)ctrl;
    }
}
