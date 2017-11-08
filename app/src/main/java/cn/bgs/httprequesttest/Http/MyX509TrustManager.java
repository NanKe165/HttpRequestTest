package cn.bgs.httprequesttest.Http;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by Vincent on 2017/11/7.
 */

public class MyX509TrustManager implements X509TrustManager {
    //如果需要对证书进行校验，需要这里去实现，如果不实现的话是不安全
    X509Certificate certificate;

    public MyX509TrustManager(X509Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        for (X509Certificate xf:x509Certificates){
            //检查证书是否有效
            try {
                xf.verify(certificate.getPublicKey());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
