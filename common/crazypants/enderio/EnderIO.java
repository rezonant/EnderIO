package crazypants.enderio;

import com.google.common.eventbus.Subscribe;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitRecipes;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.me.ItemMeConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderfaceRecipes;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.item.ItemRecipes;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.machine.MachineRecipes;
import crazypants.enderio.machine.RedstoneModePacketProcessor;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.monitor.ItemMJReader;
import crazypants.enderio.machine.painter.BlockCustomFence;
import crazypants.enderio.machine.painter.BlockCustomFenceGate;
import crazypants.enderio.machine.painter.BlockCustomSlab;
import crazypants.enderio.machine.painter.BlockCustomStair;
import crazypants.enderio.machine.painter.BlockCustomWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MaterialRecipes;
import crazypants.enderio.teleport.BlockTravelAnchor;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.TeleportRecipes;
import crazypants.enderio.trigger.TriggerEnderIO;
import crazypants.enderio.trigger.TriggerProviderEIO;
import crazypants.enderio.waila.WailaRegistration;

@Mod(name = "EnderIO", modid = "EnderIO", version = "1.0.2", dependencies = "required-after:Forge@[9.11.0.883,)")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { "EnderIO" }, packetHandler = PacketHandler.class)
public class EnderIO {

  @Instance("EnderIO")
  public static EnderIO instance;

  @SidedProxy(clientSide = "crazypants.enderio.ClientProxy", serverSide = "crazypants.enderio.CommonProxy")
  public static CommonProxy proxy;

  public static GuiHandler guiHandler = new GuiHandler();

  // Materials
  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static BlockFusedQuartz blockFusedQuartz;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;

  // Enderface
  public static BlockEnderIO blockEnderIo;
  public static ItemEnderface itemEnderface;

  //Teleporting
  public static BlockTravelAnchor blockTravelPlatform;
  public static ItemTravelStaff itemTravelStaff;

  // Painter
  public static BlockPainter blockPainter;
  public static BlockCustomFence blockCustomFence;
  public static BlockCustomFenceGate blockCustomFenceGate;
  public static BlockCustomWall blockCustomWall;
  public static BlockCustomStair blockCustomStair;
  public static BlockCustomSlab blockCustomSlab;
  public static BlockCustomSlab blockCustomDoubleSlab;

  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;
  public static ItemConduitFacade itemConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;
  public static ItemMeConduit itemMeConduit;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockCrusher blockCrusher;
  public static BlockHyperCube blockHyperCube;
  public static BlockPowerMonitor blockPowerMonitor;

  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  public static ItemYetaWrench itemYetaWench;
  public static ItemMJReader itemMJReader;

  public static ITrigger triggerNoEnergy;
  public static ITrigger triggerHasEnergy;
  public static ITrigger triggerFullEnergy;
  public static ITrigger triggerIsCharging;
  public static ITrigger triggerFinishedCharging;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);

    ConduitGeometryUtil.setupBounds((float) Config.conduitScale);

    itemBasicCapacitor = ItemCapacitor.create();
    itemAlloy = ItemAlloy.create();
    blockFusedQuartz = BlockFusedQuartz.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();
    itemMachinePart = ItemMachinePart.create();
    itemPowderIngot = ItemPowderIngot.create();
    itemMaterial = ItemMaterial.create();

    blockEnderIo = BlockEnderIO.create();
    itemEnderface = ItemEnderface.create();

    blockTravelPlatform = BlockTravelAnchor.create();
    itemTravelStaff = ItemTravelStaff.create();

    blockHyperCube = BlockHyperCube.create();

    blockPainter = BlockPainter.create();
    blockCustomFence = BlockCustomFence.create();
    blockCustomFenceGate = BlockCustomFenceGate.create();
    blockCustomWall = BlockCustomWall.create();
    blockCustomStair = BlockCustomStair.create();
    blockCustomSlab = new BlockCustomSlab(false);
    blockCustomDoubleSlab = new BlockCustomSlab(true);
    blockCustomSlab.init();
    blockCustomDoubleSlab.init();

    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();
    blockReservoir = BlockReservoir.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapacitorBank = BlockCapacitorBank.create();
    blockCrusher = BlockCrusher.create();
    blockPowerMonitor = BlockPowerMonitor.create();

    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    itemConduitFacade = ItemConduitFacade.create();

    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();
    itemMeConduit = ItemMeConduit.create();

    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();

    itemYetaWench = ItemYetaWrench.create();
    itemMJReader = ItemMJReader.create();

    MaterialRecipes.registerOresInDictionary();

  }

  @EventHandler
  public void load(FMLInitializationEvent event) {

    instance = this;

    NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
    MinecraftForge.EVENT_BUS.register(this);

    //Register Custom Dungeon Loot here
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal()), 1, 3, 60));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST)
        .addItem(new WeightedRandomChestContent(new ItemStack(EnderIO.itemYetaWench, 1, 0), 1, 1, 15));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(EnderIO.itemMJReader, 1, 0), 1, 1, 1));

    ItemStack staff = new ItemStack(EnderIO.itemTravelStaff, 1, 0);
    itemTravelStaff.setFull(staff);
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 30));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Item.netherQuartz), 3, 16, 40));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Item.netherStalkSeeds), 1, 4, 30));
    ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Item.enderPearl), 1, 2, 30));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal()), 5, 20, 50));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal()), 3, 14, 35));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal()), 2, 6, 20));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
        new WeightedRandomChestContent(new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal()), 2, 6, 10));
    ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(staff, 1, 1, 5));
    ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 20));

    PacketHandler.instance.addPacketProcessor(new RedstoneModePacketProcessor());

    EnderfaceRecipes.addRecipes();
    MaterialRecipes.addRecipes();
    ConduitRecipes.addRecipes();
    MachineRecipes.addRecipes();
    ItemRecipes.addRecipes();
    TeleportRecipes.addRecipes();

    triggerNoEnergy = new TriggerEnderIO("enderIO.trigger.noEnergy", 0);
    triggerHasEnergy = new TriggerEnderIO("enderIO.trigger.hasEnergy", 1);
    triggerFullEnergy = new TriggerEnderIO("enderIO.trigger.fullEnergy", 2);
    triggerIsCharging = new TriggerEnderIO("enderIO.trigger.isCharging", 3);
    triggerFinishedCharging = new TriggerEnderIO("enderIO.trigger.finishedCharging", 4);

    ActionManager.registerTriggerProvider(new TriggerProviderEIO());
    
    proxy.load();
    
    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.waila.WailaRegistration.register");
  }
  
  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    MaterialRecipes.registerExternalOresInDictionary();
    CrusherRecipeManager.getInstance().loadRecipesFromConfig();
    AlloyRecipeManager.getInstance().loadRecipesFromConfig();
    MaterialRecipes.addOreDictionaryRecipes();
    MachineRecipes.addOreDictionaryRecipes();
    ConduitRecipes.addOreDictionaryRecipes();


  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    HyperCubeRegister.load();
    FMLInterModComms.sendMessage("Waila", "register", "crazypants.enderio.waila.WailaProvider");
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    HyperCubeRegister.unload();
  }
}
