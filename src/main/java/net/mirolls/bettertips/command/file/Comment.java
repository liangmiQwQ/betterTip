package net.mirolls.bettertips.command.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Comment {
    private static int determineCommentLines(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
        int commentLines = 0;

        // 根据自定义条件确定需要提取的注释行数
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("#")) {
                commentLines++;
            } else {
                break; // 如果读取到非注释行，则停止计数
            }
        }

        reader.close();
        return commentLines;
    }

    public static String readComments(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        StringBuilder commentsBuilder = new StringBuilder();

        int commentLines = determineCommentLines(inputFile);

        // 逐行读取输入文件内容，提取注释
        for (int i = 0; i < commentLines; i++) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                break; // 如果读取到文件末尾或空行则停止
            }
            commentsBuilder.append(line).append(System.lineSeparator());
        }

        reader.close();
        return commentsBuilder.toString();
    }

    public static void writeComments(String comments, String outputFile) throws IOException {
        // 读取原有的内容
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append(System.lineSeparator());
        }
        reader.close();

        // 将注释和原有内容写入文件
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
        writer.write(comments);
        writer.write(contentBuilder.toString());
        writer.close();
    }
}
