package crazypants.enderio.conduit.liquid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class ConduitTank implements IFluidTank {

  private FluidStack fluid;
  private int capacity;
  private int tankPressure = 0;

  ConduitTank(int capacity) {
    this.capacity = capacity;
  }

  @Override
  public int getFluidAmount() {
    return fluid == null ? 0 : fluid.amount;
  }

  public float getFilledRatio() {
    if (getFluidAmount() <= 0) {
      return 0;
    }
    if (getCapacity() <= 0) {
      return -1;
    }
    return (float) getFluidAmount() / getCapacity();
  }

  public boolean isFull() {
    return getFluidAmount() >= getCapacity();
  }

  public void setAmount(int amount) {
    if (fluid != null) {
      fluid.amount = amount;
    }
  }

  public int getAvailableSpace() {
    return getCapacity() - getFluidAmount();
  }

  public void addAmount(int amount) {
    setAmount(getFluidAmount() + amount);
  }

  @Override
  public FluidTankInfo getInfo() {
    return new FluidTankInfo(this);
  }

  @Override
  public FluidStack getFluid() {
    return this.fluid;
  }

  @Override
  public int getCapacity() {
    return this.capacity;
  }

  public void setLiquid(FluidStack liquid) {
    this.fluid = liquid;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
    if (getFluidAmount() > capacity) {
      setAmount(capacity);
    }
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    if (resource == null || resource.fluidID <= 0) {
      return 0;
    }

    if (fluid == null || fluid.fluidID <= 0) {
      if (resource.amount <= capacity) {
        if (doFill) {
          fluid = resource.copy();
        }
        return resource.amount;
      } else {
        if (doFill) {
          fluid = resource.copy();
          fluid.amount = capacity;
        }
        return capacity;
      }
    }

    if (!fluid.isFluidEqual(resource)) {
      return 0;
    }

    int space = capacity - fluid.amount;
    if (resource.amount <= space) {
      if (doFill) {
        fluid.amount += resource.amount;
      }
      return resource.amount;
    } else {
      if (doFill) {
        fluid.amount = capacity;
      }
      return space;
    }

  }

  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    if (fluid == null || fluid.fluidID <= 0) {
      return null;
    }
    if (fluid.amount <= 0) {
      return null;
    }

    int used = maxDrain;
    if (fluid.amount < used) {
      used = fluid.amount;
    }

    if (doDrain) {
      fluid.amount -= used;
    }

    FluidStack drained = new FluidStack(fluid.fluidID, used);

    if (fluid.amount < 0) {
      fluid.amount = 0;
    }
    return drained;
  }

  public void setTankPressure(int pressure) {
    this.tankPressure = pressure;
  }

  public String getLiquidName() {
    return fluid != null ? FluidRegistry.getFluidName(fluid) : null;
  }

  public boolean containsValidLiquid() {
    return fluid != null && FluidRegistry.getFluidName(fluid) != null;
  }

  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    if (containsValidLiquid()) {
      fluid.writeToNBT(nbt);
    } else {
      nbt.setString("emptyTank", "");
    }
    return nbt;
  }

  public IFluidTank readFromNBT(NBTTagCompound nbt) {
    if (!nbt.hasKey("emptyTank")) {
      FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbt);
      if (liquid != null) {
        setLiquid(liquid);
      }
    }
    return this;
  }

  public void remove(int amount) {
    if (fluid == null) {
      return;
    }
    fluid.amount -= amount;
    fluid.amount = Math.max(0, fluid.amount);
  }

}