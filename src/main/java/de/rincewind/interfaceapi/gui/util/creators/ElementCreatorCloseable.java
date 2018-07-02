package de.rincewind.interfaceapi.gui.util.creators;

import de.rincewind.interfaceapi.exceptions.ElementCreationException;
import de.rincewind.interfaceapi.gui.elements.ElementContentSlot;
import de.rincewind.interfaceapi.gui.elements.ElementCounter;
import de.rincewind.interfaceapi.gui.elements.ElementInput;
import de.rincewind.interfaceapi.gui.elements.ElementItem;
import de.rincewind.interfaceapi.gui.elements.ElementList;
import de.rincewind.interfaceapi.gui.elements.ElementMap;
import de.rincewind.interfaceapi.gui.elements.ElementMultiButton;
import de.rincewind.interfaceapi.gui.elements.ElementOutput;
import de.rincewind.interfaceapi.gui.elements.ElementSelector;
import de.rincewind.interfaceapi.gui.elements.ElementSwitcher;
import de.rincewind.interfaceapi.gui.elements.abstracts.Element;

public class ElementCreatorCloseable implements ElementCreator {

	private boolean closed;

	private final ElementCreator wrapper;

	public ElementCreatorCloseable(ElementCreator wrapper) {
		assert wrapper != null : "The wrapper is null";
		
		this.wrapper = wrapper;
	}

	@Override
	public <T extends Element> T newElement(Class<T> elementCls) {
		this.validateClosed();
		return this.wrapper.newElement(elementCls);
	}

	@Override
	public ElementCounter newCounter() {
		this.validateClosed();
		return this.wrapper.newCounter();
	}

	@Override
	public ElementInput newInput() {
		this.validateClosed();
		return this.wrapper.newInput();
	}

	@Override
	public ElementList newList() {
		this.validateClosed();
		return this.wrapper.newList();
	}

	@Override
	public ElementOutput newOutput() {
		this.validateClosed();
		return this.wrapper.newOutput();
	}

	@Override
	public ElementSwitcher newSwitcher() {
		this.validateClosed();
		return this.wrapper.newSwitcher();
	}

	@Override
	public ElementItem newItem() {
		this.validateClosed();
		return this.wrapper.newItem();
	}

	@Override
	public ElementMultiButton newMultiButton() {
		this.validateClosed();
		return this.wrapper.newMultiButton();
	}

	@Override
	public ElementSelector newSelector() {
		this.validateClosed();
		return this.wrapper.newSelector();
	}

	@Override
	public ElementMap newMap() {
		this.validateClosed();
		return this.wrapper.newMap();
	}

	@Override
	public ElementContentSlot newContentSlot() {
		this.validateClosed();
		return this.wrapper.newContentSlot();
	}

	public void close() {
		this.closed = true;
	}

	public boolean isClosed() {
		return this.closed;
	}

	private void validateClosed() {
		if (this.closed) {
			throw new ElementCreationException("The creator is already closed");
		}
	}

}