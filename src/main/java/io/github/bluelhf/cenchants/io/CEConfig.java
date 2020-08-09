package io.github.bluelhf.cenchants.io;


import com.moderocky.mask.annotation.Configurable;
import com.moderocky.mask.template.Config;
import io.github.bluelhf.cenchants.cEnchants;
import org.jetbrains.annotations.NotNull;

public class CEConfig implements Config {


    @Configurable
    public int logLevel = 0;


    public CEConfig() {
        load();
    }

    @Override
    public @NotNull String getFolderPath() {
        return cEnchants.get().getDataFolder().toString();
    }

    @Override
    public @NotNull String getFileName() {
        return "config.yml";
    }
}
