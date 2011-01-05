package org.xmlcml.cif;

/**
 * CIF-specific Exceptions.
 * 
 */
public class CIFException extends Exception {

	private static final long serialVersionUID = -7532581897369835633L;

	private ParserMessage parserMessage;

	/**
	 * exception with message.
	 * 
	 * @param s
	 *            the message
	 */
	public CIFException(String s) {
		super(s);
	}
	/** constructor.
     * 
     * @param message
     * @param e
	 */
	public CIFException(String message, Exception e) {
		super(message, e);
	}

    /** constructor.
     * 
     * @param m
     */
	public CIFException(ParserMessage m) {
		super(m.getMessage() + " at line " + m.getLine());
		parserMessage = m;
	}

    /** get message.
     * 
     * @return message
     */
	public ParserMessage getParserMessage() {
		return parserMessage;
	}

    /** set message.
     * 
     * @param parserMessage
     */
	public void setParserMessage(ParserMessage parserMessage) {
		this.parserMessage = parserMessage;
	}
}