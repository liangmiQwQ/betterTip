package net.mirolls.bettertips.color;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static net.mirolls.bettertips.BetterTips.LOGGER;

public class MinecraftColor {
    private static final Random random = new Random();

    public static MutableText getMinecraftTextWithColor(String text, String color) {
        MutableText basicText = Text.literal(text);
        if (color != null) { // 如果你是null，不好意思我们没有共同语言
            if (!color.endsWith("colorful") && !color.startsWith("#")) { // 这个情况是如果不是colorful和start不是#的情况
                // 尝试获取对应的Formatting枚举，如果颜色无效，则colorFormatting为null
                Formatting colorFormatting = Formatting.byName(color.toUpperCase());

                // 如果颜色有效，则应用该颜色
                if (colorFormatting != null) {
                    basicText.setStyle(Style.EMPTY.withColor(colorFormatting));
                }
                // 如果颜色无效，可以在这里应用默认颜色或者不做任何操作
                // 例如：basicText.setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            } else if (Objects.equals(color, "colorful")) {
                basicText = createColorfulText(text);
            } else if (Objects.equals(color, "light_colorful")) {
                // 和createColorfulText差不多的
                MutableText resultText = Text.empty(); // 创建一个空的MutableText作为起始点
                for (char c : text.toCharArray()) {
                    LOGGER.info(generateBrightColor());
                    TextColor colorHex = TextColor.fromRgb(hexToRgb(generateBrightColor())); // 创建一个颜色
                    // 为每个字符创建一个MutableText，并设置随机颜色
                    MutableText charText = Text.literal(String.valueOf(c))
                            .setStyle(Style.EMPTY.withColor(colorHex)); // 和加载hex颜色的差不多
                    resultText.append(charText); // 将字符文本附加到结果文本上
                }
            } else if (Objects.equals(color, "dark_colorful")) {
                // 和createColorfulText差不多的
                MutableText resultText = Text.empty(); // 创建一个空的MutableText作为起始点
                for (char c : text.toCharArray()) {
                    TextColor colorHex = TextColor.fromRgb(hexToRgb(generateDarkColor())); // 创建一个颜色
                    // 为每个字符创建一个MutableText，并设置随机颜色
                    MutableText charText = Text.literal(String.valueOf(c))
                            .setStyle(Style.EMPTY.withColor(colorHex)); // 和加载hex颜色的差不多
                    resultText.append(charText); // 将字符文本附加到结果文本上
                }
            } else if (color.startsWith("#")) {
                TextColor colorHex = TextColor.fromRgb(hexToRgb(color));
                // 如果颜色有效，则应用该颜色
                basicText.setStyle(Style.EMPTY.withColor(colorHex));
            }
            return basicText;
        }
        return basicText;
    }

    public static MutableText createColorfulText(String message) {
        MutableText resultText = Text.empty(); // 创建一个空的MutableText作为起始点

        for (char c : message.toCharArray()) {
            // 为每个字符创建一个MutableText，并设置随机颜色
            MutableText charText = Text.literal(String.valueOf(c))
                    .setStyle(Style.EMPTY.withColor(Formatting.byColorIndex(random.nextInt(16))));
            resultText.append(charText); // 将字符文本附加到结果文本上
        }

        return resultText;
    }

    public static int hexToRgb(String hexColor) {
        Color color = Color.decode(hexColor);
        return color.getRGB();
    }

    public static String generateBrightColor() {
        Random random = new Random();
        int r, g, b;

        // 生成RGB颜色值，确保亮度达到一定阈值
        do {
            r = random.nextInt(256);
            g = random.nextInt(256);
            b = random.nextInt(256);
        } while (calculateBrightness(r, g, b) < 180);

        // 转换为hex格式
        return String.format("#%02x%02x%02x", r, g, b);
    }

    // 生成随机暗颜色
    public static String generateDarkColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        // 转换为hex格式
        return String.format("#%02x%02x%02x", r, g, b);
    }

    // 计算颜色的亮度
    private static double calculateBrightness(int r, int g, int b) {
        return Math.sqrt(r * r * .299 + g * g * .587 + b * b * .114);
    }
}
