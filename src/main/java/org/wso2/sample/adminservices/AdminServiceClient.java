package org.wso2.sample.adminservices;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.java.security.SSLProtocolSocketFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.model.DataHolder;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

abstract class AdminServiceClient {

    Stub stub;
    private String serviceUrl;
    DataHolder dataHolder;
    List<String> successLists = new ArrayList<>();
    List<String> failedLists = new ArrayList<>();

    AdminServiceClient(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
    }

    void init(String cookie){

        ServiceClient client = this.stub._getServiceClient();
        org.apache.axis2.client.Options options = client.getOptions();

        if(cookie != null){
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        }

        if(dataHolder.isKeyStoreEnabled()){
            setSystemProperties();
        } else {
            disableTrustingAllCerts(options);
        }

        options.setManageSession(true);
    }

    private void setSystemProperties(){

        System.setProperty("javax.net.ssl.trustStore", dataHolder.getKeyStorePath());
        System.setProperty("javax.net.ssl.trustStorePassword",
                new String(dataHolder.getKeyStorePassword()));
        System.setProperty("javax.net.ssl.trustStoreType", ServiceClientConstant.KEY_STORE_TYPE);

    }

    private void disableTrustingAllCerts(org.apache.axis2.client.Options options){

        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                return null;
            }
        }
        };

        Protocol protocol = null;

        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            SSLProtocolSocketFactory sslFactory = new SSLProtocolSocketFactory(sslContext);
            protocol = new Protocol("https",
                    (ProtocolSocketFactory) sslFactory, dataHolder.getPort());
        } catch (Exception ex) {
            // take action
        }

        options.setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, protocol);

    }

    void printStatus(String operation, String type) {

        System.out.println("*********************************************");

        if(!successLists.isEmpty()){

            System.out.println(String.format(ServiceClientConstant.SUCCESS_MESSAGE_STRING, operation, type, this.successLists.size()));

            for (String obj : successLists) {
                System.out.println(obj);
            }
        }

        if(!failedLists.isEmpty()){

            System.out.println(String.format(ServiceClientConstant.FAILED_MESSAGE_STRING, operation, type, this.failedLists.size()));
            for (String obj : failedLists) {
                System.out.println(obj);
            }
        }

        successLists.clear();
        failedLists.clear();
    }



}
