package org.xmlcml.cif;

import nu.xom.Document;

/**
 * Handler to build CIF from SAX-like callbacks.
 * 
 * <p>
 * This is the main public interface that most SAX applications implement: if
 * the application needs to be informed of basic parsing events, it implements
 * this public interface and registers an instance with the SAX parser using the
 * setContentHandler method. The parser uses the instance to report basic
 * document-related events like the start and end of elements and character
 * data.
 * </p>
 * <p>
 * The order of events in this public interface is very important, and mirrors
 * the order of information in the document itself. For example, all of an
 * element's content (character data, processing instructions, and/or
 * subelements) will appear, in order, between the startElement event and the
 * corresponding endElement event.
 * </p>
 * <p>
 * 
 * This public interface is similar to the now-deprecated SAX 1.0
 * DocumentHandler public interface, but it adds support for Namespaces and for
 * reporting skipped entities (in non-validating XML processors).
 * </p>
 * <p>
 * 
 * Implementors should note that there is also a Java class ContentHandler in
 * the java.net package; that means that it's probably a bad idea to do
 * </p>
 * 
 * <pre>
 * import java.net.*;
 * import org.xml.sax.*;
 * 
 * </pre>
 * 
 * <p>
 * In fact, "import ...*" is usually a sign of sloppy programming anyway, so the
 * user should consider this a feature rather than a bug.
 * </p>
 * <p>
 * NOTE: This design is closely modelled on the SAX design for XML (Megginson,
 * Murray-Rust, Bray and many others, http://sax.sf.net) and re-uses much of the
 * material (special thanks to David Megginson).
 * </p>
 * <p>
 * This handler builds a CIFDOM from the CIFSAX events.
 * </p>
 * <p>
 * CIF applications programmers may use CIFContentHandler as follows:
 * 
 * <pre>
 </pre>
 * 
 * </p>
 */

public class DOMBuilderContentHandler extends DefaultContentHandler {

	CIF root;

	CIFDataBlock dataBlock = null;

	/**
	 * create new handler.
	 */
	public DOMBuilderContentHandler() {
	}

	/**
	 * Receive an object for locating the origin of SAX document events.
	 * <p>
	 * 
	 * SAX parsers are strongly encouraged (though not absolutely required) to
	 * supply a locator: if it does so, it must supply the locator to the
	 * application by invoking this method before invoking any of the other
	 * methods in the ContentHandler public interface.
	 * </p>
	 * <p>
	 * 
	 * The locator allows the application to determine the end position of any
	 * document-related event, even if the parser is not reporting an error.
	 * Typically, the application will use this information for reporting its
	 * own errors (such as character content that does not match an
	 * application's business rules). The information returned by the locator is
	 * probably not sufficient for use with a search engine.
	 * </p>
	 * <p>
	 * 
	 * Note that the locator will return correct information only during the
	 * invocation of the events in this public interface. The application should
	 * not attempt to use it at any other time.
	 * </p>
	 * 
	 * @param locator
	 *            An object that can return the location of any SAX document
	 *            event.
	 */
	public void setLocator(CIFLocator locator) {
	}

	/**
	 * Receive notification of the beginning of a document.
	 * <p>
	 * 
	 * The CIF parser will invoke this method only once, before any other
	 * methods in this public interface (except for setDocumentLocator).
	 * </p>
	 * 
	 * @return new CIFDocumentImpl
	 * @see #endDocument
	 * @throws CIFException
	 */
	public Document startDocument() throws CIFException {
		this.root = new CIF();
		return new Document(this.root);
	}

	/**
	 * Receive notification of the end of a document.
	 * <p>
	 * The SAX parser will invoke this method only once, and it will be the last
	 * method invoked during the parse. The parser shall not invoke this method
	 * until it has either abandoned parsing (because of an unrecoverable error)
	 * or reached the end of input.
	 * </p>
	 * 
	 * @see #startDocument
	 * @throws CIFException
	 */
	public void endDocument() throws CIFException {
	}

	/**
	 * Receive notification of the beginning of a data block.
	 * 
	 * <p>
	 * The Parser will invoke this method at the beginning of every dataBlock
	 * (data_foo) in the CIF document. CIF contains no explicit endDataBlock
	 * event, but this must be explicitly generated by a CIF parser (even when
	 * the dataBlock is empty). All of the dataBlock's content will be reported,
	 * in order, before the corresponding endDataBlock event.
	 * </p>
	 * 
	 * @param id
	 *            the id of the dataBlock
	 * @throws CIFException
	 *             dataBlock has no id
	 */
	public void startDataBlock(String id) throws CIFException {

		dataBlock = new CIFDataBlock(id);
		root.add(dataBlock);
	}

