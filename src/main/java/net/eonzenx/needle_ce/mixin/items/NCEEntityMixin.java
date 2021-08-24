package net.eonzenx.needle_ce.mixin.items;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class NCEEntityMixin
{
    @Shadow public abstract boolean collidesWith(Entity other);



//    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Lnet/minecraft/block/ShapeContext;Lnet/minecraft/util/collection/ReusableStream;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
//    private static void adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, ShapeContext context, ReusableStream<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir) {
//        if (entity != null) {
//        }
//    }

//    @Override
//    public boolean blah(Entity other) {
//        var correctCollidesWith = super.collidesWith(other);
//        if (correctCollidesWith) {
//            if (other instanceof LivingEntity lOther) {
//                if (simpleStaminaComponent == null) simpleStaminaComponent = SimpleStaminaComponent.get(this);
//                var lOtherStamina = SimpleStaminaComponent.get(lOther);
//
//                if (lOtherStamina.hasBeenDominoed()) {
//                    simpleStaminaComponent.dominoer(lOther);
//                }
//
//                if (simpleStaminaComponent.hasBeenDominoed()) {
//                    lOtherStamina.dominoer((LivingEntity) (Object) this);
//                }
//            }
//        }
//
//        return correctCollidesWith;
//    }
}
