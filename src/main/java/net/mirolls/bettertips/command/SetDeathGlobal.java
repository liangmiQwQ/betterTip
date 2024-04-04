package net.mirolls.bettertips.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetDeathGlobal {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setDeathGlobal")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument("deathID", StringArgumentType.string()) // 理论上来说，ID是String类型的
                                .executes(context -> {
                                    String deathID = StringArgumentType.getString(context, "deathID");

                                    // 先进行一个校验，防止整个配置文件崩坏
                                    if (isValidDeathYamlKey(deathID)) {
                                        context.getSource().sendFeedback(() -> Text.literal("set模式"), false);
                                        return 1;
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.translatable("command.unknown.argument"), false);
                                        return 0;
                                    }
                                })))
                .then(literal("query")
                        .executes(context -> {
                            return 1;
                        }))));
    }

    private static boolean isValidDeathYamlKey(String key) {
        String regex = "^(death\\.attack\\.|death\\.fell\\.)[._a-zA-Z]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }
}
