package cn.bgs.httprequesttest.Http;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Vincent on 2017/11/7.
 */

public class HttpUtils {
    private static  HttpUtils instance;
    private HttpUtils(){

    }
    public static HttpUtils getInstance(){
        if (instance==null){
            instance=new HttpUtils();
        }
        return instance;
    }
    public interface HttpRequestCallBack{
        void onSuccess(String msg);
        void onFail(Exception e);
    }

    /**
     *  通过自定义X509Certificate,检验有效证书 请求网址
     * @param context
     * @param path
     * @param callBack
     */
    public void Request(final Context context, final String path, final HttpRequestCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(path);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                    //初始化sslContext
                    SSLContext ssl=SSLContext.getInstance("TLS");
                    TrustManager[] trustManagers={new MyX509TrustManager(getX509Certificate(context))};
                    ssl.init(null, trustManagers,new SecureRandom());
                    SSLSocketFactory socketFactory = ssl.getSocketFactory();
                    conn.setSSLSocketFactory(socketFactory);
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            if (s.equals("kyfw.12306.cn")){
                                return  true;
                            }
                            return false;
                        }
                    });

                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    String s = inputToString(inputStream);
                    callBack.onSuccess(s);

                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFail(e);
                }
            }
        }).start();
    }

    /**
     * 通过系统拿到TrustManager,检验有效证书，请求网址
     * @param context
     * @param path
     * @param callBack
     */
    public void Request2(final Context context,final String path,final HttpRequestCallBack callBack)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(path);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();


                    SSLContext ssl=SSLContext.getInstance("TLS");
                    //定义一个 TrustManagerFactory,让这个工厂生成TrustManager数组
                    String defaultType = KeyStore.getDefaultType();
                    KeyStore instance = KeyStore.getInstance(defaultType);
                    instance.load(null);
                    instance.setCertificateEntry("srca",getX509Certificate(context));
                    String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();//得到默认算法
                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
                    trustManagerFactory.init(instance);
                    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                    ssl.init(null,trustManagers,new SecureRandom());
                    SSLSocketFactory socketFactory = ssl.getSocketFactory();
                    conn.setSSLSocketFactory(socketFactory);
                    //添加一个主机名称校验器
                    conn.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            if (s.equals("kyfw.12306.cn")){
                                return true;
                            }
                            return false;
                        }
                    });

                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    String s = inputToString(inputStream);
                    callBack.onSuccess(s);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onFail(e);
                }
            }
        }).start();

    }



    private String inputToString(InputStream inputStream) throws IOException {
        StringBuilder sb=new StringBuilder();
        int temp;
        byte[] buf=new byte[1024];
        while ((temp=inputStream.read(buf))!=-1){
            sb.append(new String(buf,0,temp));
        }
        String s = sb.toString();
        return s;
    }

    private X509Certificate getX509Certificate(Context context) throws IOException, CertificateException {
        InputStream is = context.getAssets().open("srca.cer");
        CertificateFactory instance = CertificateFactory.getInstance("X.509");
        X509Certificate  certificate = (X509Certificate) instance.generateCertificate(is);
        return certificate;

    }
}
