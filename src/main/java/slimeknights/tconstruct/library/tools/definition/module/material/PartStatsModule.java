package slimeknights.tconstruct.library.tools.definition.module.material;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.MaterialStatProvider;
import slimeknights.tconstruct.library.tools.stat.MaterialStatProviders;

import java.util.List;
import java.util.function.Supplier;

/** Tool using tool parts for its material stats, allows part swapping and tool building */
public class PartStatsModule extends MaterialStatsModule implements ToolPartsHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierHookProvider.<PartStatsModule>defaultHooks(ToolHooks.TOOL_STATS, ToolHooks.TOOL_TRAITS, ToolHooks.TOOL_MATERIALS, ToolHooks.TOOL_PARTS, ToolHooks.MATERIAL_REPAIR);
  public static final RecordLoadable<PartStatsModule> LOADER = RecordLoadable.create(
    STAT_PROVIDER_FIELD,
    WeightedPart.LOADABLE.list(1).requiredField("parts", m -> m.parts),
    PartStatsModule::new).validate((module, error) -> {
    module.validate(error);
    return module;
  });

  /** Parts for serializing */
  @VisibleForTesting
  @Getter
  private final List<WeightedPart> parts;
  /*** Flattened parts for the hook */
  private final List<IToolPart> flatParts;

  public PartStatsModule(MaterialStatProvider statProvider, List<WeightedPart> parts) {
    super(statProvider, parts.stream().map(part -> new WeightedStatType(part.part.getStatType(), part.weight)).toList());
    this.parts = parts;
    this.flatParts = parts.stream().map(WeightedPart::part).toList();
  }

  @Override
  public RecordLoadable<PartStatsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public List<IToolPart> getParts(ToolDefinition definition) {
    return flatParts;
  }

  /** Stat with weights */
  @VisibleForTesting
  public record WeightedPart(IToolPart part, int weight) {
    public static final RecordLoadable<WeightedPart> LOADABLE = RecordLoadable.create(
      TinkerLoadables.TOOL_PART_ITEM.requiredField("item", WeightedPart::part),
      IntLoadable.FROM_ONE.defaultField("weight", 1, WeightedPart::weight),
      WeightedPart::new).compact(TinkerLoadables.TOOL_PART_ITEM.flatXmap(item -> new WeightedPart(item, 1), WeightedPart::part), s -> s.weight == 1);
  }


  /* Builder */

  public static Builder parts(MaterialStatProvider statProvider) {
    return new Builder(statProvider);
  }

  /** Starts a builder for melee harvest stats */
  public static Builder meleeHarvest() {
    return parts(MaterialStatProviders.MELEE_HARVEST);
  }

  /** Starts a builder for ranged stats */
  public static Builder ranged() {
    return parts(MaterialStatProviders.RANGED);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableList.Builder<WeightedPart> parts = ImmutableList.builder();
    private final MaterialStatProvider statProvider;

    /** Adds a part to the builder */
    public Builder part(IToolPart part, int weight) {
      parts.add(new WeightedPart(part, weight));
      return this;
    }

    /** Adds a part to the builder */
    public Builder part(Supplier<? extends IToolPart> part, int weight) {
      return part(part.get(), weight);
    }

    /** Adds a part to the builder */
    public Builder part(IToolPart part) {
      return part(part, 1);
    }

    /** Adds a part to the builder */
    public Builder part(Supplier<? extends IToolPart> part) {
      return part(part, 1);
    }

    /** Builds the module */
    public PartStatsModule build() {
      return new PartStatsModule(statProvider, parts.build());
    }
  }
}
