package net.mirolls.bettertips.death;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static net.mirolls.bettertips.BetterTips.LOGGER;

public class ResourceReader {

    public static String readResourceAsString(String resourcePath) {
        // 获取当前线程的类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 使用try-with-resources确保输入流正确关闭
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            // 使用BufferedReader和InputStreamReader读取输入流
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                // 将输入流内容转换为字符串
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return null;
        }
    }
}
