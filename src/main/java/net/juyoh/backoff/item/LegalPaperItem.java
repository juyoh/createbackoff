package net.juyoh.backoff.item;

import net.juyoh.backoff.CreateBackOff;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LegalPaperItem extends Item {
    public LegalPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        ItemStack orderStack = new ItemStack(CreateBackOff.RESTRAINING_ORDER.asItem()).copyWithCount(player.getItemInHand(usedHand).getCount());
        orderStack.set(ModItemComponents.ENTITY_COMPONENT, new EntityTypeComponent(interactionTarget.getName().getString()));
        player.setItemInHand(usedHand, orderStack);

        player.playSound(SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
        player.displayClientMessage(RestrainingOrderItem.getTooltip(orderStack), true);

        if (interactionTarget instanceof Mob) {
            ((Mob) interactionTarget).setTarget(player);
        }

        return InteractionResult.SUCCESS;
    }
}
