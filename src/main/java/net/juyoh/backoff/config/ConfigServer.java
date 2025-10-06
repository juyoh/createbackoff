package net.juyoh.backoff.config;

import net.createmod.catnip.config.ConfigBase;

public class ConfigServer extends ConfigBase {
    public final ModStress stressValues = nested(0, ModStress::new, "Stress values");

    @Override
    public String getName() {
        return "server";
    }
}
