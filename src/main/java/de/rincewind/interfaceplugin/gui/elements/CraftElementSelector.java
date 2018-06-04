package de.rincewind.interfaceplugin.gui.elements;

import java.util.Objects;
import java.util.function.UnaryOperator;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import de.rincewind.interfaceapi.gui.components.Displayable;
import de.rincewind.interfaceapi.gui.elements.ElementSelector;
import de.rincewind.interfaceapi.gui.elements.abstracts.Element;
import de.rincewind.interfaceapi.gui.elements.util.Icon;
import de.rincewind.interfaceapi.gui.elements.util.Point;
import de.rincewind.interfaceapi.gui.windows.abstracts.WindowEditor;
import de.rincewind.interfaceapi.handling.element.ElementInteractEvent;
import de.rincewind.interfaceapi.handling.element.ItemSelectEvent;
import de.rincewind.interfaceplugin.gui.elements.abstracts.CraftElementDisplayable;

public class CraftElementSelector extends CraftElementDisplayable implements ElementSelector {

	public static String INSTRUCTIONS_SELECT = "§7§lLK: §7§oItem auf Courser auswählen";
	public static String INSTRUCTIONS_UNSELECT = "§7§lLK: §7§oAusgwähltes Item abwählen";
	public static String INSTRUCTIONS_COLLECT = "§7§lMK: §7§oAusgewähltes Item aufsammeln";

	public static UnaryOperator<ItemStack> cloneFunction = (item) -> {
		return item.clone();
	};

	private boolean canUnselect;
	private boolean canCollect;
	private boolean copyAmount;

	private ItemStack selected;

	public CraftElementSelector(WindowEditor handle) {
		super(handle);

		this.canUnselect = true;
		this.canCollect = true;
		this.selected = null;

		this.getComponent(Element.INSTRUCTIONS).setEnabled(true);

		this.setIcon(new Icon(Material.FISHING_ROD, 0, "§eWähle ein Item aus"));

		this.getEventManager().registerListener(ElementInteractEvent.class, (event) -> {
			if (event.getClickType() == ClickType.LEFT) {
				if (event.getCourserItem() != null) {
					this.setSelected(event.getCourserItem());
				} else if (this.canUnselect) {
					this.setSelected(null);
				}
			} else if (event.getClickType() == ClickType.MIDDLE) {
				if (this.canCollect && this.selected != null && event.getCourserItem() == null) {
					event.setCourserItem(CraftElementSelector.cloneFunction.apply(this.selected));
				}
			}
		}).monitor();
	}

	@Override
	public void canUnselect(boolean value) {
		if (this.canUnselect != value) {
			this.canUnselect = value;
			this.updateInstructions(this.getDisplayableEnabled());
		}
	}

	@Override
	public void copyAmount(boolean value) {
		this.copyAmount = value;
	}

	@Override
	public void canCollect(boolean value) {
		if (this.canCollect != value) {
			this.canCollect = value;
			this.updateInstructions(this.getDisplayableEnabled());
		}
	}

	@Override
	public void setSelected(ItemStack item) {
		this.setSelected(item, true);
	}

	@Override
	public void setSelected(ItemStack item, boolean fireEvent) {
		if (Objects.equals(this.selected, item)) {
			return;
		}

		if (item != null) {
			item = item.clone();

			if (!this.copyAmount) {
				item.setAmount(1);
			}
		}

		this.selected = item;
		this.update();

		if (fireEvent) {
			this.getEventManager().callEvent(ItemSelectEvent.class, new ItemSelectEvent(this));
		}
	}

	@Override
	public boolean canUnselect() {
		return this.canUnselect;
	}

	@Override
	public boolean copyAmount() {
		return this.copyAmount;
	}

	@Override
	public boolean canCollect() {
		return this.canCollect;
	}

	@Override
	public boolean isItemSelected() {
		return this.selected != null;
	}

	@Override
	public ItemStack getSelected() {
		return this.selected;
	}

	@Override
	public void setIcon(Displayable item) {
		if (this.showInstructions()) {
			if (this.getDisplayableEnabled().hasStaticIcon()
					&& this.hasInstructionsSet(this.getDisplayableEnabled(), CraftElementSelector.INSTRUCTIONS_SELECT)) {

				this.getDisplayableEnabled().getIcon().getLore().setEnd(null);
			}

			if (item.hasStaticIcon()) {
				item.getIcon().getLore().setEnd(CraftElementSelector.INSTRUCTIONS_SELECT);
			}
		}

		super.setIcon(item);
	}

	@Override
	protected Icon getIcon0(Point point) {
		if (this.isEnabled()) {
			if (this.selected != null) {
				return new Icon(this.selected); // TODO Append Instructions
			} else if (this.showInstructions() && !this.getDisplayableEnabled().hasStaticIcon()) {
				Icon icon = this.getDisplayableEnabled().getIcon();
				icon.getLore().setEnd(CraftElementSelector.INSTRUCTIONS_SELECT);
				return icon;
			}
		}

		return super.getIcon0(point);
	}

	private void updateInstructions(Displayable display) {
		if (display.hasStaticIcon()) {
			if (this.selected != null) {
				display.getIcon().getLore().setEnd((this.canUnselect ? CraftElementSelector.INSTRUCTIONS_UNSELECT : "")
						+ (this.canCollect ? CraftElementSelector.INSTRUCTIONS_COLLECT : ""));
			} else {
				display.getIcon().getLore().setEnd(CraftElementSelector.INSTRUCTIONS_SELECT);
			}
		}
	}

}
