package com.gildedgames.aether.world.structure;

import com.gildedgames.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.gildedgames.aether.world.structurepiece.silverdungeon.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class SilverDungeonStructure extends Structure {
    public static final Codec<SilverDungeonStructure> CODEC = simpleCodec(SilverDungeonStructure::new);
    public SilverDungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        ChunkPos chunkpos = context.chunkPos();
        RandomSource random = context.random();

        int x = chunkpos.getMiddleBlockX();
        int z = chunkpos.getMiddleBlockZ();

        int maxHeight = 128;
        int minHeight = chunkGenerator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, context.randomState()) - 2;

        int height;
        if (random.nextInt(5) < 3) {
            height = minHeight + 18;
            if (height < maxHeight) {
                height += random.nextInt(maxHeight - height);
            }
        } else {
            height = Math.max(minHeight, 35 + random.nextInt(70));
        }

        BlockPos blockpos = new BlockPos(chunkpos.getMiddleBlockX(), height, chunkpos.getMiddleBlockZ());


        return Optional.of(new GenerationStub(blockpos, piecesBuilder -> this.generatePieces(piecesBuilder, context, blockpos)));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos input) {
        RandomSource randomSource = context.random();
        Rotation rotation = Rotation.getRandom(randomSource);
        Direction direction = rotation.rotate(Direction.SOUTH);
        StructureTemplateManager manager = context.structureTemplateManager();

        BlockPos elevatedPos = input.relative(rotation.rotate(Direction.NORTH), 54).relative(rotation.rotate(Direction.WEST), 15);

        this.buildCloudBed(builder, randomSource, elevatedPos, direction);

        SilverTemplePiece rear = new SilverTemplePiece(
                manager,
                "rear",
                elevatedPos,
                rotation
        );
        builder.addPiece(rear);

        BlockPos bossRoomPos = elevatedPos.offset((direction.getStepX() + direction.getStepZ()) * 5, 3, (direction.getStepZ() - direction.getStepX()) * 5);

        SilverBossRoom bossRoom = new SilverBossRoom(
                manager,
                "boss_room",
                bossRoomPos,
                rotation
        );
        builder.addPiece(bossRoom);

        SilverBossDetail bossDetail = new SilverBossDetail(
                manager, "boss_detail",
                bossRoomPos.offset(direction.getStepX() + direction.getStepZ(), 2, direction.getStepZ() - direction.getStepX()),
                rotation
        );
        builder.addPiece(bossDetail);

        int xOffset = direction.getStepX() * rear.getBoundingBox().getXSpan();
        int zOffset = direction.getStepZ() * rear.getBoundingBox().getZSpan();

        BlockPos offsetPos = elevatedPos.offset(xOffset, 0, zOffset);

        SilverTemplePiece exterior = new SilverTemplePiece(
                manager,
                "skeleton",
                offsetPos,
                rotation
        );
        builder.addPiece(exterior);

        SilverDungeonBuilder grid = new SilverDungeonBuilder(randomSource, 3, 3, 3);
        grid.assembleDungeon(builder, manager, offsetPos, rotation, direction);
    }

    /**
     * Builds a cloud bed under the silver dungeon. This serves as a work-around for being unable to place blocks in
     * neighboring chunks.
     */
    private void buildCloudBed(StructurePiecesBuilder builder, RandomSource random, BlockPos origin, Direction direction) {
        int xBounds;
        int zBounds;
        BlockPos.MutableBlockPos offset = origin.mutable().move(0, -1, 0);
        switch (direction) {
            case SOUTH -> {
                xBounds = 50;
                zBounds = 77;
                offset.move(-10, 0, -11);
            }
            case NORTH -> {
                xBounds = 50;
                zBounds = 77;
                offset.move(-40, 0, -66);
            }
            case EAST -> {
                xBounds = 77;
                zBounds = 50;
                offset.move(-11, 0, -40);
            }
            case WEST -> {
                xBounds = 77;
                zBounds = 50;
                offset.move(-66, 0, -10);
            }
            default -> {
                xBounds = 77;
                zBounds = 50;
            }
        }


        Map<ChunkPos, Set<BlockPos>> chunks = new HashMap<>();
        Set<BlockPos> positions = new HashSet<>();
        for (int tries = 0; tries < 100; tries++) {
            int x = offset.getX() + random.nextInt(xBounds);
            int y = 0;
            int z = offset.getZ() + random.nextInt(zBounds);
            int xTendency = random.nextInt(3) - 1;
            int zTendency = random.nextInt(3) - 1;

            for (int n = 0; n < 10; ++n) {
                x += random.nextInt(3) - 1 + xTendency;
                if (random.nextBoolean()) {
                    y += random.nextInt(3) - 1;
                }
                z += random.nextInt(3) - 1 + zTendency;

                for (int x1 = x; x1 < x + random.nextInt(4) + 3; ++x1) {
                    for (int y1 = y; y1 < y + random.nextInt(1) + 2; ++y1) {
                        for (int z1 = z; z1 < z + random.nextInt(4) + 3; ++z1) {
                            if (Math.abs(x1 - x) + Math.abs(y1 - y) + Math.abs(z1 - z) < 4 + random.nextInt(2)) {
                                BlockPos newPosition = new BlockPos(x1, y1, z1);
                                positions.add(newPosition);
                                chunks.computeIfAbsent(new ChunkPos(newPosition), (pos) -> new HashSet<>());
                            }
                        }
                    }
                }
            }
        }

        chunks.forEach(((chunkPos, blockPosSet) -> {
            blockPosSet.addAll(positions.stream().filter(pos -> (new ChunkPos(pos).equals(chunkPos))).toList());
            builder.addPiece(new LegacyCloudBed(blockPosSet,
                    new BoundingBox(chunkPos.getMinBlockX(), origin.getY(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), origin.getY(), chunkPos.getMaxBlockZ()),
                    direction));
        }));
    }

    /**
     * Set the dungeon bounds when using the place command
     */
    @Override
    public void afterPlace(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, PiecesContainer piecesContainer) {
        AABB chunkBounds = new AABB(chunkBox.minX(), chunkBox.minY(), chunkBox.minZ(), chunkBox.maxX(), chunkBox.maxY(), chunkBox.maxZ());
        level.getLevel().getEntitiesOfClass(ValkyrieQueen.class, chunkBounds).forEach(queen -> {
            BoundingBox box = piecesContainer.calculateBoundingBox();
            AABB dungeonBounds = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1);
            queen.setDungeonBounds(dungeonBounds);
        });
    }

    @Override
    public StructureType<?> type() {
        return AetherStructureTypes.SILVER_DUNGEON.get();
    }
}
