package de.rincewind.interfaceplugin.gui.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import org.bukkit.event.inventory.ClickType;

import de.rincewind.interfaceapi.gui.components.ActionItem;
import de.rincewind.interfaceapi.gui.components.Displayable;
import de.rincewind.interfaceapi.gui.components.DisplayableDisabled;
import de.rincewind.interfaceapi.gui.elements.ElementList;
import de.rincewind.interfaceapi.gui.elements.abstracts.Element;
import de.rincewind.interfaceapi.gui.elements.util.Icon;
import de.rincewind.interfaceapi.gui.elements.util.SelectModifiers;
import de.rincewind.interfaceapi.gui.util.Color;
import de.rincewind.interfaceapi.gui.util.Direction;
import de.rincewind.interfaceapi.gui.util.Point;
import de.rincewind.interfaceapi.gui.windows.abstracts.WindowEditor;
import de.rincewind.interfaceapi.handling.InterfaceListener;
import de.rincewind.interfaceapi.handling.element.CustomActionPerformEvent;
import de.rincewind.interfaceapi.handling.element.ElementInteractEvent;
import de.rincewind.interfaceapi.handling.element.ListChangeSelectEvent;
import de.rincewind.interfaceapi.util.InterfaceUtils;
import de.rincewind.interfaceplugin.Validate;
import de.rincewind.interfaceplugin.gui.elements.abstracts.CraftElement;

public class CraftElementList extends CraftElement implements ElementList {

	public static String INSTRUCTIONS_SELECT = InterfaceUtils.instructions(ClickType.LEFT, "Dieses Element auswählen");
	public static String INSTRUCTIONS_UNSELECT = InterfaceUtils.instructions(ClickType.LEFT, "Dieses Element abwählen");
	public static String INSTRUCTIONS_MULTISELECT_SET = InterfaceUtils.instructions(ClickType.RIGHT, "Multi-Selection setzen");
	public static String INSTRUCTIONS_MULTISELECT_UNSET = InterfaceUtils.instructions(ClickType.RIGHT, "Multi-Selection entfernen");

	private Color color;

	private int selected;
	private int startIndex;

	private Displayable disabledIcon;

	private Direction type;

	private List<Displayable> items;
	private UnaryOperator<Icon> modifier;

	public CraftElementList(WindowEditor handle) {
		super(handle);

		this.selected = -1;
		this.startIndex = 0;
		this.type = Direction.HORIZONTAL;
		this.color = Color.TRANSLUCENT;
		this.modifier = SelectModifiers.MAGENTA_GLASS;
		this.disabledIcon = DisplayableDisabled.default_icon;
		this.items = new ArrayList<>();

		this.getComponent(Element.ENABLED).setEnabled(true);
		this.getComponent(Element.WIDTH).setEnabled(true);
		this.getComponent(Element.HEIGHT).setEnabled(true);
		this.getComponent(Element.INSTRUCTIONS).setEnabled(true);

		this.getEventManager().registerListener(ElementInteractEvent.class, (event) -> {
			int index = event.getPoint().getCoord(this.type) + this.startIndex;

			if (index >= this.getSize()) {
				return;
			}

			if (event.getClickType() == ClickType.LEFT) {
				if (index == this.getSelectedIndex()) {
					this.deselect();
				} else if (index < this.getSize()) {
					this.select(index);
				}
			} else if (event.getClickType() == ClickType.SHIFT_LEFT) {
				Displayable item = this.items.get(index);

				if (item instanceof ActionItem) {
					((ActionItem) item).performCustomAction(event.getPlayer(), () -> {
						this.update();
						this.getEventManager().callEvent(CustomActionPerformEvent.class, new CustomActionPerformEvent(this, item));
					});
				}
			}
		}).addAfter();
	}

