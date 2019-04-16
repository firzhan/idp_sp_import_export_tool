package org.wso2.sample.model;

import java.util.HashMap;
import java.util.Map;

public class DataHolder {

    private String hostName;
    private int port;
    private String adminUserName;
    private char[] adminPassword;
    private String idpFolderLocation;
    private String spFolderLocation;

    private boolean keyStoreEnabled;
    private String keyStorePath;
    private char[] keyStorePassword;

    private String adminRole;

    private Map<String, String> userMap = new HashMap<>();

    private String[] roleNamesArray = new String[0];

    public String getAdminRole() {

        return adminRole;
    }

    public void setAdminRole(String adminRole) {

        this.adminRole = adminRole;
    }

    public void addUser(String userName, String password){
        userMap.putIfAbsent(userName, password);
    }

    public Map<String, String> getUserMap() {

        return userMap;
    }

    public String[] getRoleNamesArray() {

        return roleNamesArray;
    }

    public void setRoleNamesArray(String[] roleNamesArray) {

        this.roleNamesArray = roleNamesArray;
    }

    public boolean isKeyStoreEnabled() {

        return keyStoreEnabled;
    }

    public void setKeyStoreEnabled(boolean keyStoreEnabled) {

        this.keyStoreEnabled = keyStoreEnabled;
    }

    public String getKeyStorePath() {

        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {

        this.keyStorePath = keyStorePath;
    }

    public char[] getKeyStorePassword() {

        return keyStorePassword;
    }

    public void setKeyStorePassword(char[] keyStorePassword) {

        this.keyStorePassword = keyStorePassword;
    }

    public String getHostName() {

        return hostName;
    }

    public void setHostName(String hostName) {

        this.hostName = hostName;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public String getAdminUserName() {

        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {

        this.adminUserName = adminUserName;
    }

    public char[] getAdminPassword() {

        return adminPassword;
    }

    public void setAdminPassword(char[] adminPassword) {

        this.adminPassword = adminPassword;
    }

    public String getIdpFolderLocation() {

        return idpFolderLocation;
    }

    public void setIdpFolderLocation(String idpFolderLocation) {

        this.idpFolderLocation = idpFolderLocation;
    }

    public String getSpFolderLocation() {

        return spFolderLocation;
    }

    public void setSpFolderLocation(String spFolderLocation) {

        this.spFolderLocation = spFolderLocation;
    }
}
