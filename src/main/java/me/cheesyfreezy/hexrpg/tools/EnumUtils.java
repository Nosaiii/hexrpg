package me.cheesyfreezy.hexrpg.tools;

public class EnumUtils {
    public static <T extends Enum<T>> T fromName(Class<T> enumType, String name) {
        name = name.toUpperCase();
        name = name.replace(" ", "_");

        return Enum.valueOf(enumType, name);
    }
}