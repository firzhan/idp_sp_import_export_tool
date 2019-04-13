package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.wso2.carbon.identity.application.common.model.xsd.ApplicationBasicInfo;
import org.wso2.carbon.identity.application.common.model.xsd.ImportResponse;
import org.wso2.carbon.identity.application.common.model.xsd.SpFileContent;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.stream.Stream;

public class IdentityApplicationMgtAdminServiceClient extends AdminServiceClient {

    public IdentityApplicationMgtAdminServiceClient(String serviceUrl, String cookie) throws AdminServicesClientException {

        try {
            this.stub = new IdentityApplicationManagementServiceStub(serviceUrl);
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault occurred in Identity Application Mgt Service Stub",
                    axisFault);
        }

        init(cookie, false);
    }

    public void exportAllSPs() throws AdminServicesClientException  {

        try {
            ApplicationBasicInfo[] applicationBasicInfos = ((IdentityApplicationManagementServiceStub) this.stub).
                    getAllApplicationBasicInfo();

            for(ApplicationBasicInfo applicationBasicInfo : applicationBasicInfos){

                System.out.println(applicationBasicInfo.getApplicationName());

                String output = ((IdentityApplicationManagementServiceStub) this.stub).exportApplication(applicationBasicInfo.getApplicationName(), true);
                System.out.println(output);
            }
        } catch (RemoteException |  IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            throw new AdminServicesClientException("IDPs importing operation failed",  e);
        }
    }

    public void importAllSPs() throws AdminServicesClientException  {

        String filePath = ServiceClientConstant.IDP_SOURCE_DIRECTORY_NAME + File.separator;
        try (Stream<Path> paths = Files.walk(Paths.get(filePath))) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".xml")).
                    forEach((path) ->{
                String fileName = path.getFileName().toString().
                        replaceFirst("[.][^.]+$", "");

                try {
                    String content = new String(Files.readAllBytes(path));

                    SpFileContent spFileContent = new SpFileContent();
                    spFileContent.setContent(content);
                    spFileContent.setFileName(fileName);
                    ImportResponse importResponse = ((IdentityApplicationManagementServiceStub) this.stub).importApplication(spFileContent);
                        System.out.println("Application name : " + importResponse.getApplicationName() + " Status : " + importResponse.getResponseCode());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
                    e.printStackTrace();
                }
                /*String fileName = path.getFileName().toString().
                         replaceFirst("[.][^.]+$", "");
                System.out.println(fileName);*/
              /*  ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                IdentityProvider identityProvider = null;
                try {
                    identityProvider = mapper.readValue(path.toFile(), IdentityProvider.class);
                    ((IdentityProviderMgtServiceStub) this.stub).addIdP(identityProvider);
                }  catch (IOException e) {
                    System.err.println("Reading the file failed for " + path.getFileName());
                    e.printStackTrace();
                }catch (IdentityProviderMgtServiceIdentityProviderManagementExceptionException e) {
                    System.err.println("Error has thrown from the stub of " + identityProvider.getIdentityProviderName()
                    );
                    e.printStackTrace();
                }*/
            });
        } catch (IOException e) {
            System.err.println("Directory Reading Operation Failed");
            e.printStackTrace();
        }
    }

}
