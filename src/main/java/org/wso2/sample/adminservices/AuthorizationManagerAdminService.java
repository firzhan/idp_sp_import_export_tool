package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.carbon.um.ws.api.stub.RemoteAuthorizationManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.UserStoreExceptionException;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.exception.AdminServicesInvocationFailed;
import org.wso2.sample.model.DataHolder;

import java.rmi.RemoteException;

public class AuthorizationManagerAdminService extends AdminServices {

    private static final Logger log =
            Logger.getLogger(AuthorizationManagerAdminService.class);

    public AuthorizationManagerAdminService(DataHolder dataHolder,
                                     String cookie) throws AdminServicesClientException {
        super(dataHolder);


        try {
            this.stub =
                    new RemoteAuthorizationManagerServiceStub(String.
                            format(ServiceClientConstant.SERVICE_URL_FORMAT,
                                    dataHolder.getHostName(), dataHolder.getPort(),
                                    ServiceClientConstant.REMOTE_AUTHORIZATION_MGT_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault occurred" +
                    " in Remote Authorization Mgt Service Stub", axisFault);
        }

        init(cookie);
    }

    public String[] getPermissionOfSuperUser() throws AdminServicesInvocationFailed {

        try {
            return ((RemoteAuthorizationManagerServiceStub) this.stub).
            getAllowedUIResourcesForUser(this.dataHolder.getAdminUserName(), "/");
        } catch (RemoteException | UserStoreExceptionException e) {
            throw new AdminServicesInvocationFailed("Obtaining the super-user" +
                    " permission tree is failed", e);
        }
    }
}
