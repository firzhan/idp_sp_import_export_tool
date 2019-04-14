/**
 * Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.sample.constant;

public class ServiceClientConstant {

    public static final String PROPERTIES_FILE_NAME = "is_migration_client.properties";

    public static final String HOST_NAME_PROPERTY_STRING = "km.hostname";
    public static final String HTTPS_SERVICE_PORT_PROPERTY_STRING = "km.port";
    public static final String SERVICE_URL_FORMAT = "https://%s:%d/services/%s";
    public static final String ADMIN_USER_NAME_PROPERTY_STRING = "km.admin.username";
    public static final String ADMIN_PASSWORD_PROPERTY_STRING = "km.admin.password";
    public static final String IDP_STORAGE_DIRECTORY_PROPERTY_STRING = "km.idp.directory.location";
    public static final String SP_STORAGE_DIRECTORY_PROPERTY_STRING = "km.sp.directory.location";
    public static final String KM_KEYSTORE_ENABLED_PROPERTY_STRING = "km.keystore.enabled";
    public static final String KM_KEYSTORE_PATH_PROPERTY_STRING = "km.keystore.path";
    public static final String KM_KEYSTORE_PASSWORD_PROPERTY_STRING = "km.keystore.password";

    public static final int CREATED_STATUS = 201;

    public static final String IDP_TYPE = "IDPs";
    public static final String SP_TYPE = "SPs";

    public static final String AUTHENTICATION_SERVICE_NAME = "AuthenticationAdmin";
    public static final String IDP_MGT_SERVICE_NAME = "IdentityProviderMgtService";
    public static final String APP_MGT_SERVICE_NAME = "IdentityApplicationManagementService";

    public static final String ROLES_PROPERTY_STRING = "km.roles";
    public static final String USERNAMES_PROPERTY_STRING = "km.usernames";
    public static final String PASSWORDS_PROPERTY_STRING = "km.passwords";

    public static String OPTIONS_INFORMATION_PARA_STRING = "Please choose a number to continue....\n";
    public static String OPTIONS_1_PARA_STRING = "1 - IDP/SP Fetch/Push\n2 -" +
            " Users/Roles creation    \n";
    public static String OPTIONS_2_PARA_STRING = "1 - IDP/SP Fetch\n2 - " +
            "IDP/SP" +
            " Push    \n";
    public static String EXIT_STRING = "X - Exit The Game    ";

    public static String INPUT_SELECTION_REGEX = "[12]{1}";

    public static final String KEY_STORE_TYPE = "jks";

    public static final String SUCCESS_MESSAGE_STRING = "Successfully " +
            "%s %s count : %d";

    public static final String FAILED_MESSAGE_STRING = "Failed to " +
            "%s %s count : %d";

    public static final String PUBLISH_KEYWORD = "Published";
    public static final String FETCH_KEYWORD = "Fetched";
    private ServiceClientConstant() {

    }
}
