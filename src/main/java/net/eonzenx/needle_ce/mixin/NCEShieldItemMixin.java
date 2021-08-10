package net.eonzenx.needle_ce.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShieldItem.class)
public class NCEShieldItemMixin
{
    protected PlayerEntity user;

    @Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)LTypedActionResult;", at = @At("HEAD"))
    private void catchUser(World world, PlayerEntity user, Hand hand, CallbackInfo info) {
        this.user = user;
        System.out.println("Mixin here");
    }


}
