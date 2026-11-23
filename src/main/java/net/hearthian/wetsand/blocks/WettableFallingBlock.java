package net.hearthian.wetsand.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WettableFallingBlock extends SandBlock implements Wettable {
    private final HumidityLevel humidityLevel;

    public WettableFallingBlock(HumidityLevel humidityLevel, int dustColor, Properties properties) {
        super(dustColor, properties);
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
    public HumidityLevel getHumidityLevel() {
        return humidityLevel;
    }
}
