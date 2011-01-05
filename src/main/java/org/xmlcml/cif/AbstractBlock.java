package org.xmlcml.cif;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * default implementation of CIFBlock.
 */
public abstract class AbstractBlock extends CIFElement implements Comparable<AbstractBlock> {

	/**
	 * tag.
	 */
	public final static String TAG = "block";
	static Logger logger = Logger.getLogger(AbstractBlock.class.getName());
	private List<CIFItem> itemList;
	private List<CIFLoop> loopList;

	protected AbstractBlock() {
		super(TAG);
	}

	protected AbstractBlock(String s) {
		super(s);
	}

	protected AbstractBlock(String elementName, String id) throws CIFException {
		super(elementName);
		setId(id);
	}

	protected AbstractBlock(AbstractBlock abstractBlock) {
		super(abstractBlock);
		this.itemList = new ArrayList<CIFItem>();
		for (CIFItem item : itemList) {
			this.itemList.add(new CIFItem(item));
		}
		this.loopList = new ArrayList<CIFLoop>();
		for (CIFLoop loop : loopList) {
			this.loopList.add(new CIFLoop(loop));
		}
	}
	
	/**
	 * add comment.
	 * 
	 * @param comment
	 * @throws CIFException
	 */
	public void add(CIFComment comment) throws CIFException {

		try {
			this.appendChild(comment);
		} catch (Exception e) {
			CIFUtil.BUG(comment.getDocument().toString() + S_SLASH
					+ this.getDocument());
		}
	}

	/**
	 * get block id. normally the data_id
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.getAttributeValue(ATTRIBUTE_ID);
	}

	/**
	 * adds item, checking for duplicates.
	 * 
	 * @param item
	 * @param checkDuplicates
	 * @return message
	 * @throws RuntimeException
	 *             for duplicate items and checkDuplicates
	 */
	public ParserMessage add(CIFItem item, boolean checkDuplicates) {
		String itemName = item.getName().toLowerCase();
		ParserMessage m = null;
		if (this.getChildItem(itemName) != null) {
			if (checkDuplicates) {
				m = new ParserMessage("Duplicate item name: " + itemName);
//				throw new RuntimeException(m.getMessage());
				System.err.println(m.getMessage());
			}
		}
		if (item.getParent() != null) {
			item.detach();
		}
		super.appendChild(item);
		return m;
	}

	/**
	 * add loop. uses checkDuplicates = true.
	 * 
	 * @param newLoop
	 * @throws CIFException
	 */
	public void add(CIFLoop newLoop) throws CIFException {
		add(newLoop, true);
	}

	/**
	 * add loop.
	 * 
	 * @param newLoop
	 * @param checkDuplicates
	 *            if true throws error for duplicate loop
	 * @throws CIFException
	 */
	public void add(CIFLoop newLoop, boolean checkDuplicates)
	throws CIFException {
		if (checkDuplicates) {
			List<String> newNames = newLoop.getNameList();
			List<CIFLoop> loopList = this.getLoopList();
			for (CIFLoop loop : loopList) {
				List<String> names = loop.getNameList();
				for (String name : names) {
					for (String newName : newNames) {
						if (newName.equalsIgnoreCase(name)) {
							throw new CIFException(
									"Duplicate column name in loop: " + name);
						}
					}
				}
			}
		} else {
			; //
		}
		super.appendChild(newLoop);
	}

	/**
	 * get list of items.
	 * 
	 * @return list of items (empty if none)
	 */
	public List<CIFItem> getItemList() {
		itemList = new ArrayList<CIFItem>();
		Elements elements = this.getChildElements(CIFItem.TAG);
		for (int i = 0; i < elements.size(); i++) {
			itemList.add((CIFItem) elements.get(i));
		}
		return itemList;
	}

	/** sort items by name.
	 */
	public void sortItemList() {
		getItemList();
		Collections.sort(itemList);
	}

	/**
	 * get list of loops.
	 * 
	 * @return list of loops (empty if none)
	 */
	public List<CIFLoop> getLoopList() {
		loopList = new ArrayList<CIFLoop>();
		Elements elements = this.getChildElements(CIFLoop.TAG);
		for (int i = 0; i < elements.size(); i++) {
			loopList.add((CIFLoop) elements.get(i));
		}
		return loopList;
	}

	/**
	 * get loop containing a given name. not necessarily the required name (key)
	 * as this depends on the dictionary
	 * 
	 * @param name
	 *            and in the nameList
	 * @return the loop or null
	 */
	public CIFLoop getLoopContainingName(String name) {
		CIFLoop loop = null;
		List<CIFLoop> loopList = this.getLoopList();
		for (CIFLoop loop0 : loopList) {
			List<String> nameList = loop0.getNameList();
			if (CIFUtil.isStringInList(name, nameList, true) != -1) {
				loop = loop0;
				break;
			}
		}
		return loop;
	}

	/**
	 * gets single Item child with given item name.
	 * 
	 * @param itemName
	 *            case-insensitive
	 * @return the item or null
	 */
	public CIFItem getChildItem(String itemName) {
		List<CIFItem> itemList = getItemList();
		CIFItem item0 = null;
		for (CIFItem item : itemList) {
			if (item.getName().equalsIgnoreCase(itemName)) {
				item0 = item;
				break;
			}
		}
		return item0;
	}

