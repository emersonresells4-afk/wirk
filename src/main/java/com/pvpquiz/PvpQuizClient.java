package com.pvpquiz;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PvpQuizClient implements ClientModInitializer {
    private static boolean pendingQuiz = false;
    private static boolean pendingManager = false;
    private static boolean logChat = false;
    private static long lastTrigger = 0;

    private static void trigger() {
        long now = System.currentTimeMillis();
        if (now - lastTrigger < 8000) return; // ignore duplicate messages for the same fight
        lastTrigger = now;
        pendingQuiz = true;
    }

    private static boolean onTargetServer(Minecraft mc) {
        if (mc.getCurrentServer() == null) return false;
        return mc.getCurrentServer().ip.toLowerCase().contains("mcpvp.club");
    }

    private static void say(FabricClientCommandSource src, String s) {
        src.sendFeedback(Component.literal("[PvPQuiz] " + s));
    }

    @Override
    public void onInitializeClient() {
        QuizStore.load();

        // Watch chat for "you finished a fight" messages
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            String text = message.getString();
            if (text.startsWith("[PvPQuiz")) return;

            if (logChat) {
                mc.player.displayClientMessage(Component.literal("[PvPQuiz log] " + text), false);
            }
            if (!onTargetServer(mc)) return;

            String lower = text.toLowerCase();
            String me = mc.player.getName().getString().toLowerCase();
            if (!lower.contains(me)) return;
            for (String k : QuizStore.keywords()) {
                if (lower.contains(k.toLowerCase())) {
                    trigger();
                    return;
                }
            }
        });

        // Open screens once no other screen (chat, death screen...) is in the way
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.screen != null) return;
            if (pendingManager) {
                pendingManager = false;
                mc.setScreen(new QuestionManagerScreen());
            } else if (pendingQuiz) {
                pendingQuiz = false;
                QuizStore.QA qa = QuizStore.random();
                if (qa != null) mc.setScreen(new QuizScreen(qa));
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("pvpquiz")
                .executes(ctx -> { pendingManager = true; return 1; })
                .then(literal("test").executes(ctx -> { pendingQuiz = true; return 1; }))
                .then(literal("log").executes(ctx -> {
                    logChat = !logChat;
                    say(ctx.getSource(), "Chat logging " + (logChat ? "ON" : "OFF"));
                    return 1;
                }))
                .then(literal("keyword")
                    .then(literal("list").executes(ctx -> {
                        say(ctx.getSource(), "Trigger words: " + String.join(", ", QuizStore.keywords()));
                        return 1;
                    }))
                    .then(literal("add").then(argument("word", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String w = StringArgumentType.getString(ctx, "word").trim();
                            QuizStore.addKeyword(w);
                            say(ctx.getSource(), "Added trigger word: " + w);
                            return 1;
                        })))
                    .then(literal("remove").then(argument("word", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String w = StringArgumentType.getString(ctx, "word").trim();
                            say(ctx.getSource(), QuizStore.removeKeyword(w) ? "Removed: " + w : "Not found: " + w);
                            return 1;
                        }))))));
    }
}