	/**
	 * Receive notification of the end of a data block.
	 * 
	 * <p>
	 * The SAX parser will invoke this method at the end of every element in the
	 * XML document; there will be a corresponding startElement event for every
	 * endElement event (even when the element is empty).
	 * </p>
	 * 
	 * @param id
	 * @throws CIFException
	 */
	public void endDataBlock(String id) throws CIFException {
	}

	/**
	 * Receive notification of a CIFItem.
	 * 
	 * <p>
	 * The Parser will invoke this method at the end of every item (tag+data) in
	 * the CIF document.
	 * </p>
	 * 
	 * @param item
	 *            to add
	 * @throws CIFException
     * @return message
	 */
	public ParserMessage addItem(CIFItem item) throws CIFException {
		if (dataBlock == null) {
			throw new CIFException("NULL current dataBlock");
		}
		return dataBlock.add(item, checkDuplicates);
	}

	/**
	 * Receive notification of a CIFLoop.
	 * 
	 * <p>
	 * The Parser will invoke this method at the end of every loop (loop_ +
	 * names + values) in the CIF document.
	 * </p>
	 * 
	 * @param loop
	 *            to add
	 * @throws CIFException
	 *             bad shape of loop, and several others
	 */
	public void addLoop(CIFLoop loop) throws CIFException {
		dataBlock.add(loop, checkDuplicates);
	}

	/**
	 * Receive notification of ignorable whitespace between other elements.
	 * <p>
	 * At present a no-op
	 * </p>
	 * <p>
	 * 
	 * CIFSAX parsers may return all contiguous whitespace in a single chunk, or
	 * they may split it into several chunks
	 * </p>
	 * 
	 * @param s
	 *            the whitespace
	 * @throws CIFException -
	 *             Any CIF exception, possibly wrapping another exception.
	 */
	public void addWhitespace(String s) throws CIFException {
	}

	/**
	 * Receive notification of a fullline comment or block of comments.
	 * 
	 * <p>
	 * Comments are for humans to read in documents and dictionaries and
	 * strictly are not part of the data. Users should not rely on comment data
	 * being passed through CIFDOM. However since comments sometimes contain
	 * critical identification or intellectual property info we provide a
	 * mechanism for passing them. Note that the <b>position of comments has no
	 * clear semantics.</b> The order of comments and their chunking is
	 * preserved but not their relation to other elements.
	 * </p>
	 * <p>
	 * Comments can occur in the following places:
	 * <ul>
	 * <li>within a line (up to end)</li>
	 * <li>Before the first dataBlock</li>
	 * <li>between data items</li>
	 * </ul>
	 * Inline comments are restricted to a single String and this can be used to
	 * identify them. Other comments often occur in blocks and we use the
	 * convention that all contiguous comments are part of a single commonBlock.
	 * No blank lines are allowed in such a block; they split it into separate
	 * blocks.
	 * </p>
	 * 
	 * @param data
	 *            the comment values (without leading #). All comments are
	 *            right-trimmed
	 * @throws CIFException -
	 *             Any CIFException, possibly wrapping another exception.
	 * @see #comment
	 */
	public void comments(String[] data) throws CIFException {
		CIFComment comment = new CIFComment(data);
		if (dataBlock == null) {
			root.appendChild(comment);
		} else {
			dataBlock.add(comment);
		}
	}

	/**
	 * Receive notification of an inline comment.
	 * <p>
	 * Comments are for humans to read in documents and dictionaries and
	 * strictly are not part of the data. Users should not rely on comment data
	 * being passed through CIFDOM. However since comments sometimes contain
	 * critical identification or intellectual property info we provide a
	 * mechanism for passing them. Note that the <b>position of comments has no
	 * clear semantics.</b> The order of comments and their chunking is
	 * preserved but not their relation to other elements.
	 * </p>
	 * <p>
	 * Comments can occur in the following places:
	 * <ul>
	 * <li>within a line (up to end)</li>
	 * <li>Before the first dataBlock</li>
	 * <li>between data items</li>
	 * </ul>
	 * Inline comments are restricted to a single String and this can be used to
	 * identify them. Other comments often occur in blocks and we use the
	 * convention that all contiguous comments are part of a single commonBlock.
	 * No blank lines are allowed in such a block; they split it into separate
	 * blocks.
	 * </p>
	 * 
	 * @param data
	 *            the comment values (without leading #). All comments are
	 *            right-trimmed
	 * @throws CIFException -
	 *             Any CIFException, possibly wrapping another exception.
	 * @see #comments
	 */
	public void comment(String data) throws CIFException {
		CIFComment comment = new CIFComment(data);
		if (dataBlock == null) {
			root.appendChild(comment);
		} else {
			dataBlock.appendChild(comment);
		}
	}
}
