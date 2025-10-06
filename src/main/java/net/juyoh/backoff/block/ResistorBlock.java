package net.juyoh.backoff.block;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.item.ModItemComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class ResistorBlock extends KineticBlock implements IBE<ResistorBlockEntity> {
    public ResistorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Class<ResistorBlockEntity> getBlockEntityClass() {
        return ResistorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ResistorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.LOOT_COLLECTOR.get();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(CreateBackOff.RESTRAINING_ORDER.asItem())) {
            stack.consume(1, player);
            ResistorBlockEntity blockEntity = ((ResistorBlockEntity) level.getBlockEntity(pos));
            if (!Objects.equals(blockEntity.filter, "") && !player.isCreative()) {
                blockEntity.dropStack();
            }
            if (stack.has(ModItemComponents.ENTITY_COMPONENT)) {
                player.displayClientMessage(Component.translatable("tooltip.createbackoff.resistor.bound").withStyle(ChatFormatting.GREEN), true);
                blockEntity.filter = stack.get(ModItemComponents.ENTITY_COMPONENT).name();
                return ItemInteractionResult.SUCCESS;
            }

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        CreateBackOff.resistors.put(pos, worldIn.dimension());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof ResistorBlockEntity) {
            if (placer instanceof Player) {
                ((ResistorBlockEntity) blockEntity).owner = placer.getUUID();
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if (entity instanceof ResistorBlockEntity) {
            ((ResistorBlockEntity) entity).dropStack();
            CreateBackOff.resistors.remove(pPos, pLevel.dimension());
        }
    }
}
