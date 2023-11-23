package de.plasticghoul;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ToolReaplacer.MODID)
public class ToolReaplacer
{
    public static final String MODID = "mctoolreplacer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ToolReaplacer() {
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onItemBreak(PlayerDestroyItemEvent itemEvent) {
		//Get item that broke
		Item brokenItem = itemEvent.getOriginal().getItem();
		LOGGER.info("A tool just broke: " + brokenItem.getName(null).toString());

		//Get player inventory to determine if there is another tool of the same kind that just broke
		Inventory playerInventory = itemEvent.getEntity().getInventory();
		
		//Determine if there is another tool of the same kind in the players inventory
		List<ItemStack> sameTools = playerInventory.items.stream().filter(x -> x.getItem().getClass() == brokenItem.getClass())
				.collect(Collectors.toList());
		if (sameTools != null && sameTools.size() > 0) {
			List<ItemStack> sameMatTool = sameTools.stream()
					.filter(x -> x.getDescriptionId() == brokenItem.getDescriptionId()).collect(Collectors.toList());
			ItemStack newTool = null;

			if (sameMatTool != null && sameMatTool.size() > 0) {
				newTool = sameMatTool.get(0);
			} else {
				newTool = sameTools.get(0);
			}

			if (newTool != null) {
				LOGGER.info("Found another tool in players inventory: " + newTool.getDisplayName().toString());
				
				try {
					//Don't 'move' item to toolbar, but add item of the same kind and remove one in the inventory
					playerInventory.add(playerInventory.selected, newTool);
					playerInventory.removeItem(newTool);

					LOGGER.info("Added new tool to the toolbar...");
				} catch (Exception e) {
					LOGGER.error("Error while moving new tool to toolbar:");
					LOGGER.error(e.getStackTrace().toString());
				}
			}
		}
	}
}
