package org.xmlcml.cif;

/**
 * Basic public interface for CIF error handlers.
 * <p>
 * This is based largely on the SAX atomMatchStrategy and the terminology is
 * similar.
 * </p>
 * <p>
 * If a CIFSAX application needs to implement customized error handling, it must
 * implement this public interface and then register an instance with the
 * CIFParser using the setErrorHandler method. The parser will then report all
 * errors and warnings through this public interface.
 * </p>
 * <p>
 * WARNING: If an application does not register an ErrorHandler, XML parsing
 * errors will go unreported and bizarre behaviour may result.
 * </p>
 * <p>
 * For XML processing errors, a CIF driver must use this public interface
 * instead of throwing an exception: it is up to the application to decide
 * whether to throw an exception for different types of errors and warnings.
 * Note, however, that there is no requirement that the parser continue to
 * provide useful information after a call to fatalError (in other words, a CIF
 * driver class could catch an exception and report a fatalError).
 * </p> //
 * 
 * //this throws a warning
 * see #CIFParser.setErrorHandler(CIFErrorHandler) 
 */
public interface CIFErrorHandler {

	/**
	 * Receive notification of a recoverable error.
	 * <p>
	 * The CIF version follows SAX atomMatchStrategy, This corresponds to the
	 * definition of "error" in section 1.2 of the W3C XML 1.0 Recommendation.
	 * For example, a validating parser would use this callback to report the
	 * violation of a validity constraint. The default behaviour is to take no
	 * action.
	 * </p>
	 * <p>
	 * The CIF parser must continue to provide normal parsing events after
	 * invoking this method: it should still be possible for the application to
	 * process the document through to the end. If the application cannot do so,
	 * then the parser should report a fatal error even if the XML 1.0
	 * recommendation does not require it to do so.
	 * </p>
	 * <p>
	 * Filters may use this method to report other, non-XML errors as well.
	 * </p>
	 * 
	 * @param locator
	 *            to report position of error
     * @param message
	 * @throws CIFException -
	 *             Any CIFException, possibly wrapping another exception.
	 */
	void error(String message, CIFLocator locator) throws CIFException;

	/**
	 * Receive notification of a non-recoverable error.
	 * <p>
	 * 
	 * This corresponds to the definition of "fatal error" in section 1.2 of the
	 * W3C XML 1.0 Recommendation. For example, a parser would use this callback
	 * to report the violation of a well-formedness constraint.
	 * </p>
	 * <p>
	 * 
	 * The application must assume that the document is unusable after the
	 * parser has invoked this method, and should continue (if at all) only for
	 * the sake of collecting addition error messages: in fact, CIF parsers are
	 * free to stop reporting any other events once this method has been
	 * invoked.
	 * </p>
	 * 
	 * @param locator
	 *            to report position of error
	 * @param message
     * @throws CIFException -
	 *             Any CIFException, possibly wrapping another exception.
	 */
	void fatalError(String message, CIFLocator locator) throws CIFException;

	/**
	 * Receive notification of a warning.
	 * <p>
	 * CIF parsers will use this method to report conditions that are not errors
	 * or fatal errors as defined by the XML 1.0 recommendation. The default
	 * behaviour is to take no action.
	 * </p>
	 * <p>
	 * 
	 * The CIF parser must continue to provide normal parsing events after
	 * invoking this method: it should still be possible for the application to
	 * process the document through to the end.
	 * </p>
	 * <p>
	 * 
	 * Filters may use this method to report other, non-XML warnings as well.
	 * </p>
	 * <p>
	 * 
	 * @param message
	 * @param locator
	 *            to report position of error
	 * @throws CIFException -
	 *             Any CIFException, possibly wrapping another exception.
	 */
	void warning(String message, CIFLocator locator) throws CIFException;

    /** skip errors.
     * 
     * @param skip if true skip errors and try to continue else throw
     */
	void setSkipErrors(boolean skip);
}
