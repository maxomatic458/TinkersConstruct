package slimeknights.tconstruct.library.tools.definition.module.build;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/** Module that allows a tool to perform tool actions */
public record ToolActionsModule(Set<ToolAction> actions) implements ToolActionToolHook, ToolModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierHookProvider.<ToolActionsModule>defaultHooks(ToolHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionsModule> LOADER = RecordLoadable.create(Loadables.TOOL_ACTION.set().requiredField("tool_actions", ToolActionsModule::actions), ToolActionsModule::new);

  public static ToolActionsModule of(ToolAction... actions) {
    return new ToolActionsModule(ImmutableSet.copyOf(actions));
  }

  @Override
  public RecordLoadable<ToolActionsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ToolAction toolAction) {
    return actions.contains(toolAction);
  }
}
