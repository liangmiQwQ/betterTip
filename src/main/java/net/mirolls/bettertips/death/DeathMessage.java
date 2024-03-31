package net.mirolls.bettertips.death;

public class DeathMessage {
    private String color;
    private String message;

    String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
