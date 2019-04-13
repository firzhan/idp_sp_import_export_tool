/**
 *  Copyright (c) 2014 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.sample.adminservices;



import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.java.security.SSLProtocolSocketFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.model.DataHolder;

import java.rmi.RemoteException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AuthenticationAdminServiceClient extends AdminServiceClient {

    public AuthenticationAdminServiceClient(DataHolder dataHolder) throws AdminServicesClientException {
        super(dataHolder);
        try {
            this.stub = new AuthenticationAdminStub( String.format(ServiceClientConstant.SERVICE_URL_FORMAT,
                    dataHolder.getHostName(), dataHolder.getPort(),
                    ServiceClientConstant.AUTHENTICATION_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            String errMsg = "Axis2 soap faul occurred in AuthenticationAdminStub";
            throw new AdminServicesClientException(errMsg, axisFault);
        }

        init(null);
    }

    public String login() throws AdminServicesClientException{

        try {
            ((AuthenticationAdminStub)this.stub).login(dataHolder.getAdminUserName(),
                    new String(dataHolder.getAdminPassword()),
                    dataHolder.getHostName());
        } catch (RemoteException e) {
            String errMsg = "Establishing Connection Failed with back end when log-in";
            throw new AdminServicesClientException(errMsg, e);
        } catch (LoginAuthenticationExceptionException e) {
            String errMsg = "log-in operation failed";
            throw new AdminServicesClientException(errMsg, e);
        }
        ServiceContext serviceContext = ((AuthenticationAdminStub)this.stub).
                _getServiceClient().getLastOperationContext().getServiceContext();
        return (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
    }

  /*  protected void init(String cookie){

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
    }*/





}
