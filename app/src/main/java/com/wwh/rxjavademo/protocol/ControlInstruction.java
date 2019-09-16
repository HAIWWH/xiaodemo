package com.wwh.rxjavademo.protocol;

public class ControlInstruction {
    public static WifiSwitchProtocol mWifiSwitchProtocol;

    /*打开设备*/
    public static byte[] OpenDevice(){
        mWifiSwitchProtocol = new WifiSwitchProtocol(0x01,0x01);
        return mWifiSwitchProtocol.sendProtocol();
    }

    /*关闭设备*/
    public static byte[] CloseDevice(){
        mWifiSwitchProtocol = new WifiSwitchProtocol(0x01,0x02);
        return mWifiSwitchProtocol.sendProtocol();
    }
}
