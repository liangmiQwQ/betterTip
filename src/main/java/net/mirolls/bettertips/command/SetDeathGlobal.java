package net.mirolls.bettertips.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetDeathGlobal {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setDeathGlobal")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument("deathID", IntegerArgumentType.integer())
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.literal("set模式"), false);
                                    return 1;
                                })))
                .then(literal("query")
                        .executes(context -> {
                            return 1;
                        }))));
    }
}
