package net.mirolls.bettertips.death;

import java.util.Map;

public class DeathConfigYaml {
    private Map<String, DeathMessage> global;
    
    public Map<String, DeathMessage> getGlobal() {
        return global;
    }

    public void setGlobal(Map<String, DeathMessage> global) {
        this.global = global;
    }

}

