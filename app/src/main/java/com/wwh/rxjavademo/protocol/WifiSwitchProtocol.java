package com.wwh.rxjavademo.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WifiSwitchProtocol {
    private static byte[] HEAD;
    private static int PROTOL_HEAD; //协议头
    private static int DEVICE_ADDRESS;//设备地址
    private static int DEVICE_FUNCTION;//设备功能
    private static int DEVICE_CONTROL_STATUS;//设备控制状态
    private static int PARITY_BIT;//校验位

    private WifiSwitchProtocol() {

    }

    public WifiSwitchProtocol(int deviceFunction, int deviceControlStatus) {
        initHead();
        DEVICE_FUNCTION = deviceFunction;
        DEVICE_CONTROL_STATUS = deviceControlStatus;

    }

    public byte[] sendProtocol() {
        byte[] sendByte = HEAD;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream headSB = new DataOutputStream(baos);

        try {
            headSB.write(sendByte);
            headSB.write(DEVICE_FUNCTION);
            headSB.write(DEVICE_CONTROL_STATUS);
            headSB.write(getBCC());
            sendByte = baos.toByteArray();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                headSB.close();
                baos.close();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        return sendByte;
    }

    private final void initHead() {
        PROTOL_HEAD = 0xA1;
        DEVICE_ADDRESS = 0x01;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream headSB = new DataOutputStream(baos);
        try {
            headSB.writeByte(PROTOL_HEAD);
            headSB.writeByte(DEVICE_ADDRESS);
            HEAD = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                headSB.close(); //关闭输入流
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getBCC() {
        return PROTOL_HEAD^DEVICE_ADDRESS^DEVICE_FUNCTION^DEVICE_CONTROL_STATUS;
    }
}