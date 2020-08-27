package svenhjol.meson;

import svenhjol.charm.event.CommonSetupCallback;
import svenhjol.meson.handler.ConfigHandler;
import svenhjol.meson.handler.ModuleHandler;

import java.util.List;
import java.util.function.Consumer;

public abstract class MesonMod {
    private ModuleHandler moduleHandler;
    private ConfigHandler configHandler;
    private String id;

    public void init(String id) {
        this.id = id;

        Meson.INSTANCE.register(this);

        this.moduleHandler = new ModuleHandler(this, getModules());
        this.configHandler = new ConfigHandler(this);

        // initialize all modules
        eachModule(MesonModule::init);
        if (Meson.isClient())
            eachModule(MesonModule::initClient);

        // listen for common setup events
        CommonSetupCallback.EVENT.register(() -> {
            eachEnabledModule(MesonModule::setup);

            if (Meson.isClient())
                eachEnabledModule(MesonModule::setupClient);
        });
    }

    public String getId() {
        return id;
    }

    public abstract List<Class<? extends MesonModule>> getModules();

    public void eachModule(Consumer<MesonModule> consumer) {
        Meson.loadedModules.get(this.id)
            .values()
            .forEach(consumer);
    }

    public void eachEnabledModule(Consumer<MesonModule> consumer) {
        Meson.loadedModules.get(this.id)
            .values()
            .stream()
            .filter(m -> m.enabled)
            .forEach(consumer);
    }
}