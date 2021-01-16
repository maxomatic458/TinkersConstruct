package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.DuctContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctTankWrapper;

import javax.annotation.Nullable;

/**
 * Filtered drain tile entity
 */
public class DuctTileEntity extends DrainTileEntity implements INamedContainerProvider {
  private static final String TAG_ITEM = "item";

  @Getter
  private final DuctItemHandler itemHandler = new DuctItemHandler(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);
  private static final ITextComponent TITLE = new TranslationTextComponent(Util.makeTranslationKey("gui", "duct"));

  public DuctTileEntity() {
    this(TinkerSmeltery.duct.get());
  }

  protected DuctTileEntity(TileEntityType<?> type) {
    super(type);
  }


  /* Container */

  @Override
  public ITextComponent getDisplayName() {
    return TITLE;
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inventory, PlayerEntity playerEntity) {
    return new DuctContainer(id, inventory, this);
  }


  /* Capability */

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  protected LazyOptional<IFluidHandler> makeWrapper(LazyOptional<IFluidHandler> capability) {
    return LazyOptional.of(() -> new DuctTankWrapper(capability.orElse(emptyInstance), itemHandler));
  }


  /* NBT */

  @Override
  public void read(BlockState state, CompoundNBT tags) {
    super.read(state, tags);
    if (tags.contains(TAG_ITEM, NBT.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
    return tags;
  }
}