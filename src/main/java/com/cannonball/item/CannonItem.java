package com.cannonball.item;

import com.cannonball.entity.CannonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CannonItem extends Item {
    public CannonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();

        BlockPos spawnPos = clickedPos.relative(clickedFace);
        double spawnX = spawnPos.getX() + 0.5;
        double spawnY = spawnPos.getY();
        double spawnZ = spawnPos.getZ() + 0.5;

        if (!level.isClientSide) {
            CannonEntity cannon = new CannonEntity(level, spawnX, spawnY, spawnZ);
            Player player = context.getPlayer();
            if (player != null) {
                // Face towards the direction the player is looking
                cannon.setYRot(player.getYRot());
            }
            level.addFreshEntity(cannon);
            level.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.8F, 0.7F);

            ItemStack stack = context.getItemInHand();
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
