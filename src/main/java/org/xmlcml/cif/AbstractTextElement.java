package org.xmlcml.cif;

import nu.xom.Node;
import nu.xom.Text;

/**
 * provides support for comments and value elements (cell and item).
 */
public abstract class AbstractTextElement extends CIFElement {

	/**
	 * create text element.
	 * 
	 * @param name
	 */
	public AbstractTextElement(String name) {
		super(name);
	}

	/** copy constructor.
	 * 
	 * @param abstractTextElement
	 */
	public AbstractTextElement(AbstractTextElement abstractTextElement) {
		super(abstractTextElement);
	}
	
	protected void setTextValue(String s) {
		Text text = this.getText();
		if (text == null) {
            if (this.getChildCount() == 0) {
    			text = new Text(s);
    			this.appendChild(text);
    		}
        }
        if (text != null) {
            text.setValue(s);
        }
	}

	private Text getText() {
		Text text = null;
		Node child = (this.getChildCount() > 0) ? this.getChild(0) : null;
		if (child != null) {
			if (child instanceof Text) {
				text = (Text) child;
			}
		}
		return text;
	}

	/**
	 * get value.
	 * 
	 * @return value or null
	 */
//	public String getValue() {
//		Text text = this.getText();
//		return (text == null) ? null : text.getValue();
//	}
}
