package cn.bgs.httprequesttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.bgs.httprequesttest.Http.HttpUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View view) {
        HttpUtils.getInstance().Request2(this, "https://kyfw.12306.cn/otn/regist/init", new HttpUtils.HttpRequestCallBack() {
            @Override
            public void onSuccess(String msg) {
                Log.e(TAG, "onSuccess: "+msg );
            }

            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "onFail: "+e.getMessage() );
            }
        });
    }
}
