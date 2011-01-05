package org.xmlcml.cif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import nu.xom.Document;

/**
 * default parser.
 */
public class CIFParser implements CIFConstants, CIFLocator {

	/** error.
	 * 
	 * @author pm286
	 *
	 */
	public enum Error {
		/** */
		UNQUOTED_NO_UNDERSCORE(
		"unquoted item value must not start with underscore"),
		DATABLOCK_NO_ID("Data block must have ID"),
		UNTERMINATED_QUOTE("Unterminated quote");
		;
		/** value */
		public String value;
		private Error(String v) {
			value = v;
		}
	}

	int MAXTRY = 5;


	private static final Logger LOG = Logger.getLogger(CIFParser.class
			.getName());

	static String quoteString(String token) {
		if (token.charAt(0) == C_UNDER || token.indexOf(C_SPACE) != -1
				|| token.indexOf(C_TAB) != -1 || token.indexOf(C_APOS) != -1
				|| token.indexOf(C_QUOT) != -1) {
			return C_APOS + token + C_APOS;
		}
		return token;
	}

	// settings

	int columnNumber = 0;
	List<String> commentVector;
	protected CIFContentHandler contentHandler;
	CIFDataBlock dataBlock;
	boolean debug = true;
	// if true reads to first 'data_'
	boolean skipErrors = false;
	boolean skipHeader = false;
	boolean echoInput = false;
	boolean checkDuplicates = false;
	boolean heuristicCorrection = true;
	boolean renameBlockIds = false;
	int blockIdCount = 1;
	protected CIFErrorHandler errorHandler;
	boolean inItem = false;
	boolean inLoop = false;
	boolean inSave = false;
	boolean inSemiColon = false;
	boolean inWhite = false;
	CIFItem item;
	Map<String, String> itemMap;
	String lastToken = null;
	String line = null;
	int lineNumber = 0;
	CIFLoop loop;
	Map<String, CIFLoop> loopMap;
	List<String> loopNameList;
	List<String> loopValueList;
	int MAXTOKLENGTH = 1000;
	char quoteChar;

	CIFSaveFrame saveFrame;
	boolean start = false;
	boolean startHeader = false;
	// boolean su = false;
	protected String systemId = "unknown";
	StringBuffer textBuffer;
	String token = S_EMPTY;
	char[] tokenChars = new char[MAXTOKLENGTH];
	int tokenLength = 0;

	/**
	 * create default parser.
	 */
	public CIFParser() {
		setContentHandler(new DOMBuilderContentHandler());
		setErrorHandler(new DefaultErrorHandler());
		setEchoInput(false);
		// setSU(false);
		setDebug(false);
		// setDebug(true);

		inLoop = false;
		inItem = false;
		inSave = false;
		lastToken = null;
		start = true;
		commentVector = null;
		startHeader = false;
		checkDuplicates = false;
		contentHandler.setCheckDuplicates(checkDuplicates);

		LOG.setLevel(Level.WARNING);
	}

	/**
	 * set whether to skip errors and recover.
	 * 
	 * @param skip
	 *            sets flag
	 */
	public void setSkipErrors(boolean skip) {
		skipErrors = skip;
		if (errorHandler != null)
			errorHandler.setSkipErrors(skip);
	}

	/**
	 * set whether to rename block ids to integers
	 * 
	 * @param skip
	 *            sets flag
	 */
	public void setBlockIdsAsIntegers(boolean rename) {
		renameBlockIds = rename;
	}

	/**
	 * set check whether itmes or loops are duplicates.
	 * 
	 * @param check
	 *            sets flag
	 */
	public void setCheckDuplicates(boolean check) {
		checkDuplicates = check;
		contentHandler.setCheckDuplicates(check);
	}

	/**
	 * 
	 * @param pos
	 * @param s
	 * @throws CIFException
	 *             TODO Needs cleaning up
	 */
	void addComment(int pos, String s) throws CIFException {
		char[] chArray = s.toCharArray();
		for (char c : chArray) {
			// get rid of any illegal characters from the string
			if (!CIFUtil.isAnyPrintChar(c)) {
				s = s.replace(c, ' ');
			}
		}
		if (pos > 0) {
			contentHandler.comment(s.substring(pos + 1));
			commentVector = null;
		} else {
			if (commentVector == null) {
				commentVector = new Vector<String>();
			}
			commentVector.add(s);
		}
	}

