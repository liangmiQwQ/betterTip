package net.mirolls.bettertips.color;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class MinecraftColor {
    private static final Random random = new Random();

    public static MutableText getMinecraftTextWithColor(String text, String color) {
        MutableText basicText = Text.literal(text);
        if (!Objects.equals(color, "colorful") && color != null && !color.startsWith("#")) { // 这个情况是如果不是colorful和start不是#的情况
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
        } else if (color.startsWith("#")) {
            TextColor colorHex = TextColor.fromRgb(hexToRgb(color));
            // 如果颜色有效，则应用该颜色
            basicText.setStyle(Style.EMPTY.withColor(colorHex));
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
}
