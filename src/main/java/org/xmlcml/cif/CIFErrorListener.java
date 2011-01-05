package org.xmlcml.cif;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Error listener for transformation.
 * 
 * heavily modelled on Transformer and its Listener
 */
public class CIFErrorListener implements ErrorListener {

	CIFErrorListener errorListener = new CIFErrorListener();

	/**
	 * Default constructor.
	 */
	public CIFErrorListener() {
	}

	/**
	 * Receive notification of a warning.
	 * <p>
	 * Transformer can use this method to report conditions that are not errors
	 * or fatal errors. The default behaviour is to take no action.
	 * </p>
	 * <p>
	 * After invoking this method, the Transformer must continue with the
	 * transformation. It should still be possible for the application to
	 * process the document through to the end.
	 * </p>
	 * <p>
	 * 
	 * @param exception -
	 *            The warning information encapsulated in a transformer
	 *            exception.
	 * @throws TransformerException -
	 *             if the application chooses to discontinue the transformation.
	 */
	public void warning(TransformerException exception)
			throws TransformerException {
	}

	/**
	 * Receive notification of a recoverable error.
	 * </p>
	 * <p>
	 * 
	 * The transformer must continue to try and provide normal transformation
	 * after invoking this method. It should still be possible for the
	 * application to process the document through to the end if no other errors
	 * are encountered.
	 * </p>
	 * <p>
	 * 
	 * @param exception -
	 *            The error information encapsulated in a transformer exception.
	 * @throws TransformerException -
	 *             if the application chooses to discontinue the transformation.
	 */
	public void error(TransformerException exception)
			throws TransformerException {
	}

	/**
	 * Receive notification of a non-recoverable error.
	 * </p>
	 * <p>
	 * 
	 * The transformer must continue to try and provide normal transformation
	 * after invoking this method. It should still be possible for the
	 * application to process the document through to the end if no other errors
	 * are encountered, but there is no guarantee that the output will be
	 * useable.
	 * </p>
	 * <p>
	 * 
	 * @param exception -
	 *            The error information encapsulated in a transformer exception.
	 * @throws TransformerException -
	 *             if the application chooses to discontinue the transformation.
	 */
	public void fatalError(TransformerException exception)
			throws TransformerException {
	}
}