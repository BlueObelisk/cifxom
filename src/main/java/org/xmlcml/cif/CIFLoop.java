package org.xmlcml.cif;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

/**
 * A CIF Loop.
 * 
 * can access rows and columns of the loop. may also wrap each table element in
 * a cell tag
 */

public class CIFLoop extends CIFElement implements Comparable<CIFLoop> {

	/**
	 * loop tag.
	 * 
	 */
	public final static String TAG = "loop";

	static Logger logger = Logger.getLogger(CIFLoop.class.getName());

	/** keys on each column */
//	protected Map[] keyMap;

	private List<String> nameList;

	private List<CIFTableCell> tableCellList;

	/**
	 * create empty loop. do not use
	 */
	public CIFLoop() {
		super(TAG);
		logger.setLevel(Level.WARNING);
	}

	/** copy constructor.
	 * 
	 * @param cifLoop
	 */
	public CIFLoop(CIFLoop cifLoop) {
		super(cifLoop);
		this.nameList = new ArrayList<String>();
		for (String name : cifLoop.nameList) {
			this.nameList.add(name);
		}
		this.tableCellList = new ArrayList<CIFTableCell>();
		for (CIFTableCell tableCell : tableCellList) {
			this.tableCellList.add(tableCell);
		}
	}
	/**
	 * create from names and values.
	 * 
	 * @param names
	 * @param values
	 * @throws CIFException
	 *             names and values have incompatible counts
	 */
	public CIFLoop(List<String> names, List<String> values) throws CIFException {
		this();
		this.setNamesAndValues(names, values);
	}

	/**
	 * create recursively from an XML element.
	 * 
	 * @param element
	 *            with name "loop"
	 * @param failOnError
	 *            fail if violates schema
	 * @throws CIFException
	 *             if this and descendants violate schema.
	 */
	public CIFLoop(Element element, boolean failOnError) throws CIFException {
		this();
		validateName(TAG);
		copyAttributes(element, new String[] { 
				CIFAttribute.NAMES.value,
				CIFAttribute.VALUES.value
		}, failOnError);
		copyChildren(element, new String[] { CIFRow.TAG }, failOnError);
		int ncell = -1;
		Elements rows = this.getChildElements(CIFRow.TAG);
		for (int i = 0; i < rows.size(); i++) {
			Elements cells = rows.get(i).getChildElements(CIFTableCell.TAG);
			if (ncell == -1) {
				ncell = cells.size();
			} else if (ncell != cells.size()) {
				throw new CIFException("cells do not form rectangular table "
						+ ncell + S_SLASH + cells.size());
			}
		}
	}

	void setNamesAndValues(List<String> nameList, List<String> valueList)
	throws CIFException {
		setNames(nameList);
		logger.info("added loop names/values " + nameList.size() + S_SLASH
				+ valueList.size());
		setValues(valueList, nameList.size());
	}

	void setValues(List<String> valueList, int nameCount) throws CIFException {

		if (valueList.size() % nameCount != 0) {
			throw new CIFException("Number of values (" + valueList.size()
					+ ") not divisible by names (" + nameCount + ")");
		}
		int i = 0;
		this.clearRows();
		List<String> cellList = null;
		//        String rowVal = "";
		for (String value : valueList) {
			if (i++ % nameCount == 0) {
				if (cellList != null) {
					try {
						this.addRow(new CIFRow(cellList));
					} catch (CIFException e) {
						CIFUtil.BUG(e);
					}
				}
				cellList = new ArrayList<String>();
			}
			cellList.add(value);
		}
		this.addRow(new CIFRow(cellList));
	}

	private void addRow(CIFRow row) {
		this.appendChild(row);
	}

