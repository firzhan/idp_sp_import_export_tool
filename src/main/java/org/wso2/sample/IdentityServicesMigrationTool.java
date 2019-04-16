package org.wso2.sample;

import org.apache.log4j.Logger;
import org.wso2.sample.adminservices.AuthenticationAdminService;
import org.wso2.sample.adminservices.AuthorizationManagerAdminService;
import org.wso2.sample.adminservices.IdentityApplicationMgtAdminService;
import org.wso2.sample.adminservices.IdentityProviderAdminService;
import org.wso2.sample.adminservices.UserStoreMgtAdminService;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.exception.AdminServicesInvocationFailed;
import org.wso2.sample.exception.MisConfigurationException;
import org.wso2.sample.internal.DataLoader;
import org.wso2.sample.internal.Utils;
import org.wso2.sample.model.DataHolder;

import java.util.Arrays;

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
            AuthenticationAdminService authenticationAdminServiceClient =
                    new AuthenticationAdminService(dataHolder);

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

                IdentityProviderAdminService identityProviderAdminServiceClient = new IdentityProviderAdminService(tool.getDataHolder(), adminCookie);
                IdentityApplicationMgtAdminService identityApplicationMgtAdminServiceClient = new IdentityApplicationMgtAdminService(tool.getDataHolder(), adminCookie);

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

                if(tool.getDataHolder().getRoleNamesArray().length > 0){

                    AuthorizationManagerAdminService authorizationManagerAdminService = new
                            AuthorizationManagerAdminService(tool.getDataHolder(), adminCookie);

                    String[] permissionsArray =
                            authorizationManagerAdminService.getPermissionOfSuperUser();

                    UserStoreMgtAdminService userStoreMgtAdminService =
                            new UserStoreMgtAdminService(tool.getDataHolder()
                                    , adminCookie);
                    userStoreMgtAdminService.addRoles(permissionsArray);
                    userStoreMgtAdminService.addUsers();
                }
            }

        } catch (MisConfigurationException | AdminServicesInvocationFailed | AdminServicesClientException e) {
            log.error("Exiting the program", e);
        }
    }

}
