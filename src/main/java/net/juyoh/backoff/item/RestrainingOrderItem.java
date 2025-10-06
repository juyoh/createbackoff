package net.juyoh.backoff.item;

import net.juyoh.backoff.CreateBackOff;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class RestrainingOrderItem extends Item {
    public RestrainingOrderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isCrouching()) {
            ItemStack stack = player.getItemInHand(usedHand);
            if (!stack.has(ModItemComponents.ENTITY_COMPONENT)) {
                return InteractionResultHolder.fail(stack);
            }
            stack.set(ModItemComponents.ENTITY_COMPONENT, new EntityTypeComponent("*"));
            player.setItemInHand(usedHand, stack);
            player.displayClientMessage(RestrainingOrderItem.getTooltip(stack), true);

            player.playSound(SoundEvents.VILLAGER_WORK_ARMORER);

            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
    }
    public static MutableComponent getTooltip(ItemStack stack) {
        if (!stack.has(ModItemComponents.ENTITY_COMPONENT)) {
            return (Component.translatable("tooltip.createbackoff.restraining_order.empty"));
        } else if (Objects.equals(stack.get(ModItemComponents.ENTITY_COMPONENT).name(), "*")) {
            return (Component.translatable("tooltip.createbackoff.restraining_order.wildcard").withStyle(ChatFormatting.GREEN));
        } else {
            return (Component.translatable("tooltip.createbackoff.restraining_order.bound").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(stack.get(ModItemComponents.ENTITY_COMPONENT).name())).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(getTooltip(stack));
    }
}
