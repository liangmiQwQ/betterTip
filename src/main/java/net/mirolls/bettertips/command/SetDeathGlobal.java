package net.mirolls.bettertips.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.mirolls.bettertips.death.DeathConfig;
import net.mirolls.bettertips.death.DeathConfigYaml;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.mirolls.bettertips.BetterTips.LOGGER;

public class SetDeathGlobal {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setDeathGlobal")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument("deathID", StringArgumentType.string()) // 理论上来说，ID是String类型的
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(SetDeathGlobal::commandSetHandle)))) // 为了缩进美观
                .then(literal("query")
                        .executes(context -> {
                            return 1;
                        }))));
    }

    // 创建这个方法的目的是缩进太难受了要爆炸了
    private static int commandSetHandle(CommandContext<ServerCommandSource> context) {
        String deathID = StringArgumentType.getString(context, "deathID");
        String message = StringArgumentType.getString(context, "message");

        // 先进行一个校验，防止整个配置文件崩坏
        if (isValidDeathYamlKey(deathID)) {
            context.getSource().sendFeedback(() -> Text.literal("set模式"), false);
            try {
                ObjectMapper mapper = DeathConfig.getConfigMapper();
                File configYaml = new File("bettertips/death.config.yaml");
                mapper.readValue(configYaml, DeathConfigYaml.class);

            } catch (IOException e) {
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
}
