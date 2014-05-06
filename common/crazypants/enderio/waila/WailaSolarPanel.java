package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.solar.TileEntitySolarPanel;
import crazypants.util.Lang;
import crazypants.util.TextColorUtil;
import crazypants.util.WailaUtil;

public class WailaSolarPanel extends WailaDataProvider {
	@Override
	public String getModuleName() {
		return "Energy";
	}

	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (!config.getConfig("enderio.official.enabled"))
			return null;
		
		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileEntitySolarPanel) {
			TileEntitySolarPanel panel = (TileEntitySolarPanel)te;

			float energyStored = panel.getLastCollectionValue();
			//System.out.println("Client end: "+energyStored);
			float energyMax = panel.getMaxEnergyPerTick();
			
			return TextColorUtil.DARK_GRAY+" ("+Math.round(energyStored/energyMax*100)+"% "+Lang.localize("gui.powerMonitor.of")+" "+
				PowerDisplayUtil.format(energyMax, true, true)+")"+TextColorUtil.GRAY;
			
		}
		
		return null;
	}
}
