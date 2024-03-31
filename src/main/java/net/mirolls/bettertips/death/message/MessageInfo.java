package net.mirolls.bettertips.death.message;

public class MessageInfo {
    private String deathID; // death.attack.xxx death.fell.xxx
    private String deceasedName; // deceased display name(获取方法 new Text().getString())
    private String killerName; // 杀手
    private String killItem; // 凶器

    public MessageInfo(String deathID, String deceasedName, String killerName, String killItem) {
        this.deathID = deathID;
        this.deceasedName = deceasedName;
        this.killerName = killerName;
        this.killItem = killItem;
    }

    public MessageInfo() {
    }

    public String getDeathID() {
        return deathID;
    }

    public void setDeathID(String deathID) {
        this.deathID = deathID;
    }

    public String getDeceasedName() {
        return deceasedName;
    }

    public void setDeceasedName(String deceasedName) {
        this.deceasedName = deceasedName;
    }

    public String getKillerName() {
        return killerName;
    }

    public void setKillerName(String killerName) {
        this.killerName = killerName;
    }

    public String getKillItem() {
        return killItem;
    }

    public void setKillItem(String killItem) {
        this.killItem = killItem;
    }
}
