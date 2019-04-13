/**
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.wso2.sample.model.DataHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IdentityProviderAdminServiceClient extends AdminServiceClient {

    private static final Log log =
            LogFactory.getLog(IdentityProviderAdminServiceClient.class);

    public IdentityProviderAdminServiceClient(DataHolder dataHolder,
                                              String cookie) throws AdminServicesClientException {

        super(dataHolder);
        try {
            this.stub =
                    new IdentityProviderMgtServiceStub(String.format(ServiceClientConstant.SERVICE_URL_FORMAT, dataHolder.getHostName(), dataHolder.getPort(), ServiceClientConstant.IDP_MGT_SERVICE_NAME), );
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault " +
                    "occurred" + " in Identity Provider Mgt Service Stub",
                    axisFault);
        }

        init(cookie);
    }

    public void fetchIDPs() throws AdminServicesClientException {

        try {
            IdentityProvider[] identityProviders =
                    ((IdentityProviderMgtServiceStub) this.stub).getAllIdPs();

            for (IdentityProvider identityProvider : identityProviders) {
                System.out.println(identityProvider.getIdentityProviderName());
                System.out.println(identityProvider);

                IdentityProvider identityProviderCompleteInformation =
                        ((IdentityProviderMgtServiceStub) this.stub).
                        getIdPByName(identityProvider.getIdentityProviderName());

                ObjectWriter objectWriter =
                        new ObjectMapper().writer().withDefaultPrettyPrinter();
                //String json = objectWriter.writeValueAsString
                // (identityProvider);

                try (FileOutputStream fileOutputStream =
                             new FileOutputStream(ServiceClientConstant.IDP_SOURCE_DIRECTORY_NAME + File.separator + identityProvider.getIdentityProviderName() + ".json")) {
                    objectWriter.writeValue(fileOutputStream,
                            identityProviderCompleteInformation);
                } catch (JsonGenerationException | JsonMappingException e) {
                    throw new AdminServicesClientException(String.format(
                            "Converting the IDP: %s into a JSON object " +
                                    "failed",
                            identityProviderCompleteInformation.getIdentityProviderName()), e);
                } catch (IOException e) {
                    throw new AdminServicesClientException(String.format(
                            "File writing operation failed for the IDP: %s",
                            identityProviderCompleteInformation.getIdentityProviderName()), e);
                }
            }

        } catch (RemoteException | IdentityProviderMgtServiceIdentityProviderManagementExceptionException e) {
            throw new AdminServicesClientException("IDPs importing operation "
                    + "failed", e);
        }
    }

    public void pushIDPs() {

        String filePath = String.format("%s%s",
                dataHolder.getIdpFolderLocation(),
                dataHolder.getIdpFolderLocation().endsWith(File.separator) ?
                        "" : File.separator);

        List<String> successLists = new ArrayList<>();
        List<String> failedLists = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(filePath))) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".json")).forEach((path) -> {

                /*String fileName = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
                System.out.println(fileName);*/

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IdentityProvider identityProvider = null;
                try {
                    identityProvider = mapper.readValue(path.toFile(),
                            IdentityProvider.class);
                    ((IdentityProviderMgtServiceStub) this.stub).addIdP(identityProvider);
                    successLists.add(identityProvider.getIdentityProviderName());
                } catch (IOException | IdentityProviderMgtServiceIdentityProviderManagementExceptionException e) {

                    String message = String.format("Create the IDP %s failed " +
                            "from the directory location %s",
                            path.getFileName(),
                            identityProvider != null ?
                                    identityProvider.getIdentityProviderName() : "");
                    System.out.println(message);

                    if( identityProvider != null)
                        failedLists.add(identityProvider.getIdentityProviderName());
                }
            });
        } catch (IOException e) {
            System.err.println("Directory Reading Operation Failed");
            e.printStackTrace();
        }
    }

    private void getIDPInformation(String idpName) throws IOException,
            IdentityProviderMgtServiceIdentityProviderManagementExceptionException {

        IdentityProvider identityProvider =
                ((IdentityProviderMgtServiceStub) this.stub).getIdPByName(idpName);

        ObjectWriter objectWriter =
                new ObjectMapper().writer().withDefaultPrettyPrinter();
        //String json = objectWriter.writeValueAsString(identityProvider);
        objectWriter.writeValue(new FileOutputStream("idp.json"),
                identityProvider);

        //JSON from file to Object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        IdentityProvider obj = mapper.readValue(new File("idp.json"),
                IdentityProvider.class);
        System.out.println(obj);
        //objectMapper.writeValue(new File("c:\\employee.json"), employee);

        // System.out.println(json);
      /*  System.out.println(identityProvider.getIdentityProviderName());
        System.out.println(identityProvider.getDisplayName());
        System.out.println(identityProvider.getEnable());
        System.out.println(identityProvider.getAlias());
        System.out.println(identityProvider.getCertificate());

        if(!StringUtils.isEmpty(identityProvider
        .getIdentityProviderDescription())){
            System.out.println(identityProvider
            .getIdentityProviderDescription());
        }

        CertificateInfo[] certificateInfos = identityProvider
        .getCertificateInfoArray();

        for(CertificateInfo certificateInfo : certificateInfos){
            System.out.println(certificateInfo.getCertValue());
            System.out.println(certificateInfo.getThumbPrint());
        }

        ClaimConfig claimConfig = identityProvider.getClaimConfig();

        System.out.println(claimConfig.getAlwaysSendMappedLocalSubjectId());

        ClaimMapping[] claimMappings = claimConfig.getClaimMappings();

        for(ClaimMapping claimMapping : claimMappings){
            System.out.println(claimMapping.getDefaultValue());
            System.out.println(claimMapping.getMandatory());
            System.out.println(claimMapping.getRequested());

            Claim localClaim = claimMapping.getLocalClaim();
            System.out.println(localClaim.getClaimId());
            System.out.println(localClaim.getClaimUri());

            Claim remoteClaim = claimMapping.getRemoteClaim();
            System.out.println(remoteClaim.getClaimId());
            System.out.println(remoteClaim.getClaimUri());
        }


        FederatedAuthenticatorConfig defaultAuthenticatorConfig =
        identityProvider.getDefaultAuthenticatorConfig();
        System.out.println(defaultAuthenticatorConfig.getEnabled());
        System.out.println(defaultAuthenticatorConfig.getDisplayName());
        System.out.println(defaultAuthenticatorConfig.getName());
        System.out.println(defaultAuthenticatorConfig.getValid());

        Property[] defaultProperties = defaultAuthenticatorConfig
        .getProperties();

        for(Property property : defaultProperties){
            System.out.println(property.getAdvanced());
            System.out.println(property.getConfidential());
            System.out.println(property.getDefaultValue());
            System.out.println(property.getDescription());
            System.out.println(property.getDisplayName());
            System.out.println(property.getDisplayOrder());
            System.out.println(property.getName());
            System.out.println(property.getRequired());
            System.out.println(property.getType());
            System.out.println(property.getValue());
        }

        ProvisioningConnectorConfig defaultProvisioningConnectorConfig =
        identityProvider.getDefaultProvisioningConnectorConfig();
        System.out.println(defaultProvisioningConnectorConfig.getBlocking());
        System.out.println(defaultProvisioningConnectorConfig.getEnabled());
        System.out.println(defaultProvisioningConnectorConfig.getName());
        System.out.println(defaultProvisioningConnectorConfig.getRulesEnabled
        ());
        System.out.println(defaultProvisioningConnectorConfig.getValid());

        FederatedAuthenticatorConfig[] federatedAuthenticatorConfigs =
        identityProvider.getFederatedAuthenticatorConfigs();

        for(FederatedAuthenticatorConfig federatedAuthenticatorConfig :
        federatedAuthenticatorConfigs){
            System.out.println(federatedAuthenticatorConfig.getDisplayName());
            System.out.println(federatedAuthenticatorConfig.getEnabled());
            System.out.println(federatedAuthenticatorConfig.getName());
            System.out.println(federatedAuthenticatorConfig.getValid());
            Property[] federatedProperties = federatedAuthenticatorConfig
            .getProperties();

            for(Property federatedProperty : federatedProperties){

                if(StringUtils.isEmpty(federatedProperty.getDefaultValue())){
                    System.out.println(federatedProperty.getDefaultValue());
                }

                if(StringUtils.isEmpty(federatedProperty.getDescription())){
                    System.out.println(federatedProperty.getDescription());
                }

                if(StringUtils.isEmpty(federatedProperty.getDisplayName())){
                    System.out.println(federatedProperty.getDisplayName());
                }

                if(StringUtils.isEmpty(federatedProperty.getType())){
                    System.out.println(federatedProperty.getType());
                }

                if(StringUtils.isEmpty(federatedProperty.getValue())){
                    System.out.println(federatedProperty.getValue());
                }

                System.out.println(federatedProperty.getAdvanced());
                System.out.println(federatedProperty.getConfidential());
                System.out.println(federatedProperty.getName());
                System.out.println(federatedProperty.getDisplayOrder());
                System.out.println(federatedProperty.getRequired());

            }
        }

        System.out.println(identityProvider.getFederationHub());

        if(!StringUtils.isEmpty(identityProvider.getHomeRealmId())){
            System.out.println(identityProvider.getHomeRealmId());
        }

        if(!StringUtils.isEmpty(identityProvider.getId())){
            System.out.println(identityProvider.getId());
        }

*/
    }

    public void getAllIDPsList() throws RemoteException,
            IdentityProviderMgtServiceIdentityProviderManagementExceptionException {
        //return
        IdentityProvider[] identityProviders =
                ((IdentityProviderMgtServiceStub) this.stub).getAllIdPs();
        for (IdentityProvider identityProvider : identityProviders) {
            System.out.println(identityProvider.getIdentityProviderName());
            System.out.println(identityProvider);
        }

        try {
            getIDPInformation("SampleIDP2222");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printStatus(){

    }
}