	private void addToken(char quoteChar) throws CIFException {
		String s = new String(tokenChars, 0, tokenLength);
		try {
			readToken(quoteChar, s);
		} catch (CIFException cifex) {
			throw new CIFException(cifex + " at line " + lineNumber);
		}
		tokenLength = 0;
	}

	private void createNewItem(String name) throws CIFException {
		if (itemMap == null) {
			itemMap = new HashMap<String, String>();
		}
		name = name.toLowerCase();
		if (itemMap.containsKey(name)) {
			if (checkDuplicates) {
				errorHandler.error("Duplicate item: " + name, this);
			} else {
				errorHandler.warning("Duplicate item: " + name, this);
			}
		}
		itemMap.put(name, name);
		item = new CIFItem();
		try {
			item.setName(name);
			inItem = true;
		} catch (RuntimeException e) {
			inItem = false;
			errorHandler.error(e.getMessage(), this);
		}
	}

	private void processLoop() throws CIFException {
		if (debug) {
			LOG.fine("processing loop");
		}
		if (loop == null) {
			errorHandler.error("null loop", this);
		}
		List<String> nameList = loop.getNameList();
		if (nameList == null) {
			errorHandler.error("null loopNameList", this);
		}
		if (loopValueList == null) {
			errorHandler.error("null loopValueList", this);
		}
		if (nameList.size() == 0) {
			errorHandler.error("Loop terminated without names", this);
		}
		if (loopValueList.size() == 0) {
			errorHandler.error("Loop terminated without values", this);
		}

		loop.setValues(loopValueList, nameList.size());
		// add loop; handler may wish to check for duplicates
		contentHandler.addLoop(loop);
	}

	/**
	 * get current column number.
	 * 
	 * @return the number
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	/**
	 * Gets debug.
	 * 
	 * @return debug debug output on/off (default false)
	 */
	public boolean getDebug() {
		return debug;
	}

	/**
	 * Gets echoInput.
	 * 
	 * @return echoInput input will/not be echoed (default false)
	 */
	public boolean getEchoInput() {
		return echoInput;
	}

	/**
	 * get current line number.
	 * 
	 * @return the number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * object being parsed.
	 * 
	 * @return the system id (e.g. filename or url)
	 */
	public String getSystemId() {
		return systemId;
	}

	boolean isAcceptableCIFChar(char c) {
		return (c <= 8 || c == 11 || c == 12 || (c >= 14 && c < 32)) ? false
				: true;
	}

	/**
	 * parse a file.
	 * 
	 * @param file
	 * @throws CIFException
	 * @throws IOException
	 * @return document
	 */
	public Document parse(File file) throws CIFException, IOException {
		return parse(new BufferedReader(new FileReader(file)));
	}

