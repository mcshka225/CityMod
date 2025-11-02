/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.citymod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import com.example.citymod.CitymodMod;

public class CitymodModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CitymodMod.MODID);
	public static final RegistryObject<Item> CITY_BLOCK = block(CitymodModBlocks.CITY_BLOCK);

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		try {
			// Пробуем разные способы получить путь
			ResourceLocation loc = block.getId();

			// Способ 1: Через reflection (на крайний случай)
			java.lang.reflect.Method getPathMethod = loc.getClass().getMethod("getPath");
			String path = (String) getPathMethod.invoke(loc);
			return REGISTRY.register(path, () -> new BlockItem(block.get(), new Item.Properties()));

		} catch (Exception e) {
			// Способ 2: Через строковое представление
			String fullId = block.getId().toString();
			String path = fullId.contains(":") ? fullId.split(":")[1] : fullId;
			return REGISTRY.register(path, () -> new BlockItem(block.get(), new Item.Properties()));
		}
	}
}