/*
 * Copyright (c) 2014 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.wso2.carbon.identity.application.common.model.idp.xsd.IdentityProvider;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityProviderManagementExceptionException;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceStub;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.internal.Utils;
import org.wso2.sample.model.DataHolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.stream.Stream;

public class IdentityProviderAdminService extends AdminServices {

    private static final Logger log =
            Logger.getLogger(IdentityProviderAdminService.class);

    public IdentityProviderAdminService(DataHolder dataHolder,
                                        String cookie) throws AdminServicesClientException {

        super(dataHolder);
        try {
            this.stub =
                    new IdentityProviderMgtServiceStub(String.
                            format(ServiceClientConstant.SERVICE_URL_FORMAT,
                                    dataHolder.getHostName(), dataHolder.getPort(),
                                    ServiceClientConstant.IDP_MGT_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault " +
                    "occurred in Identity Provider Mgt Service Stub",
                    axisFault);
        }

        init(cookie);
    }

    public void fetchIdentityProviders()/* throws AdminServicesClientException*/ {

        String filePath =
                Utils.getFormattedFilePath(dataHolder.getIdpFolderLocation());

        try {
            IdentityProvider[] identityProviders =
                    ((IdentityProviderMgtServiceStub) this.stub).getAllIdPs();

            if (identityProviders != null) {

                for (IdentityProvider identityProvider : identityProviders) {

                    IdentityProvider identityProviderCompleteInformation =
                            ((IdentityProviderMgtServiceStub) this.stub).
                            getIdPByName(identityProvider.getIdentityProviderName());

                    ObjectWriter objectWriter =
                            new ObjectMapper().writer().withDefaultPrettyPrinter();

                    try (FileOutputStream fileOutputStream =
                                 new FileOutputStream(filePath + identityProvider.getIdentityProviderName() + ".json")) {
                        objectWriter.writeValue(fileOutputStream,
                                identityProviderCompleteInformation);
                        this.successLists.add(identityProvider.getIdentityProviderName());
                    } catch (JsonGenerationException | JsonMappingException e) {
                        this.failedLists.add(identityProvider.getIdentityProviderName());
                        String message = String.format("Failed to write " +
                                        "the fetched IDP configuration %s " +
                                        "into a file",
                                identityProviderCompleteInformation.getIdentityProviderName());
                        log.error(message, e);
                    } catch (IOException e) {
                        this.failedLists.add(identityProvider.getIdentityProviderName());
                        String message = String.format("Fetching the configuration " +
                                "of " +
                                "IDP " +
                                "%s is successful. But internal error caused " +
                                "the abortion of file writing operation",
                                identityProviderCompleteInformation.getIdentityProviderName());
                        log.error(message, e);

                    }
                }
            }
        } catch (RemoteException | IdentityProviderMgtServiceIdentityProviderManagementExceptionException e) {
            printStatus(ServiceClientConstant.FETCH_KEYWORD, ServiceClientConstant.IDP_TYPE);
            log.error("Failed to obtain the basic information on existing " +
                    "Identity Providers from the key-manager ", e);
        }

        printStatus(ServiceClientConstant.FETCH_KEYWORD, ServiceClientConstant.IDP_TYPE);

    }

    public void publishIdentityProviders(){

        String filePath =
                Utils.getFormattedFilePath(dataHolder.getIdpFolderLocation());

        try (Stream<Path> paths = Files.walk(Paths.get(filePath))) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach((path) -> {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IdentityProvider identityProvider = null;
                try {
                    identityProvider = mapper.readValue(path.toFile(),
                            IdentityProvider.class);
                    ((IdentityProviderMgtServiceStub) this.stub).addIdP(identityProvider);
                    successLists.add(identityProvider.getIdentityProviderName());
                } catch (IOException | IdentityProviderMgtServiceIdentityProviderManagementExceptionException e) {

                    String message = String.format("The IDP %s " +
                                    "publishing failed from the directory location %s",
                            path.getFileName(), identityProvider != null ?
                                    identityProvider.getIdentityProviderName() : "");
                    log.error(message, e);

                    if (identityProvider != null) {
                        failedLists.add(identityProvider.getIdentityProviderName());
                    }
                }
            });
        } catch (IOException e) {
            printStatus(ServiceClientConstant.PUBLISH_KEYWORD,
                    ServiceClientConstant.IDP_TYPE);
            String message = String.format("The Identity Providers couldn't be published " +
                    "in the environment as the directory %s couldn't be " +
                    "loaded", filePath);
            log.error(message, e);
            /*throw new AdminServicesClientException("IDPs importing operation "
                    + "failed", e);*/
        }

        printStatus(ServiceClientConstant.PUBLISH_KEYWORD,
                ServiceClientConstant.IDP_TYPE);
    }
}
