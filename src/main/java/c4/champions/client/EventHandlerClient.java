package c4.champions.client;

import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {

    @SubscribeEvent
    public void onMovementInput(InputUpdateEvent evt) {

        if (evt.getEntityLiving().isPotionActive(ChampionsRegistry.jailed)) {
            MovementInput input = evt.getMovementInput();
            input.sneak = false;
            input.jump = false;
            input.moveForward = 0;
            input.moveStrafe = 0;
        }
    }

    @SubscribeEvent
    public void renderChampionHealth(RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            RayTraceResult mouseOver = ClientUtil.getMouseOver(evt.getPartialTicks(), ConfigHandler.client.healthVisibility);

            if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entity = mouseOver.entityHit;
                Champion chp = ChampionCapability.getElite(entity);

                if (chp != null && !ConfigHandler.hideEffects) {
                    EntityLiving living = (EntityLiving)entity;
                    ClientUtil.renderChampionHealth(living, chp);
                    evt.setCanceled(true);
                }
            }
        }
    }
}
