package net.hearthian.wetsand.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class WettableBlock extends Block implements Wettable {
    private final HumidityLevel humidityLevel;

    public WettableBlock(HumidityLevel humidityLevel, Properties properties) {
        super(properties);
        this.humidityLevel = humidityLevel;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return Wettable.getIncreasedHumidityBlock(state.getBlock()).isPresent();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tickHumidity(state, level, pos);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        if (state.is(CAN_GROW_SUGAR_CANE_TAG) && plantable == Blocks.SUGAR_CANE) {
            return true;
        }
        return super.canSustainPlant(state, level, pos, facing, plantable);
    }

    @Override
    public HumidityLevel getHumidityLevel() {
        return humidityLevel;
    }
}
