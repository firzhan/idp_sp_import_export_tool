# idp_sp_import_export_tool

This is a java migration client tool to import and export the configurations of the Identity Providers and the Service Providers from WSO2 Identity Server across multiple environments. 

This tool is more or less similar to the API Import and Export tool.

Further information on Identity Providers and Service Providers can be found at https://docs.wso2.com/display/IS570/Configuring+a+SP+and+IdP+Using+Configuration+Files.

Before proceeding with building the java source, initially, we have to configure the **is_migration_client.properties** file.

# is_migration_client.properties

This properties file contains the information that required to identify the WSO2 Identity Server, adminstrative credentials of the WSO2 Identity Server and other related informations.

The way how the values are picked from the properties file depends on the client execution **mode**. The client executes on two different modes. Those are

            **Downloading mode**
            
            **Publishing mode**
            
###### Downloading Mode

The client runs in this mode to download and store the IDP and SP configurations from the WSO2 IS. Therefore, the configurations - *km.hostname, km.port, km.admin.username, km.admin.password* define the server from where the SPs and IDPs will be fetched. 

Subsequently the configurations **km.idp.directory.location** and **km.sp.directory.location** denote the locations to save the the downloaded configs of IDPs and SPs.


###### Publishing Mode

The client runs in this mode to publish the IDP and SP configurations to the WSO2 IS. 

Therefore, the configurations - *km.hostname, km.port, km.admin.username, km.admin.password* define the server to where the SPs and IDPs will be published.

The locations of the IDP and SP configurations could be defined as the values of the **km.idp.directory.location** and **km.sp.directory.location**. 

However, before publishing the configurations, we have to define the roles with the permissions to publish IDPs and SPs.
For the moment, I have defined the publishing roles to have the same permissions as the super-user's one. However, this can be changed in the code. 

The users created in the option **km.usernames** will be assigned to the roles defined under ##km.roles##
The relationship/mapping between the #km.usernames# and #km.passwords# is one-to-one. 
In other terms, the user idp has password admin123 and the user sp1 has password admin1234.

**Note**: 
The admin service requires the usernames and role names to consist atleast 3 characters and the password should have 
minimum length of 5. 


The sample  **is_migration_client.properties** file can be found over here.

**km.hostname=localhost**
**km.port=9443**
**km.admin.username=admin**
**km.admin.password=admin**
**km.idp.directory.location=/Users/firzhan/CODE_REPO/tmp/idp**
**km.sp.directory.location=/Users/firzhan/CODE_REPO/tmp/sp**
**km.keystore.enabled=false**
**km.keystore.path=/Users/firzhan/zone1/wso2is-km-5.7.0/repository/resources/security/client-truststore.jks**
**km.admin.role=admin**
**km.roles=idp,sp1**
**km.usernames=idp,sp1**
**km.passwords=admin123,admin1234**


1) After checking out the repo, the tool can be built using 'mvn clean install' command

2) The resulting jar 'is_migration_tool.jar' could be found inside the target directory.

3) Eventually the jar can be moved into a seprate directory and should be placed along with the is_migration_client.properties 
   file.
   
├── tool_directory
│   │   ├── is_migration_tool.jar
│   │   ├── is_migration_client.properties
 
 
 
