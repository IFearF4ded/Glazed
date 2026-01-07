package com.nnpg.glazed.modules;

import com.nnpg.glazed.GlazedAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class TPASpammer extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Player name
    private final Setting<String> targetPlayer = sgGeneral.add(new StringSetting.Builder()
        .name("target-player")
        .description("Player to send TPA requests to.")
        .defaultValue("")
        .build()
    );

    // Toggle between /tpa and /tpahere
    private final Setting<Boolean> tpaHere = sgGeneral.add(new BoolSetting.Builder()
        .name("tpa-here")
        .description("Use /tpahere instead of /tpa.")
        .defaultValue(false)
        .build()
    );

    // Delay slider (anti-spam safe)
    private final Setting<Integer> delaySeconds = sgGeneral.add(new IntSetting.Builder()
        .name("delay-seconds")
        .description("Delay between TPA messages (seconds).")
        .defaultValue(3)
        .min(3)              // SAFE minimum
        .sliderMin(3)
        .sliderMax(30)
        .build()
    );

    private int timer;

    public TPASpammer() {
        super(GlazedAddon.CATEGORY, "TPA-Spammer", "Automatically sends /tpa or /tpahere.");
    }

    @Override
    public void onActivate() {
        timer = 0;

        if (targetPlayer.get().isEmpty()) {
            error("No target player set.");
            toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (targetPlayer.get().isEmpty()) return;

        timer++;

        if (timer >= delaySeconds.get() * 20) {
            sendTPA();
            timer = 0;
        }
    }

    private void sendTPA() {
        String command = tpaHere.get() ? "/tpahere " : "/tpa ";
        ChatUtils.sendPlayerMsg(command + targetPlayer.get());

        info("Sent %s to %s",
            tpaHere.get() ? "TPAHERE" : "TPA",
            targetPlayer.get()
        );
    }
}
