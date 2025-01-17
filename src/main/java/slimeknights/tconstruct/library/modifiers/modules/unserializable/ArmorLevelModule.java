package slimeknights.tconstruct.library.modifiers.modules.unserializable;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for keeping track of the total level of a modifier across all pieces of equipment. Does not support incremental, use {@link ArmorStatModule} for that.
 * @see ArmorStatModule
 * @see TinkerDataKey
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule
 */
public record ArmorLevelModule(TinkerDataKey<Integer> key, boolean allowBroken, @Nullable TagKey<Item> heldTag) implements ModifierHookProvider, EquipmentChangeModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierHookProvider.<ArmorLevelModule>defaultHooks(TinkerHooks.EQUIPMENT_CHANGE);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, modifier.intEffectiveLevel(), allowBroken, heldTag);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    addLevelsIfArmor(tool, context, key, -modifier.intEffectiveLevel(), allowBroken, heldTag);
  }


  /* Helpers */

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addLevels(EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount) {
    context.getTinkerData().ifPresent(data -> {
      int totalLevels = data.get(key, 0) + amount;
      if (totalLevels <= 0) {
        data.remove(key);
      } else {
        data.put(key, totalLevels);
      }
    });
  }

  /** Checks if the given slot is valid */
  public static boolean validSlot(IToolStackView tool, EquipmentSlot slot, @Nullable TagKey<Item> heldTag) {
    return slot.getType() == Type.ARMOR || heldTag != null && tool.hasTag(heldTag);
  }

  /**
   * Adds levels to the given key in entity modifier data for an armor modifier
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   * @param heldTag  Tag to check to validate held items, if null held items are considered to never be valid
   */
  public static void addLevelsIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Integer> key, int amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (validSlot(tool, context.getChangedSlot(), heldTag) && (allowBroken || !tool.isBroken())) {
      addLevels(context, key, amount);
    }
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static int getLevel(LivingEntity living, TinkerDataKey<Integer> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0);
  }
}
