package net.juyoh.backoff.item;

import net.juyoh.backoff.CreateBackOff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ResistorBlockItem extends BlockItem {

    public ResistorBlockItem(Block block, Properties properties) {
        super(CreateBackOff.RESISTOR.get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            CustomData component = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (component.contains("filter")) {
                String filter = component.copyTag().getString("filter");
                if (filter.equals("*")) {
                    tooltipComponents.add(Component.translatable("tooltip.createbackoff.restraining_order.wildcard").withStyle(ChatFormatting.GREEN));
                } else {
                    tooltipComponents.add(Component.translatable("tooltip.createbackoff.restraining_order.bound").withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(filter)));
                }
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
