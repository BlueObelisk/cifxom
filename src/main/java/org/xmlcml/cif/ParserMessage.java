package org.xmlcml.cif;

/** message from parser.
 * 
 * @author ojd26
 *
 */
public class ParserMessage {
	int line;

	int col;

	String message;

    /** constructor.
     * 
     * @param message
     */
	public ParserMessage(String message) {
		this.message = message;
	}

    /** constructor.
     * 
     * @param message
     * @param line in CIF
     */
	public ParserMessage(String message, int line) {
		this.line = line;
		this.message = message;
	}

    /** constructor.
     * 
     * @param message
     * @param line in CIF
     * @param col in CIF
     */
	public ParserMessage(String message, int line, int col) {
		this.line = line;
		this.col = col;
		this.message = message;
	}

    /** get current line number.
     * 
     * @return line number
     */
	public int getLine() {
		return line;
	}

    /** set line number.
     * 
     * @param line number
     */
	public void setLine(int line) {
		this.line = line;
	}

    /** get message.
     * 
     * @return message
     */
	public String getMessage() {
		return message;
	}

    /** set message.
     * 
     * @param message
     */
	public void setMessage(String message) {
		this.message = message;
	}

    /** get column number.
     * 
     * @return column number
     */
	public int getCol() {
		return col;
	}

    /** set column number.
     * 
     * @param col number
     */
	public void setCol(int col) {
		this.col = col;
	}
}
