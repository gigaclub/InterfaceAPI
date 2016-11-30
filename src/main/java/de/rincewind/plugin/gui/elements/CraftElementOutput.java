package de.rincewind.plugin.gui.elements;

import org.bukkit.inventory.ItemStack;

import de.rincewind.api.gui.components.Modifyable;
import de.rincewind.api.gui.elements.ElementOutput;
import de.rincewind.api.gui.elements.util.ClickAction;
import de.rincewind.api.handling.events.ElementInteractEvent;
import de.rincewind.api.handling.events.OutputConsumeEvent;
import de.rincewind.plugin.gui.elements.abstracts.CraftElementSlot;
import lib.securebit.Validate;

public class CraftElementOutput extends CraftElementSlot implements ElementOutput {
	
	private boolean empty;
	
	public CraftElementOutput(Modifyable handle) {
		super(handle);
		
		this.empty = true;
		
		this.getBlocker().unlock();
		this.getBlocker().addAction(ClickAction.PLACE);
		
		this.getEventManager().registerListener(ElementInteractEvent.class, (event) -> {
			if (!this.empty && this.isEmpty()) {
				this.empty = true;
				this.getEventManager().callEvent(OutputConsumeEvent.class, new OutputConsumeEvent(this, event.getPlayer()));
			}
		}).addAfter();
	}
	
	@Override
	public void output(ItemStack item) {
		this.output(item, false);
	}
	
	@Override
	public void output(ItemStack item, boolean flag) {
		Validate.notNull(item, "The item cannot be null!");
		
		if (!flag && !this.isEmpty()){
			return;
		} else {
			this.setContent(item);
			this.empty = false;
		}
	}
	
}
