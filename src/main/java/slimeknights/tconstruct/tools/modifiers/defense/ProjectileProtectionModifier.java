package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
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

public class ProjectileProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> implements ProtectionModifierHook, TooltipModifierHook {
  /** Entity data key for the data associated with this modifier */
  public static final TinkerDataKey<ModifierMaxLevel> PROJECTILE_DATA = TConstruct.createKey("projectile_protection");
  public ProjectileProtectionModifier() {
    super(PROJECTILE_DATA);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.PROTECTION, TinkerHooks.TOOLTIP);
  }

  @Override
  protected ModifierMaxLevel createData(EquipmentChangeContext context) {
    return new ModifierMaxLevel();
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && source.isProjectile()) {
      modifierValue += modifier.getEffectiveLevel() * 2.5f;
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    ProtectionModule.addResistanceTooltip(tool, this, modifier.getEffectiveLevel() * 2.5f, player, tooltip);
  }
}
