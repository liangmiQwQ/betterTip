package net.mirolls.bettertips.death;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.mirolls.bettertips.BetterTips.LOGGER;


public class DeathConfig {
    private static void createConfig() {
        String defaultConfigContent = ResourceReader.readResourceAsString("death.config.yaml");
        File gameDir = new File(".");
        File bettertipsFolder = new File(gameDir, "bettertips");

        if (!bettertipsFolder.exists() && !bettertipsFolder.mkdirs()) {
            LOGGER.error("[BetterTips|ERROR] Can't create folder to save the config file.");
            return; // 如果无法创建目录，则直接返回
        }

        File configFile = new File(bettertipsFolder, "death.config.yaml");
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile() && defaultConfigContent != null) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                        writer.write(defaultConfigContent);
                    } catch (IOException e) {
                        LOGGER.error("[BetterTips|ERROR] Failed to write to config file.", e);
                    }
                } else {
                    if (defaultConfigContent == null) {
                        LOGGER.error("[BetterTips|ERROR] Internal error, cannot read default file");
                    } else {
                        LOGGER.error("[BetterTips|ERROR] Can't create file to save the config.");
                    }
                }
            } catch (IOException e) {
                LOGGER.error("[BetterTips|ERROR] An error occurred while creating the config file.", e);
            }
        }
    }


    private static boolean isConfigFileHere() {
        File gameDir = new File(".");
        File bettertipsFolder = new File(gameDir, "bettertips");
        if (!bettertipsFolder.exists()) {
            // 没有目录
            return false;
        }
        File configFile = new File(bettertipsFolder, "death.config.yaml");
        // 没有文件
        return configFile.exists(); // 有文件返回true, 没文件返回false
    }

    public static void initConfig() {
        if (!isConfigFileHere()) {
            // 如果没有则创建，反正保证的就是一个有文件
            createConfig();
        }
    }

    public static String getMsg(String playerName, String deathMsgID) {
        initConfig();
        return "";

    }

    public static String getColor(String playerName, String deathMsgID) {
        initConfig();
        return "";
    }

    private static DeathConfigYaml getConfig() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream(Paths.get("bettertips/death.config.yaml"));
        return yaml.load(inputStream);
    }
}
