# idp_sp_import_export_tool

This is a java migration client tool to import and export the configurations of the Identity Providers and the Service Providers from WSO2 Identity Server across multiple environments. 

This tool is more or less similar to the API Import and Export tool.

Further information on Identity Providers and Service Providers can be found at https://docs.wso2.com/display/IS570/Configuring+a+SP+and+IdP+Using+Configuration+Files.

Before proceeding with building the java source, initially, we have to configure the is_migration_client.properties file.

# is_migration_client.properties

The proerties file consist of following configuration.

**km.hostname=localhost**

**km.port=9443**

**km.admin.username=admin**

**km.admin.password=admin**

Hostname of the intended WSO2 Identity server. 
This hostname, port, username and password differs whether we are running the server to export the configurations from the # # WSO2 IS to a file system or importing it to a destination server

# This direcotry location is used to store the fetched IDP and SP confogirations.
# Afterwards, when publishing the configurations, the relevant configurations of IDP and SP can be found at the following 
# directory locations
km.idp.directory.location=/Users/firzhan/CODE_REPO/tmp/idp
km.sp.directory.location=/Users/firzhan/CODE_REPO/tmp/sp

# Option to enable or disable the keystore. 
# If the keystore option is disabled, the keystore path doesn't have to be defined.
km.keystore.enabled=false
km.keystore.path=/Users/firzhan/zone1/wso2is-km-5.7.0/repository/resources/security/client-truststore.jks


# These options are related to configuring the users and roles who have the priviledges to publish IDP and SPs
# Basically, we are setting the admin priviledges to other publishing roles and this can be changed in the code.
# The users created in the option #km.usernames# will be assigned to the roles defined under ##km.roles##
# The relationship/mapping between the #km.usernames# and #km.passwords# is one-to-one. In other terms, 
# user idp has password admin123 and the user sp1 has password admin1234
# The admin service requires the usernames and role names to consist atleast 3 characters. And the password should have 
# minimum length of 5 
km.admin.role=admin

km.roles=idp,sp1
km.usernames=idp,sp1
km.passwords=admin123,admin1234

1) After checking out the repo, the tool can be built using 'mvn clean install' command

2) The resulting jar 'is_migration_tool.jar' could be found inside the target directory.

3) Eventually the jar can be moved into a seprate directory and should be placed along with the is_migration_client.properties 
   file.
   
├── tool_directory
│   │   ├── is_migration_tool.jar
│   │   ├── is_migration_client.properties
 
 
 
