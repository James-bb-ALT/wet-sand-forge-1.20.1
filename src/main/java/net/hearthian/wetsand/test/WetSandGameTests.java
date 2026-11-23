package net.hearthian.wetsand.test;

import net.hearthian.wetsand.ModBlocks;
import net.hearthian.wetsand.WetSand;
import net.hearthian.wetsand.blocks.Wettable;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(WetSand.MOD_ID)
@Mod.EventBusSubscriber(modid = WetSand.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WetSandGameTests {
    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(WetSandGameTests.class);
    }

    @GameTest(template = "empty")
    public static void testSandDrenchingProgression(GameTestHelper helper) {
        BlockPos waterPos = new BlockPos(1, 2, 1);
        BlockPos sand1Pos = new BlockPos(2, 2, 1); // dist 1 -> can reach SOAKED
        BlockPos sand2Pos = new BlockPos(3, 2, 1); // dist 2 -> can reach WET
        BlockPos sand3Pos = new BlockPos(4, 2, 1); // dist 3 -> can reach MOIST

        for (int x = 0; x <= 5; x++) {
            for (int z = 0; z <= 2; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            }
        }
        helper.setBlock(new BlockPos(0, 2, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 2, 0), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 2, 2), Blocks.STONE);

        helper.setBlock(waterPos, Blocks.WATER);
        helper.setBlock(sand1Pos, Blocks.SAND);
        helper.setBlock(sand2Pos, Blocks.SAND);
        helper.setBlock(sand3Pos, Blocks.SAND);

        ServerLevel level = helper.getLevel();
        BlockPos abs1 = helper.absolutePos(sand1Pos);
        BlockPos abs2 = helper.absolutePos(sand2Pos);
        BlockPos abs3 = helper.absolutePos(sand3Pos);

        // Verify vanilla sand is randomly ticking via BlockBehaviourMixin
        helper.assertTrue(level.getBlockState(abs1).isRandomlyTicking(), "Vanilla sand should be randomly ticking");

        // Tick sand1: SAND -> MOIST -> WET -> SOAKED
        level.getBlockState(abs1).randomTick(level, abs1, level.random);
        helper.assertBlockPresent(ModBlocks.MOIST_SAND.get(), sand1Pos);

        level.getBlockState(abs1).randomTick(level, abs1, level.random);
        helper.assertBlockPresent(ModBlocks.WET_SAND.get(), sand1Pos);

        level.getBlockState(abs1).randomTick(level, abs1, level.random);
        helper.assertBlockPresent(ModBlocks.SOAKED_SAND.get(), sand1Pos);

        // Tick sand2: SAND -> MOIST -> WET, stops before SOAKED
        level.getBlockState(abs2).randomTick(level, abs2, level.random);
        helper.assertBlockPresent(ModBlocks.MOIST_SAND.get(), sand2Pos);

        level.getBlockState(abs2).randomTick(level, abs2, level.random);
        helper.assertBlockPresent(ModBlocks.WET_SAND.get(), sand2Pos);

        level.getBlockState(abs2).randomTick(level, abs2, level.random);
        helper.assertBlockPresent(ModBlocks.WET_SAND.get(), sand2Pos);

        // Tick sand3: SAND -> MOIST, stops before WET
        level.getBlockState(abs3).randomTick(level, abs3, level.random);
        helper.assertBlockPresent(ModBlocks.MOIST_SAND.get(), sand3Pos);

        level.getBlockState(abs3).randomTick(level, abs3, level.random);
        helper.assertBlockPresent(ModBlocks.MOIST_SAND.get(), sand3Pos);

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testSugarCaneOnWetSandWithoutWater(GameTestHelper helper) {
        BlockPos wetPos = new BlockPos(1, 1, 1);
        BlockPos canePos = new BlockPos(1, 2, 1);

        helper.setBlock(wetPos, ModBlocks.WET_SAND.get());
        BlockState caneState = Blocks.SUGAR_CANE.defaultBlockState();
        helper.assertTrue(caneState.canSurvive(helper.getLevel(), helper.absolutePos(canePos)),
                "Sugar cane should survive on wet_sand without adjacent water");

        helper.setBlock(wetPos, ModBlocks.SOAKED_RED_SAND.get());
        helper.assertTrue(caneState.canSurvive(helper.getLevel(), helper.absolutePos(canePos)),
                "Sugar cane should survive on soaked_red_sand without adjacent water");

        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void testDryWithGlassBottle(GameTestHelper helper) {
        BlockPos sandPos = new BlockPos(1, 1, 1);
        helper.setBlock(sandPos, ModBlocks.SOAKED_SAND.get());

        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE, 1));

        BlockPos absPos = helper.absolutePos(sandPos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absPos), Direction.UP, absPos, false);
        ForgeHooks.onRightClickBlock(player, InteractionHand.MAIN_HAND, absPos, hit);

        helper.assertBlockPresent(ModBlocks.WET_SAND.get(), sandPos);
        boolean hasWaterBottle = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER) {
                hasWaterBottle = true;
                break;
            }
        }
        helper.assertTrue(hasWaterBottle, "Player should receive a water bottle after drying soaked sand");
        helper.succeed();
    }
}
