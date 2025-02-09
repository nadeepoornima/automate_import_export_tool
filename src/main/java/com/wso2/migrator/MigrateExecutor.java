package com.wso2.migrator;

import java.io.IOException;
import java.util.logging.Logger;

public class MigrateExecutor {
    private static final Logger LOGGER = Logger.getLogger(MigrateExecutor.class.getName());
    public static void main(String[] args) {
        LOGGER.info("***** Welcome to API-Application Migrator *****");
        ReadConfigFile configs = new ReadConfigFile();
//        Source Environment details
        String srcEnvName = configs.getProperty("SRC.ENV.NAME");
        String srcEncClientRegistartion = configs.getProperty("SRC.ENV.CLIENT.REG.ENDPOINT");
        String srcEnvAdminEndpoint = configs.getProperty("SRC.APIM.ADMIN.ENDPOINT");
        String srcEnvTokenEndpoint = configs.getProperty("SRC.TOKEN.ENDPOINT");
        String srcEncPubEndpoint = configs.getProperty("SRC.PUB.ENDPOINT");
        String srcDevportalEndpoint = configs.getProperty("SRC.DEVPORTAL.ENDPOINT");

        System.out.println("Src Env Name: "+ srcEnvName);
        System.out.println("Src Env Clt reg: "+ srcEncClientRegistartion);
        System.out.println("Src  Admin: "+ srcEnvAdminEndpoint);
        System.out.println("Src Token: "+ srcEnvTokenEndpoint);
        System.out.println("Src  Publisher: "+ srcEncPubEndpoint);
        System.out.println("Src Devportal: "+ srcDevportalEndpoint);

//        Target Environment details
        String tgtEnvName = configs.getProperty("TRG.ENV.NAME");
        String tgtEncClientRegistartion = configs.getProperty("TRG.ENV.CLIENT.REG.ENDPOINT");
        String tgtEnvAdminEndpoint = configs.getProperty("TRG.APIM.ADMIN.ENDPOINT");
        String tgtEnvTokenEndpoint = configs.getProperty("TRG.TOKEN.ENDPOINT");
        String tgtEncPubEndpoint = configs.getProperty("TRG.PUB.ENDPOINT");
        String tgtDevportalEndpoint = configs.getProperty("TRG.DEVPORTAL.ENDPOINT");

        System.out.println("Tgt Env Name: "+ tgtEnvName);
        System.out.println("Tgt Env Clt reg: "+ tgtEncClientRegistartion);
        System.out.println("Tgt  Admin: "+ tgtEnvAdminEndpoint);
        System.out.println("Tgt Token: "+ tgtEnvTokenEndpoint);
        System.out.println("Tgt  Publisher: "+ tgtEncPubEndpoint);
        System.out.println("Tgt Devportal: "+ tgtDevportalEndpoint);

        LOGGER.info("***** Adding Src Environemnt *****");
        addSrcEnvironemnt(srcEnvName,srcEncClientRegistartion,srcEnvAdminEndpoint,srcEnvTokenEndpoint,
                srcEncPubEndpoint,srcDevportalEndpoint);

    }

    public static void addSrcEnvironemnt(String env, String registration, String admin, String token, String publisher
            , String devportal ) {
        String addSrcEnvcommand = String.format(
                "apictl add env %s --registration %s --admin %s --token %s  --publisher %s --devportal %s ",
                env,
                registration,
                admin,
                token,
                publisher,
                devportal
        );
        System.out.println("Running command: " + addSrcEnvcommand);
        LOGGER.info("Executing: " + addSrcEnvcommand);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", addSrcEnvcommand);
            processBuilder.inheritIO(); // This makes sure the output/error is displayed in the console
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
                LOGGER.info("Command executed successfully.");
            } else {
                System.err.println("Command execution failed with exit code: " + exitCode);
                LOGGER.severe("Command execution failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            LOGGER.severe("Exception while executing command: " + e.getMessage());
        }
    }
}
