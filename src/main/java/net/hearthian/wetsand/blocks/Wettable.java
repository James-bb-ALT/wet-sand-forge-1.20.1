package net.hearthian.wetsand.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.hearthian.wetsand.ModBlocks;
import net.hearthian.wetsand.WetSand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface Wettable {
    int HUMIDITY_RANGE = 3;

    TagKey<Block> WETTABLE_TAG = TagKey.create(Registries.BLOCK,
            new ResourceLocation(WetSand.RESOURCE_NAMESPACE, "wettable"));
    TagKey<Block> CAN_GROW_SUGAR_CANE_TAG = TagKey.create(Registries.BLOCK,
            new ResourceLocation(WetSand.RESOURCE_NAMESPACE, "can_grow_sugar_cane"));

    Supplier<BiMap<Block, Block>> HUMIDITY_LEVEL_INCREASES = Suppliers.memoize(() ->
            ImmutableBiMap.<Block, Block>builder()
                    .put(Blocks.SAND, ModBlocks.MOIST_SAND.get())
                    .put(ModBlocks.MOIST_SAND.get(), ModBlocks.WET_SAND.get())
                    .put(ModBlocks.WET_SAND.get(), ModBlocks.SOAKED_SAND.get())
                    .put(Blocks.RED_SAND, ModBlocks.MOIST_RED_SAND.get())
                    .put(ModBlocks.MOIST_RED_SAND.get(), ModBlocks.WET_RED_SAND.get())
                    .put(ModBlocks.WET_RED_SAND.get(), ModBlocks.SOAKED_RED_SAND.get())
                    .build());

    Supplier<BiMap<Block, Block>> HUMIDITY_LEVEL_DECREASES = Suppliers.memoize(() ->
            HUMIDITY_LEVEL_INCREASES.get().inverse());

    HumidityLevel getHumidityLevel();

    default Optional<BlockState> tryDrench(BlockState state, ServerLevel level, BlockPos pos) {
        return tryDrenchForLevel(getHumidityLevel(), state, level, pos);
    }

    default void tickHumidity(BlockState state, ServerLevel level, BlockPos pos) {
        tickHumidityForLevel(getHumidityLevel(), state, level, pos);
    }

    static Optional<BlockState> tryDrenchForLevel(HumidityLevel levelEnum, BlockState state, ServerLevel level, BlockPos pos) {
        int currentLevel = levelEnum.ordinal();
        AtomicInteger maxReachableHumidity = new AtomicInteger(0);

        BlockPos.findClosestMatch(pos, HUMIDITY_RANGE, HUMIDITY_RANGE, checkPos -> {
            if (level.getFluidState(checkPos).is(Fluids.WATER) || level.getFluidState(checkPos).is(Fluids.FLOWING_WATER)) {
                int dist = getChebyshevDistance(checkPos, pos);
                if (HUMIDITY_RANGE - currentLevel >= dist) {
                    maxReachableHumidity.set(HUMIDITY_RANGE - dist + 1);
                    return true;
                }
            }
            return false;
        });

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            if (level.getFluidState(neighborPos).is(Fluids.WATER)) {
                return getHumidityResult(state);
            }

            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(WETTABLE_TAG) && neighborState.getBlock() instanceof Wettable neighborWettable) {
                int neighborLevel = neighborWettable.getHumidityLevel().ordinal();
                if (neighborLevel > currentLevel && currentLevel < maxReachableHumidity.get()) {
                    return getHumidityResult(state);
                }
            }
        }

        return Optional.empty();
    }

    static void tickHumidityForLevel(HumidityLevel levelEnum, BlockState state, ServerLevel level, BlockPos pos) {
        tryDrenchForLevel(levelEnum, state, level, pos).ifPresent(newState -> level.setBlockAndUpdate(pos, newState));
    }

    static Optional<Block> getDecreasedHumidityBlock(Block block) {
        return Optional.ofNullable(HUMIDITY_LEVEL_DECREASES.get().get(block));
    }

    default Optional<BlockState> getDecreasedHumidityState(BlockState state) {
        return getDecreasedHumidityBlock(state.getBlock()).map(target -> target.withPropertiesOf(state));
    }

    static Optional<Block> getIncreasedHumidityBlock(Block block) {
        return Optional.ofNullable(HUMIDITY_LEVEL_INCREASES.get().get(block));
    }

    static Optional<BlockState> getHumidityResult(BlockState state) {
        return getIncreasedHumidityBlock(state.getBlock()).map(target -> target.withPropertiesOf(state));
    }

    private static int getChebyshevDistance(BlockPos a, Vec3i b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        int dz = Math.abs(a.getZ() - b.getZ());
        return Math.max(Math.max(dx, dy), dz);
    }

    enum HumidityLevel implements StringRepresentable {
        UNAFFECTED("unaffected"),
        MOIST("moist"),
        WET("wet"),
        SOAKED("soaked");

        private final String id;

        HumidityLevel(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
