package net.mirolls.bettertips.command.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verifier {
    public static boolean isValidDeathYamlKey(String key) {
        String regex = "^(death\\.attack\\.|death\\.fell\\.)[._a-zA-Z]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }
}
