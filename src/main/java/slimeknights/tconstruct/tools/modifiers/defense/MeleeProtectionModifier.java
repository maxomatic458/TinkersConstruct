package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class MeleeProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> implements ProtectionModifierHook, TooltipModifierHook {
  private static final UUID SPEED_UUID = UUID.fromString("6f030b1e-e9e1-11ec-8fea-0242ac120002");
  private static final TinkerDataKey<ModifierMaxLevel> KEY = TConstruct.createKey("melee_protection");

  public MeleeProtectionModifier() {
    super(KEY);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.PROTECTION, TinkerHooks.TOOLTIP);
  }

  @Override
  protected void set(ModifierMaxLevel data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    float oldMax = data.getMax();
    super.set(data, slot, scaledLevel, context);
    float newMax = data.getMax();
    // 5% bonus attack speed for the largest level
    if (oldMax != newMax) {
      AttributeInstance instance = context.getEntity().getAttribute(Attributes.ATTACK_SPEED);
      if (instance != null) {
        instance.removeModifier(SPEED_UUID);
        if (newMax != 0) {
          instance.addTransientModifier(new AttributeModifier(SPEED_UUID, "tconstruct.melee_protection", 0.03 * newMax, Operation.MULTIPLY_BASE));
        }
      }
    }
  }

  @Override
  protected void reset(ModifierMaxLevel data, EquipmentChangeContext context) {
    super.reset(data, context);
    AttributeInstance instance = context.getEntity().getAttribute(Attributes.ATTACK_SPEED);
    if (instance != null) {
      instance.removeModifier(SPEED_UUID);
    }
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (DamageSourcePredicate.CAN_PROTECT.matches(source) && DamageSourcePredicate.MELEE.matches(source)) {
      modifierValue += modifier.getEffectiveLevel() * 2.5;
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    ProtectionModule.addResistanceTooltip(tool, this, modifier.getEffectiveLevel() * 2.5f, player, tooltip);
  }

  @Override
  protected ModifierMaxLevel createData(EquipmentChangeContext context) {
    return new ModifierMaxLevel();
  }
}
