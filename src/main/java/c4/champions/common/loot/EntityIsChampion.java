package c4.champions.common.loot;

import c4.champions.Champions;
import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;

public class EntityIsChampion implements EntityProperty {

    private final int tier;
    private final int minTier;
    private final int maxTier;

    public EntityIsChampion(int tierIn, int minTierIn, int maxTierIn) {
        this.tier = tierIn;
        this.minTier = minTierIn;
        this.maxTier = maxTierIn;
    }

    public boolean testProperty(@Nonnull Random random, @Nonnull Entity entityIn) {
        Champion chp = ChampionCapability.getElite(entityIn);

        if (chp != null) {
            int tier = chp.getRank().getTier();

            if (this.tier == 0) {
                return (minTier == 0 || tier >= minTier) && (maxTier == 0 || tier <= maxTier);
            }
            return this.tier == tier;
        }
        return false;
    }

    public static class Serializer extends EntityProperty.Serializer<EntityIsChampion> {

        public Serializer() {
            super(new ResourceLocation(Champions.MODID, "is_champion"), EntityIsChampion.class);
        }

        @Nonnull
        public JsonElement serialize(@Nonnull EntityIsChampion property, @Nonnull JsonSerializationContext serializationContext) {
            JsonObject json = new JsonObject();
            json.addProperty("tier", property.tier);
            json.addProperty("min_tier", property.minTier);
            json.addProperty("max_tier", property.maxTier);
            return json;
        }

        @Nonnull
        public EntityIsChampion deserialize(@Nonnull JsonElement element, @Nonnull JsonDeserializationContext
                deserializationContext) {
            JsonObject json = element.getAsJsonObject();
            int tier = JsonUtils.getInt(json, "tier", 0);
            int minTier = JsonUtils.getInt(json, "min_tier", 0);
            int maxTier = JsonUtils.getInt(json, "max_tier", 0);
            return new EntityIsChampion(tier, minTier, maxTier);
        }
    }
}
