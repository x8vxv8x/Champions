package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import org.apache.logging.log4j.Level;

public class Rank {

    private final int tier;
    private final int color;
    private final int affixes;
    private final int growthFactor;
    private final float chance;
    private final String[] potions;

    public Rank() {
        this(0, 0, 0, 0, 0, new String[]{});
    }

    public Rank(int tier, int affixes, int growthFactor, float chance, int color, String[] potions) {
        this.tier = tier;
        this.affixes = affixes;
        this.growthFactor = growthFactor;
        this.chance = chance;
        this.color = color;
        this.potions = potions;
    }

    public int getTier() {
        return tier;
    }

    public int getColor() {
        String[] colors = ConfigHandler.client.colors;
        if (colors.length >= tier && tier > 0) {
            String s = colors[tier - 1];

            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Non-integer in color config! " + s);
                return color;
            }
        }
        return color;
    }

    public int getAffixes() {
        return affixes;
    }

    public int getGrowthFactor() {
        return growthFactor;
    }

    public float getChance() {
        return chance;
    }

    public String[] getPotions() {
        return potions;
    }

    public void applyGrowth(EntityLivingBase entity) {
        applyGrowth(entity, SharedMonsterAttributes.MAX_HEALTH, ConfigHandler.growth.health, 2);
        applyGrowth(entity, SharedMonsterAttributes.ATTACK_DAMAGE, ConfigHandler.growth.attackDamage, 2);
        applyGrowth(entity, SharedMonsterAttributes.ARMOR, ConfigHandler.growth.armor, 0);
        applyGrowth(entity, SharedMonsterAttributes.ARMOR_TOUGHNESS, ConfigHandler.growth.armorToughness, 0);
        applyGrowth(entity, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, ConfigHandler.growth.knockbackResist, 0);
        entity.setHealth(entity.getMaxHealth());
    }

    private void applyGrowth(EntityLivingBase entity, IAttribute attribute, double amount, int operation) {
        IAttributeInstance att = entity.getEntityAttribute(attribute);

        if (att == null) {
            return;
        }

        double oldValue = att.getBaseValue();
        double scaledAmount = amount * growthFactor;
        double newValue;

        switch (operation) {
            case 0: newValue = oldValue + scaledAmount; break;
            case 1: newValue = oldValue * scaledAmount; break;
            case 2: newValue = oldValue * (1 + scaledAmount); break;
            default: return;
        }
        att.setBaseValue(newValue);
    }
}
