package com.wwh.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wwh.rxjavademo.Utils.AreaDeviceBean;
import com.wwh.rxjavademo.rxjava.AreaDevicePresent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;

public class MainActivity  extends BaseActivity implements IAreaDeviceView {


    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is;

    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;



    private TextView mNumTv;
    private ImageView mLocalIv;
    private TextView mLocalNameTv;
    private TextView mLocalIpTv;
    private TextView mLocalMacTv;
    private ListView mListView;
    private AreaDeviceAdapter mAdapter;
    private AreaDevicePresent mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new Thread(new AreaDevicePresent.runare(new AreaDeviceBean())).start();
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_area_device);

    }

    @Override
    public void findView() {
        mNumTv = findViewById(R.id.activity_area_device_conn_num);

        mLocalIv = findViewById(R.id.item_area_device_iv);
        mLocalNameTv = findViewById(R.id.item_area_device_name);
        mLocalIpTv = findViewById(R.id.item_area_device_ip);
        mLocalMacTv = findViewById(R.id.item_area_device_mac);
        mListView = findViewById(R.id.activity_area_device_list);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mPresent = new AreaDevicePresent(this, mContext);
        mAdapter = new AreaDeviceAdapter(mContext, mPresent.getData());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void upLocalData(AreaDeviceBean localBean) {
        mLocalIv.setImageResource(localBean.getResId());
        mLocalNameTv.setText(localBean.getName());
        mLocalIpTv.setText(localBean.getIp());
        mLocalMacTv.setText(localBean.getMac());

//            if("b4:6d:83:16:d0:b4".equals(localBean.getMac())){
//                new Thread(new AreaDevicePresent.runare(new AreaDeviceBean())).start();
//            }
    }

    @Override
    public void upListData(boolean isChange, int numDevice) {
        mNumTv.setText(String.valueOf(numDevice));
        if (isChange && mAdapter != null) {//避免异常
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void contect(final String ip, final int por){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket(ip, por);
                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMasage(final String s){
        // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
//                    outputStream = socket.getOutputStream();

                    // 步骤2：写入需要发送的数据到输出流对象中
                    socket.getOutputStream().write((s+"\n").getBytes("utf-8"));
                    // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                    // 步骤3：发送数据到服务端
                    socket.getOutputStream().flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
