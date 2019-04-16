package org.wso2.sample.internal;

import org.apache.commons.lang.StringUtils;
import org.wso2.sample.constant.ServiceClientConstant;
import org.wso2.sample.exception.MisConfigurationException;
import org.wso2.sample.model.DataHolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataLoader {

    public static DataHolder loadProperties() throws MisConfigurationException {

        String path = "./" + ServiceClientConstant.PROPERTIES_FILE_NAME;

        DataHolder dataHolder = new DataHolder();


        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(path)){

            properties.load(inputStream);
            dataHolder.setHostName(properties.getProperty(ServiceClientConstant.HOST_NAME_PROPERTY_STRING));
            dataHolder.setPort(Integer.valueOf(properties.getProperty(ServiceClientConstant.HTTPS_SERVICE_PORT_PROPERTY_STRING)));
            dataHolder.setAdminUserName(properties.getProperty(ServiceClientConstant.ADMIN_USER_NAME_PROPERTY_STRING));
            dataHolder.setAdminPassword(properties.getProperty(ServiceClientConstant.ADMIN_PASSWORD_PROPERTY_STRING).toCharArray());
            dataHolder.setIdpFolderLocation(properties.getProperty(ServiceClientConstant.IDP_STORAGE_DIRECTORY_PROPERTY_STRING));
            dataHolder.setSpFolderLocation(properties.getProperty(ServiceClientConstant.SP_STORAGE_DIRECTORY_PROPERTY_STRING));
            dataHolder.setKeyStoreEnabled(Boolean.valueOf(properties.getProperty(ServiceClientConstant.KM_KEYSTORE_ENABLED_PROPERTY_STRING)));
            dataHolder.setAdminRole(properties.getProperty(ServiceClientConstant.KM_ADMIN_ROLE_PROPERTY_STRING));

            if(dataHolder.isKeyStoreEnabled()){
                dataHolder.setKeyStorePath(properties.getProperty(ServiceClientConstant.KM_KEYSTORE_PATH_PROPERTY_STRING));
                dataHolder.setKeyStorePassword(properties.getProperty(ServiceClientConstant.KM_KEYSTORE_PASSWORD_PROPERTY_STRING).toCharArray());
            }

            String roleName = properties.getProperty(ServiceClientConstant.ROLES_PROPERTY_STRING);

            if(!StringUtils.isEmpty(roleName)){
                String[] roles = roleName.split(",");
                dataHolder.setRoleNamesArray(roles);
            }

            String userNamesList = properties.getProperty(ServiceClientConstant.USERNAMES_PROPERTY_STRING);
            String passwordsList = properties.getProperty(ServiceClientConstant.PASSWORDS_PROPERTY_STRING);

            if( (!StringUtils.isEmpty(userNamesList)) && (!StringUtils.isEmpty(passwordsList)) ){


                String[] users = userNamesList.split(",");
                String[] passwords = passwordsList.split(",");

                if(users.length != passwords.length){
                    throw new MisConfigurationException("Username Counts and Password Counts are mismatching");
                }

                for(int index = 0; index < users.length; ++index){
                    dataHolder.addUser(users[index], passwords[index]);
                }
            }

        } catch (IOException e){
            String message = String.format("Exception occured while trying to" +
                    " close the already read  property file : %s",
                    ServiceClientConstant.PROPERTIES_FILE_NAME);

            throw new MisConfigurationException(message, e);
        }

        return dataHolder;
    }

}
