package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.identity.application.common.model.xsd.ApplicationBasicInfo;
import org.wso2.carbon.identity.application.common.model.xsd.ImportResponse;
import org.wso2.carbon.identity.application.common.model.xsd.SpFileContent;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.AdminServicesClientException;
import org.wso2.sample.internal.Utils;
import org.wso2.sample.model.DataHolder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.stream.Stream;

public class IdentityApplicationMgtAdminServiceClient extends AdminServiceClient {

    public IdentityApplicationMgtAdminServiceClient(DataHolder dataHolder,
                                                    String cookie) throws AdminServicesClientException {

        super(dataHolder);
        try {
            this.stub =
                    new IdentityApplicationManagementServiceStub(String.format(ServiceClientConstant.SERVICE_URL_FORMAT, dataHolder.getHostName(), dataHolder.getPort(), ServiceClientConstant.APP_MGT_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault occurred" +
                    " in Identity Application Mgt Service Stub", axisFault);
        }

        init(cookie);
    }

    public void fetchSPs() /*throws AdminServicesClientException */{

        String filePath =
                Utils.getFormattedFilePath(dataHolder.getSpFolderLocation());

        try {
            ApplicationBasicInfo[] applicationBasicInfos =
                    ((IdentityApplicationManagementServiceStub) this.stub).
                    getAllApplicationBasicInfo();

            if (applicationBasicInfos != null) {

                for (ApplicationBasicInfo applicationBasicInfo :
                        applicationBasicInfos) {

                    String output =
                            ((IdentityApplicationManagementServiceStub) this.stub).
                            exportApplication(applicationBasicInfo.getApplicationName(), true);
                    try (Writer fileWriter =
                                 new FileWriter(filePath + applicationBasicInfo.getApplicationName() + ".xml")) {
                        fileWriter.write(output);
                        successLists.add(applicationBasicInfo.getApplicationName());
                    } catch (IOException e1) {
                        failedLists.add(applicationBasicInfo.getApplicationName());
                        System.err.println(String.format("Failed to write " +
                                "the" + " SP configuration %s into a file",
                                applicationBasicInfo.getApplicationName()));
                        System.err.println(e1.getMessage());
                    }
                }
            }
        } catch (RemoteException | IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            printStatus(ServiceClientConstant.FETCH_KEYWORD,
                    ServiceClientConstant.SP_TYPE);
          /*  throw new AdminServicesClientException("SPs pushing operation " +
                    "failed", e);*/
        }

        printStatus(ServiceClientConstant.FETCH_KEYWORD,
                ServiceClientConstant.SP_TYPE);
    }

    public void pushAllSPs() /*throws AdminServicesClientException */{

        String filePath =
                Utils.getFormattedFilePath(dataHolder.getSpFolderLocation());

        try (Stream<Path> paths = Files.walk(Paths.get(filePath))) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".xml")).
                    forEach((path) -> {
                        String fileName = path.getFileName().toString().
                                replaceFirst("[.][^.]+$", "");

                        try {
                            String content =
                                    new String(Files.readAllBytes(path));

                            SpFileContent spFileContent = new SpFileContent();
                            spFileContent.setContent(content);
                            spFileContent.setFileName(fileName);
                            ImportResponse importResponse =
                                    ((IdentityApplicationManagementServiceStub) this.stub).
                                    importApplication(spFileContent);

                            if (importResponse.getResponseCode() == ServiceClientConstant.CREATED_STATUS) {
                                successLists.add(importResponse.getApplicationName());
                            } else {
                                failedLists.add(importResponse.getApplicationName());
                                System.err.println(String.format("Failed to " +
                                        "publish " + "the SP %s " +
                                        "configuration",
                                        importResponse.getApplicationName()));
                            }

                        } catch (IOException | IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
                            System.out.println(e.getMessage());
                            System.err.println(String.format("Failed to read "
                                    + "the SP configuration file %s",
                                    fileName));
                            failedLists.add(fileName);
                        }
                    });
        } catch (IOException e) {
            /*printStatus(ServiceClientConstant.PUBLISH_KEYWORD,
                    ServiceClientConstant.SP_TYPE);
*/           /* throw new AdminServicesClientException("Directory Reading " +
                    "Operation Failed", e);*/

        }

        printStatus(ServiceClientConstant.PUBLISH_KEYWORD,
                ServiceClientConstant.SP_TYPE);
    }

}
