package org.xmlcml.cif;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.log4j.Logger;

/**
 * default implementation of base class for all CIF instance components in a
 * CIFDocument.
 * 
 */

public abstract class CIFElement extends Element implements CIFConstants {

    @SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(CIFElement.class);
    /** allowed attributes in XML representation.*/
    public enum CIFAttribute {
        /** item name.*/
        NAME("name"),
        /** item name.*/
        NAMES("names"),
        /** data type (numb or char).*/
        DATATYPE("dataType"), 
        /** standard uncertainty.*/
        SU("su"),
        /** numeric value.*/
        NUMERICVALUE("numericValue"),
        /** numeric value.*/
        VALUES("values"),
        ;
        /** value.*/
        public String value;
        private CIFAttribute(String v) {
            value = v;
        }
    }

    /** allowed dataTypes.
     */
    public enum DataType {
        /** numeric.*/
        NUMB("numb"),
        /** character.*/
        CHAR("char"),
        ;
        /** the string value.*/
        public String value;
        private DataType(String v) {
            value = v;
        }
        /** gets data type by value.
         * 
         * @param s representation of type
         * @return type or null
         */
        public static DataType getDataType(String s) {
            DataType dataType = null;
            if (s != null) {
                for (DataType dt : DataType.values()) {
                    if (dt.value.equals(s)) {
                        dataType = dt;
                        break;
                    }
                }
            }
            return dataType;
        }
    }
    

	/**
	 * create element with arbitrary name. should not normally be called outside
	 * the package (use constructors in dataBlock, etc.
	 * 
	 * @param tagName
	 */
	protected CIFElement(String tagName) {
		super(tagName);
	}
	
	protected CIFElement(CIFElement element) {
		super(element.getLocalName());
		copyAttributes(element);
		copyChildren(element);
	}

