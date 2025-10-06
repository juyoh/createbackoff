package net.juyoh.backoff.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import joptsimple.internal.Strings;
import net.createmod.catnip.lang.LangBuilder;
import net.juyoh.backoff.CreateBackOff;
import net.juyoh.backoff.item.EntityTypeComponent;
import net.juyoh.backoff.item.ModItemComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.createmod.catnip.lang.LangBuilder.DEFAULT_SPACE_WIDTH;

public class ResistorBlockEntity extends KineticBlockEntity {
    public ResistorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    String filter = "";
    UUID owner;

    @Override
    public void onLoad() {
        super.onLoad();
        CreateBackOff.resistors.put(getBlockPos(), level.dimension());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        filter = compound.getString("filter");
        owner = compound.getUUID("owner");

    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putString("filter", filter);
        compound.putUUID("owner", owner);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("filter", filter);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(Strings.repeat(' ', getIndents(Minecraft.getInstance().font))).append(Component.translatable("tooltip.createbackoff.resistor.goggles")));
        tooltip.add(getTooltip());
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
    static int getIndents(Font font) {
        int spaceWidth = font.width(" ");
        if (DEFAULT_SPACE_WIDTH == spaceWidth) {
            return 4;
        }
        return Mth.ceil(DEFAULT_SPACE_WIDTH * 4 / spaceWidth);
    }
    public MutableComponent getTooltip() {
        if (filter.equals("")) {
            return (Component.translatable("tooltip.createbackoff.restraining_order.empty").withStyle(ChatFormatting.GREEN));
        } else if (Objects.equals(filter, "*")) {
            return (Component.translatable("tooltip.createbackoff.restraining_order.wildcard"));
        } else {
            return (Component.translatable("tooltip.createbackoff.restraining_order.bound").withStyle(ChatFormatting.GOLD)
                    .append(filter));
        }
    }

    public void dropStack() {
        if (!Objects.equals(filter, "")) {
            ItemStack stack =  new ItemStack(CreateBackOff.RESTRAINING_ORDER.asItem());
            stack.setCount(1);
            stack.set(ModItemComponents.ENTITY_COMPONENT, new EntityTypeComponent(filter));
            level.addFreshEntity(new ItemEntity(level, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), stack));
            resetFilter();
        }

    }
    public static int getMaxSize() {
        return calculateSize(AllConfigs.server().kinetics.maxRotationSpeed.get());
    }
    public int calculateSize() {
        return calculateSize(this.getSpeed());
    }
    //public static int calculateSize(float speedIn) {
    //    return speedIn == 0 ? 0 : (int) (speedIn / 8) + 4;
    //}
    public static int calculateSize(float speedIn) {
        return 6;
    }
    public boolean filterEquals(String translatedName) {
        return translatedName.equals(filter) || filter.equals("*");
    }
    public boolean filterEqualsPlayer(UUID uuid) {
        return uuid == owner;
    }

    public String getFilter() {
        return filter;
    }

    public UUID getOwner() {
        return owner;
    }

    public void resetFilter() {
        filter = "";
    }

    public boolean isInside(Vec3 pos) {
        Vec3 thisPos = this.getBlockPos().getBottomCenter();
        int size = calculateSize();
        AABB box = new AABB(thisPos.add(size, size, size), thisPos.add(-size, -size, -size));
        return box.contains(pos);
    }
    public boolean isInsideWall(Vec3 pos) {
        Vec3 thisPos = this.getBlockPos().getBottomCenter();
        int size = calculateSize();
        AABB regularBox = new AABB(thisPos.add(size, size, size), thisPos.add(-size, -size, -size));
        AABB smallerBox = new AABB(thisPos.add(size - 1, size - 1, size - 1), thisPos.add(-(size - 1), -(size - 1), -(size - 1)));

        return !smallerBox.contains(pos) && regularBox.contains(pos);
    }
    public boolean isPlayerOwner(UUID player) {
        if (owner == null) {
            return false;
        }
        return owner.equals(player);
    }
}
