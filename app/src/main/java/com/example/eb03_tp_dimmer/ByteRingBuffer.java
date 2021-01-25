package com.example.eb03_tp_dimmer;

import java.nio.BufferOverflowException;

/**
 * Classe de buffer circulaire.
 */
public class ByteRingBuffer {
    /**Array des valeurs du buffer*/
    private byte[] buffer;
    /**Taille totale maximale du buffer*/
    private int totSize;
    /**Nombre de valeurs présentes dans le buffer*/
    private int nbVal;
    /**Index de lecture*/
    private int readIndex;
    /**Index d'écriture*/
    private int writeIndex;

    /**
     * Constructeur du buffer circulaire
     * @param nbEl Nombre d'éléments maximum voulus
     */
    public ByteRingBuffer(int nbEl){
        buffer = new byte[nbEl];
        totSize = nbEl;
        readIndex = 0;
        writeIndex = -1;
    }

    /**
     * Fonction d'ajout de variable byte dans le buffer
     * @throws BufferOverflowException Quand on arrive au bout du buffer
     * @param in byte ajouté
     * @return 0 si la variable a bien été ajoutée
     */
    public int put(byte in){
        if(nbVal == totSize) {
            /*writeIndex = incIndex(writeIndex);
            readIndex = incIndex(readIndex);
            buffer[writeIndex] = in;
            return 1;*/
            throw new BufferOverflowException();
        }else{
            writeIndex = incIndex(writeIndex);
            buffer[writeIndex] = in;
            nbVal += 1;
            return 0;
        }
    }

    /**
     * Fonction d'ajout de tableau de variables
     * @param in Tableau de bytes ajouté
     * @return 0 si le tableau a bien été ajouté
     */
    public int put(byte[] in){
        int replaced = 0;
        for(byte b : in){
            replaced = put(b);
        }
        return replaced;
    }

    /**
     * Fonction de récupération de variable
     * @return Byte disponible le plus ancien
     */
    public byte get(){
        if(nbVal > 0){
            byte toreturn = buffer[readIndex];
            readIndex = incIndex(readIndex);
            nbVal -= 1;
            buffer[readIndex-1] = 0;
            return toreturn;
        }
        else{
            throw new NullPointerException();
        }
    }

    /**
     * Fonction permettant de vider le buffer
     * @return Contenu total du buffer, tableau de bytes
     */
    public byte[] getAll(){
        if(nbVal > 0){
            byte[] returnBuffer = new byte[nbVal];
            for(int i=0;i<returnBuffer.length;i++){
                returnBuffer[i] = get();
            }
            nbVal = 0;
            readIndex = 0;
            writeIndex = 0;
            return returnBuffer;
        }
        else{
            throw new NullPointerException();
        }
    }

    /**
     *
     * @return Nombre de variables contenues dans le buffer
     */
    public int bytesToRead(){
        return nbVal;
    }

    /**
     * Description du contenu du ByteRingBuffer
     * @return String
     */
    public String toString(){
        String s = "ByteRingBuffer containing [";
        int readPtr = readIndex;
        for(int i=0;i<nbVal;i++){
            s += buffer[readPtr];
            if(i < nbVal-1){
                s += ",";
            }
            readPtr = incIndex(readPtr);
        }
        s += "]";
        return s;
    }

    /**
     * Permet l'incrémentation des index de lecture/écriture
     * @param index index à incrémenter
     * @return index incrémenté
     */
    private int incIndex(int index){
        int ind = index;
        if(index == totSize-1){
            ind = 0;
        }
        else{
            ind += 1;
        }
        return ind;
    }

    /**
     * Permet la décrémentation des index de lecture/écriture
     * @param index index à décrémenter
     * @return index décrémenté
     */
    private int decIndex(int index){
        int ind = index;
        if(index == 0){
            ind = totSize-1;
        }
        else{
            ind -= 1;
        }
        return ind;
    }
}
