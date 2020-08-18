package svenhjol.meson;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import svenhjol.meson.handler.ConfigHandler;
import svenhjol.meson.handler.LogHandler;
import svenhjol.meson.handler.ModuleHandler;
import svenhjol.meson.helper.StringHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class Meson {
    private ModuleHandler moduleHandler;
    private ConfigHandler configHandler;
    private String id;
    public static Map<String, Map<String, MesonModule>> loadedModules = new ConcurrentHashMap<>();
    public static LogHandler LOG = new LogHandler("Meson");

    public void init(String id) {
        this.id = id;
        this.moduleHandler = new ModuleHandler(this, getModules());
        this.configHandler = new ConfigHandler(this);

        // initialize all modules
        eachModule(MesonModule::init);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            eachModule(MesonModule::initClient);

        // setup all enabled modules
        eachEnabledModule(MesonModule::setup);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            eachModule(MesonModule::initClient);
    }

    public String getId() {
        return id;
    }

    public abstract List<Class<? extends MesonModule>> getModules();

    public void eachModule(Consumer<MesonModule> consumer) {
        loadedModules.get(this.id)
            .values()
            .forEach(consumer);
    }

    public void eachEnabledModule(Consumer<MesonModule> consumer) {
        loadedModules.get(this.id)
            .values()
            .stream()
            .filter(m -> m.enabled)
            .forEach(consumer);
    }

    public static boolean enabled(String moduleName) {
        String[] split = moduleName.split(":");
        String mod = split[0];
        String module = split[1];

        if (!loadedModules.containsKey(mod))
            return false;

        if (module.contains("_"))
            module = StringHelper.snakeToUpperCamel(module);

        if (!loadedModules.get(mod).containsKey(module))
            return false;

        return loadedModules.get(mod).get(module).enabled;
    }
}
