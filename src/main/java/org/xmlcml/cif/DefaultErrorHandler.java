package org.xmlcml.cif;

import java.util.ArrayList;
import java.util.List;

/**
 * default CIFSAX error handler.
 * 
 * can be subclassed to add new or different functionality.
 * <p>
 * At present stores all errors in vectors and replays them later
 * </p>
 */
public class DefaultErrorHandler implements CIFErrorHandler {

	boolean skipErrors = false;

	List<ParserMessage> errors = new ArrayList<ParserMessage>(50);

	List<ParserMessage> fatalErrors = new ArrayList<ParserMessage>(1);

	List<ParserMessage> warnings = new ArrayList<ParserMessage>(50);

	/**
	 * trap error.
	 * 
	 * @param message
	 *            exception logging error
	 * @param loc
	 *            locator
	 * @throws CIFException
	 */
	public void error(String message, CIFLocator loc) throws CIFException {
		ParserMessage parserMessage = new ParserMessage(message, loc
				.getLineNumber(), loc.getColumnNumber());
		errors.add(parserMessage);
		if (!skipErrors)
			throw new CIFException(parserMessage);
	}

	/**
	 * trap fatal error.
	 * 
	 * @param message
	 *            exception logging error
	 * @param loc
	 *            locator
	 * @throws CIFException
	 * @throws CIFException
	 */
	public void fatalError(String message, CIFLocator loc) throws CIFException {
		ParserMessage parserMessage = new ParserMessage(message, loc
				.getLineNumber(), loc.getColumnNumber());
		fatalErrors.add(parserMessage);
		throw new CIFException(parserMessage);
	}

	/**
	 * trap warning
	 * 
	 * @param message
	 *            exception logging warning
	 * @param loc
	 *            locator
	 */
	public void warning(String message, CIFLocator loc) {
		warnings.add(new ParserMessage(message, loc.getLineNumber(), loc
				.getColumnNumber()));
	}

    /** get skip erros flag.
     * 
     * @return flag
     */
	public boolean getSkipErrors() {
		return skipErrors;
	}

    /** set skip erros flag.
     * @param skipErrors
     */
	public void setSkipErrors(boolean skipErrors) {
		this.skipErrors = skipErrors;
	}

}