	/**
	 * Parse a buffered reader. Closes the reader once the
	 * parsing has finished.
	 * 
	 * @param bufferedReader
	 * @throws CIFException
	 * @throws IOException
	 * @return document
	 */
	public Document parse(BufferedReader bufferedReader) throws CIFException,
	IOException {
		Document document = null;
		try {
			// read into buffer so we can do some heuristics
			List<String> lines = new ArrayList<String>();
			while (true) {
				line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				// only trim end
				line = CIFUtil.trimTrail(line);
				line = CIFUtil.stripISOControls(line);
				//			System.out.println(line);
				addLine(lines);
			}
			int retry = MAXTRY;
			while (true) {
				try {
					document = parseLines(lines);
					break;
				} catch (CIFException e) {
					if (heuristicCorrection) {
						fixLine(lines, lineNumber-1, e);
						retry--;
					} else {
						throw e;
					}
				} 
				if (retry == 0) {
					throw new CIFException("Could not fix errors...");
				}
			}
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
		return document;
	}

	private void addLine(List<String> lines) {
		if (echoInput) {
			System.out.println("> " + line);
		}
		lines.add(line);
	}

	private void fixLine(List<String> lines, int lineNumber, CIFException e) throws CIFException{
		String line = lines.get(lineNumber);
		String message = e.getMessage();
		boolean fixed = false;
		if (message != null && message.indexOf(CIFParser.Error.UNTERMINATED_QUOTE.value) != -1) {
			Matcher matcher = Pattern.compile("^\\s*(_[^\\s]+)\\s+([^'\"]*)").matcher(line);
			if (matcher.find()) {
				if (matcher.groupCount() == 2) {
					line = matcher.group(1)+C_SPACE+C_APOS+matcher.group(2)+C_APOS;
					lines.set(lineNumber, line);
					fixed = true;
				}
			}
		} else if (message != null && message.indexOf(CIFParser.Error.DATABLOCK_NO_ID.value) != -1) {
			line = "data_I";
			fixed = true;
		} else if (message != null && message.indexOf(CIFItem.Message.NO_UNDER.value) != -1) {
			// test _foo bar plugh and convert to _foo 'bar plugh'
			Pattern pattern = Pattern.compile("^\\s*(_[^\\s]+)\\s+([^'\"]*)");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				if (matcher.groupCount() == 2) {
					line = matcher.group(1)+C_SPACE+C_APOS+matcher.group(2)+C_APOS;
					lines.set(lineNumber, line);
					fixed = true;
				}
			}
			// removes whitespace from dataBlock id
			matcher = Pattern.compile("^\\s*data_.*").matcher(line);
			if (matcher.find()) {
				line = line.replaceAll("\\s", "");
				lines.set(lineNumber, line);
				fixed = true;
			}
			matcher = Pattern.compile("(\\*|\\-|=){4,}").matcher(line);
			if (matcher.find()) {
				line = "";
				lines.set(lineNumber, line);
				fixed = true;
			}
		}
		if (!fixed) {
			throw new CIFException("Cannot fix line ("+(lineNumber)+"): "+line);
		}
	}

	private Document parseLines(List<String> lines) throws CIFException {
		Document document = contentHandler.startDocument();

		lineNumber = 0;
		boolean inSemiColon = false;
		for (String line : lines) {
			lineNumber++;
			// line end is always white
			inWhite = true;
			// semi-colon quoted
			if (inSemiColon) {
				// end of semi-colon text?
				if (line.length() > 0 && line.charAt(0) == C_SEMI) {
					try {
						readToken(C_SEMI, textBuffer.toString());
					} catch (CIFException cifex) {
						errorHandler.error(cifex.getMessage(), this);
					}
					inSemiColon = false;
					line = line.substring(1);
					tokenize(line);
					// do we save rest of line?
				} else {
					textBuffer.append(line);
					textBuffer.append(C_NL);
				}
				// skip empty lines except in semiColons
			} else if (line.length() == 0) {
				processComments();
				// new comment
			} else if (line.charAt(0) == C_HASH) {
				addComment(0, line);
				// new semi-colon
			} else if (line.charAt(0) == C_SEMI) {
				processComments();
				inSemiColon = true;
				textBuffer = new StringBuffer();
				textBuffer.append(line.substring(1));
				textBuffer.append(C_NL);
			} else {
				processComments();
				tokenize(line);
			}
		}
		readToken(C_NULL, null);
		contentHandler.endDocument();
		// ((CIF)document.getRootElement()).debug()
		return document;

	}

	// ========================== parsing routines =========================
	/**
	 * parse a filename.
	 * 
	 * @param filename
	 * @throws CIFException
	 * @throws IOException
	 * @return document or null
	 */
	public Document parse(String filename) throws CIFException, IOException {
		systemId = filename;
		return parse(new BufferedReader(new FileReader(filename)));
	}

	/**
	 * parse a url.
	 * 
	 * @param url
	 * @throws CIFException
	 * @throws IOException
	 * @return document or null
	 */
	public Document parse(URL url) throws CIFException, IOException {
		systemId = url.toString();
		return parse(new BufferedReader(new InputStreamReader(url.openStream())));
	}

	void processComments() throws CIFException {
		if (commentVector != null) {
			contentHandler.comments((String[]) commentVector
					.toArray(new String[0]));
		}
		commentVector = null;
	}

