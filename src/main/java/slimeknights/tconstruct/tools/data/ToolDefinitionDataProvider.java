package slimeknights.tconstruct.tools.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.aoe.BoxAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.ConditionalAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.IBoxExpansion;
import slimeknights.tconstruct.library.tools.definition.module.aoe.TreeAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.VeiningAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MaxTierHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import slimeknights.tconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.ParticleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.MaterialStatProviders;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolActions;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ToolDefinitions;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.RepairKitStats;
import slimeknights.tconstruct.tools.stats.SkullStats;

import static slimeknights.tconstruct.tools.TinkerToolParts.bowGrip;
import static slimeknights.tconstruct.tools.TinkerToolParts.bowLimb;
import static slimeknights.tconstruct.tools.TinkerToolParts.bowstring;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.hammerHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.largePlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.pickHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.roundPlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolBinding;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolHandle;
import static slimeknights.tconstruct.tools.TinkerToolParts.toughHandle;

public class ToolDefinitionDataProvider extends AbstractToolDefinitionDataProvider {
  public ToolDefinitionDataProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addToolDefinitions() {
    RandomMaterial tier1Material = RandomMaterial.random().tier(1).build();
    DefaultMaterialsModule defaultThreeParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material).build();
    DefaultMaterialsModule defaultFourParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material, tier1Material).build();

    // pickaxes
    define(ToolDefinitions.PICKAXE)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(pickHead)
         .part(toolHandle)
         .part(toolBinding).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 0.5f) // gains +0.5 damage from tool piercing, hence being lower than vanilla
        .set(ToolStats.ATTACK_SPEED, 1.2f).build()))
      .smallToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.pierce, 1).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG))
      .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
      .module(BoxAOEIterator.builder(0, 0, 0).addDepth(2).addHeight(1).direction(IBoxExpansion.PITCH).build());

    define(ToolDefinitions.SLEDGE_HAMMER)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(hammerHead, 2)
         .part(toughHandle)
         .part(largePlate, 1)
         .part(largePlate, 1).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 3f) // gains +5 undead damage from smite modifier
        .set(ToolStats.ATTACK_SPEED, 0.75f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.35f)
        .set(ToolStats.MINING_SPEED, 0.4f)
        .set(ToolStats.DURABILITY, 4f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.smite, 2).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG))
      .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
      .module(BoxAOEIterator.builder(1, 1, 0).addWidth(1).addHeight(1).build())
      .module(new ParticleWeaponAttack(TinkerTools.hammerAttackParticle.get()));

    define(ToolDefinitions.VEIN_HAMMER)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(hammerHead, 2)
         .part(toughHandle)
         .part(pickHead, 1)
         .part(largePlate).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 3f) // gains +1.25 damage from piercing
        .set(ToolStats.ATTACK_SPEED, 0.85f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.25f)
        .set(ToolStats.MINING_SPEED, 0.3f)
        .set(ToolStats.DURABILITY, 5.0f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.pierce, 2).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG))
      .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
      .module(new VeiningAOEIterator(2))
      .module(new ParticleWeaponAttack(TinkerTools.hammerAttackParticle.get()));


    // shovels
    define(ToolDefinitions.MATTOCK)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(smallAxeHead)
         .part(toolHandle)
         .part(roundPlate).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.5f)
        .set(ToolStats.ATTACK_SPEED, 0.9f).build()))
      .smallToolStartingSlots()
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.DURABILITY, 1.25f)
        .set(ToolStats.MINING_SPEED, 1.1f)
        .set(ToolStats.ATTACK_DAMAGE, 1.1f).build()))
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.tilling).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.AXE_DIG, ToolActions.SHOVEL_DIG))
      .module(IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_MATTOCK))
      // 200% hand speed on any axe block we do not directly target
      .module(new MiningSpeedModifierModule(2f, BlockPredicate.and(BlockPredicate.tag(BlockTags.MINEABLE_WITH_AXE), BlockPredicate.tag(TinkerTags.Blocks.MINABLE_WITH_MATTOCK).inverted())))
      .module(new VeiningAOEIterator(0));

    define(ToolDefinitions.PICKADZE)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(pickHead)
         .part(toolHandle)
         .part(roundPlate).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 0.5f)
        .set(ToolStats.ATTACK_SPEED, 1.3f).build()))
      .smallToolStartingSlots()
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.DURABILITY, 1.3f)
        .set(ToolStats.MINING_SPEED, 0.75f)
        .set(ToolStats.ATTACK_DAMAGE, 1.15f).build()))
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.pathing).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG))
      .module(IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_PICKADZE))
      .module(new MaxTierHarvestLogic(Tiers.GOLD))
      .module(BoxAOEIterator.builder(0, 0, 0).addHeight(1).build());

    define(ToolDefinitions.EXCAVATOR)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(largePlate)
         .part(toughHandle)
         .part(largePlate)
         .part(toughHandle).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.5f)
        .set(ToolStats.ATTACK_SPEED, 1.0f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.2f)
        .set(ToolStats.MINING_SPEED, 0.3f)
        .set(ToolStats.DURABILITY, 3.75f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder()
        .trait(TinkerModifiers.knockback, 2)
        .trait(ModifierIds.pathing).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.SHOVEL_DIG))
      .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_SHOVEL))
      .module(new ParticleWeaponAttack(TinkerTools.bonkAttackParticle.get()))
      .module(BoxAOEIterator.builder(1, 1, 0).addWidth(1).addHeight(1).build());


    // axes
    define(ToolDefinitions.HAND_AXE)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(smallAxeHead)
         .part(toolHandle)
         .part(toolBinding).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 6.0f)
        .set(ToolStats.ATTACK_SPEED, 0.9f).build()))
      .smallToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.stripping).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.AXE_DIG, TinkerToolActions.SHIELD_DISABLE))
      .module(IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_HAND_AXE))
      .module(new CircleAOEIterator(1, false))
      .module(new ParticleWeaponAttack(TinkerTools.axeAttackParticle.get()));

    define(ToolDefinitions.BROAD_AXE)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(broadAxeHead, 2)
         .part(toughHandle)
         .part(pickHead, 1)
         .part(toolBinding).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 5f)
        .set(ToolStats.ATTACK_SPEED, 0.6f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.65f)
        .set(ToolStats.MINING_SPEED, 0.3f)
        .set(ToolStats.DURABILITY, 4.25f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierIds.stripping).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.AXE_DIG, TinkerToolActions.SHIELD_DISABLE))
      .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_AXE))
      .module(new ConditionalAOEIterator(
        BlockPredicate.tag(TinkerTags.Blocks.TREE_LOGS), new TreeAOEIterator(0, 0),
        BoxAOEIterator.builder(0, 5, 0).addWidth(1).addDepth(1).direction(IBoxExpansion.HEIGHT).build()))
      .module(new ParticleWeaponAttack(TinkerTools.axeAttackParticle.get()));

    // scythes
    ToolModule[] scytheHarvest = {
      IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_SCYTHE),
      MiningSpeedModifierModule.tag(BlockTags.WOOL, 0.3f),
      MiningSpeedModifierModule.blocks(0.10f, Blocks.VINE, Blocks.GLOW_LICHEN),
    };
    define(ToolDefinitions.KAMA)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(smallBlade)
         .part(toolHandle)
         .part(toolBinding).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1f)
        .set(ToolStats.ATTACK_SPEED, 1.6f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 0.5f).build()))
      .smallToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder()
        .trait(ModifierIds.tilling)
        .trait(TinkerModifiers.shears)
        .trait(TinkerModifiers.harvest).build())
      // harvest
      .module(ToolActionsModule.of(ToolActions.HOE_DIG))
      .module(scytheHarvest)
      .module(new CircleAOEIterator(1, true))
      .module(new CircleWeaponAttack(1));

    define(ToolDefinitions.SCYTHE)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(TinkerToolParts.broadBlade)
         .part(TinkerToolParts.toughHandle)
         .part(TinkerToolParts.toolBinding)
         .part(TinkerToolParts.toughHandle).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1f)
        .set(ToolStats.ATTACK_SPEED, 0.7f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.MINING_SPEED, 0.45f)
        .set(ToolStats.DURABILITY, 2.5f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder()
        .trait(ModifierIds.tilling)
        .trait(TinkerModifiers.aoeSilkyShears)
        .trait(TinkerModifiers.harvest).build())
      // behavior
      .module(scytheHarvest)
      .module(BoxAOEIterator.builder(1, 1, 2).addExpansion(1, 1, 0).addDepth(2).build())
      .module(new CircleWeaponAttack(2));


    // swords
    define(ToolDefinitions.DAGGER)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(smallBlade)
         .part(toolHandle).build())
      .module(DefaultMaterialsModule.builder().material(tier1Material, tier1Material).build())
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 3f)
        .set(ToolStats.ATTACK_SPEED, 2.0f)
        .set(ToolStats.BLOCK_AMOUNT, 10)
        .set(ToolStats.USE_ITEM_SPEED, 1.0f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 0.65f)
        .set(ToolStats.MINING_SPEED, 0.75f)
        .set(ToolStats.DURABILITY, 0.75f).build()))
      .smallToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder()
        .trait(TinkerModifiers.padded, 1)
        .trait(TinkerModifiers.offhandAttack)
        .trait(TinkerModifiers.silkyShears).build())
      // behavior
      .module(ToolActionsModule.of(ToolActions.SWORD_DIG, ToolActions.HOE_DIG))
      .module(IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_DAGGER))
      .module(MiningSpeedModifierModule.blocks(7.5f, Blocks.COBWEB));

    ToolModule[] swordHarvest = {
      IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_SWORD),
      MiningSpeedModifierModule.blocks(7.5f, Blocks.COBWEB),
      MiningSpeedModifierModule.blocks(100f, Blocks.BAMBOO, Blocks.BAMBOO_SAPLING)
    };
    define(ToolDefinitions.SWORD)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(smallBlade)
         .part(toolHandle)
         .part(toolHandle).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 3f)
        .set(ToolStats.ATTACK_SPEED, 1.6f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.MINING_SPEED, 0.5f)
        .set(ToolStats.DURABILITY, 1.1f).build()))
      .smallToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder().trait(TinkerModifiers.silkyShears).build())
      .module(ToolActionsModule.of(ToolActions.SWORD_DIG))
      // behavior
      .module(swordHarvest)
      .module(new SweepWeaponAttack(1));

    define(ToolDefinitions.CLEAVER)
      // parts
      .module(PartStatsModule.meleeHarvest()
         .part(broadBlade)
         .part(toughHandle)
         .part(toughHandle)
         .part(largePlate).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 3f)
        .set(ToolStats.ATTACK_SPEED, 1.0f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 1.5f)
        .set(ToolStats.MINING_SPEED, 0.25f)
        .set(ToolStats.DURABILITY, 3.5f).build()))
      .largeToolStartingSlots()
      // traits
      .module(ToolTraitsModule.builder()
        .trait(TinkerModifiers.severing, 2)
        .trait(TinkerModifiers.aoeSilkyShears).build())
      // behavior
      .module(ToolActionsModule.of(ToolActions.SWORD_DIG))
      .module(swordHarvest)
      .module(new SweepWeaponAttack(2));

    // bows
    define(ToolDefinitions.CROSSBOW)
      // parts
      .module(PartStatsModule.ranged()
         .part(bowLimb)
         .part(bowGrip)
         .part(bowstring).build())
      .module(defaultThreeParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.ATTACK_DAMAGE, 0f)
        .set(ToolStats.ATTACK_SPEED, 1.0f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.DURABILITY, 2f).build()))
      .smallToolStartingSlots();
    define(ToolDefinitions.LONGBOW)
      // parts
      .module(PartStatsModule.ranged()
         .part(bowLimb)
         .part(bowLimb)
         .part(bowGrip)
         .part(bowstring).build())
      .module(defaultFourParts)
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 120)
        .set(ToolStats.ATTACK_DAMAGE, 0f)
        .set(ToolStats.ATTACK_SPEED, 1.0f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.DURABILITY, 1.5f).build())) // gets effectively 2x durability from having 2 heads
      .largeToolStartingSlots();

    // special
    define(ToolDefinitions.FLINT_AND_BRICK)
      // stats
      .module(new SetStatsModule(StatsNBT.builder().set(ToolStats.DURABILITY, 100).build()))
      .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 1)))
      // traits
      .module(ToolTraitsModule.builder()
        .trait(TinkerModifiers.firestarter)
        .trait(TinkerModifiers.fiery)
        .trait(ModifierIds.scorching).build())
      // repair
      .module(new MaterialRepairModule(MaterialIds.searedStone, HeadMaterialStats.ID))
      .module(new MaterialRepairModule(MaterialIds.scorchedStone, HeadMaterialStats.ID));
    // staff
    MaterialRepairModule staffRepair = new MaterialRepairModule(MaterialIds.slimewood, LimbMaterialStats.ID);
    define(ToolDefinitions.SKY_STAFF)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 375)
        .set(ToolStats.BLOCK_AMOUNT, 15)
        .set(ToolStats.USE_ITEM_SPEED, 0.4f)
        .set(ToolStats.VELOCITY, 0.8f)
        .set(ToolStats.DRAW_SPEED, 1.25f).build()))
      .module(ToolSlotsModule.builder()
        .slots(SlotType.UPGRADE, 5)
        .slots(SlotType.ABILITY, 2).build())
      .module(ToolTraitsModule.builder().trait(ModifierIds.overslimeFriend).build())
      .module(staffRepair)
      .module(new CircleAOEIterator(1, false))
      .module(DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.EARTH_STAFF)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 800)
        .set(ToolStats.BLOCK_AMOUNT, 35)
        .set(ToolStats.USE_ITEM_SPEED, 0.4f)
        .set(ToolStats.PROJECTILE_DAMAGE, 1.5f)
        .set(ToolStats.ACCURACY, 0.9f).build()))
      .module(ToolSlotsModule.builder()
        .slots(SlotType.UPGRADE, 2)
        .slots(SlotType.DEFENSE, 3)
        .slots(SlotType.ABILITY, 2).build())
      .module(ToolTraitsModule.builder().trait(ModifierIds.overslimeFriend).build())
      .module(staffRepair)
      .module(new CircleAOEIterator(1, false))
      .module(DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.ICHOR_STAFF)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 1225)
        .set(ToolStats.BLOCK_AMOUNT, 15)
        .set(ToolStats.USE_ITEM_SPEED, 0.4f)
        .set(ToolStats.VELOCITY, 1.2f)
        .set(ToolStats.DRAW_SPEED, 0.75f).build()))
      .module(ToolSlotsModule.builder()
        .slots(SlotType.UPGRADE, 2)
        .slots(SlotType.ABILITY, 3).build())
      .module(ToolTraitsModule.builder().trait(ModifierIds.overslimeFriend).build())
      .module(staffRepair)
      .module(new CircleAOEIterator(1, false))
      .module(DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.ENDER_STAFF)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 1520)
        .set(ToolStats.BLOCK_AMOUNT, 15)
        .set(ToolStats.BLOCK_ANGLE, 140)
        .set(ToolStats.USE_ITEM_SPEED, 0.4f)
        .set(ToolStats.PROJECTILE_DAMAGE, 3f)
        .set(ToolStats.ACCURACY, 0.5f).build()))
      .module(ToolSlotsModule.builder()
        .slots(SlotType.UPGRADE, 3)
        .slots(SlotType.DEFENSE, 1)
        .slots(SlotType.ABILITY, 2).build())
      .module(ToolTraitsModule.builder()
        .trait(ModifierIds.overslimeFriend)
        .trait(ModifierIds.reach, 2).build())
      .module(staffRepair)
      .module(new CircleAOEIterator(1, false))
      .module(DualOptionInteraction.INSTANCE);


    // travelers armor
    ToolModule[] travelersModules = {
      ToolSlotsModule.builder()
                     .slots(SlotType.UPGRADE, 3)
                     .slots(SlotType.DEFENSE, 2)
                     .slots(SlotType.ABILITY, 1).build(),
      new MaterialRepairModule(MaterialIds.copper, HeadMaterialStats.ID)
    };
    defineArmor(ArmorDefinitions.TRAVELERS)
      .module(slots -> SetStatsModule.armor(slots)
        .durabilityFactor(10)
        .setEach(ToolStats.ARMOR, 1, 4, 5, 1))
      .module(ArmorSlotType.CHESTPLATE, new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.55f).build()))
      .module(travelersModules)
      .module(new MaterialRepairModule(MaterialIds.leather, RepairKitStats.ID))
      .module(ArmorSlotType.BOOTS, ToolTraitsModule.builder().trait(ModifierIds.snowBoots).build());
    define(ArmorDefinitions.TRAVELERS_SHIELD)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 200)
        .set(ToolStats.BLOCK_AMOUNT, 10)
        .set(ToolStats.BLOCK_ANGLE, 90)
        .set(ToolStats.USE_ITEM_SPEED, 0.8f).build()))
      .module(travelersModules)
      .module(new MaterialRepairModule(MaterialIds.wood, HeadMaterialStats.ID))
      .module(ToolTraitsModule.builder().trait(TinkerModifiers.blocking).build())
      .module(new PreferenceSetInteraction(InteractionSource.RIGHT_CLICK, new SingleModifierPredicate(TinkerModifiers.blocking.getId())));

    // plate armor
    ToolModule[] plateModules = {
      ToolSlotsModule.builder()
                     .slots(SlotType.UPGRADE, 1)
                     .slots(SlotType.DEFENSE, 4)
                     .slots(SlotType.ABILITY, 1).build(),
      new MaterialRepairModule(MaterialIds.cobalt, HeadMaterialStats.ID)
    };
    defineArmor(ArmorDefinitions.PLATE)
      .module(slots -> SetStatsModule.armor(slots)
        .durabilityFactor(30)
        .setEach(ToolStats.ARMOR, 2, 5, 7, 2)
        .setAll(ToolStats.ARMOR_TOUGHNESS, 2)
        .setAll(ToolStats.KNOCKBACK_RESISTANCE, 0.1f))
      .module(ArmorSlotType.CHESTPLATE, new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build()))
      .module(plateModules);
    define(ArmorDefinitions.PLATE_SHIELD)
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 500)
        .set(ToolStats.BLOCK_AMOUNT, 100)
        .set(ToolStats.BLOCK_ANGLE, 180)
        .set(ToolStats.ARMOR_TOUGHNESS, 2).build()))
      .module(plateModules)
      .module(ToolTraitsModule.builder().trait(TinkerModifiers.blocking).build())
      .module(new PreferenceSetInteraction(InteractionSource.RIGHT_CLICK, new SingleModifierPredicate(TinkerModifiers.blocking.getId())));

    // slime suit
    ToolModule[] slimeModules = {
      ToolSlotsModule.builder()
                     .slots(SlotType.UPGRADE, 5)
                     .slots(SlotType.ABILITY, 1).build(),
      new MaterialRepairModule(MaterialIds.enderslime, RepairKitStats.ID)
    };
    ToolTraitsModule.Builder slimeTraits = ToolTraitsModule.builder().trait(ModifierIds.overslimeFriend);
    defineArmor(ArmorDefinitions.SLIMESUIT)
      // not using durabilityFactor as helmet stats give a bonus too
      .module(slots -> SetStatsModule.armor(slots)
         .setEach(ToolStats.DURABILITY, 546, 630, 672, 362))
      .module(ArmorSlotType.CHESTPLATE, new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build()))
      .module(slimeModules)
      // repair
      .module(ArmorSlotType.CHESTPLATE, new MaterialRepairModule(MaterialIds.phantom, RepairKitStats.ID))
      .module(ArmorSlotType.LEGGINGS, new MaterialRepairModule(MaterialIds.chorus, HeadMaterialStats.ID))
      .module(ArmorSlotType.BOOTS, new MaterialRepairModule(MaterialIds.leather, RepairKitStats.ID))
      // stats
      .module(ArmorSlotType.HELMET, MaterialStatsModule.stats(MaterialStatProviders.SKULL).stat(SkullStats.ID, 1).build())
      .module(ArmorSlotType.HELMET, DefaultMaterialsModule.builder().material(RandomMaterial.random().build()).build())
      .module(ArmorSlotType.HELMET, slimeTraits.build())
      // traits
      .module(ArmorSlotType.CHESTPLATE, slimeTraits.copy().trait(ModifierIds.wings).build())
      .module(ArmorSlotType.LEGGINGS, slimeTraits.copy()
        .trait(ModifierIds.pockets, 1)
        .trait(TinkerModifiers.shulking, 1).build())
      .module(ArmorSlotType.BOOTS, slimeTraits.copy()
        .trait(TinkerModifiers.bouncy)
        .trait(TinkerModifiers.leaping, 1).build());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Definition Data Generator";
  }
}
