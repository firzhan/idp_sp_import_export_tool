package org.wso2.sample;

import org.wso2.sample.adminservices.AuthenticationAdminServiceClient;
import org.wso2.sample.adminservices.IdentityApplicationMgtAdminServiceClient;
import org.wso2.sample.adminservices.IdentityProviderAdminServiceClient;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.exception.AdminServicesInvocationFailed;
import org.wso2.sample.exception.MisConfigurationException;
import org.wso2.sample.internal.DataLoader;
import org.wso2.sample.internal.Utils;
import org.wso2.sample.model.DataHolder;

public class IdentityServicesMigrationTool {

    private DataHolder dataHolder;

    private IdentityServicesMigrationTool() throws MisConfigurationException {

        init();
    }

    private DataHolder getDataHolder() {

        return dataHolder;
    }

    private void init() throws MisConfigurationException {

        this.dataHolder = DataLoader.loadProperties();

    }

    private String getCookie() {

        try {
            AuthenticationAdminServiceClient authenticationAdminServiceClient =
                    new AuthenticationAdminServiceClient(dataHolder);

            return authenticationAdminServiceClient.login();
        } catch (AdminServicesClientException e) {
            throw new AdminServicesInvocationFailed("Admin Services Login " + "Failed");
        }
    }

    public static void main(String[] args) {

        System.out.println(ServiceClientConstant.OPTIONS_INFORMATION_PARA_STRING);
        System.out.println(ServiceClientConstant.EXIT_STRING);

        IdentityServicesMigrationTool tool = null;
        try {
            tool = new IdentityServicesMigrationTool();
            String adminCookie = tool.getCookie();

            if (adminCookie == null) {
                throw new AdminServicesInvocationFailed("Cookie generation " + "failed during the login operation");
            }

            int userSelection =
                    Utils.readInput(ServiceClientConstant.OPTIONS_1_PARA_STRING,
                            ServiceClientConstant.INPUT_SELECTION_REGEX);

            if (userSelection == 1) {

                userSelection =
                        Utils.readInput(ServiceClientConstant.OPTIONS_2_PARA_STRING, ServiceClientConstant.INPUT_SELECTION_REGEX);

                IdentityProviderAdminServiceClient identityProviderAdminServiceClient = new
                        IdentityProviderAdminServiceClient(tool.getDataHolder(), adminCookie);

                if (userSelection == 1) {
                    //Export
                } else {
                    //Import
                }
            }

        } catch (MisConfigurationException | AdminServicesInvocationFailed | AdminServicesClientException e) {

            System.err.println(e.getMessage());
            System.err.println("Exiting the program");
        }



        if (operationType == 1) {

            operationType =
                    Utils.readInput(ServiceClientConstant.OPTIONS_2_PARA_STRING, ServiceClientConstant.INPUT_SELECTION_REGEX);

            IdentityProviderAdminServiceClient identityProviderAdminServiceClient = new IdentityProviderAdminServiceClient(String.format(ServiceClientConstant.SERVICE_URL_FORMAT, tool.getDataHolder().getHostName(), tool.getDataHolder().getPort(), ServiceClientConstant.IDP_MGT_SERVICE_NAME), adminCookie);

            if (operationType == 1) {
                //Export
            } else {
                //Import
            }
        }

        String authenticationAdminServiceURL =
                ServiceClientConstant.SERVICE_URL + ServiceClientConstant.AUTHENTICATION_SERVICE_NAME;
        String idpMgtServiceURL =
                ServiceClientConstant.SERVICE_URL + ServiceClientConstant.IDP_MGT_SERVICE_NAME;
        String identityAppMgtServiceURL =
                ServiceClientConstant.SERVICE_URL + ServiceClientConstant.APP_MGT_SERVICE_NAME;

        // setting the system properties for javax.net.ssl
        //AuthenticationAdminServiceClient.setSystemProperties
        // (ServiceClientConstant.CLIENT_TRUST_STORE_PATH,
        //       ServiceClientConstant.KEY_STORE_TYPE, ServiceClientConstant
        //       .KEY_STORE_PASSWORD);

        if (adminCookie != null) {
            IdentityProviderAdminServiceClient identityProviderAdminServiceClient = new IdentityProviderAdminServiceClient(idpMgtServiceURL, adminCookie);

            /*if(Integer.valueOf(args[0]) == 1){
                identityProviderAdminServiceClient.importAllIDPs();
            } else {

            }*/

            //identityProviderAdminServiceClient.importAllIDPs();
            //identityProviderAdminServiceClient.exportAllIDPs();

            IdentityApplicationMgtAdminServiceClient identityApplicationMgtAdminServiceClient = new IdentityApplicationMgtAdminServiceClient(identityAppMgtServiceURL, adminCookie);
            //identityApplicationMgtAdminServiceClient.exportAllSPs();
            identityApplicationMgtAdminServiceClient.importAllSPs();
        }
    }

}
