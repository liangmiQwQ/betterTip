package net.mirolls.bettertips.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.mirolls.bettertips.command.file.Comment;
import net.mirolls.bettertips.command.file.Verifier;
import net.mirolls.bettertips.death.DeathConfig;
import net.mirolls.bettertips.death.DeathConfigYaml;
import net.mirolls.bettertips.death.DeathMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        if (Verifier.isValidDeathYamlKey(deathID)) {
            try {
                final String CONFIG_FILE_PATH = "bettertips/death.config.yaml";
                ObjectMapper mapper = DeathConfig.getConfigMapper();
                File configYaml = new File(CONFIG_FILE_PATH);
                String comments = Comment.readComments(CONFIG_FILE_PATH);
                DeathConfigYaml config = mapper.readValue(configYaml, DeathConfigYaml.class);

                config = updateGlobalMessage(config, deathID, message);

                mapper.writeValue(configYaml, config);
                // 写入注释
                Comment.writeComments(comments + System.lineSeparator(), CONFIG_FILE_PATH);

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

        if (Verifier.isValidDeathYamlKey(deathID)) {
            try {
                final String CONFIG_FILE_PATH = "bettertips/death.config.yaml";
                ObjectMapper mapper = DeathConfig.getConfigMapper();
                File configYaml = new File(CONFIG_FILE_PATH);
                String comments = Comment.readComments(CONFIG_FILE_PATH);
                DeathConfigYaml config = mapper.readValue(configYaml, DeathConfigYaml.class);

                config = updateGlobalColor(config, deathID, color);

                mapper.writeValue(configYaml, config);
                // 写入注释
                Comment.writeComments(comments + System.lineSeparator(), CONFIG_FILE_PATH);

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
