package net.hearthian.wetsand;

import net.hearthian.wetsand.blocks.SoakedBlock;
import net.hearthian.wetsand.blocks.Wettable;
import net.hearthian.wetsand.blocks.WettableBlock;
import net.hearthian.wetsand.blocks.WettableFallingBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WetSand.RESOURCE_NAMESPACE);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WetSand.RESOURCE_NAMESPACE);

    public static final RegistryObject<Block> MOIST_SAND = registerBlock("moist_sand",
            () -> new WettableFallingBlock(Wettable.HumidityLevel.MOIST, 0xDBD3A0,
                    BlockBehaviour.Properties.copy(Blocks.SAND)));

    public static final RegistryObject<Block> WET_SAND = registerBlock("wet_sand",
            () -> new WettableBlock(Wettable.HumidityLevel.WET,
                    BlockBehaviour.Properties.copy(Blocks.SAND)));

    public static final RegistryObject<Block> SOAKED_SAND = registerBlock("soaked_sand",
            () -> new SoakedBlock(BlockBehaviour.Properties.copy(Blocks.SAND)));

    public static final RegistryObject<Block> MOIST_RED_SAND = registerBlock("moist_red_sand",
            () -> new WettableFallingBlock(Wettable.HumidityLevel.MOIST, 0xA95821,
                    BlockBehaviour.Properties.copy(Blocks.RED_SAND)));

    public static final RegistryObject<Block> WET_RED_SAND = registerBlock("wet_red_sand",
            () -> new WettableBlock(Wettable.HumidityLevel.WET,
                    BlockBehaviour.Properties.copy(Blocks.RED_SAND)));

    public static final RegistryObject<Block> SOAKED_RED_SAND = registerBlock("soaked_red_sand",
            () -> new SoakedBlock(BlockBehaviour.Properties.copy(Blocks.RED_SAND)));

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.getEntries().putAfter(new ItemStack(Items.SAND), new ItemStack(MOIST_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(new ItemStack(MOIST_SAND.get()), new ItemStack(WET_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(new ItemStack(WET_SAND.get()), new ItemStack(SOAKED_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.getEntries().putAfter(new ItemStack(Items.RED_SAND), new ItemStack(MOIST_RED_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(new ItemStack(MOIST_RED_SAND.get()), new ItemStack(WET_RED_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(new ItemStack(WET_RED_SAND.get()), new ItemStack(SOAKED_RED_SAND.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
