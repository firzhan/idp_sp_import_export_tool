package org.wso2.sample;

import org.apache.log4j.Logger;
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

    private static final Logger log =
            Logger.getLogger(IdentityServicesMigrationTool.class);

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

        try {
            IdentityServicesMigrationTool tool =
                    new IdentityServicesMigrationTool();
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
                IdentityApplicationMgtAdminServiceClient identityApplicationMgtAdminServiceClient = new
                        IdentityApplicationMgtAdminServiceClient(tool.getDataHolder(), adminCookie);

                if (userSelection == 1) {
                    //Fetch
                    identityProviderAdminServiceClient.fetchIdentityProviders();
                    identityApplicationMgtAdminServiceClient.fetchServiceProviders();

                } else {
                    //Push
                    identityProviderAdminServiceClient.publishIdentityProviders();
                    identityApplicationMgtAdminServiceClient.publishServiceProviders();
                }
            } else { // 2

            }

        } catch (MisConfigurationException | AdminServicesInvocationFailed | AdminServicesClientException e) {

            log.error("Exiting the program", e);
        }
    }

}
