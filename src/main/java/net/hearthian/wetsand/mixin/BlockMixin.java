package net.hearthian.wetsand.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "isRandomlyTicking(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void wetSand$isRandomlyTicking(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND)) {
            cir.setReturnValue(true);
        }
    }
}
