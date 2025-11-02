/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.citymod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import com.example.citymod.block.entity.CityBlockBlockEntity;
import com.example.citymod.CitymodMod;

public class CitymodModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CitymodMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> CITY_BLOCK = register("city_block", CitymodModBlocks.CITY_BLOCK, CityBlockBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}