package crazypants.util;

import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class WailaUtil {

	public static String WailaStyle     = "\u00A4";
	public static String WailaIcon      = "\u00A5";
	public static String TAB         = WailaStyle + WailaStyle +"a";
	public static String ALIGNRIGHT  = WailaStyle + WailaStyle +"b";
	public static String ALIGNCENTER = WailaStyle + WailaStyle +"c";	
	public static String HEART       = WailaStyle + WailaIcon  +"a";
	public static String HHEART      = WailaStyle + WailaIcon  +"b";
	public static String EHEART      = WailaStyle + WailaIcon  +"c";
	
	public static String formatColoredWailaValue(double value, boolean perTick)
	{
		String color = "";
		if (value == 0)
			color = TextColorUtil.GRAY;
		else if (value < 0)
			color = TextColorUtil.RED;
		else
			color = TextColorUtil.GREEN;
		
		return color+formatWailaValue(value, perTick)+TextColorUtil.GRAY;
	}
	
	public static String formatRedstoneStatus(AbstractMachineEntity te)
	{
		boolean redstoneCheckPassed = te.hasRedstoneCheckPassed();
		RedstoneControlMode rsMode = te.getRedstoneControlMode();

		return formatRedstoneStatus(rsMode, redstoneCheckPassed);
	}
	
	public static String formatRedstoneStatus(RedstoneControlMode rsMode, boolean redstoneCheckPassed) {
		String rsModeStr = null;
		String onStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.on");
		String offStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.off");
		String withSignalStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.withSignal");
		String withoutSignalStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.withoutSignal");
		
		if (rsMode == RedstoneControlMode.NEVER)
			rsModeStr = "Disabled";
		else if (rsMode == RedstoneControlMode.ON)
			rsModeStr = redstoneCheckPassed ? 
					TextColorUtil.GREEN_2+onStr+TextColorUtil.DARK_GRAY+" "+withSignalStr 
					: TextColorUtil.RED+offStr+TextColorUtil.DARK_GRAY+" "+withoutSignalStr;
		else if (rsMode == RedstoneControlMode.OFF)
			rsModeStr = redstoneCheckPassed ? 
					TextColorUtil.GREEN_2+onStr+TextColorUtil.DARK_GRAY+" "+withoutSignalStr
					: TextColorUtil.RED+offStr+TextColorUtil.DARK_GRAY+" "+withSignalStr;
		
		if (rsModeStr == null)
			return null;
		
		return TextColorUtil.DARK_GRAY+rsModeStr+TextColorUtil.GRAY;
	}

	public static String formatRedstoneStatus(TileCapacitorBank capBank) {
		String inStr = formatRedstoneStatus(capBank.getController().getInputControlMode(), capBank.getInputControlState());
		String outStr = formatRedstoneStatus(capBank.getController().getOutputControlMode(), capBank.getOutputControlState());
		
		if (inStr == null && outStr == null)
			return null;
		
		return
			(inStr != null? Lang.localize("gui.powerMonitor.in")+": "+inStr+"    ": "")+
			(outStr != null? Lang.localize("gui.powerMonitor.out")+": "+outStr : "");
	}
	
	public static String formatWailaValue(double value, boolean perTick)
	{
		return PowerDisplayUtil.formatPower(value)+PowerDisplayUtil.abrevation()
				+(perTick? PowerDisplayUtil.perTickStr() : "");
	}
	
	public static String getWailaModByLine(String module)
	{
		return TextColorUtil.DARK_BLUE+TextColorUtil.ITALIC+"Ender IO"+TextColorUtil.DARK_GRAY+" "+module;
	}
}