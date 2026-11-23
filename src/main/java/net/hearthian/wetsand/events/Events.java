package net.hearthian.wetsand.events;

import net.hearthian.wetsand.blocks.Wettable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Optional;

public class Events {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack heldStack = event.getItemStack();

        if (player.isSpectator() || !heldStack.is(Items.GLASS_BOTTLE)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.is(Wettable.WETTABLE_TAG) || !(state.getBlock() instanceof Wettable wettable)) {
            return;
        }

        Optional<BlockState> decreased = wettable.getDecreasedHumidityState(state);
        if (decreased.isEmpty()) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            heldStack.shrink(1);
            ItemStack waterBottle = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            if (!player.addItem(waterBottle)) {
                player.drop(waterBottle, false);
            }
            serverLevel.setBlockAndUpdate(pos, decreased.get());
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