	/**
	 * reads next token
	 * 
	 * @param quoteChar
	 * @param value
	 * @exception CIFException
	 */
	void readToken(char quoteChar, String value) throws CIFException {
		if (debug) {
			LOG.fine("Tok: " + value);
		}
		String keyword = "";
		boolean finishedParse = false;
		if (value != null) {
			keyword = value.toLowerCase();
		} else {
			finishedParse = true;
		}
		// start
		if (start) {
			if (!(keyword.startsWith(S_DATA))) {
				if (!skipHeader) {
					errorHandler.error("Must start CIF with " + S_DATA
							+ "; not: " + line, this);
				} else if (!startHeader) {
					LOG.fine("Skipped header starting with: " + line);
					startHeader = true;
				}
			} else {
				start = false;
			}
			// looking for item value
		} else if (inItem) {
			if (value.equals("") || value.charAt(0) == C_UNDER
					&& quoteChar == C_NULL) {
				errorHandler.error(
						Error.UNQUOTED_NO_UNDERSCORE.value,
						this);
			}
			if (finishedParse || keyword.equals(S_LOOP)
					|| keyword.startsWith(S_SAVE) || keyword.startsWith(S_DATA)) {
				errorHandler.error("Missing item value", this);
			}
			item.setTextValue(value);
			ParserMessage m = contentHandler.addItem(item);
			if (m != null) {
				errorHandler.error(m.getMessage(), this);
			}
			inItem = false;
		} else if (inLoop) {
			// add loop names
			if (!finishedParse && quoteChar == C_NULL
					&& value.charAt(0) == C_UNDER) {
				if (loopNameList != null) {
					if (debug) {
						LOG.fine("+++++++ Adding loop name: " + value);
					}
					loopNameList.add(value);
				} else {
					// new item
					processLoop();
					inLoop = false;
					createNewItem(value);
				}
				// end of loop
			} else if (finishedParse || keyword.equals(S_LOOP)
					|| keyword.startsWith(S_SAVE) || keyword.startsWith(S_DATA)) {
				inLoop = false;
				processLoop();
				// a value
			} else {
				// start of values?
				if (loopNameList != null) {
					LOG.info("-------------adding loop name "
							+ loopNameList.size());
					loop.setNames(loopNameList);
					List<String> namexx = loop.getNameList();
					LOG.info("........." + namexx.size());
					if (debug) {
						LOG.info("creating new loop with name count:"
								+ loopNameList.size());
					}
					loopValueList = new ArrayList<String>();
					loopNameList = null;
				}
				loopValueList.add(value);
				if (debug) {
					LOG.info("_____added value to loop size("
							+ loopValueList.size() + "):" + value);
				}
			}
			// process later
		} else if (finishedParse || keyword.equals(S_LOOP)
				|| keyword.startsWith(S_SAVE) || keyword.startsWith(S_DATA)) {
			;
			// must be an item
		} else {
			createNewItem(value);
		}

		if (keyword.equals(S_LOOP)) {
			inLoop = true;
			loop = new CIFLoop();
			loopNameList = new ArrayList<String>();
		} else if (keyword.startsWith(S_DATA)) {
			if (inSave) {
				errorHandler.fatalError("Unexpected " + S_DATA
						+ " in saveFrame", this);
			}
			if (keyword.equals(S_DATA)) {
				errorHandler.fatalError(Error.DATABLOCK_NO_ID.value, this);
			}

			String dataId = "";
			if (renameBlockIds) {
				// rename the block ID to an incremental integer
				dataId = String.valueOf(blockIdCount);
				blockIdCount++;
			} else {
				// use the id provided in the CIF
				dataId = value.substring(5);
			}
			contentHandler.startDataBlock(dataId);
			loopMap = new HashMap<String, CIFLoop>();
			itemMap = new HashMap<String, String>();

		} else if (keyword.equals(S_STOP) || keyword.equals(S_GLOBAL)) {
			errorHandler.fatalError("STAR keyword not allowed in CIF: "
					+ keyword, this);
		} else if (keyword.equals(S_SAVE)) {
			if (!inSave) {
				errorHandler.fatalError("Unexpected " + S_SAVE, this);
			}
			inSave = false;
		} else if (keyword.startsWith(S_SAVE)) {
			if (inSave) {
				errorHandler.fatalError("Nested saveFrames forbidden", this);
			}
			String saveId = value.substring(S_SAVE.length());
			if (saveId.equals(S_EMPTY)) {
				errorHandler.fatalError("saveFrame requires ID", this);
			}
			Map<String, CIFSaveFrame> saveFrameMap = dataBlock
			.getSaveFrameMap();
			if (saveFrameMap.containsKey(saveId)) {
				errorHandler.fatalError(
						"saveFrame ID not unique within dataBlock: " + saveId,
						this);
			}
			saveFrame = new CIFSaveFrame(saveId);
			saveFrame.setId(saveId);
			dataBlock.add(saveFrame);
			inSave = true;
		}
	}

