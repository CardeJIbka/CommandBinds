package cardejibka.commandbinds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandBindsConfig {

    public static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("commandbinds.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final List<CommandBind> binds = new ArrayList<>();

    private CommandBindsConfig() {}

    public static List<CommandBind> getBinds() {
        return Collections.unmodifiableList(binds);
    }

    public static void setBinds(List<CommandBind> newBinds) {
        binds.clear();
        binds.addAll(newBinds);
        save();
    }

    public static void addBind(CommandBind bind) {
        binds.add(bind);
        save();
    }

    public static void removeBind(int index) {
        if (index >= 0 && index < binds.size()) {
            binds.remove(index);
            save();
        }
    }

    public static void removeByKey(int keyCode) {
        binds.removeIf(b -> b.keyCode() == keyCode);
        save();
    }

    public static boolean hasKey(int keyCode) {
        return binds.stream().anyMatch(b -> b.keyCode() == keyCode);
    }


    public static void load() {
        binds.clear();
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;

            JsonArray arr = root.getAsJsonArray("binds");
            if (arr == null) return;

            for (var elem : arr) {
                JsonObject obj = elem.getAsJsonObject();
                int keyCode   = obj.get("keyCode").getAsInt();
                String text   = obj.get("text").getAsString();
                String label  = obj.has("label") ? obj.get("label").getAsString() : text;
                binds.add(new CommandBind(keyCode, text, label));
            }
        } catch (Exception e) {
            CommandBinds.LOGGER.error("[CommandBinds] Failed to load config", e);
        }
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray arr = new JsonArray();

            for (CommandBind bind : binds) {
                JsonObject obj = new JsonObject();
                obj.addProperty("keyCode", bind.keyCode());
                obj.addProperty("text", bind.text());
                obj.addProperty("label", bind.label());
                arr.add(obj);
            }

            root.add("binds", arr);
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (Exception e) {
            CommandBinds.LOGGER.error("[CommandBinds] Failed to save config", e);
        }
    }
}
