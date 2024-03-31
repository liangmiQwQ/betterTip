package net.mirolls.bettertips.death;

import net.minecraft.text.Text;
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

    public static String getMsg(String playerName, String deathMsgID) throws IOException {
        initConfig(); // 初始化配置，这里假设这个方法加载或者刷新了配置
        DeathConfigYaml config = getConfig(); // 获取配置实例，这里假设这个方法返回配置的当前状态
        LOGGER.info(config.toString());//进行排查

        // 首先检查是否有针对特定玩家的配置
        if (config.getPlayer().get(playerName) == null) {
            // 如果没有特定玩家的配置，检查全局配置
            if (config.getGlobal().get(deathMsgID) == null || config.getGlobal().get(deathMsgID).getMessage() == null) {
                // 如果全局配置中也没有这个消息ID，或者消息内容为null，返回默认消息
                return Text.translatable(deathMsgID).getString();
            } else {
                // 如果全局配置中有这个消息ID，并且消息内容不为null，返回该消息内容
                return config.getGlobal().get(deathMsgID).getMessage();
            }
        } else {
            // 如果有针对特定玩家的配置，检查这个玩家是否有针对该死亡消息ID的配置
            if (config.getPlayer().get(playerName).get(deathMsgID) == null || config.getPlayer().get(playerName).get(deathMsgID).getMessage() == null) {
                // 如果玩家没有对这个死亡消息ID进行配置，或者配置的消息内容为null，返回默认消息
                return Text.translatable(deathMsgID).getString(); //这才是真的默认消息
            } else {
                // 如果玩家对这个死亡消息ID进行了配置，并且消息内容不为null，返回该消息内容
                return config.getPlayer().get(playerName).get(deathMsgID).getMessage();
            }
        }
    }

    public static String getColor(String playerName, String deathMsgID) throws IOException {
        initConfig();
        DeathConfigYaml config = getConfig();
        if (config.getPlayer().get(playerName) == null) {
            // 如果这位玩家是null，没有进行配置
            if (config.getGlobal().get(deathMsgID).getColor() == null) {
                // 服主很懒，没有全局配置
                return ""; // 返回默认颜色，无色
            } else {
                // 腐竹非常滴勤劳，配置了
                return config.getGlobal().get(deathMsgID).getColor();
            }
        } else {
            // 玩家进行了配置，进行第二层判断，是否有配置该key
            if (config.getPlayer().get(playerName).get(deathMsgID) == null) {
                // 玩家没有进行该死亡信息的配置
                return ""; // 我爱默认
            } else {
                // 玩家对此信息进行了配置
                return config.getPlayer().get(playerName).get(deathMsgID).getColor();
            }
        }
    }

    private static DeathConfigYaml getConfig() throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream(Paths.get("bettertips/death.config.yaml"));
//        return yaml.load(inputStream);
        // To fix Caused by: java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class net.mirolls.bettertips.death.DeathConfigYaml (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; net.mirolls.bettertips.death.DeathConfigYaml is in unnamed module of loader
        return yaml.loadAs(inputStream, DeathConfigYaml.class);
    }
}
