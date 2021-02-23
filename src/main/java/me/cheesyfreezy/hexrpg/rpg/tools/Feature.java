package me.cheesyfreezy.hexrpg.rpg.tools;

import me.cheesyfreezy.hexrpg.tools.ConfigFile;

public class Feature {
    private String featureName;
    private boolean enabled;

    public Feature(String featureName) {
        this.featureName = featureName;
        enabled = ConfigFile.getConfig("config.yml").getBoolean("feature-settings." + featureName);
    }

    public static Feature getFeature(String featureName) {
        return new Feature(featureName);
    }

    public String getFeatureName() {
        return featureName;
    }

    public boolean isEnabled() {
        return enabled;
    }
}