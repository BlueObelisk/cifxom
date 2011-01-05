package org.xmlcml.cif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Elements;

/**
 * default implementation of data block.
 */

public class CIFDataBlock extends AbstractBlock {

	/**
	 * tag.
	 * 
	 */
	public final static String TAG = "datablock";

	static Logger logger = Logger.getLogger(CIFDataBlock.class.getName());

	protected List<CIFSaveFrame> saveFrameList;
	protected Map<String, CIFSaveFrame> saveFrameMap;

	/**
	 * create DataBlock.
	 * 
	 * @param id
	 *            of dataBlock
	 * @throws CIFException
	 *             id has invalid syntax
	 */
	public CIFDataBlock(String id) throws CIFException {
		super(TAG, id);
		initDataBlock();
		setId(id);
	}

	private void initDataBlock() {
		saveFrameList = new ArrayList<CIFSaveFrame>();
		saveFrameMap = new HashMap<String, CIFSaveFrame>();
	}

	/**
	 * create recursively from an XML element.
	 * 
	 * @param element
	 *            with name "data"
	 * @param failOnError
	 *            fail if violates schema
	 * @throws CIFException
	 *             if this and descendants violate schema.
	 */
	public CIFDataBlock(Element element, boolean failOnError)
			throws CIFException {
		this(element.getAttributeValue(ATTRIBUTE_ID));
		validateName(TAG);
		copyAttributes(element, new String[] { ATTRIBUTE_ID }, failOnError);
		copyChildren(element, new String[] { CIFItem.TAG, CIFLoop.TAG,
				CIFComment.TAG }, failOnError);
	}
	
	public CIFDataBlock(CIFDataBlock dataBlock) {
		super(dataBlock);
		initDataBlock();
		
	}

	/**
	 * add a saveFrame.
	 * 
	 * @param saveFrame
	 *            to add
	 * @throws CIFException
	 */
	public void add(CIFSaveFrame saveFrame) throws CIFException {
		String saveId = saveFrame.getId();
		if (saveFrameMap.containsKey(saveId)) {
			throw new CIFException("Duplicate saveFrameID: " + saveId);
		}
		try {
			this.appendChild(saveFrame);
		} catch (Exception e) {
			throw new RuntimeException("CIFDataBlockImpl add bug: " + e);
		}
		saveFrameMap.put(saveId, saveFrame);
		saveFrameList.add(saveFrame);
	}

	/**
	 * get list of saveFrame children.
	 * 
	 * @return the child saveFrames
	 */
	public List<CIFSaveFrame> getSaveFrameList() {
		saveFrameList = new ArrayList<CIFSaveFrame>();
		Elements elements = this.getChildElements(CIFSaveFrame.TAG);
		for (int i = 0; i < elements.size(); i++) {
			saveFrameList.add((CIFSaveFrame) elements.get(i));
		}
		return saveFrameList;
	}

	/**
	 * get map of saveFrame children.
	 * 
	 * @return the map indexed by id
	 */
	public Map<String, CIFSaveFrame> getSaveFrameMap() {
		return saveFrameMap;
	}
}