	/**
	 * processes the SU of the value. only relevant for CIFItem and CIFLoop
	 * (otherwise passes to children) if the TextValue is of form 1.234(12)
	 * calculates numeric Value and SU and sets attributes if valid
	 * 
	 * @param failOnError
	 *            abort if not of numeric form else no-op
	 * 
	 */
	public void processSu(boolean failOnError) {
		Elements elements = this.getChildElements();
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof CIFElement) {
				CIFElement element = (CIFElement) elements.get(i);
				try {
					element.processSu(failOnError);
				} catch (RuntimeException e) {
					throw new RuntimeException("child " + i + " ("
							+ element.getLocalName() + ") : " + e.getMessage());
				}
			}
		}
	}

	/**
	 * copies attributes. makes subclass if necessary.
	 * 
	 * @param element
	 *            to copy from
	 * @param allowed
	 * @param failOnError
	 */
	public void copyAttributes(Element element) {
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute att = element.getAttribute(i);
			Attribute newAtt = (Attribute) att.copy();
			this.addAttribute(newAtt);
		}
	}

	/**
	 * copies attributes. makes subclass if necessary.
	 * 
	 * @param element
	 *            to copy from
	 * @param allowed
	 * @param failOnError
	 */
	public void copyAttributes(Element element, String[] allowed,
			boolean failOnError) {
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute att = element.getAttribute(i);
			if (failOnError
					&& CIFUtil.indexOf(att.getLocalName(), allowed, false) == -1) {
				throw new RuntimeException("attribute: " + att.getLocalName()
						+ " is not allowed in element: "
						+ element.getLocalName());
			}
			Attribute newAtt = (Attribute) att.copy();
			this.addAttribute(newAtt);
		}
	}

	/**
	 * copies children. make subclasses when required
	 * 
	 * @param element
	 *            to copy from
	 * @param allowed
	 * @param failOnError
	 *            schema error
	 * @throws CIFException
	 */
	public void copyChildren(Element element) {
		for (int i = 0; i < element.getChildCount(); i++) {
			Node newChild = this.createSubclassedNode(element.getChild(i));
			this.appendChild(newChild);
		}
	}

	/**
	 * copies children. make subclasses when required
	 * 
	 * @param element
	 *            to copy from
	 * @param allowed
	 * @param failOnError
	 *            schema error
	 * @throws CIFException
	 */
	public void copyChildren(Element element, String[] allowed,
			boolean failOnError) throws CIFException {
		for (int i = 0; i < element.getChildCount(); i++) {
			Node newChild = this.createSubclassedNode(element.getChild(i),
					allowed, failOnError);
			this.appendChild(newChild);
		}
	}

	/** create node of correct class.
	 * 
	 * @param childNode
	 * @param allowed
	 * @param failOnError
	 * @return the node
	 * @throws CIFException
	 */
	private Node createSubclassedNode(Node childNode) {
		Node newNode = null;
		if (childNode instanceof Text) {
			newNode = new Text(childNode.getValue());
		} else if (childNode instanceof Comment) {
			newNode = new Comment(childNode.getValue());
		} else if (childNode instanceof ProcessingInstruction) {
		} else if (childNode instanceof Element) {
			Element child = (Element) childNode;
			String childName = child.getLocalName();
			if (childName.equals(CIF.TAG)) {
				newNode = new CIF((CIF)child);
			} else if (childName.equals(CIFComment.TAG)) {
				newNode = new CIFComment(child.getValue());
			} else if (childName.equals(CIFDataBlock.TAG)) {
				newNode = new CIFDataBlock((CIFDataBlock)child);
			} else if (childName.equals(CIFItem.TAG)) {
				newNode = new CIFItem((CIFItem)child);
			} else if (childName.equals(CIFLoop.TAG)) {
				newNode = new CIFLoop((CIFLoop)child);
			} else if (childName.equals(CIFRow.TAG)) {
				newNode = new CIFRow((CIFRow)child);
			} else if (childName.equals(CIFSaveFrame.TAG)) {
				// newNode = new CIFSaveFrame(child);
			} else if (childName.equals(CIFTableCell.TAG)) {
				newNode = new CIFTableCell((CIFTableCell)child);
			} else if (child instanceof CIFElement) {
				CIFUtil.BUG("Unsupported CIFElement: " + child);
			} else {
				CIFUtil.BUG("Unsupported element: " + childNode);
			}
		}
		return newNode;
	}


	/** create node of correct class.
	 * 
	 * @param childNode
	 * @param allowed
	 * @param failOnError
	 * @return the node
	 * @throws CIFException
	 */
	private Node createSubclassedNode(Node childNode, String[] allowed,
			boolean failOnError) throws CIFException {
		Node newNode = null;
		if (childNode instanceof Text) {
			newNode = new Text(childNode.getValue());
		} else if (childNode instanceof Comment) {
			newNode = new Comment(childNode.getValue());
		} else if (childNode instanceof ProcessingInstruction) {
		} else if (childNode instanceof Element) {
			Element child = (Element) childNode;
			if (failOnError
					&& CIFUtil.indexOf(child.getLocalName(), allowed, false) == -1) {
				throw new RuntimeException(child.getLocalName()
						+ " is not allowed as child of: " + this.getLocalName());
			}
			String childName = child.getLocalName();
			if (childName.equals(CIF.TAG)) {
				newNode = new CIF(child, failOnError);
			} else if (childName.equals(CIFComment.TAG)) {
				newNode = new CIFComment(child.getValue());
			} else if (childName.equals(CIFDataBlock.TAG)) {
				newNode = new CIFDataBlock(child, failOnError);
			} else if (childName.equals(CIFItem.TAG)) {
				newNode = new CIFItem(child, failOnError);
			} else if (childName.equals(CIFLoop.TAG)) {
				newNode = new CIFLoop(child, failOnError);
			} else if (childName.equals(CIFRow.TAG)) {
				newNode = new CIFRow(child, failOnError);
			} else if (childName.equals(CIFSaveFrame.TAG)) {
				// newNode = new CIFSaveFrame(child);
			} else if (childName.equals(CIFTableCell.TAG)) {
				newNode = new CIFTableCell(child, failOnError);
			} else if (child instanceof CIFElement) {
				CIFUtil.BUG("Unsupported CIFElement: " + child);
			} else {
				CIFUtil.BUG("Unsupported element: " + childNode);
			}
		}
		return newNode;
	}

	void validateName(String name) {
		if (!this.getLocalName().equals(name)) {
			throw new RuntimeException("element name must be: " + name);
		}
	}

	/**
	 * write to XML.
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeXML(Writer w) throws IOException {
		final String xmlStr = toXML();
		if (xmlStr != null) {
			w.write(xmlStr);
		}
	}

	/**
	 * write to HTML.
	 * 
	 * normally overridden; default is to output node name
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeHTML(Writer w) throws IOException {
		w.write("<h3>Unsupported: " + getQualifiedName() + "</h3>\n");
	}

	/**
	 * normalize if possible.
	 * 
	 * normally overridden
	 */
	public void normalize() {
	}

	/**
	 * canonicalize if possible.
	 * 
	 * normally overridden
	 */
	public void canonicalize() {
	}

	/**
	 * debugging utility. writes contents as XML.
	 */
	public void debug() {
		System.out.println(toXML());
	}

	/** outputs the CIFElement as a CIF string.
	 * uses writeCIF(StringWriter) for each element.
	 * @return as cif string
	 */
	public String toCIFString() {
        StringWriter w = new StringWriter();
        try {
            writeCIF(w);
            w.close();
        } catch (Exception e) {
            CIFUtil.BUG(e);
        }
		return w.toString();
	}

	/**
	 * writes element as CIF.
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeCIF(Writer w) throws IOException {
		Elements childElements = this.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element child = childElements.get(i);
			if (child instanceof CIFElement) {
				((CIFElement) child).writeCIF(w);
			} else {
				throw new RuntimeException("Unexpected element "
						+ child.getLocalName());
			}
		}
	}

}
