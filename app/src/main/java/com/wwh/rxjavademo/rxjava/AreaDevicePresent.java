package com.wwh.rxjavademo.rxjava;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import com.wwh.rxjavademo.IAreaDeviceView;
import com.wwh.rxjavademo.Utils.AllUitls;
import com.wwh.rxjavademo.Utils.AreaDeviceBean;
import com.wwh.rxjavademo.protocol.ControlInstruction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AreaDevicePresent {

    private IAreaDeviceView mIView; //设备接口
    private Context mContext;
    private List<AreaDeviceBean> mData; //局域网设备的数据
    private String mLocalIP; //本地ip



    public AreaDevicePresent(IAreaDeviceView iView, Context context) {
        this.mIView = iView;
        this.mContext = context;
        initData();
    }

    public List<AreaDeviceBean> getData() {
        return mData;
    }

    private void initData(){
        mLocalIP = AllUitls.getIPAddressStr(mContext);
        mData = new ArrayList<>();
        initLocalDevice();
        try {
            Observable.create(new ObservableOnSubscribe<List>() {
                @Override
                public void subscribe(ObservableEmitter<List> emitter) throws Exception {
                    AllUitls.initAreaIp(mContext);
                    List<AreaDeviceBean> beans = new ArrayList<>();
                    int sum = 0;
                    while (beans.size() == 0 && sum < 8) {
                        beans.addAll(AllUitls.getAllCacheMac(mLocalIP));
                        SystemClock.sleep(beans.size()>0?0:1000);
                        sum++;
                    }
                    emitter.onNext(beans);
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Observer<List>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List list) {
                        mData.clear();
                        mData.addAll(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mIView.upListData(true, mData.size() + 1);//加上本机
                        for(AreaDeviceBean areaDeviceBean : mData){
                            if("b4:6d:83:16:d0:b4".equals(areaDeviceBean.getMac())){
                                new Thread(new AreaDevicePresent.runare(areaDeviceBean)).start();
                            }
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLocalDevice() {
        AreaDeviceBean bean = new AreaDeviceBean();
        bean.setName(Build.MODEL + "(我的设备)");
        bean.setIp(mLocalIP);
        bean.setMac(AllUitls.getLocalMac());
        mIView.upLocalData(bean);
    }


    public static class runare implements Runnable {

        AreaDeviceBean areaDeviceBean;

        public runare(AreaDeviceBean areaDeviceBean) {
            this.areaDeviceBean = areaDeviceBean;
        }
//           String ip = areaDeviceBean.getIp();
        @Override
        public void run() {
            try {
                // 创建Socket对象 & 指定服务端的IP 及 端口号
                Socket s = new Socket(areaDeviceBean.getIp(), 8888);
                OutputStream outputStream = s.getOutputStream();
                outputStream.write(ControlInstruction.OpenDevice());
                // 发送读取的数据到服务端
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
