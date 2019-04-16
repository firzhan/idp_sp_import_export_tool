package org.wso2.sample.adminservices;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
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

public class IdentityApplicationMgtAdminService extends AdminServices {

    private static final Logger log =
            Logger.getLogger(IdentityApplicationMgtAdminService.class);

    public IdentityApplicationMgtAdminService(DataHolder dataHolder,
                                              String cookie) throws AdminServicesClientException {

        super(dataHolder);
        try {
            this.stub =
                    new IdentityApplicationManagementServiceStub(String.
                            format(ServiceClientConstant.SERVICE_URL_FORMAT,
                                    dataHolder.getHostName(), dataHolder.getPort(),
                                    ServiceClientConstant.APP_MGT_SERVICE_NAME));
        } catch (AxisFault axisFault) {
            throw new AdminServicesClientException("Axis2 soap fault occurred" +
                    " in Identity Application Mgt Service Stub", axisFault);
        }

        init(cookie);
    }

    public void fetchServiceProviders() /*throws AdminServicesClientException */{

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
                        String message = String.format("Failed to write " +
                                        "the fetched SP configuration %s into" +
                                        " a file",
                                applicationBasicInfo.getApplicationName());
                        log.error(message, e1);
                    }
                }
            }
        } catch (RemoteException | IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            log.error("The operations for fetching the service providers got " +
                    "aborted", e);
            printStatus(ServiceClientConstant.FETCH_KEYWORD, ServiceClientConstant.SP_TYPE);
        }

        printStatus(ServiceClientConstant.FETCH_KEYWORD,
                ServiceClientConstant.SP_TYPE);
    }

    public void publishServiceProviders() /*throws AdminServicesClientException */{

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
                                String message = String.format("Failed to " +
                                                "publish the Service Provider" +
                                                " %s's configuration", fileName);
                                log.error(message);
                            }

                        } catch (IOException | IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
                            String errorMessage = String.format(
                                    "Publishing failed as the  " +
                                    "the reading of the SP configuration" +
                                    "operation from the file %s failed", fileName);
                            log.error(errorMessage, e);
                            failedLists.add(fileName);
                        }
                    });
        } catch (IOException e) {
            log.error("The Service Providers couldn't be pushed into the " +
                    "environment", e);
        }

        printStatus(ServiceClientConstant.PUBLISH_KEYWORD,
                ServiceClientConstant.SP_TYPE);
    }

}
