package org.xmlcml.cif;

/**
 * Public interface for associating a CIF event with a document location.
 * 
 * <p>
 * based heavily on SAX.
 * </p>
 * 
 * <p>
 * If a CIFSAX parser provides location information to the CIFSAX application,
 * it does so by implementing this public interface and then passing an instance
 * to the application using the content handler's setLocator method. The
 * application can use the object to obtain the location of any other content
 * handler event in the CIF source document.
 * </p>
 * <p>
 * Note that the results returned by the object will be valid only during the
 * scope of each content handler method: the application will receive
 * unpredictable results if it attempts to use the locator at any other time.
 * </p>
 * <p>
 * CIFSAX parsers are not required to supply a locator, but they are very
 * strongly encouraged to do so. If the parser supplies a locator, it must do so
 * before reporting any other document events. If no locator has been set by the
 * time the application receives the startDocument event, the application should
 * assume that a locator is not available.
 * </p>
 * <p>
 * NOTE: This design is closely modelled on the SAX design for XML (Megginson,
 * Murray-Rust, Bray and many others (http://www.xml.org/sax,
 * http://www.sax.org, http://sax.sf.net)
 * </p>
 * <p>
 * CIF applications programmers may use Locator as follows:
 * 
 * <pre>
 * // This class listens for startData events
 * static class MyHandler extends DefaultCIFHandler {
 * 	CIFLocator locator;
 * 
 * 	public void setLocator(CIFLocator locator) {
 * 		this.locator = locator;
 * 	}
 * 
 * 	// This method is called when an element is encountered
 * 	public void startData(String id) {
 * 		if (locator != null) {
 * 			int col = locator.getColumnNumber();
 * 			int line = locator.getLineNumber();
 * 			String systemId = locator.getSystemId();
 * 			logger.INFO(&quot;Data block in: &quot; + systemId + &quot; at line/col: &quot; + line
 * 					+ &quot;/&quot; + col);
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * </p>
 */
public interface CIFLocator {

	/**
	 * Return the system identifier for the current document event.
	 * 
	 * <p>
	 * NOTE: SystemId represents the address of the document and is normally a
	 * URL or fileId.
	 * </p>
	 * <p>
	 * The return value is the system identifier of the document entity or of
	 * the external parsed entity in which the markup triggering the event
	 * appears.
	 * </p>
	 * <p>
	 * If the system identifier is a URL, the parser must resolve it fully
	 * before passing it to the application.
	 * </p>
	 * 
	 * @return A string containing the system identifier, or null if none is
	 *         available.
	 */
	String getSystemId();

	/**
	 * Return the line number where the current document event ends.
	 * 
	 * <p>
	 * Warning: The return value from the method is intended only as an
	 * approximation for the sake of error reporting; it is not intended to
	 * provide sufficient information to edit the character content of the
	 * original XML document.
	 * </p>
	 * <p>
	 * The return value is an approximation of the line number in the document
	 * entity or external parsed entity where the markup triggering the event
	 * appears.
	 * </p>
	 * <p>
	 * If possible, the SAX driver should provide the line position of the first
	 * character after the text associated with the document event. The first
	 * line in the document is line 1.
	 * </p>
	 * <p>
	 * 
	 * @return The line number, or -1 if none is available.
	 * @see #getColumnNumber()
	 */
	int getLineNumber();

	/**
	 * Return the column number where the current document event ends.
	 * 
	 * <p>
	 * Warning: The return value from the method is intended only as an
	 * approximation for the sake of error reporting; it is not intended to
	 * provide sufficient information to edit the character content of the
	 * original XML document.
	 * </p>
	 * <p>
	 * The return value is an approximation of the column number in the document
	 * entity or external parsed entity where the markup triggering the event
	 * appears.
	 * </p>
	 * <p>
	 * If possible, the SAX driver should provide the line position of the first
	 * character after the text associated with the document event. The first
	 * column in each line is column 1.
	 * </p>
	 * 
	 * @return The column number, or -1 if none is available.
	 * @see #getLineNumber()
	 */
	int getColumnNumber();

}
