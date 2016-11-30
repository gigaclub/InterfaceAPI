package de.rincewind.api.gui.elements.abstracts;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;

import de.rincewind.api.gui.components.EventBased;
import de.rincewind.api.gui.components.Modifyable;
import de.rincewind.api.gui.components.UserMemory;
import de.rincewind.api.gui.elements.util.ClickBlocker;
import de.rincewind.api.gui.elements.util.ElementComponent;
import de.rincewind.api.gui.elements.util.ElementComponentType;
import de.rincewind.api.gui.elements.util.Icon;
import de.rincewind.api.gui.elements.util.Point;
import de.rincewind.api.gui.windows.abstracts.WindowColorable;

/**
 * This is the basic element. All other elements extend from this class.
 * Although this class contains the methods {@link Element#getWidth()} and
 * {@link Element#getHeight()} you cannot change the size in this basic-element.
 * 
 * @author Rincewind34
 * @since 2.3.3
 * 
 * @see ElementDisplayable
 * @see ElementSlot
 */
public abstract interface Element extends EventBased, UserMemory {
	
	public static final ElementComponentType<Boolean> ENABLED = new ElementComponentType<>(boolean.class, "enabled");
	
	public static final ElementComponentType<Integer> WIDTH = new ElementComponentType<>(int.class, "width");
	
	public static final ElementComponentType<Integer> HEIGHT = new ElementComponentType<>(int.class, "height");
	
	/**
	 * Sets the position of this element. Before changing the position,
	 * {@link Modifyable#clearItemsFrom(Element)} will be called and
	 * {@link Modifyable#readItemsFrom(Element)} after changing the position.
	 * 
	 * If the x or y coordinate is negative or greater than the size of for example
	 * the window added this element to, this element may not be rendered.
	 * 
	 * @param point to set
	 * 
	 * @throws NullPointerException if the point is <code>null</code>
	 */
	public abstract void setPoint(Point point);
	
	public abstract void update();
	
	public abstract void priorize();
	
	/**
	 * Sets the {@link ClickBlocker} for this element. The blocker blocks some
	 * {@link InventoryAction}-s for example like pickup or drop from/to the slot.
	 * By the default all actions are canceled.
	 * 
	 * @param blocker to set
	 * 
	 * @throws NullPointerException if the blocker is null
	 */
	public abstract void setBlocker(ClickBlocker blocker);
	
	/**
	 * Sets the visibility of this element. If the visibility is set to
	 * <code>false</code>, the element will be replaced for example in the
	 * {@link WindowColorable} with the background of the window this element
	 * is added to.
	 * 
	 * @param visible hiding this element if the value is <code>false</code>.
	 */
	public abstract void setVisible(boolean visible);
	
	/**
	 * Returns <code>true</code> if this element is visible and 
	 * <code>false</code> if not.
	 * 
	 * @return <code>true</code> if this element is visible and 
	 * 			<code>false</code> if not
	 */
	public abstract boolean isVisible();
	
	public abstract boolean isEnabled();
	
	/**
	 * Returns the position of this element.
	 * 
	 * @return the position of this element
	 */
	public abstract Point getPoint();
	
	public abstract Icon getIcon(Point point);
	
	/**
	 * Returns the {@link ClickBlocker} set to this element.
	 * 
	 * @return the {@link ClickBlocker} set to this element
	 */
	public abstract ClickBlocker getBlocker();
	
	public abstract List<Point> getPoints();
	
	public abstract <T> void setComponentValue(ElementComponentType<T> type, T value);
	
	public abstract <T> T getComponentValue(ElementComponentType<T> type);
	
	public abstract <T> ElementComponent<T> getComponent(ElementComponentType<T> type);
	
	public default void iterate(Consumer<Point> action) {
		this.getPoints().forEach(action);
	}
	
	public default boolean isInside(Point point) {
		return this.getPoints().contains(point);
	}
	
}
