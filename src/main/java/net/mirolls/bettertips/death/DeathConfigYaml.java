package net.mirolls.bettertips.death;

import java.util.Map;

public class DeathConfigYaml {
    private Map<String, DeathMessage> global;
    private Map<String, Map<String, DeathMessage>> player;


    public Map<String, DeathMessage> getGlobal() {
        return global;
    }

    public void setGlobal(Map<String, DeathMessage> global) {
        this.global = global;
    }

    public Map<String, Map<String, DeathMessage>> getPlayer() {
        return player;
    }

    public void setPlayer(Map<String, Map<String, DeathMessage>> player) {
        this.player = player;
    }
}