	@Override
	public void setDisabledIcon(Displayable icon) {
		this.disabledIcon = Displayable.checkNull(icon);

		if (!this.isEnabled()) {
			this.update();
		}
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setSelectModifyer(UnaryOperator<Icon> modifier) {
		Validate.notNull(modifier, "The modifier cannot be null!");

		this.modifier = modifier;
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public Icon getDisabledIcon() {
		return this.disabledIcon.getIcon();
	}

	@Override
	public void addItem(Displayable item) {
		Validate.notNull(item, "The item cannot be null!");

		this.items.add(item);
		this.update();
	}

	@Override
	public void addItem(int index, Displayable item) {
		Validate.notNull(item, "The item cannot be null!");

		this.items.add(index, item);

		if (index <= this.selected) {
			this.selected = this.selected + 1;
		}

		this.update();
	}

	@Override
	public <T extends Enum<?>> void addItems(Class<T> cls) {
		Validate.notNull(cls, "The class cannot be null");

		for (T enumInstance : cls.getEnumConstants()) {
			this.items.add(Displayable.of(enumInstance));
		}
	}

	@Override
	public void removeSelected() {
		if (this.selected == -1) {
			throw new IllegalStateException("No item selected");
		}

		this.items.remove(this.selected);
		this.deselect();
		this.update();
	}

	@Override
	public void removeItem(Displayable item) {
		Validate.notNull(item, "The item cannot be null");

		if (this.getSelectedItem() == item) {
			this.deselect();
		}

		this.items.remove(item);
		this.update();
	}

	@Override
	public void clear() {
		this.deselect();
		this.items.clear();
		this.update();
	}

	@Override
	public void setType(Direction type) {
		Validate.notNull(type, "The type cannot be null");

		this.type = type;
		this.update();
	}

	@Override
	public void setStartIndex(int index) {
		if (0 > index || index > this.getSize() - 1 || this.startIndex == index) {
			return;
		}

		this.startIndex = index;
		this.update();
	}

	@Override
	public void addScroler(Element btn, int value) {
		Validate.notNull(btn, "The button cannot be null");

		if (value == 0) {
			throw new RuntimeException("The value cannot be zero!");
		}

		btn.getEventManager().registerListener(ElementInteractEvent.class, this.new ActionHandler(value)).addAfter();
	}

	@Override
	public boolean isSelected() {
		return this.selected != -1;
	}

	@Override
	public boolean canSelect() {
		return this.getSize() > 0;
	}

	@Override
	public int getStartIndex() {
		return this.startIndex;
	}

	@Override
	public int getSelectedIndex() {
		return this.selected;
	}

	@Override
	public void selectLast() {
		this.select(this.items.size() - 1, true);
	}

	@Override
	public void select(Object item) {
		this.select(item, true);
	}

	@Override
	public void select(Object item, boolean fireEvent) {
		int index = this.items.indexOf(item);

		if (index != -1) {
			this.select(this.items.indexOf(item), fireEvent);
		}
	}

	@Override
	public void select(int index) {
		this.select(index, true);
	}

	@Override
	public void select(int index, boolean fireEvent) {
		if (this.selected == index) {
			return;
		}

		if (index < -1 || index >= this.items.size()) {
			throw new IllegalArgumentException("Index out of range: " + index);
		}

		this.selected = index;

		if (this.selected != -1) {
			if (this.selected < this.startIndex) {
				this.startIndex = this.selected;
			} else if (this.selected >= this.startIndex + this.getBounds().getLength(this.type)) {
				this.startIndex = this.selected - (this.getBounds().getLength(this.type) - 1);
			}
		}

		if (fireEvent) {
			this.getEventManager().callEvent(ListChangeSelectEvent.class, new ListChangeSelectEvent(this, index));
		}

		this.update();
	}

	@Override
	public void deselect() {
		this.select(-1, true);
	}

	@Override
	public void deselect(boolean fireEvent) {
		this.select(-1, fireEvent);
	}

	@Override
	public Displayable getSelectedItem() {
		if (this.selected == -1) {
			return null;
		} else {
			return this.items.get(this.selected);
		}
	}

	@Override
	public Displayable getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public UnaryOperator<Icon> getSelectModifier() {
		return this.modifier;
	}

	@Override
	public <T> T getSelected() {
		if (this.selected == -1) {
			return null;
		} else {
			return Displayable.readPayload(this.items.get(this.selected));
		}
	}

	@Override
	public <T> T get(int index) {
		if (index < 0 || this.items.size() <= index) {
			throw new IllegalArgumentException("Invalid index");
		}

		return Displayable.readPayload(this.items.get(index));
	}

	@Override
	public List<Displayable> getItems() {
		return Collections.unmodifiableList(this.items);
	}

	@Override
	protected Icon getIcon0(Point point) {
		if (!this.isEnabled()) {
			return this.getDisabledIcon();
		}

		int index = this.startIndex + point.getCoord(this.type);

		if (index >= this.getSize()) {
			return this.color.asIcon();
		}

		Displayable item = this.items.get(index);
		Icon icon = item.getIcon();
		String instructions;

		if (index == this.selected) {
			instructions = CraftElementList.INSTRUCTIONS_UNSELECT;
			icon = this.modifier.apply(icon);
		} else {
			instructions = CraftElementList.INSTRUCTIONS_SELECT;
		}

		if (item instanceof ActionItem) {
			instructions = instructions + "\\n"
					+ InterfaceUtils.instructions(ClickType.SHIFT_LEFT, ((ActionItem) item).getCustomActionInstructions());
		}

		return this.updateInstructions(icon, instructions);
	}

	private class ActionHandler implements InterfaceListener<ElementInteractEvent> {

		private int value;

		private ActionHandler(int value) {
			this.value = value;
		}

		@Override
		public void onAction(ElementInteractEvent event) {
			if (event.getClickType().isLeftClick()) {
				CraftElementList.this.setStartIndex(CraftElementList.this.getStartIndex() + (this.value * (event.isShiftClick() ? 2 : 1)));
			} else if (event.getClickType() == ClickType.RIGHT) {
				int selected = CraftElementList.this.selected;

				if (selected != -1) {
					CraftElementList.this.setStartIndex(Math.max(0, selected - (CraftElementList.this.getWidth() / 2)));
				}
			}
		}

	}

	@Override
	public void setMultiSelectionAllowed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMultiSelectionBound(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMultiSelectionAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMultiSelectionBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UnaryOperator<Icon> getMultiSelectModifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> getMultiSelected() {
		// TODO Auto-generated method stub
		return null;
	}

}
