package com.example.eb03_tp_dimmer;

import java.nio.BufferOverflowException;

public class ByteRingBuffer {
    private byte[] buffer;
    private int totSize;
    private int nbVal;
    private int readIndex;
    private int writeIndex;


    public ByteRingBuffer(int nbEl){
        buffer = new byte[nbEl];
        totSize = nbEl;
        readIndex = 0;
        writeIndex = -1;
    }

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

    public int put(byte[] in){
        int replaced = 0;
        for(byte b : in){
            replaced = put(b);
        }
        return replaced;
    }

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

    public int bytesToRead(){
        return nbVal;
    }

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