	/**
	 * set the content handler. normally required before parsing
	 * 
	 * @param contentHandler
	 *            (default is defaultContentHandler)
	 */
	public void setContentHandler(CIFContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	/**
	 * Sets debug.
	 * 
	 * @param debug
	 *            debug output on/off (default false)
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * skip header. there is no formal header, but sometimes authors put
	 * unstructured text before the first data_. This is illegal and this skips
	 * and deletes it. Comments are still honoured
	 * 
	 * @param skip
	 */
	public void setSkipHeader(boolean skip) {
		this.skipHeader = skip;
	}

	/**
	 * Sets echoInput.
	 * 
	 * @param echoInput
	 *            input will/not be echoed (default false)
	 */
	public void setEchoInput(boolean echoInput) {
		this.echoInput = echoInput;
	}

	/** set the error handler. normally required before parsing.
	 * 
	 * @param errorHandler
	 *            (default is defaultErrorHandler)
	 */
	public void setErrorHandler(CIFErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	private void tokenize(String line) throws CIFException {
		tokenLength = 0;
		quoteChar = ' ';
		int l = line.length();
		for (int i = 0; i < l; i++) {
			char c = line.charAt(i);
			if (!isAcceptableCIFChar(c)) {
				String errS = "Unacceptable CIF Character [" + c + "] value("
				+ (int) c + ") at line " + lineNumber;
				errorHandler.error(errS, this);
			}
			if (inWhite) {
				// skip all whitespace in whitespace
				if (Character.isWhitespace(c)) {
					;
					// comment; grab rest of line and record whether comment
					// begins in mid line
				} else if (c == C_HASH) {
					addComment(i, line);
					break;
					// start of quote
				} else if (c == C_APOS || c == C_QUOT) {
					quoteChar = c;
					inWhite = false;
					tokenLength = 0;
					// new token
				} else {
					tokenChars[0] = c;
					tokenLength = 1;
					inWhite = false;
				}
				// within a quote
			} else if (quoteChar == C_APOS || quoteChar == C_QUOT) {
				// possible end of quote
				if (c == quoteChar) {
					// next character is whitespace, end of quote
					if (i + 1 == l
							|| Character.isWhitespace(line.charAt(i + 1))) {
						// FIXME this may be wrong
						addToken(quoteChar);
						quoteChar = ' ';
						// new token
						tokenLength = 0;
						inWhite = true;
					} else {
						tokenChars[tokenLength++] = c;
					}
				} else {
					tokenChars[tokenLength++] = c;
				}
				// within an ordinary token
			} else {
				if (Character.isWhitespace(c)) {
					addToken(C_NULL);
					inWhite = true;
				} else {
					tokenChars[tokenLength++] = c;
				}
			}
		}
		if (quoteChar != C_SPACE) {
			errorHandler.fatalError(Error.UNTERMINATED_QUOTE.value+" (" + quoteChar
					+ ") at line: " + lineNumber, this);
		}
		// FIXME - this may be allowed for item values
		if (tokenLength != 0) {
			addToken(C_NULL);
		}
	}

	/** get error handler.
	 * 
	 * @return handler
	 */
	public CIFErrorHandler getErrorHandler() {
		return errorHandler;
	}

}
