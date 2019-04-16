package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wso2.carbon.um.ws.api.stub.PermissionDTO;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.exception.AdminServicesInvocationFailed;
import org.wso2.sample.exception.AdminServicesOperationException;
import org.wso2.sample.model.DataHolder;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserStoreMgtAdminService extends AdminServices {

    private static final Logger log =
            Logger.getLogger(UserStoreMgtAdminService.class);

    public UserStoreMgtAdminService(DataHolder dataHolder, String cookie) throws AdminServicesClientException {

        super(dataHolder);

        try {
            this.stub = new RemoteUserStoreManagerServiceStub(String.
                    format(ServiceClientConstant.SERVICE_URL_FORMAT,
                            dataHolder.getHostName(), dataHolder.getPort(),
                            ServiceClientConstant.REMOTE_USERSTORE_MGT_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault " +
                    "occurred" + " in Remote UserStore Mgt Service Stub",
                    axisFault);
        }

        init(cookie);
    }

    public void addRoles(String[] permissionsArrays) throws AdminServicesOperationException {

        List<PermissionDTO> permissionDTOList = new ArrayList<>();

        for (String permission : permissionsArrays) {

            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setResourceId(permission);
            permissionDTO.setAction("ui.execute");

            permissionDTOList.add(permissionDTO);
        }

        String[] roles = this.dataHolder.getRoleNamesArray();

        for (String role : roles) {

            if (!StringUtils.isEmpty(role)) {

                try {
                    ((RemoteUserStoreManagerServiceStub) this.stub).addRole(role.trim(), null, permissionDTOList.stream().toArray(PermissionDTO[]::new));
                    successLists.add(role);
                } catch (RemoteException | RemoteUserStoreManagerServiceUserStoreExceptionException e) {
                    failedLists.add(role);
                    printStatus(ServiceClientConstant.ADDED_KEYWORD,
                            ServiceClientConstant.ROLE_TYPE);
                    throw new AdminServicesOperationException(String.format(
                            "Failed to add the role %s", role.trim()), e);
                }
            }
        }

        printStatus(ServiceClientConstant.ADDED_KEYWORD,
                ServiceClientConstant.ROLE_TYPE);

    }

    public void addUsers() throws AdminServicesOperationException {

        String[] roles = this.dataHolder.getRoleNamesArray();

        Map<String, String> userMap = this.dataHolder.getUserMap();

        userMap.forEach((k, v) -> {

            try {
                ((RemoteUserStoreManagerServiceStub) this.stub).addUser(k, v,
                        roles, null, "default", false);
                successLists.add(k);
            } catch (RemoteException | RemoteUserStoreManagerServiceUserStoreExceptionException e) {
                failedLists.add(k);
                String errorMsg = String.format("Failed to add the User %s ",
                        k);
                printStatus(ServiceClientConstant.ADDED_KEYWORD,
                        ServiceClientConstant.USER_TYPE);
                throw new AdminServicesInvocationFailed(errorMsg, e);
            }
        });

        printStatus(ServiceClientConstant.ADDED_KEYWORD,
                ServiceClientConstant.USER_TYPE);
    }

}
