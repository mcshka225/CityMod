/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.citymod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import com.example.citymod.block.CityBlockBlock;
import com.example.citymod.CitymodMod;

public class CitymodModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, CitymodMod.MODID);
	public static final RegistryObject<Block> CITY_BLOCK = REGISTRY.register("city_block", () -> new CityBlockBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}