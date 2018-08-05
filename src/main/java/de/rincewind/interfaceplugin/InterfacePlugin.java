package de.rincewind.interfaceplugin;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import de.rincewind.interfaceapi.InterfaceAPI;
import de.rincewind.interfaceapi.gui.components.Displayable;
import de.rincewind.interfaceapi.gui.elements.util.Icon;
import de.rincewind.interfaceapi.util.InterfaceUtils;
import de.rincewind.interfaceplugin.listener.InventoryClickListener;
import de.rincewind.interfaceplugin.listener.InventoryCloseListener;
import de.rincewind.interfaceplugin.listener.PlayerQuitListener;

public class InterfacePlugin extends JavaPlugin {
	
	public static InterfacePlugin instance;
	
	static {
		Displayable.put(GameMode.class, InterfaceUtils::convertGameMode);
		Displayable.put(Environment.class, InterfaceUtils::convertEnvironment);
		Displayable.put(World.class, InterfaceUtils::convertWorld);
		Displayable.put(EntityType.class, InterfaceUtils::convertEntityType);
		Displayable.put(PotionEffectType.class, InterfaceUtils::convertPotionEffectType);
		Displayable.put(PotionType.class, InterfaceUtils::convertPotionType);
		Displayable.put(Material.class, (material) -> {
			return new Icon(material, "§7" + material.name());
		});
		Displayable.put(Boolean.class, (input) -> {
			return input ? new Icon(Material.GREEN_CONCRETE, "§aTrue") : new Icon(Material.RED_CONCRETE, "§cFalse");
		});
		
		Displayable.copy(Boolean.class, boolean.class);
	}
	
	@Override
	public void onEnable() {
		InterfacePlugin.instance = this;
		
		InterfaceAPI.enable();
		
		this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
	}
	
	@Override
	public void onDisable() {
		InterfaceAPI.disable();
	}
	
}