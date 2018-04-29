package de.rincewind.interfaceapi.elements.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rincewind.interfaceapi.gui.elements.util.Icon;
import de.rincewind.test.TestServer;
import junit.framework.Assert;

public class IconTest {
	
	@BeforeClass
	public static void initServer() {
		TestServer.setup();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullItemStack() {
		new Icon((ItemStack) null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAirItemStack() {
		new Icon(new ItemStack(Material.AIR));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAirMaterial() {
		new Icon(Material.AIR);
	}

	@Test
	public void testEqualsAir() {
		Assert.assertTrue(Icon.AIR.equals(Icon.AIR));
	}
	
	@Test
	public void testEqualsComplex() {
		Icon icon1 = new Icon(Material.STONE, 0, "Mein Stein");
		Icon icon2 = new Icon(Material.STONE, 0, "Mein Stein");
		
		Assert.assertTrue(icon1.equals(icon2));
	}
	
	@Test
	public void testImmutableAir() {
		Icon icon = Icon.AIR.count(2).rename("My new name").enchant();
		Assert.assertTrue(icon.toItem().equals(new ItemStack(Material.AIR)));
	}
	
}