	/** removes all child rows.*/
	void clearRows() {
		Elements rows = this.getChildElements(CIFRow.TAG);
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).detach();
		}
	}

	void setNames(List<String> nameList) {
		this.addAttribute(new Attribute(CIFAttribute.NAMES.value, CIFUtil.concatenate(nameList)));
	}

	/**
	 * create from names and values.
	 * 
	 * @param names
	 * @param values
	 * @throws CIFException
	 *             names and values have incompatible counts
	 */
	public CIFLoop(String[] names, String[] values) throws CIFException {
		this();
		List<String> nameList = new ArrayList<String>();
		for (String name : names) {
			nameList.add(name);
		}
		List<String> valueList = new ArrayList<String>();
		for (String value : values) {
			valueList.add(value);
		}
		this.setNamesAndValues(nameList, valueList);
	}

	/**
	 * return list of names.
	 * 
	 * @return list of names
	 */
	public List<String> getNameList() {
		nameList = new ArrayList<String>();
		String names = this.getAttributeValue(CIFAttribute.NAMES.value);
		if (names != null) {
			
			List <String> strList = new ArrayList<String>() ;
			StringTokenizer st = new StringTokenizer(names);
		     while (st.hasMoreTokens()) {
		         strList.add(st.nextToken()) ;
		     }

			for (String name : strList) {
				nameList.add(name);
			}
		}
		return nameList;
	}

	/**
	 * get list of values.
	 * 
	 * @return COPIED list of values as strings (must be multiple of names)
	 */
	public List<String> getValueList() {
		List<String> valueList = new ArrayList<String>();
		Elements rows = this.getChildElements(CIFRow.TAG);
		for (int i = 0; i < rows.size(); i++) {
			CIFRow row = (CIFRow) rows.get(i);
			Elements cells = row.getChildElements(CIFTableCell.TAG);
			for (int j = 0; j < cells.size(); j++) {
				CIFTableCell cell = (CIFTableCell) cells.get(j);
				valueList.add(cell.getValue());
			}
		}
		return valueList;
	}

	/**
	 * get value for a given cell. if row is outside range (0-(nrows-1)) or col
	 * is outside range (0-(ncols-1)) return null
	 * 
	 * @param row
	 *            number (from 0)
	 * @param col
	 *            number (from 0)
	 * @return value
	 */
	public String getLoopValue(int row, int col) {
		String result = null;
		int ncols = this.getNameList().size();
		Elements rows = this.getChildElements(CIFRow.TAG);
		if (row >= 0 && row < rows.size() && col >= 0 || col < ncols) {
			Elements cells = rows.get(row).getChildElements(CIFTableCell.TAG);
			CIFTableCell cell = (CIFTableCell) cells.get(col);
			return cell.getValue();
		}
		return result;
	}

	/**
	 * get SU for a given cell. if row is outside range (0-(nrows-1)) or col is
	 * outside range (0-(ncols-1)) or no SU was given return null
	 * 
	 * @param row
	 *            number (from 0)
	 * @param col
	 *            number (from 0)
	 * @return SU
	 */

	public String getSU(int row, int col) {
		String result = "";
		// TODO
		/*--
		 if (row < 0 || row >= nrows || col < 0 || col >= ncols) {
		 result = null;
		 } else {
		 // +1 because of header
		 CIFElement rowNode = (CIFElement) getChild (row + 1);
		 result =  ((CIFElement) rowNode.getChild (col)).getAttributeValue (CIFConstants.ATTRIBUTE_NAME);
		 }
		 --*/
		CIFUtil.NYI();
		return result;
	}

	/**
	 * get column count.
	 * 
	 * @return 0 if none
	 */
	public int getColumnCount() {
		return getNameList().size();
	}

	/**
	 * get row count.
	 * 
	 * @return 0 if none
	 */
	public int getRowCount() {
		return this.getValueList().size() / getColumnCount();
	}

	/**
	 * get values for a given column. if columnName is unknown return null
	 * 
	 * @param columnName
	 * @return list of values or null
	 */
	public List<String> getColumnValues(String columnName) {
		return getColumnValues(getColumnNumber(columnName));
	}

	/**
	 * get values for a given column. if col is outside range (0-(ncols-1))
	 * return null
	 * 
	 * @param col number (from 0)
	 * @return list of values
	 */
	public List<String> getColumnValues(int col) {
		List<String> l = null;
		List<CIFTableCell> cellList = this.getColumnCells(col);
		if (cellList != null) {
			l = new ArrayList<String>();
			for (CIFTableCell cell : cellList) {
				l.add(cell.getValue());
			}
		}
		return l;
	}

	/**
	 * get numeric values for a given column. if col is outside range
	 * (0-(ncols-1)) return null if column is not numeric, return null
	 * 
	 * @param col
	 *            number (from 0)
	 * @return array of doubles
	 */
	public double[] getNumericColumnValues(int col) {
		double[] dd = null;
		// List<String> l = null;
		List<CIFTableCell> cellList = this.getColumnCells(col);
		int i = 0;
		if (cellList != null) {
			dd = new double[cellList.size()];
			for (CIFTableCell cell : cellList) {
				Double d = cell.getNumericValue();
				if (d == null) {
					dd = null;
					break;
				}
				dd[i++] = d.doubleValue();
			}
		}
		return dd;
	}

	/**
	 * get numeric values for a given column. if columnName is unknown return
	 * null
	 * 
	 * @param columnName
	 * @return array of values or null
	 */
	public double[] getNumericColumnValues(String columnName) {
		return getNumericColumnValues(getColumnNumber(columnName));
	}

	/**
	 * get cells for a given column. if col is outside range (0-(ncols-1))
	 * return null
	 * 
	 * @param col
	 *            number (from 0)
	 * @return list of values
	 */
	public List<CIFTableCell> getColumnCells(int col) {
		tableCellList = new ArrayList<CIFTableCell>();
		int ncols = getColumnCount();
		if (col < 0 || col >= ncols) {
			return null;
		}
		Elements rows = this.getChildElements(CIFRow.TAG);
		for (int i = 0; i < rows.size(); i++) {
			CIFRow row = (CIFRow) rows.get(i);
			CIFTableCell cell = (CIFTableCell) row.getChildElements(CIFTableCell.TAG).get(col);
			tableCellList.add(cell);
		}
		return tableCellList;
	}

	// checks for a rectangular table
	boolean checkRectangular() {
		return getValueList().size() % getNameList().size() == 0;
	}


	/**
	 * get number of column with name name (case-insensitive)
	 * 
	 * @param name -
	 *            the column name
	 * @return int number of column (starts at zero) -1 = not found
	 */
	public int getColumnNumber(String name) {
		int col = -1;
		String names = getAttributeValue(CIFAttribute.NAMES.value);
		if (names != null) {
			int i = 0;
			
			List <String> strList = new ArrayList<String>() ;
			StringTokenizer st = new StringTokenizer(names);
		     while (st.hasMoreTokens()) {
		         strList.add(st.nextToken()) ;
		     }

			for (String name0 : strList) {
				if (name0.equalsIgnoreCase(name)) {
					col = i;
					break;
				}
				i++;
			}
		}
		return col;
	}

	/**
	 * makes a subset and/or sorted Loop. The new loop preserves rows but has a
	 * (sub)set of the columns in order columns[] Values in columns should be
	 * within 0...columns-1. Duplication is not checked.
	 * 
	 * @param columns
	 *            serial numbers of columns
	 * @throws CIFException
	 * @return subLoop
	 */
	public CIFLoop selectColumns(int[] columns) throws CIFException {
		List<String> nameList = this.getNameList();
		List<String> newNameList = new ArrayList<String>();
		for (int j = 0; j < columns.length; j++) {
			int col = columns[j];
			if (col < 0 || col >= nameList.size()) {
				throw new CIFException("Bad column in selectColumns: " + col);
			}
			String newName = nameList.get(col);
			newNameList.add(newName);
		}
		int nrows = this.getRowCount();
		int ncols = nameList.size();
		List<String> valueList = this.getValueList();
		List<String> newValueList = new ArrayList<String>();
		for (int i = 0; i < nrows; i++) {
			for (int j = 0; j < columns.length; j++) {
				int col = columns[j];
				newValueList.add(valueList.get(i * ncols + col));
			}
		}
		CIFLoop newLoop = new CIFLoop(newNameList, newValueList);
		return newLoop;
	}

	@SuppressWarnings("unused")
	private void addName(String name) throws CIFException {
		if (name == null) {
			throw new RuntimeException("cannot add null name");
		}
		String names = this.getAttributeValue(CIFAttribute.NAME.value);
		if (names == null) {
			names = name;
		} else if (names.contains(name)) {
			throw new CIFException("already contains name: " + name);
		}
	}

	/**
	 * makes a subset and/or sorted Loop. The new loop preserves columns but has
	 * a (sub)set of the rows in order rows[]. Values in rows should be within
	 * 0...nrows-1. Duplication is not checked
	 * 
	 * @param rows
	 *            serial numbers of rows
	 * @throws CIFException
	 * @return subLoop
	 */
	public CIFLoop selectRows(int[] rows) throws CIFException {
		List<String> valueList = this.getValueList();
		List<String> newValueList = new ArrayList<String>();
		int nrows = this.getRowCount();
		int ncols = this.getColumnCount();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			if (row < 0 || row >= nrows) {
				throw new CIFException("Bad row in selectRows: " + row);
			}
			int p = row * ncols;
			for (int j = p; j < p + ncols; j++) {
				newValueList.add(valueList.get(j));
			}
		}
		CIFLoop newLoop = new CIFLoop(this.getNameList(), newValueList);
		return newLoop;
	}

	/**
	 * writes loop to an HTML table.
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeHTML(Writer w) throws IOException {
		List<String> nameList = this.getNameList();
		Elements rows = this.getChildElements(CIFRow.TAG);
		if (nameList.size() > 0) {
			String category = CIFUtil.getGuessedCategory(nameList);
			w.write("<br/>\n<table border=\"1\">\n");
			if (category.length() > 1) {
				w.write("<caption><b>" + category + "</b></caption>");
			}
			w.write("<tr>\n");
			for (String name : nameList) {
				if (category.length() > 1) {
					name = name.substring(category.length());
				}
				w.write("<th>" + name + "</th>\n");
			}
			w.write("</tr>\n");

			for (int i = 0; i < rows.size(); i++) {
				((CIFRow) rows.get(i)).writeHTML(w);
			}
		}
		w.write("</table>\n<br/>\n");
	}

	/**
	 * normalize.
	 * 
	 * all names are converted to lower case all values are whitespace
	 * normalized
	 */
	public void normalize() {
		List<String> names = this.getNameList();
		List<String> names1 = new ArrayList<String>();
		for (String name : names) {
			names1.add(name.toLowerCase());
		}
		this.setNames(names1);
		int i = 0;
		List<String> valueList = this.getValueList();
		for (String value : valueList) {
			valueList.set(i++, CIFUtil.normalize(value));
		}
		try {
			this.setValues(valueList, names.size());
		} catch (CIFException e) {
			CIFUtil.BUG(e);
		}
	}

	/**
	 * get cell from loop.
	 * 
	 * @param rowNum
	 * @param colNum
	 * @return cell (null if indexes out of bounds)
	 */
	public CIFTableCell getCell(int rowNum, int colNum) {
		Elements rows = this.getChildElements(CIFRow.TAG);
		CIFTableCell cell = null;
		if (rowNum >= 0 && rowNum < rows.size()) {
			CIFRow row = (CIFRow) rows.get(rowNum);
			Elements childCells = row.getChildElements(CIFTableCell.TAG);
			if (colNum >= 0 && colNum < childCells.size()) {
				cell = (CIFTableCell) childCells.get(colNum);
			}
		}
		return cell;
	}

	/**
	 * canonicalize.
	 * 
	 * the loop is first normalized.
	 * column names are then sorted in increasingly
	 * lexical order and the order of columns adjusted
	 */
	public void canonicalize() {
		normalize();
		Collections.sort(nameList);
		CIFUtil.NYI();
	}

	/**
	 * get names in canonical order.
	 * 
	 * column names are sorted do NOT alter current Loop
	 * 
	 * @return names in ascending order
	 */
	public String[] getCanonicalizedNames() {
		normalize();
		List<String> nameList = this.getNameList();
		String[] sorted = nameList.toArray(new String[0]);
		// for (int i = 0; i < sorted.length; i++) {
		// sorted[i] = new String(nameList.get(i));
		// }
		Arrays.sort(sorted);
		return sorted;
	}

	/**
	 * create a new canonicalized loop.
	 * 
	 * do not alter current loop.
	 * 
	 * @return canonicalized loop
	 */
	public CIFLoop getCanonicalizedLoop() {
		normalize();
		String[] canonNames = getCanonicalizedNames();
		int[] newCols = new int[canonNames.length];
		for (int i = 0; i < canonNames.length; i++) {
			newCols[i] = this.getColumnNumber(canonNames[i]);
		}
		CIFLoop canonicalLoop = null;
		try {
			canonicalLoop = selectColumns(newCols);
		} catch (CIFException e) {
			CIFUtil.BUG(e);
		}
		return canonicalLoop;
	}


	/**
	 * writes a loop. all names written on separate lines makes sure that lines
	 * do not exceed 80 chars does NOT close writer
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeCIF(Writer w) throws IOException {
		w.write(S_LOOP+S_NL);
		List<String> nameList = this.getNameList();
		int nameCount = nameList.size();
		for (String name : nameList) {
			w.write(name+S_NL);
		}
		List<String> valueList = this.getValueList();
		int valueCount = 0;
		int charCount = 0;
		boolean startRow = true;
		for (String value : valueList) {
			if (valueCount % nameCount == 0) {
				charCount = 0;
				startRow = true;
			}
			String vv = CIFUtil.getQuotedString(value).trim();
			// string too long (discount SEMI-COLON quotes)
			if (!vv.startsWith(""+C_SEMI)) {
				if (vv.length() > 80) {
					throw new RuntimeException("Loop value > 80 chars: [" + vv + "]");
				}
			} else {
				vv = S_NL+vv;
			}
			charCount += vv.length();
			if (charCount > 79) {
				w.write(S_NL);
				charCount = 0;
				startRow = true;
			}
			// add space except before next line
			if (!startRow) {
				w.write(C_SPACE);
				// charCount must be incremented as each value added that
				// doesn't start
				// on a new line is followed by a space.
				charCount++;
			}
			w.write(vv);
			if (valueCount % nameCount == nameCount-1) {
				w.write(S_NL);
			}
			valueCount++;
			startRow = false;
		}
	}

	/** delete column.
	 * 
	 * @param name
	 */
	public void deleteColumn(String name) {
		deleteColumn(getColumnNumber(name));
	}


	/**
	 * delete a given column. if col is outside range (0-(ncols-1))
	 * return no-op
	 * 
	 * @param col number (from 0)
	 */
	public void deleteColumn(int col) {
		int ncols = getColumnCount();
		if (col >= 0 && col < ncols) {
			Elements rows = this.getChildElements(CIFRow.TAG);
			for (int i = 0; i < rows.size(); i++) {
				CIFRow row = (CIFRow) rows.get(i);
				CIFTableCell cell = (CIFTableCell) row.getChildElements(CIFTableCell.TAG).get(col);
				cell.detach();
			}
			List<String> names = this.getNameList();
			names.remove(col);
			setNames(names);
		}
	}

	/** for sorting loops
	 * since order of rows is immaterial and cannot be normalized
	 * without a dictionary, only uses names
	 * compares case-insensitive nameLists after sorting
	 * if column counts differ, then return -1, 0, 1 according to standard order
	 * if the same count, return lexical ordering
	 * if still identical compare rowCount as -1, 0 , 1
	 * @param loop to compare
	 * @return normal compare result (or 0 if loop == null)
	 */
	public int compareTo(CIFLoop loop) {
		int result = 0;
		if (loop == null) {
			// null loop, return 0
		} else {
			result = CIFLoop.compareNameLists(this.getNameList(), loop.getNameList());
			if (result == 0) {
				if (this.getRowCount() < loop.getRowCount()) {
					result = -1;
				} else if (this.getRowCount() > loop.getRowCount()) {
					result = 1;
				}
			}
		}
		return result;
	}

	/** compare nameLists.
	 * case-insensitive.
	 * uses copies internally
	 * if lengths are different lengths reurns -1, 1
	 * else returns first non-zero String.compareTo()
	 * @param names1
	 * @param names2
	 * @return (0 if either arg is null)
	 */
	public static int compareNameLists(List<String> names1, List<String> names2) {
		int result = 0;
		if (names1 != null && names2 != null) {
			if (names1.size() < names2.size()) {
				result = -1;
			} else if (names1.size() > names2.size()) {
				result = 1;
			}

			if (result == 0) {
				List<String> nameList1 = new ArrayList<String>();
				List<String> nameList2 = new ArrayList<String>();
				for (int i = 0; i < names1.size(); i++) {
					nameList1.add(names1.get(i).toLowerCase());
					nameList1.add(names2.get(i).toLowerCase());
				}
				Collections.sort(nameList1);
				Collections.sort(nameList2);
				for (int i = 0; i < names1.size(); i++) {
					result = nameList1.get(i).compareTo(nameList2.get(i));
					if (result != 0) {
						break;
					}
				}
			}
		}
		return result;
	}

    @Override
    public CIFLoop copy() {
        return new CIFLoop(this);
    }
    
}
