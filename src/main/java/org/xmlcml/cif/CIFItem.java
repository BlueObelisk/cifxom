package org.xmlcml.cif;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/**
 * default implementation of an item.
 * 
 * The XML representation is of the form:
 * <item name="_foo" dataType="numb" su="0.12">23.34</item>
 */
public class CIFItem extends AbstractValueElement implements Comparable<CIFItem>  {

	String[] ALLOWED_ATTRIBUTES = new String[]{
			CIFAttribute.DATATYPE.value,
			CIFAttribute.NAME.value,
			CIFAttribute.NUMERICVALUE.value,
			CIFAttribute.SU.value,
	};
	/** errors and warning for CIFItem.
	 * @author pm286
	 */
	public enum Message {
		/** item has no name.*/
		NULL_NAME("null name for item"),
		/** item has no name.*/
		NO_UNDER("item name does not start with underscore: "),
		/** item has no name.*/
		NULL_VALUE("null value for item");
		/** string value.*/
		public String value;
		private Message(String v) {
			value = v;
		}
	}
	/**
	 * item tag.
	 * 
	 */
	public final static String TAG = "item";

	static Logger logger = Logger.getLogger(CIFItem.class.getName());

	/**
	 * create item.
	 */
	public CIFItem() {
		super(CIFItem.TAG);
	}

	/**
	 * create item in document context.
	 * 
	 * @param name of item
	 * @param value of item
	 */
	public CIFItem(String name, String value) {
		this();
		this.setName(name);
		this.setTextValue(value);
	}


	public CIFItem(CIFItem cifItem) {
		super(cifItem);
	}
	/**
	 * create item in document context.
	 * 
	 * @param name
	 *            of item
	 * @param value
	 *            of item
	 * @param su
	 *            of item
	 * @param nDecimal
	 *            number of significant figures
	 * @throws CIFException
	 *             syntax violation or ontology/dictionary violation
	 */
	public CIFItem(String name, double value, double su, int nDecimal)
	throws CIFException {
		this();
		setName(name);
		this.setValue(value, su, nDecimal);
	}

	/**
	 * create recursively from an XML element.
	 * 
	 * @param element
	 *            with name "cif"
	 * @param failOnError
	 *            fail if violates schema
	 */
	public CIFItem(Element element, boolean failOnError) {
		this();
		validateName(TAG);
		// must come before copyAttributes as this clears numeric stuff
		this.setTextValue(element.getValue());
		this.copyAttributes(element, ALLOWED_ATTRIBUTES, failOnError);
		this.setTextValueFromAttributes();
	}

	/**
	 * set the name for a data item. normally used when building a CIFDOM. data
	 * names should never be reset implementers may check the value of a name or
	 * whether it violates any CIF syntax or dictionary restrictions.
	 * 
	 * @param name (should be compliant with CIF syntax)
	 */
	public void setName(String name) {
		if (!name.startsWith("_") || name.length() < 2) {
			throw new RuntimeException(Message.NO_UNDER.value
					+ name);
		}
		addAttribute(new Attribute(CIFAttribute.NAME.value, name));
	}

	/**
	 * writes item to HTML.
	 * of form: "_foo&nbsp;<i>bar</i><br/>"
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeHTML(Writer w) throws IOException {

		StringBuffer sb = new StringBuffer();
		sb.append(this.getName());
		sb.append("&nbsp;<i>");
		String s = this.getValue();
		s = CIFUtil.translateCIF2ISO(s);
		s = CIFUtil.translateCIF2HTML(s);
		sb.append(s);
		sb.append("</i>");
		sb.append("<br/>\n");
		w.write(sb.toString());
	}

	/** normalize item name and value.
	 * 
	 * lowercase names and normalize values
	 * 
	 * tested in {@link org.xmlcml.cif.CIFItemTest#testNormalize()}.
	 */
	public void normalize() {
		String name = getName();
		setName(name.toLowerCase());
		setTextValue(CIFUtil.normalize(this.getValue()));
	}

	/**
	 * canonicalize.
	 * 
	 * normalize
	 */
	public void canonicalize() {
		normalize();
	}

	/** for sorting items
	 * compares case-insensitive names
	 * @param item to compare
	 * @return normal compare result (or 0 if item == null)
	 */
	public int compareTo(CIFItem item) {
		int result = 0;
		if (item == null) {
			// null item, return 0
		} else {
			result =  this.getName().toLowerCase().
			compareTo(item.getName().toLowerCase());
		}
		return result;
	}

	/**
	 * writes an item. does NOT close writer
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeCIF(Writer w) throws IOException {
		String name = this.getName();
		if (name == null) {
			throw new RuntimeException(Message.NULL_NAME.value);
		}
		String value = this.getValue();
		if (value.length() > 80) {
			value = makeLineLengthsMax80Chars(value, 0);
		}
		value = CIFUtil.getQuotedString(value);
		if (value == null) {
			throw new RuntimeException(Message.NULL_VALUE.value);
		}
		if (value.length() > 80 && !value.trim().startsWith(""+C_SEMI)) {
			throw new RuntimeException("CIF value > 80 characters: [" + value + "]");
		}
		String s = this.getName() + " " + value;
		if (s.length() > 80 && value.length() < 80) {
			w.write(this.getName()+S_NL);
			w.write(value+S_NL);
		} else {
			w.write(this.getName() + " " + value+S_NL);
		}
	}

	/**
	 * Goes through the provided String and adds new line characters
	 * so that the maximum line length is 80 characters.  Is recursive.
	 * 
	 * @param value - the String to be altered.
	 * @param startChar - the starting position of the iteration 
	 * through the Strings chars.  This should always be called 
	 * initially with a value of 0 (zero).
	 * 
	 * @return String containing the contents of the supplied value, 
	 * but with newline chars added so that the maximum line length
	 * is 80 chars.
	 */
	public String makeLineLengthsMax80Chars(String value, int lastNewline) {
		int lastWhitespace = lastNewline;
		for (int i = lastNewline; i < value.length(); i++) {
			if (Character.isWhitespace(value.charAt(i))) {
				if ((i - lastNewline) > 80) {
					value = CIFUtil.replaceChar(value, lastWhitespace, S_NL);
					lastNewline = lastWhitespace;
				} else {
					lastWhitespace = i;
				}
			}
		}
		return value;
	}

    @Override
    public CIFItem copy() {
        return new CIFItem(this);
    }
    
}
