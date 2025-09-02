package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;
import net.minecraft.world.level.tile.BlockLog;

public enum EnumTreeType {
	OAK("Oak", new BlockState(Block.leaves, BlockLeaves.OakMetadata), new BlockState(Block.wood, BlockLog.OakMetadata), new BlockState(Block.sapling, BlockLeaves.OakMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return rand.nextInt(8) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
		}
	},
	
	BIRCH("Birch", new BlockState(Block.leaves, BlockLeaves.BirchMetadata), new BlockState(Block.wood, BlockLog.BirchMetadata), new BlockState(Block.sapling, BlockLeaves.BirchMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenForest(true);
		}
	},
	
	TAIGA("Taiga", new BlockState(Block.leaves, BlockLeaves.SpruceMetadata), new BlockState(Block.wood, BlockLog.SpruceMetadata), new BlockState(Block.sapling, BlockLeaves.SpruceMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return rand.nextBoolean() ? new WorldGenTaiga1() : new WorldGenTaiga2();
		}
	},
	
	HUGE("Huge", new BlockState(Block.leaves, BlockLeaves.JungleMetadata), new BlockState(Block.wood, BlockLog.JungleMetadata), new BlockState(Block.sapling, BlockLeaves.JungleMetadata), true) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenHugeTrees(true, 16 + rand.nextInt(16));
		}
	},

	SWAMP("Swamp", new BlockState(Block.leaves, 0), new BlockState(Block.wood, 0), new BlockState(Block.sapling, 0xff)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenSwamp();
		}
	},
	;
	
	public final BlockState leaves;
	public final BlockState wood;
	public final BlockState sapling;
	
	public final String name;
	
	public final boolean needsFourSaplings;
	
	public WorldGenerator getGen(Random rand) {
		return new WorldGenTrees(false);
	}
	
	public static BlockState getSaplingFromLeaves(BlockState leaves) {
		return new BlockState(Block.sapling, leaves.getMetadata());
	}
	
	public static EnumTreeType findTreeTypeFromLeaves(BlockState leaves) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(leaves.equals(e.leaves)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	public static EnumTreeType findTreeTypeFromSapling(BlockState sapling) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(sapling.equals(e.sapling)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	public static EnumTreeType findTreeTypeFromWood(BlockState wood) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(wood.equals(e.wood)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	EnumTreeType(String name, BlockState leaves, BlockState wood, BlockState sapling) {
		this(name, leaves, wood, sapling, false);
	}
	
	EnumTreeType(String name, BlockState leaves, BlockState wood, BlockState sapling, boolean needsFourSaplings) {
		this.name = name;
		this.leaves = leaves;
		this.wood = wood;
		this.sapling = sapling;
		this.needsFourSaplings = needsFourSaplings;
	}
	
}
