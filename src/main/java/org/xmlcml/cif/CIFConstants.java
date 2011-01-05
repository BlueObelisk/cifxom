package org.xmlcml.cif;


/**
 * definition of the tags used for parsing CIFs.
 */
public interface CIFConstants {
	// =============== CIFDOM attributes ==============
	/** the name attribute for a data item. */
	String ATTRIBUTE_ID = "id";

	// =============== CIF syntax ==============
	/** */
	String S_DATA = "data_";

	/** */
	String S_LOOP = "loop_";

	/** */
	String S_SAVE = "save_";

	/** */
	String S_STOP = "stop_";

	/** */
	String S_GLOBAL = "global_";

	// =============== CIF reserved chars ==============

	/** */
	char C_APOS = '\'';

	/** */
	char C_EQUALS = '=';

	/** */
	char C_GT = '>';

	/** */
	char C_HASH = '#';

	/** */
	char C_LBRAK = '(';

	/** */
	char C_LT = '<';

	/** */
	char C_NL = '\n';

	/** */
	char C_NULL = '\0';

	/** */
	char C_SEMI = ';';

	/** */
	char C_QUOT = '"';

	/** */
	char C_RBRAK = ')';

    /** space */
    char C_SPACE = ' ';

    /** slash */
    char C_SLASH = '/';

	/** */
	char C_TAB = '\t';

	/** */
	char C_UNDER = '_';

	/** */
	String S_APOS = "'";

	/** */
	String S_COLON = ":";

	/** */
	String S_EMPTY = "";

	/** */
	String S_EQUALS = "=";

	/** */
	String S_HASH = "#";

	/** */
	String S_NL = "\n";

	/** */
	String S_UNDER = "_";

	/** */
	String S_SLASH = "/";
	
	/** . */
	String S_PERIOD = ".";

	/** . */
	String S_QUERY = "?";

	// =============== misc ==============

	/** */
	String DOCUMENT = "document";

	/** */
	String DICTIONARY = "dictionary";

	String XMLNS = "xmlns";
	
	/**
	 * self-identification string in CIF documents
	 */
	String CIF11 = "#\\#CIF_1.1.";

	/** for CML dictionaries
	 * 
	 */
	String CIFX_NS = "http://www.xml-cml.org/schema/cifx";

	/** for CML dictionaries
	 * 
	 */
	String CIFX_PREFIX = "cifx";
	
	/** for CIF
	 * 
	 */
	String CIF_NS = "http://www.xml-cml.org/schema/cif";

	String IUCR_PREFIX = "iucr";
	String IUCR_URI = "http://www.xml-cml.org/schema/iucr";
	String IUCR_URI_HASH = IUCR_URI+S_HASH;

	String IUCR_XMLNS_PREFIX = XMLNS + S_COLON + IUCR_PREFIX + 
	    S_EQUALS + S_APOS + IUCR_URI + S_APOS;

	
	// =============== directories ==============

	// ============= tests ===========

}
