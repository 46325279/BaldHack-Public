package club.baldhack.mixin.client;

import club.baldhack.module.modules.movement.SafeWalk;
import club.baldhack.BaldHack;
import club.baldhack.event.events.EntityEvent;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by 086 on 16/11/2017.
 */
@Mixin(Entity.class)
public class MixinEntity {

    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocity(Entity entity, double x, double y, double z) {
        EntityEvent.EntityCollision entityCollisionEvent = new EntityEvent.EntityCollision(entity, x, y, z);
        BaldHack.EVENT_BUS.post(entityCollisionEvent);
        if (entityCollisionEvent.isCancelled()) return;

        entity.motionX += x;
        entity.motionY += y;
        entity.motionZ += z;

        entity.isAirBorne = true;
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return SafeWalk.shouldSafewalk() || entity.isSneaking();
    }

}
