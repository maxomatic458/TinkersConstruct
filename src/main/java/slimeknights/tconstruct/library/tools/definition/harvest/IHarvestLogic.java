package slimeknights.tconstruct.library.tools.definition.harvest;

import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Logic to determine the harvest speed of a block */
public interface IHarvestLogic extends IHaveLoader {
  /** Default harvest logic instance */
  IHarvestLogic DEFAULT = new IHarvestLogic() {
    @Override
    public boolean isEffective(IToolStackView tool, BlockState state) {
      return false;
    }

    @Override
    public IGenericLoader<? extends IHarvestLogic> getLoader() {
      throw new UnsupportedOperationException("Cannot serialize the default harvest logic");
    }
  };
  /** Harvest logic loader registry */
  GenericLoaderRegistry<IHarvestLogic> LOADER = new GenericLoaderRegistry<>("Harvest Logic", DEFAULT, false);

  /** Determines if the tool is effective against the given block */
  boolean isEffective(IToolStackView tool, BlockState state);

  /** Gets the destroy speed against the given block */
  default float getDestroySpeed(IToolStackView tool, BlockState state) {
    return isEffective(tool, state) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1.0f;
  }

  /** Gets the tier to display in tooltips, used for harvest logic that limits harvest tier */
  default Tier getTier(IToolStackView tool) {
    return tool.getStats().get(ToolStats.HARVEST_TIER);
  }
}
