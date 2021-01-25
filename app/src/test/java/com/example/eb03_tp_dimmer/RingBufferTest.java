package com.example.eb03_tp_dimmer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test du ByteRingBuffer
 */
public class RingBufferTest {
    @Test
    public void firstTest() {
        ByteRingBuffer test = new ByteRingBuffer(10);
        test.put((byte)65);
        System.out.println(test);

        byte[] toput = {(byte)22,(byte)52,(byte)21,(byte)11};
        test.put(toput);
        System.out.println(test);

        byte ret_test;
        ret_test = test.get(); //get 65
        System.out.println(ret_test);

        ret_test = test.get(); //get 22
        System.out.println(ret_test);

        int nb = test.bytesToRead(); //nb bytes left : 3
        System.out.println(nb);

        byte[] ret_test2;
        System.out.println(test);
        ret_test2 = test.getAll();
        System.out.print("Test2 array : [");
        for(byte i : ret_test2){
            System.out.print(i);
            System.out.print(",");
        }
        System.out.println("]\n");
        System.out.println(test);
    }
}
