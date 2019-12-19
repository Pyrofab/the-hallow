package com.fabriccommunity.thehallow.world.biome;

import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;

import com.fabriccommunity.thehallow.registry.HallowedEntities;
import com.fabriccommunity.thehallow.registry.HallowedFeatures;
import com.fabriccommunity.thehallow.world.feature.HallowedBiomeFeatures;

// TODO
public class PumpkinPatchBiome extends HallowedBaseBiome {
	public PumpkinPatchBiome() {
		super(new Settings().surfaceBuilder(SURFACE_BUILDER).precipitation(Precipitation.NONE).category(Category.PLAINS).depth(0.125f).scale(0.07f).temperature(0.7f).downfall(0.8f).waterColor(4159204).waterFogColor(329011));
		
		GRASS_COLOR = 0xC9C92A;
		FOLIAGE_COLOR = 0xC9C92A;
		
		this.addStructureFeature(Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004D, MineshaftFeature.Type.NORMAL)));
		
		HallowedBiomeFeatures.addGrass(this);
		HallowedBiomeFeatures.addLakes(this);
		//is not the same as other biome pumpkins, do not use HallowedBiomeFeatures pumpkins
		this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, HallowedBiomeFeatures.configureFeature(HallowedFeatures.COLORED_PUMPKIN, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(10)));
		HallowedBiomeFeatures.addDefaultHallowedTrees(this);
		
		this.addSpawn(EntityCategory.CREATURE, new SpawnEntry(HallowedEntities.PUMPCOWN, 8, 4, 8));
	}
}
