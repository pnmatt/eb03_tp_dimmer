package com.example.eb03_tp_dimmer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test du FrameProcessor
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void FrameProcessorTest() {
        FrameProcessor fp_test = new FrameProcessor();
        byte[] msg = {0x06};
        byte[] test = fp_test.toFrame((byte)0x07,msg);
        for(byte b : test){
            System.out.println(String.format("%02X",b));
        }

        byte[] frame = {0x05,0x00,0x02,0x07,0x06,0x0C,0x06,0x0A,0x06,0x0B,-15,0x04};
        fp_test.fromFrame(frame);
        byte[] ftest = fp_test.rxData;
        System.out.println();
        for(byte b : ftest){
            System.out.print(String.format("%02X",b));
            System.out.print(",");
        }
    }
}