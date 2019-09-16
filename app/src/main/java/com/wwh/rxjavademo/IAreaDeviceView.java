package com.wwh.rxjavademo;

import com.wwh.rxjavademo.Utils.AreaDeviceBean;

public interface IAreaDeviceView {
    /**
     *更新本地数据
     */
    void upLocalData(AreaDeviceBean localBean);

    /**
     *更新list数据
     */
    void upListData(boolean isChange, int numDevice);
}
