package c4.champions.integrations.scalinghealth;

import c4.champions.common.config.ConfigHandler;
import java.util.Map;
import java.util.TreeMap;

public class ChampionDifficulty {

    private static final Map<Integer, Double> MODIFIERS = new TreeMap<>();

    public static void loadConfigs() {

        for (String s : ConfigHandler.scalingHealth.spawnModifiers) {
            String[] parsed = s.split(";");

            if (parsed.length > 1) {
                int tier = Integer.parseInt(parsed[0]);
                double modifier = Double.parseDouble(parsed[1]);

                if (tier > 0 && modifier > 0) {
                    MODIFIERS.put(tier, modifier);
                }
            }
        }
    }

    public static double getSpawnModifier(int tier) {
        return MODIFIERS.getOrDefault(tier, 0.0d);
    }
}