	/**
	 * gets single Item value with given item name.
	 * 
	 * @param itemName
	 *            (case-insensitive)
	 * @return value or null.
	 */
	public String getChildItemValue(String itemName) {
		CIFItem child = getChildItem(itemName);
		return (child == null) ? null : child.getValue();
	}

	/**
	 * gets single Item child with given name. gets child
	 * 
	 * @param itemName
	 *            (case-insensitive)
	 * @param value
	 *            (case-insensitive)
	 * @return the item or null
	 */
	public CIFItem getChildItem(String itemName, String value) {
		CIFItem child = getChildItem(itemName);
		String s = child.getValue();
		if ((s != null) && (s.equalsIgnoreCase(value))) {
			return child;
		} else {
			return null;
		}
	}

	/**
	 * gets loop including columnName. scans loop names for columnName.
	 * 
	 * @param columnName
	 *            (case-insensitive)
	 * @return the loop or null
	 */
	public CIFLoop getChildLoop(String columnName) {
		List<CIFLoop> loopList = this.getLoopList();
		CIFLoop theLoop = null;
		for (CIFLoop loop : loopList) {
			if (loop.getColumnNumber(columnName) != -1) {
				theLoop = loop;
			}
		}
		return theLoop;
	}

	/**
	 * sets the block id. checks for valid and invalid characters. I don't know
	 * what these are, so this may fail on some Ids. At least whitespace is
	 * forbidden
	 * 
	 * @param id
	 * @throws CIFException
	 */
	public void setId(String id) throws CIFException {
		if (id == null || id.length() == 0) {
			throw new CIFException("Bad id: " + id);
		}
		for (int i = 0; i < id.length(); i++) {
			char c = id.charAt(i);
			if (Character.isWhitespace(c)) {
				throw new CIFException("Whitespace (" + c + ") in id [" + id
						+ "]");
			} else if (!CIFUtil.isOrdinaryChar(c) && c != '_') {
				logger.severe("Changed bad character ("+(int)c+") in id ["+id+"] to '-'");
				c = '-';
			}
		}
		addAttribute(new Attribute(ATTRIBUTE_ID, id));
	}

	/**
	 * resets the block id.  Only used if there are duplicate blockIds in the CIF
	 * and they need to be renamed.
	 * 
	 * @param id
	 * @throws CIFException
	 */
	public void resetId(String id) throws CIFException {
		this.getAttribute(ATTRIBUTE_ID).detach();
		setId(id);
	}

	/**
	 * index the block.
	 * 
	 */
	/*--
	 public void index() throws CIFException {
	 itemMap.clear ();
	 for (CIFItem item: itemList) {
	 String itemName = item.getName().toLowerCase();
	 if (itemMap.get(itemName) != null) {
	 throw new CIFException("Duplicate item name: "+itemName);
	 }
	 itemMap.put(itemName, item);
	 }

	 loopMap.clear ();
	 logger.info(S_NL+"Loops in: "+id);
	 for (CIFLoop loop : loopList) {
	 List<String> nameList = loop.getNameList();
	 for (String name : nameList) {
	 name = name.toLowerCase();
	 if (loopMap.get(name) != null) {
	 throw new CIFException("Duplicate loop name: "+name+" in block: "+id);
	 }
	 logger.info("Indexing by: "+name);
	 loopMap.put(name, loop);
	 }
	 }
	 }
	 --*/

	/**
	 * write block as HTML.
	 * 
	 * @param w
	 *            write
	 * @exception IOException
	 */
	public void writeHTML(Writer w) throws IOException {
		w.write("<h3>");
		w.write(this.getId());
		w.write("</h3><br/><br/>\n");

		Elements childElements = getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element child = childElements.get(i);
			if (child instanceof CIFElement) {
				((CIFElement) child).writeHTML(w);
			} else {
				w.write("<!-- unknown: " + child.getLocalName() + "-->");
			}
		}
	}

	/**
	 * normalize.
	 * 
	 * normalize children and remove comments
	 * 
	 */
	public void normalize() {
		this.getItemList();
		for (CIFItem item : itemList) {
			item.normalize();
		}
		this.getLoopList();
		for (CIFLoop loop : loopList) {
			loop.normalize();
		}
	}

	/**
	 * canonicalize.
	 * 
	 * canonicalize children items are then sorted in increasing lexical order
	 * loops are then sorted in increasing lexical order
	 */
	public void canonicalize() {
		this.getItemList();
		for (CIFItem item : itemList) {
			item.canonicalize();
		}
		Collections.sort(itemList);

		this.getLoopList();
		for (CIFLoop loop : loopList) {
			loop.canonicalize();
		}
		Collections.sort(loopList);
	}

	/** compare blocks by Id
	 * case-insensitive
	 * @param block
	 * @return -1, 0 (if block == null), 1
	 */
	public int compareTo(AbstractBlock block) {
		return (block == null) ? 0 : this.getId().compareToIgnoreCase(block.getId());
	}

	/**
	 * writes a block. does NOT close writer
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeCIF(Writer w) throws IOException {
		w.write(S_DATA + this.getId()+S_NL);
		super.writeCIF(w);
	}
}
