package net.mirolls.bettertips.death;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static net.mirolls.bettertips.BetterTips.LOGGER;


public class DeathConfig {
    private static void createConfig() {
        String defaultConfigContent;
        if (System.lineSeparator().equals("\r\n")) {
            defaultConfigContent = ResourceReader.readResourceAsString("death.config.yaml.windows");
        } else if (System.lineSeparator().equals("\n")) {
            defaultConfigContent = ResourceReader.readResourceAsString("death.config.yaml");
        } else {
            LOGGER.error("[BetterTips]The system line separator comes from Mars");
            return;
        }

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
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))) {
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
        String normalMessage = "%[Normal]";

        initConfig();
        DeathConfigYaml config = getConfig();
        if (config.getPlayer().get(playerName) == null) {
            // 如果这位玩家是null，没有进行配置
            // 去弄全局
            if (config.getGlobal().get(deathMsgID) != null) {
                if (config.getGlobal().get(deathMsgID).getMessage() == null) {
                    // 那可能是空项，或者只配置了颜色
                    return normalMessage; // 不实用Text.translate的原因是防止出现%1$s这种东西
                } else {
                    // 腐竹非常滴勤劳，配置了
                    return config.getGlobal().get(deathMsgID).getMessage();
                }
            } else {
                return normalMessage; //这才是真的默认消息
            }

        } else {

            DeathMessage playerConfig = config.getPlayer().get(playerName).get(deathMsgID);
            // 玩家进行了配置，进行第二层判断，是否有配置该key
            if (playerConfig == null) {
                // 玩家没有进行该死亡信息的配置
                // 看看全局的

                if (config.getGlobal().get(deathMsgID) != null) { // 这个项直接就不存在了
                    if (config.getGlobal().get(deathMsgID).getMessage() == null) {
                        // 那可能是空项，或者只配置了颜色
                        return normalMessage; //这才是真的默认消息
                    } else {
                        // 腐竹非常滴勤劳，配置了
                        return config.getGlobal().get(deathMsgID).getMessage();
                    }
                } else {
                    return normalMessage; //这才是真的默认消息
                }
            } else {
                // fix: Caused by: java.lang.NullPointerException: Cannot invoke "String.replace(java.lang.CharSequence, java.lang.CharSequence)" because "template" is null
                // 增加两层判断，防止出现玩家只配置颜色没配置消息之类的情况
                if (playerConfig.getMessage() == null) {
                    // 默认我爱你
                    if (config.getGlobal().get(deathMsgID) != null) { // 这个项直接就不存在了
                        if (config.getGlobal().get(deathMsgID).getMessage() == null) {
                            // 那可能是空项，或者只配置了颜色
                            return normalMessage; //这才是真的默认消息
                        } else {
                            // 腐竹非常滴勤劳，配置了
                            return config.getGlobal().get(deathMsgID).getMessage();
                        }
                    } else {
                        return normalMessage; //这才是真的默认消息
                    }
                } else {
                    return config.getPlayer().get(playerName).get(deathMsgID).getMessage();
                }
            }
        }

    }

    public static String getColor(String playerName, String deathMsgID) throws IOException {
        initConfig();
        DeathConfigYaml config = getConfig();
        if (config.getPlayer().get(playerName) == null) {
            // 如果这位玩家是null，没有进行配置
            if (config.getGlobal().get(deathMsgID) != null) {
                if (config.getGlobal().get(deathMsgID).getColor() == null) {
                    // 那可能是空项，或者只配置了颜色
                    return "";
                } else {
                    // 腐竹非常滴勤劳，配置了
                    return config.getGlobal().get(deathMsgID).getColor();
                }
            } else {
                return "";
            }
        } else {
            // 玩家进行了配置，进行第二层判断，是否有配置该key
            DeathMessage playerConfig = config.getPlayer().get(playerName).get(deathMsgID);
            if (playerConfig == null) {
                // 玩家没有进行该死亡信息的配置
                // 转头投靠global
                if (config.getGlobal().get(deathMsgID) != null) {
                    if (config.getGlobal().get(deathMsgID).getColor() == null) {
                        // 那可能是空项，或者只配置了颜色
                        return "";
                    } else {
                        // 腐竹非常滴勤劳，配置了
                        return config.getGlobal().get(deathMsgID).getColor();
                    }
                } else {
                    return "";
                }
            } else {
                // fix: Caused by: java.lang.NullPointerException: Cannot invoke "String.replace(java.lang.CharSequence, java.lang.CharSequence)" because "template" is null
                // 增加两层判断，防止出现玩家只配置消息没配置颜色之类的情况
                // 虽然颜色返回null也不是不可以的啦
                if (playerConfig.getColor() == null) {
                    // 默认我爱你
                    if (config.getGlobal().get(deathMsgID) != null) { // 这个项直接就不存在了
                        if (config.getGlobal().get(deathMsgID).getColor() == null) {
                            // 那可能是空项，或者只配置了消息
                            return ""; //这才是真的默认颜色
                        } else {
                            // 腐竹非常滴勤劳，配置了
                            return config.getGlobal().get(deathMsgID).getColor();
                        }
                    } else {
                        return "";
                    }
                } else {
                    return config.getPlayer().get(playerName).get(deathMsgID).getColor();
                }
            }
        }
    }

    private static DeathConfigYaml getConfig() throws IOException {
        // 不使用SnakeYaml的原因：https://bitbucket.org/snakeyaml/snakeyaml/issues/1078/
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules(); // 查找库一类的玩意
        File configYaml = new File("bettertips/death.config.yaml");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configYaml), StandardCharsets.UTF_8);

        // InputStream inputStream = Files.newInputStream(Paths.get("bettertips/death.config.yaml"));
        // To fix Caused by: java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class net.mirolls.bettertips.death.DeathConfigYaml (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; net.mirolls.bettertips.death.DeathConfigYaml is in unnamed module of loader
        // return mapper.readValue(configYaml, DeathConfigYaml.class);
        return mapper.readValue(inputStreamReader, DeathConfigYaml.class);
    }

    public static ObjectMapper getConfigMapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        return mapper;
    }
}
