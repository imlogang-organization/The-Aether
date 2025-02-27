package com.gildedgames.aether.world.processor;

import com.gildedgames.aether.AetherTags;
import com.gildedgames.aether.block.AetherBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class VerticalGradientProcessor extends StructureProcessor {
    public static final Codec<VerticalGradientProcessor> CODEC = Codec.unit(() -> VerticalGradientProcessor.INSTANCE);

    public static final VerticalGradientProcessor INSTANCE = new VerticalGradientProcessor();


    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader reader, BlockPos templatePos, BlockPos pPos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        if (reader instanceof WorldGenLevel level) {
            // If the processor is running outside of the center chunk, return immediately.
            if (level instanceof WorldGenRegion region) {
                int x = SectionPos.blockToSectionCoord(relativeBlockInfo.pos.getX());
                int z = SectionPos.blockToSectionCoord(relativeBlockInfo.pos.getZ());
                ChunkPos chunk = region.getCenter();

                int xDistance = Math.abs(x - chunk.x);
                int zDistance = Math.abs(z - chunk.z);
                if (xDistance > 1 || zDistance > 1) {
                    return relativeBlockInfo;
                }
            }

            if (relativeBlockInfo.state.is(AetherBlocks.AETHER_DIRT.get())) {
                BlockPos below = relativeBlockInfo.pos.below();
                if (level.getBlockState(below).is(AetherTags.Blocks.HOLYSTONE)) {
                    RandomSource random = settings.getRandom(below);
                    if (random.nextBoolean()) {
                        level.setBlock(below, AetherBlocks.AETHER_DIRT.get().defaultBlockState(), 2);
                    }
                }
            }
        }
        return relativeBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return AetherStructureProcessors.VERTICAL_GRADIENT.get();
    }
}
