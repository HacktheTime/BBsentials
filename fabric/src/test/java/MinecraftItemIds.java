import de.hype.bbsentials.client.common.SystemUtils;
import net.minecraft.item.Items;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MinecraftItemIds {
    static Set<String> set;

    public static void main(String[] args) {
        set = new HashSet<>();
        for (Field declaredField : Items.class.getDeclaredFields()) {
            set.add(declaredField.getName());
        }
        System.out.println(set);
        String enums = set.stream().sorted().map(v -> "    " + v.toString()).collect(Collectors.joining(",\n")) + ";";

        String fullClass = """
                package de.hype.bbsentials.shared.constants;
                \s
                public enum VanillaItems {
                %s
                }
                """;
        SystemUtils.setClipboardContent(fullClass);
        SystemUtils.sendNotification("BB Code Helper: ", "Copied the new Minecraft ITem Ids for VanillaItems Class into your clipboard");
    }
}
