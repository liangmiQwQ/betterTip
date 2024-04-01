package net.mirolls.bettertips.death;

public class DeathMessage {
    private String color;
    private String message;

    // IDEA他给我生成的getter和setter少了public是个什么情况啊啊啊
    // To Fix Caused by: Cannot create property=message for JavaBean=net.mirolls.bettertips.death.DeathMessage@2b676779
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DeathMessage{" +
                "color='" + color + '\'' +
                ", message='" + message + '\'' +
                '}';
        // [20:27:23] [Server thread/INFO]: {color=colorful, message=有一个叫做${departed}的蠢材被岩浆烫死了}
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
