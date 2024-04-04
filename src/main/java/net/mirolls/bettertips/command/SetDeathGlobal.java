package net.mirolls.bettertips.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.mirolls.bettertips.death.DeathConfig;
import net.mirolls.bettertips.death.DeathConfigYaml;
import net.mirolls.bettertips.death.DeathMessage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.mirolls.bettertips.BetterTips.LOGGER;

public class SetDeathGlobal {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setDeathGlobal")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("message")
                        .then(argument("deathID", StringArgumentType.string()) // 理论上来说，ID是String类型的
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(SetDeathGlobal::setMessageHandle))))
                .then(literal("color")
                        .then(argument("deathID", StringArgumentType.string())
                                .then(argument("color", StringArgumentType.greedyString())
                                        .executes(SetDeathGlobal::setColorHandle))))));
    }

    // 创建这个方法的目的是缩进太难受了要爆炸了
    private static int setMessageHandle(CommandContext<ServerCommandSource> context) {
        String deathID = StringArgumentType.getString(context, "deathID");
        String message = StringArgumentType.getString(context, "message");

        // 先进行一个校验，防止整个配置文件崩坏
        if (isValidDeathYamlKey(deathID)) {
            try {
                final String CONFIG_FILE_PATH = "bettertips/death.config.yaml";
                ObjectMapper mapper = DeathConfig.getConfigMapper();
                File configYaml = new File(CONFIG_FILE_PATH);
                String comments = readComments(CONFIG_FILE_PATH);
                DeathConfigYaml config = mapper.readValue(configYaml, DeathConfigYaml.class);

                config = updateGlobalMessage(config, deathID, message);

                mapper.writeValue(configYaml, config);
                // 写入注释
                writeComments(comments + "\n", CONFIG_FILE_PATH);

                context.getSource().sendFeedback(() -> Text.literal("写入" + deathID + "消息成功"), false);
            } catch (IOException e) {
                context.getSource().sendFeedback(() -> Text.translatable("command.failed"), false);
                LOGGER.error("[BetterTips]: Cannot get the death config");
                return 0;
            }
            return 1;
        } else {
            context.getSource().sendFeedback(() -> Text.translatable("command.unknown.argument"), false);
            return 0;
        }
    }

    private static int setColorHandle(CommandContext<ServerCommandSource> context) {
        String deathID = StringArgumentType.getString(context, "deathID");
        String color = StringArgumentType.getString(context, "color");

        // 先进行一个校验，防止整个配置文件崩坏
        if (isValidDeathYamlKey(deathID)) {
            try {
                final String CONFIG_FILE_PATH = "bettertips/death.config.yaml";
                ObjectMapper mapper = DeathConfig.getConfigMapper();
                File configYaml = new File(CONFIG_FILE_PATH);
                String comments = readComments(CONFIG_FILE_PATH);
                DeathConfigYaml config = mapper.readValue(configYaml, DeathConfigYaml.class);

                config = updateGlobalColor(config, deathID, color);

                mapper.writeValue(configYaml, config);
                // 写入注释
                writeComments(comments + "\n", CONFIG_FILE_PATH);

                context.getSource().sendFeedback(() -> Text.literal("写入" + deathID + "颜色成功"), false);
            } catch (IOException e) {
                context.getSource().sendFeedback(() -> Text.translatable("command.failed"), false);
                LOGGER.error("[BetterTips]: Cannot get the death config");
                return 0;
            }
            return 1;
        } else {
            context.getSource().sendFeedback(() -> Text.translatable("command.unknown.argument"), false);
            return 0;
        }
    }


    private static boolean isValidDeathYamlKey(String key) {
        String regex = "^(death\\.attack\\.|death\\.fell\\.)[._a-zA-Z]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }

    private static String readComments(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        StringBuilder commentsBuilder = new StringBuilder();

        int commentLines = determineCommentLines(inputFile);

        // 逐行读取输入文件内容，提取注释
        for (int i = 0; i < commentLines; i++) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                break; // 如果读取到文件末尾或空行则停止
            }
            commentsBuilder.append(line).append("\n");
        }

        reader.close();
        return commentsBuilder.toString();
    }

    private static int determineCommentLines(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        int commentLines = 0;

        // 根据自定义条件确定需要提取的注释行数
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("#")) {
                commentLines++;
            } else {
                break; // 如果读取到非注释行，则停止计数
            }
        }

        reader.close();
        return commentLines;
    }


    // 写入注释到输出文件的开头
    private static void writeComments(String comments, String outputFile) throws IOException {
        // 读取原有的内容
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append("\n");
        }
        reader.close();

        // 将注释和原有内容写入文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(comments);
        writer.write(contentBuilder.toString());
        writer.close();
    }

    public static DeathConfigYaml updateGlobalMessage(DeathConfigYaml config, String key, String newMessage) {
        DeathConfigYaml updatedConfig = new DeathConfigYaml();
        updatedConfig.setGlobal(new HashMap<>(config.getGlobal())); // 复制原始 global map
        updatedConfig.setPlayer(new HashMap<>(config.getPlayer())); // 复制原始 player map

        Map<String, DeathMessage> global = updatedConfig.getGlobal();

        DeathMessage deathMessage;
        if (global.containsKey(key)) {
            deathMessage = global.get(key);
        } else {
            // 如果 key 不存在，创建一个新的 DeathMessage 对象并添加到 global 中
            deathMessage = new DeathMessage();
        }
        deathMessage.setMessage(newMessage);
        global.put(key, deathMessage);

        return updatedConfig;
    }

    public static DeathConfigYaml updateGlobalColor(DeathConfigYaml config, String key, String newColor) {
        DeathConfigYaml updatedConfig = new DeathConfigYaml();
        updatedConfig.setGlobal(new HashMap<>(config.getGlobal())); // 复制原始 global map
        updatedConfig.setPlayer(new HashMap<>(config.getPlayer())); // 复制原始 player map

        Map<String, DeathMessage> global = updatedConfig.getGlobal();

        DeathMessage deathMessage;
        if (global.containsKey(key)) {
            deathMessage = global.get(key);
        } else {
            // 如果 key 不存在，创建一个新的 DeathMessage 对象并添加到 global 中
            deathMessage = new DeathMessage();
        }
        deathMessage.setColor(newColor);
        global.put(key, deathMessage);

        return updatedConfig;
    }
}
