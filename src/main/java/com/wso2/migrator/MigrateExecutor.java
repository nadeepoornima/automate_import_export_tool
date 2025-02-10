package com.wso2.migrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class MigrateExecutor {
    private static final Logger LOGGER = Logger.getLogger(MigrateExecutor.class.getName());
    public static void main(String[] args) {
        LOGGER.info("***** Welcome to API-Application Migrator *****");
        ReadConfigFile configs = new ReadConfigFile();

        // Load Truststore from Config
        String trustStorePath = configs.getProperty("TRUSTSTORE.PATH"); // Example: "/path/to/client-truststore.jks"
        String trustStorePassword = configs.getProperty("TRUSTSTORE.PASSWORD");

        // Load TrustStore before making API calls
        SSLUtil.loadTrustStore(trustStorePath, trustStorePassword);

        // Source Environment details
        String srcEnvName = configs.getProperty("SRC.ENV.NAME");
        String srcEncClientRegistration = configs.getProperty("SRC.ENV.CLIENT.REG.ENDPOINT");
        String srcEnvAdminEndpoint = configs.getProperty("SRC.APIM.ADMIN.ENDPOINT");
        String srcEnvTokenEndpoint = configs.getProperty("SRC.TOKEN.ENDPOINT");
        String srcEncPubEndpoint = configs.getProperty("SRC.PUB.ENDPOINT");
        String srcDevportalEndpoint = configs.getProperty("SRC.DEVPORTAL.ENDPOINT");
        String srcEnvUserName = configs.getProperty("SRC.USERNAME");
        String srcEnvPassword = configs.getProperty("SRC.PASSWORD");

        // Target Environment details
        String tgtEnvName = configs.getProperty("TRG.ENV.NAME");
        String tgtEncClientRegistration = configs.getProperty("TRG.ENV.CLIENT.REG.ENDPOINT");
        String tgtEnvAdminEndpoint = configs.getProperty("TRG.APIM.ADMIN.ENDPOINT");
        String tgtEnvTokenEndpoint = configs.getProperty("TRG.TOKEN.ENDPOINT");
        String tgtEncPubEndpoint = configs.getProperty("TRG.PUB.ENDPOINT");
        String tgtDevportalEndpoint = configs.getProperty("TRG.DEVPORTAL.ENDPOINT");

        // remove environment
        String rmEnvName = configs.getProperty("RM.ENV.NAME");

        printEnvDetails("Source", srcEnvName, srcEncClientRegistration, srcEnvAdminEndpoint, srcEnvTokenEndpoint,
                srcEncPubEndpoint, srcDevportalEndpoint);
        printEnvDetails("Target", tgtEnvName, tgtEncClientRegistration, tgtEnvAdminEndpoint, tgtEnvTokenEndpoint,
                tgtEncPubEndpoint, tgtDevportalEndpoint);

        // remove existing environment
        LOGGER.info("***** Remove Environment *****");
        removeEnv(rmEnvName);
        // Add source environment
        LOGGER.info("***** Adding Source Environment *****");
        addEnvironment(srcEnvName, srcEncClientRegistration, srcEnvAdminEndpoint, srcEnvTokenEndpoint,
                srcEncPubEndpoint, srcDevportalEndpoint);

        // Get all added environments
        LOGGER.info("***** Fetching All Added Environments *****");
        getAllAddedEnvs();

        //Log into the source environment
        LOGGER.info("***** Login into the source environment *****");
        loginSrcEnv(srcEnvName,srcEnvUserName, srcEnvPassword);

        //Log out from the source environment
        LOGGER.info("***** Logout from the source environment *****");
        logoutSrcEnv(srcEnvName);
    }
    private static void printEnvDetails(String type, String name, String registration, String admin, String token, String publisher, String devportal) {
        System.out.println(type + " Environment:");
        System.out.println(" - Name: " + name);
        System.out.println(" - Client Registration: " + registration);
        System.out.println(" - Admin: " + admin);
        System.out.println(" - Token: " + token);
        System.out.println(" - Publisher: " + publisher);
        System.out.println(" - DevPortal: " + devportal);
        System.out.println("-----------------------------------------");
    }
    public static void addEnvironment(String env, String registration, String admin, String token, String publisher,
                                      String devportal) {
        String addEnvcommand = String.format(
                "apictl add env %s --registration %s --admin %s --token %s --publisher %s --devportal %s",
                env, registration, admin, token, publisher, devportal
        );
        executeCommand(addEnvcommand);
    }
    public static void getAllAddedEnvs() {
        String getEnvCommand = "apictl get envs";
        executeCommand(getEnvCommand);
    }
    public static void removeEnv(String envName){
        String removeEnvCommand = String.format("apictl remove env %s", envName);
        System.out.println(removeEnvCommand);
        executeCommand(removeEnvCommand);
    }
    public static void loginSrcEnv(String envname, String username, String password){
        String loginSrcCommand = String.format("apictl login -k %s -u %s -p %s", envname, username, password);
        System.out.println(loginSrcCommand);
        executeCommand(loginSrcCommand);
    }

    public static void logoutSrcEnv(String envname){
        String logoutSrcCommand = String.format("apictl logout -k %s", envname);
        System.out.println(logoutSrcCommand);
        executeCommand(logoutSrcCommand);
    }

    private static void executeCommand(String command) {
        LOGGER.info("Executing command: " + command);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LOGGER.info("Command executed successfully.");
            } else {
                LOGGER.severe("Command execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Exception while executing command: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
