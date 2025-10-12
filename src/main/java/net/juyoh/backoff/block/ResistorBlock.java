package net.juyoh.backoff.block;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.createmod.catnip.platform.CatnipServices;
import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.item.ModItemComponents;
import net.juyoh.backoff.network.ResistorConfigPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;

public class ResistorBlock extends KineticBlock implements IBE<ResistorBlockEntity> {
    public ResistorBlock(Properties properties) {
        super(properties);
    }

    private static VoxelShape empty = Block.box(0, 0, 0, 0, 0, 0);

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
        return ModBlockEntities.RESISTOR_BE.get();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return empty;
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return empty;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(CreateBackOff.RESTRAINING_ORDER.asItem())) {
            ResistorBlockEntity blockEntity = ((ResistorBlockEntity) level.getBlockEntity(pos));
            if (blockEntity.shouldResistEntity(player)) {
                player.displayClientMessage(Component.translatable("tooltip.createbackoff.resistor.deny").withStyle(ChatFormatting.RED), true);
                return ItemInteractionResult.FAIL;
            }
            //if the filter is already set, give it back
            if (!Objects.equals(blockEntity.filter, "*") && !player.isCreative()) {
                blockEntity.dropStack();
            }
            if (stack.has(ModItemComponents.ENTITY_COMPONENT)) {
                player.displayClientMessage(Component.translatable("tooltip.createbackoff.resistor.bound").withStyle(ChatFormatting.GREEN), true);
                String newFilter = stack.get(ModItemComponents.ENTITY_COMPONENT).name();
                blockEntity.filter = newFilter;
                stack.consume(1, player);
                level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1, 1);
                if (player.level().isClientSide) {
                    CatnipServices.NETWORK.sendToServer(new ResistorConfigPacket(pos.getX(), pos.getY(), pos.getZ(), newFilter));
                } else {
                    withBlockEntityDo(level, pos, SyncedBlockEntity::notifyUpdate);
                }
                addParticles(level, pos, level.random, blockEntity.calculateSize(), 320);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isCrouching()) {
            ResistorBlockEntity blockEntity = ((ResistorBlockEntity) level.getBlockEntity(pos));
            if (blockEntity.shouldResistEntity(player)) {
                player.displayClientMessage(Component.translatable("tooltip.createbackoff.resistor.deny").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
            //if the filter is already set, give it back
            if (!Objects.equals(blockEntity.filter, "*") && !player.isCreative()) {
                blockEntity.dropStack();
            }
            player.displayClientMessage(Component.translatable("tooltip.createbackoff.restraining_order.wildcard").withStyle(ChatFormatting.GREEN), true);
            String newFilter = "*";
            blockEntity.filter = newFilter;
            level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1, 1);
            if (player.level().isClientSide) {
                CatnipServices.NETWORK.sendToServer(new ResistorConfigPacket(pos.getX(), pos.getY(), pos.getZ(), newFilter));
            } else {
                withBlockEntityDo(level, pos, SyncedBlockEntity::notifyUpdate);
            }
            addParticles(level, pos, level.random, blockEntity.calculateSize(), 320);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
    public void addParticles(Level level, BlockPos pos, RandomSource random, int size, int amount) {
        if (size == 0) {
            return;
        }
        double x = random.nextDouble() * 3;
        double y = random.nextDouble() * 3;
        double z = random.nextDouble() * 3;

        for (int l = 0; l < amount / 3.5; l++) {
            level.addParticle(
                    new DustParticleOptions(new Vector3f(0.9f, 0.3f, 0.4f), 0.6f),
                    pos.getX() - 0.5 + x,
                    pos.getY() - 0.5 + y,
                    pos.getZ() - 0.5 + z,
                    x * size,
                    y * size,
                    z * size
            );
        }
        for (int l = 0; l < amount; l++) {
            level.addParticle(
                    new DustParticleOptions(new Vector3f(0.7f, 0.4f, 0.3f), 0.3f),
                    pos.getX() - 0.5 + x,
                    pos.getY() - 0.5 + y,
                    pos.getZ() - 0.5 + z,
                    x * size * 2,
                    y * size * 2,
                    z * size * 2
            );
        }
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

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return SoundType.METAL;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        addParticles(level, pos, random, ((ResistorBlockEntity) level.getBlockEntity(pos)).calculateSize(), 64);
    }

}
