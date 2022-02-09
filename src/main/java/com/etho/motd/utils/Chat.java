package com.etho.motd.utils;

import com.etho.motd.Motd;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {
    private static LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors().build();

    public static TextComponent color(String s) {
        s = format(s);
        return serializer.deserialize(s);
    }
    public static LegacyComponentSerializer getSerializer() {
        return serializer;
    }

    public static String format(String s) {
        FileConfiguration cfg = Config.DEFAULT;
        return s.replace("%server-prefix%", cfg.getString("general.server-name"))
                .replace("%all-online%", Integer.toString(Motd.server().getPlayerCount()));
    }

    private static final char ANSI_ESC_CHAR = '\u001B';
    private static final String RGB_STRING = ANSI_ESC_CHAR + "[38;2;%d;%d;%dm";
    private static final Pattern RBG_TRANSLATE = Pattern.compile("&#([A-F0-9]){6}", Pattern.CASE_INSENSITIVE);
    public static String convertConsole(String input) {
        Matcher matcher = RBG_TRANSLATE.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String s = matcher.group().replace("&", "").replace('x', '#');
            Color color = Color.decode(s);
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            String replacement = String.format(RGB_STRING, red, green, blue);
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString().replace("&l", "")
                .replace("&o", "")
                .replace("&n", "")
                .replace("&m", "")
                + "\033[0m";
    }

    private static final Pattern pattern = Pattern.compile("&#[a-f0-9]{6}|&[a-f0-9k-o]|&r", Pattern.CASE_INSENSITIVE);
    public static String strip(String s) {
        Matcher match = pattern.matcher(s);
        while (match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replace(color, "");
            match = pattern.matcher(s);
        }
        return s;
    }

    public static String centerText(String message1, int px) {
        if (message1 != null && !message1.equals("")) {
            String message = strip(message1);
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;
            char[] var5 = message.toCharArray();
            int toCompensate = var5.length;
            int spaceLength;
            for(spaceLength = 0; spaceLength < toCompensate; ++spaceLength) {
                char c = var5[spaceLength];
                if (c == 167) {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    if (c != 'l' && c != 'L') {
                        isBold = false;
                    } else {
                        isBold = true;
                    }
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                    ++messagePxSize;
                }
            }

            int halvedMessageSize = messagePxSize / 2;
            toCompensate = px - halvedMessageSize;
            spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;

            StringBuilder sb;
            for(sb = new StringBuilder(); compensated < toCompensate; compensated += spaceLength) {
                sb.append(" ");
            }
            return sb + message1;
        } else {
            return "";
        }
    }
}
