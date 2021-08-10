package net.eonzenx.needle_ce.entities.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class NCEShield extends ShieldItem
{
    public NCEShield(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        var direction = Vec3d.fromPolar(0, user.getYaw());
        entity.takeKnockback(1f, direction.x, direction.y);
        return super.useOnEntity(stack, user, entity, hand);
    }


}
