package io.github.block.custom

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.HORIZONTAL_FACING
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction.EAST
import net.minecraft.util.math.Direction.NORTH
import net.minecraft.util.math.Direction.SOUTH
import net.minecraft.util.math.Direction.WEST
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

private val FACING = HORIZONTAL_FACING

private val NORTH_SHAPE = Block.createCuboidShape(
    3.8026,
    0.0,
    1.8119,
    12.0026,
    22.0,
    9.5119,
)

private val SOUTH_SHAPE = Block.createCuboidShape(
    3.9974,
    0.0,
    6.4881,
    12.1974,
    22.0,
    14.1881,
)

private val WEST_SHAPE = Block.createCuboidShape(
    1.8119,
    0.0,
    3.9974,
    9.5119,
    22.0,
    12.1974,
)

private val EAST_SHAPE = Block.createCuboidShape(
    6.4881,
    0.0,
    3.8026,
    14.1881,
    22.0,
    12.0026,
)

class Chair : Block(
    FabricBlockSettings.create()
        .nonOpaque()
        .strength(-1.0f, 3600000.0f),
) {
    @Deprecated("Deprecated in Java")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape =
        VoxelShapes.union(
            when (state.get(FACING)) {
                NORTH -> NORTH_SHAPE

                SOUTH -> SOUTH_SHAPE

                WEST -> WEST_SHAPE

                EAST -> EAST_SHAPE

                else -> createCuboidShape(
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                )
            },
        )

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return this.defaultState.with(FACING, ctx.horizontalPlayerFacing.opposite)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(FACING, rotation.rotate(state.get(FACING)))
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.rotate(mirror.getRotation(state.get(FACING)))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }
}
