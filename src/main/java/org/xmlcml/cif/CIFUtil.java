package org.xmlcml.cif;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** some CIFUtils.
*/
public class CIFUtil implements CIFConstants {
	private static Logger LOG = Logger.getLogger(CIFUtil.class);
    /** messages */
    public enum Message {
        /** not yet implemented */
        NYI("not yet implemented"),
        ;
        /** value*/
        public String value;
        private Message(String v) {
            value = v;
        }
    }

	/** separator */
    enum Separator {
    	/** file separator */
    	FILE(File.separator),
    	/** URL separator */
    	URL(S_SLASH);
    	/** value */
    	public final String value;
    	private Separator(String v) {
    		this.value = v;
    	}
    }

    private final static File TEMP_DIRECTORY = new File("target"
            + File.separator + "test-outputs");
    
/**
	 *  If a URL is relative, make it absolute against the current directory. If
	 *  url already has a protocol, return unchanged
	 *
	 *@param  url                                 Description of the Parameter
	 *@return                                     Description of the Return
	 *      Value
	 *@exception  java.net.MalformedURLException  Description of the Exception
	 */
	public static String makeAbsoluteURL(String url)
	throws java.net.MalformedURLException {
		if (url == null) {
			throw new MalformedURLException("Null url");
		}
		URL baseURL;
		String fileSep = System.getProperty("file.separator");
		//		why the final slash?
		//		url = url.replace(fileSep.charAt(0), '/') + '/';
		url = url.replace(fileSep.charAt(0), '/');
		//		is url alreday a valid URL?
		boolean ok = true;
		try {
			/*URL u = */new URL(url);
		} catch (MalformedURLException mue) {
			ok = false;
			//			DOS filenames (for example C:\foo) gives problems
			String mueString = mue.toString().trim();
			int idx = mueString.indexOf("unknown protocol:");
			if (idx != -1) {
				mueString = mueString.substring(
						idx + "unknown protocol:".length()).trim();
				//				starts with X: assume DOS filename
				if (mueString.length() == 1) {
					url = "file:/" + url;
					//					throws MalformedURL if wrong
					/*URL u = */new URL(url);
					ok = true;
				}
			}
		}
		if (ok) {
			return url;
		}
		String currentDirectory = System.getProperty("user.dir");
		String file = currentDirectory.replace(fileSep.charAt(0), '/') + '/';
		if (file.charAt(0) != '/') {
			file = S_SLASH + file;
		}
		baseURL = new URL("file", null, file);
		String newUrl = new URL(baseURL, url).toString();
		return newUrl;
	}
	
    /**
     * create new file, including making directory if required This seems to be
     * a mess - f.createNewFile() doesn't seem to work A directory should have
     * a trailing file.separator
     * 
     * @param fileName
     * @return file
     * 
     * @exception IOException
     */
    public static File createNewFile(String fileName) throws IOException {
        File f = null;
        String path = null;
        int idx = fileName.lastIndexOf(File.separator);
        if (idx != -1) {
            path = fileName.substring(0, idx);
        }
        if (path != null) {
            f = new File(path);
            f.mkdirs();
        }
        if (!fileName.endsWith(File.separator)) {
            f = new File(fileName);
        }
        return f;
    }

    /** traps a bug.
     * use for programming errors where this could can "never be reached"
     * concatenates msg with "BUG" and throws {@link RuntimeException}
     * @param msg
     * @param t
     */
    public static void BUG(String msg, Throwable t) {
        msg = (msg == null || msg.trim().length() == 0) ? "" : "("+msg+")";
        throw new RuntimeException("BUG: "+msg+"should never throw: "+t.getMessage(), t);
    }
    
    /** traps a bug.
     * empty message.
     * @see #BUG(String, Throwable)
     * @param t
     */
    public static void BUG(Throwable t) {
        BUG("", t);
    }
    
    /** traps a bug.
     * @see #BUG(String, Throwable)
     * @param msg
     */
    public static void BUG(String msg) {
        BUG(msg, new RuntimeException());
    }

    /** convenience method for "not yet implemented".
     * @throws RuntimeException
     */
    public static void NYI() {
        throw new RuntimeException(Message.NYI.value);
    }
    /** extracts the su in brackets as a string.
     *
     *@param  value
     *@return The sU value
     */
    public static String getSU(String value) {
        String su = null;
// no brackets, returns null
        if (!value.endsWith(""+C_RBRAK)) {
            return su;
        }
        int idx = value.indexOf(C_LBRAK);
        if (idx == -1) {
            return su;
        }
        su = value.substring(idx + 1, value.length() - 1);
        try {
            new Double(su);
        } catch (NumberFormatException nfe) {
            su = null;
        }
        return su;
    }
    
    /** concatenate names into whitespace separated string.
     * @param names the names
     * @return string of type "_foo _bar"
     */
    public static String concatenate(List<String> names) {
    	StringBuffer sb = new StringBuffer();
    	for (String s : names) {
    		if (!s.startsWith("_") || s.indexOf(C_SPACE) != -1) {
    			throw new RuntimeException("Bad name: "+s);
    		}
    		sb.append(C_SPACE+s);
    	}
    	return (sb.length() == 0) ? "" : sb.substring(1).toString();
    }

/** character codes
    // these need converting to ISO values when I find the table
*/
    /** Greek characters, relative to a=alpha, \a=alpha
    */
    static Map<String, String> charMap = null;
    static Map<String, String> reverseCharMap = null;
        /// @cond DOXYGEN_STATIC_BLOCK_WORKAROUND
    static {
        charMap = new HashMap<String, String>();
        // lower greek
        charMap.put("\\a","[alpha]");
        charMap.put("\\b","[beta]");
        charMap.put("\\c","[chi]");
        charMap.put("\\d","[delta]");
        charMap.put("\\e","[epsilon]");
        charMap.put("\\f","[phi]");
        charMap.put("\\g","[gamma]");
        charMap.put("\\h","[eta]");
        charMap.put("\\i","[iota]");
        charMap.put("\\k","[kappa]");
        charMap.put("\\l","[lambda]");
        charMap.put("\\m","[mu]");
        charMap.put("\\n","[nu]");
        charMap.put("\\o","[omicron]");
        charMap.put("\\p","[pi]");
        charMap.put("\\q","[theta]");
        charMap.put("\\r","[rho]");
        charMap.put("\\s","[sigma]");
        charMap.put("\\t","[tau]");
        charMap.put("\\u","[upsilon]");
        charMap.put("\\w","[omega]");
        charMap.put("\\x","[xi]");
        charMap.put("\\y","[psi]");
        charMap.put("\\z","[zeta]");
         // upper greek
        charMap.put("\\A","[Alpha]");
        charMap.put("\\B","[Beta]");
        charMap.put("\\C","[Chi]");
        charMap.put("\\D","[Delta]");
        charMap.put("\\E","[Epsilon]");
        charMap.put("\\F","[Phi]");
        charMap.put("\\G","[Gamma]");
        charMap.put("\\H","[Eta]");
        charMap.put("\\I","[Iota]");
        charMap.put("\\K","[Kappa]");
        charMap.put("\\L","[Lambda]");
        charMap.put("\\M","[Mu]");
        charMap.put("\\N","[Nu]");
        charMap.put("\\O","[Omicron]");
        charMap.put("\\P","[Pi]");
        charMap.put("\\Q","[Theta]");
        charMap.put("\\R","[Rho]");
        charMap.put("\\S","[Sigma]");
        charMap.put("\\T","[Tau]");
        charMap.put("\\U","[Upsilon]");
        charMap.put("\\W","[Omega]");
        charMap.put("\\X","[Xi]");
        charMap.put("\\Y","[Psi]");
        charMap.put("\\Z","[Zeta]");
        // 32. Accents should be indicated by using the following codes before the letter to be modified (i.e. use
        //\'e for an acute e):
        //\' 	acute 	\" 	umlaut 	\= 	overbar
        charMap.put("\\'a","[aacute]");
        charMap.put("\\'e","[eacute]");
        charMap.put("\\'i","[iacute]");
        charMap.put("\\'o","[oacute]");
        charMap.put("\\'u","[uacute]");
        charMap.put("\\'A","[Aacute]");
        charMap.put("\\'E","[Eacute]");
        charMap.put("\\'I","[Iacute]");
        charMap.put("\\'O","[Oacute]");
        charMap.put("\\'U","[Uacute]");
        charMap.put("\\\"a","[auml]");
        charMap.put("\\\"e","[euml]");
        charMap.put("\\\"i","[iuml]");
        charMap.put("\\\"o","[ouml]");
        charMap.put("\\\"u","[uuml]");
        charMap.put("\\\"A","[Auml]");
        charMap.put("\\\"E","[Euml]");
        charMap.put("\\\"I","[Iuml]");
        charMap.put("\\\"O","[Ouml]");
        charMap.put("\\\"U","[Uuml]");
//\` 	grave  	\~ 	tilde  	\. 	overdot
        charMap.put("\\`a","[agrave]");
        charMap.put("\\`e","[egrave]");
        charMap.put("\\`i","[igrave]");
        charMap.put("\\`o","[ograve]");
        charMap.put("\\`u","[ugrave]");
        charMap.put("\\`A","[Agrave]");
        charMap.put("\\`E","[Egrave]");
        charMap.put("\\`I","[Igrave]");
        charMap.put("\\`O","[Ograve]");
        charMap.put("\\`U","[Ugrave]");
        charMap.put("\\~n","[ntilde]");
        charMap.put("\\~N","[Ntilde]");
//\^ 	circumflex  	\; 	ogonek 	\< 	hacek
        charMap.put("\\^a","[acirc]");
        charMap.put("\\^e","[ecirc]");
        charMap.put("\\^i","[icirc]");
        charMap.put("\\^o","[ocirc]");
        charMap.put("\\^u","[ucirc]");
        charMap.put("\\^A","[Acirc]");
        charMap.put("\\^E","[Ecirc]");
        charMap.put("\\^I","[Icirc]");
        charMap.put("\\^O","[Ocirc]");
        charMap.put("\\^U","[Ucirc]");
//\, 	cedilla  	\> 	Hungarian umlaut 	\( 	breve
        charMap.put("\\,c","[ccedil]");
        charMap.put("\\,C","[Ccedil]");
//These codes will always be followed by a non-whitespace character.
//33. Other special alphabetic characters should be indicated as follows:
//\%a 	a-ring  	\?i 	dotless i 	\&s 	German "ss"
//Capital letters may also be used in these codes, so an Angstrom symbol may be given as \%A.
        charMap.put("\\%a","[aring]");
        charMap.put("\\%A","[Aring]");
        charMap.put("\\&s","[ss]");
        charMap.put("\\%S","[SS]");
//\/o 	o-slash  	\/l 	Polish l (tex2html_wrap120) 	\/d 	barred d
        charMap.put("\\/o","[oslash]");
        charMap.put("\\/O","[OSlash]");
        charMap.put("\\/l","[lslash]");
        charMap.put("\\/L","[LSlash]");
        charMap.put("\\/d","[dbar]");
        charMap.put("\\/D","[Dbar]");
//35. Some other codes are accepted by convention. These are:
/*--
\% 	degree  	\\times 	
-- 	dash 	+-
--- 	single bond 	-+ 	tex2html_wrap_inline104
\\db 	double bond 	\\square 	square
\\tb 	triple bond 	\\neq 	tex2html_wrap_inline108
\\ddb 	delocalized double bond 	\\rangle 	>
\\sim 	~ 	\\langle 	<
(N.B. ~  is the code for subscript) 	\\rightarrow 	tex2html_wrap_inline114
\\simeq 	@ 	\\leftarrow 	tex2html_wrap_inline116
\\infty 	tex2html_wrap_inline102
--*/
        charMap.put("\\/d","[dbar]");
        charMap.put("\\/D","[Dbar]");
        charMap.put("\\%","[degree]");
        charMap.put("\\\\times","[times]");
        charMap.put("--","[dash]");
        charMap.put("+-","[plusminus]");
        charMap.put("---","[singlebond]");
        charMap.put("-+","[minusplus]");
        charMap.put("\\\\db","[doublebond]");
        charMap.put("\\\\square","[square]");
        charMap.put("\\\\tb","[triplebond]");
        charMap.put("\\\\neq","[neq]");
        charMap.put("\\\\ddb","[delocalizeddoublebond]");
        charMap.put("\\\\rangle",">");
        charMap.put("\\\\sim","~");
        charMap.put("\\\\langle","<");
        charMap.put("\\\\rightarrow","[rightarrow]");
        charMap.put("\\\\simeq","[simeq]");
        charMap.put("\\\\leftarrow","[leftarrow]");
        charMap.put("\\\\infty","[infty]");
        Iterator<String> keys = charMap.keySet().iterator();
        reverseCharMap = new HashMap<String, String>();
        while (keys.hasNext()) {
            String key = keys.next();
            reverseCharMap.put(charMap.get(key), key);
        }
        
    };
	/// @endcond
    /** decode escaped CIF character string.
    * @param s the string (starts with \)
    * @return the decoded string (null if invalid)
    */
    public static String decodeEscapedChar(String s) {
        return charMap.get(s);
    }
    /** encode into escaped CIF character string.
    * @param s the (character) to be encoded
    * @return the encoded string (null if invalid)
    */
    public static String encodeEscapedChar(String s) {
        return reverseCharMap.get(s);
    }
    /*--
      0 1 2 3 4 5 6 7 8 9 A B C D E F
00: NUL SOH STX ETX EOT ENQ ACK BEL BS HT LF VT FF CR SO SI
10: DLE DC1 DC2 DC3 DC4 NAK SYN ETB CAN EM SUB ESC FS GS RS US
20:   ! " # $ % & ' ( ) * + , - . /
30: 0 1 2 3 4 5 6 7 8 9 : ; < = > ?
40: @ A B C D E F G H I J K L M N O
50: P Q R S T U V W X Y Z [ \ ] ^ _
60: ` a b c d e f g h i j k l m n o
70: p q r s t u v w x y z { | } ~ DEL
*/
    /** is a character an ordinaryCIFChar.
    * chars [] // perhaps?
    * chars: !%&()*+,-./0123456789:<=>?@
    * ABCDEFGHIJKLMNOPQRSTUVWXYZ\^`
    * abcdefghijklmnopqrstuvwxyz{|}~
    * @param c the character
    * @return true if in set
    */
    public static boolean isOrdinaryChar(char c) {
        return (
            c == '[' ||
            c == ']' ||
            c == '!' ||
            c == '%' ||
            (c >= '(' && c <= '/') ||
            (c >= '0' && c <= '9') ||
            c == ':' ||
            (c >= '<' && c <= '@') ||
            (c >= 'A' && c <= 'Z') ||
            c == '\\' ||
            c == '^' ||
            c == '`' ||
            (c >= 'a' && c <= 'z') ||
            (c >= '{' && c <= '~')
        );
    }
    /** is character NonBlankChar.
    * <OrdinaryChar> | <double_quote> | '#' | '$' | <single_quote> | '_' |';' | '[' | ']'
    *@param c the character
    *@return true if in set
    */
    public static boolean isNonBlankChar(char c) {
        return (
            isOrdinaryChar(c) ||
            c == '"' ||
            c == '#' ||
            c == '$' ||
            c == '\'' ||
            c == '_' ||
            c == ';' ||
            c == '[' ||
            c == ']'
        );
    }
    /** is character TextLeadChar.
    * <OrdinaryChar> | <double_quote> | '#' | '$' | <single_quote> | '_' | <SP> | <HT> |'[' | ']'
    *@param c the character
    *@return true if in set
    */
    public static boolean isTextLeadChar(char c) {
        return (
            isOrdinaryChar(c) ||
            c == '"' ||
            c == '#' ||
            c == '$' ||
            c == '\'' ||
            c == '_' ||
            c == ' ' ||
            c == '\t' ||
            c == '[' ||
            c == ']'
        );
    }
    
    /** trim whitespace at line end
     * @param s to trim
     */
    public static String trimTrail(String s) {
    	String ss = null;
    	if (s != null) {
    		StringBuilder sb = new StringBuilder(s);
    		int l = s.length()-1;
    		while (l >= 0) {
    			if (Character.isWhitespace(sb.charAt(l))) {
    				sb.deleteCharAt(l);
    				l--;
    			} else {
    				break;
    			}
    		}
    		ss = sb.toString();
    	}
    	return ss;
    }
    /** is character AnyPrintChar.
    * <OrdinaryChar> | <double_quote> | '#' | '$' | <single_quote> | '_' | <SP> | <HT> | ';' | '[' | ']' yes
    *@param c the character
    *@return true if in set
    */
    public static boolean isAnyPrintChar(char c) {
        return (
            isOrdinaryChar(c) ||
            c == '"' ||
            c == '#' ||
            c == '$' ||
            c == '\'' ||
            c == '_' ||
            c == ' ' ||
            c == '\t' ||
            c == ';' ||
            c == '[' ||
            c == ']'
        );
    }
    /*--
    34. Superscripts and subscripts should be indicated by bracketing relevant characters with circumflex or tilde characters, thus:
    superscripts 	Csp^3^ 	for 	Csp3
    subscripts 	U~eq~ 	for 	Ueq
    implemented in conversion to HTML
    The closing symbol is essential to return to normal text.
    --*/

    /** translates all CIF escapes except sub/super into ISO entities.
     * @param s string to be edited
     * @return strings substributed with &foo;
     */
    public static String translateCIF2ISO(String s) {
    	StringBuffer sb = new StringBuffer();
    	List<String> escList = getCIFCharList();
    	int i = 0;
    	while (i < s.length()) {
            if (false) {
                ;//
    		} else if (s.substring(i).startsWith("\\\n")) {
    			// newline stuff;
    			sb.append(" ");
    			i += 2;
    		} else {
	    		boolean edited = false;
	    		for (String esc : escList) {
	    			if (s.substring(i).startsWith(esc)) {
	    				String isoString = charMap.get(esc);
	    				isoString = "&"+isoString.substring(1, isoString.length()-1)+";";
	    				sb.append(isoString);
	    				i += esc.length();
	    				edited = true;
	    				break;
	    			}
	    		}
	    		if (!edited) {
	    			sb.append(s.charAt(i));
	    			i++;
	    		}
    		}
    	}
    	return sb.toString();
    }

    /** translates all CIF escapes into ISO entities.
     * also adds HTML sub abd super. Maybe this should be separated.
     * @param s string to be edited
     * @return strings substributed with &foo;
     */
    public static String translateCIF2HTML(String s) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        boolean inSubs = false;
        boolean inSuper = false;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (false) {
                ;//
            } else if (c == '~') {
                if (inSubs) {
                    sb.append("</sub>");
                    inSubs = false;
                } else {
                    sb.append("<sub>");
                    inSubs = true;
                }
            } else if (c == '^') {
                if (inSuper) {
                    sb.append("</sup>");
                    inSuper = false;
                } else {
                    sb.append("<sup>");
                    inSuper = true;
                }
            } else {
                sb.append(s.charAt(i));
            }
            i++;
        }
        return sb.toString();
    }

    /** gets list of all CIF escape strings.
     * Nearly all - I got bored typing them in.
     * sorted in lexically dcescending order so that longer strings come first
     * @return list of escape strings 
     */
    public static List<String> getCIFCharList() {
    	List<String> cifCharList = new ArrayList<String>();
    	for (String s : charMap.keySet()) {
    		cifCharList.add(s);
    	}
    	Collections.sort(cifCharList);
    	Collections.reverse(cifCharList);
    	return cifCharList;
    }
    
    /** normalize a value.
    *
    * if a string trims leading and training whitespace
    * then normalize internal whitespace to single spaces
    * @param s the string to normalize
    * @return the normalized string
    */
    public static String normalize(String s) {
        s = s.trim();
        int l = s.length();
        int i = 0;
        boolean inWhite = false;
        StringBuffer sb = new StringBuffer();
        while (i < l) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!inWhite) {
                    sb.append(c);
                    inWhite = true;
                }
            } else {
                inWhite = false;
                sb.append(c);
            }
            i++;
        }
        s = sb.toString();
        return s;
    }

    /** normalize a value.
    *
    * if a string trims leading and training whitespace
    * then normalize internal whitespace to single spaces
    * @param s the string to normalize
    * @return the normalized string
    */
    public static String normalizeAll(String s) {
        s = s.trim();
        int l = s.length();
        int i = 0;
        boolean inWhite = false;
        StringBuffer sb = new StringBuffer();
        while (i < l) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c) || c == '\r' || c == '\n') {
                if (!inWhite) {
                    sb.append(' ');
                    inWhite = true;
                }
            } else {
                inWhite = false;
                sb.append(c);
            }
            i++;
        }
        s = sb.toString();
        return s;
    }

    /** normalize names.
     * creates lower case
     * replaces 'percent' and 'slash' by characters
     * @param name
     * @return name
     */
    public static String normalizeName(String name) {
    	String n = name.toLowerCase();
    	n = n.replace("_percent_", "%");
    	n = n.replace("_slash_", S_SLASH);
    	return n;
    }
    
    /** escape string with CIF quotes.
     * will add quotes if string includes whitespace or existing quotes.
     * @param s
     * @return quoted string (null if s is null)
     */
    public static String getQuotedString(String s) {
    	String ss = null;
    	boolean quote = false;
    	boolean newline = false;
    	if (s == null) {
        	// empty string
        } else if (s.equals("")) {
    		quote = true;
    	} else if (s.startsWith("_")) {
    		// underscore
    		quote = true;
    	} else {
    		// embedded whitespace?
	    	for (int i = 0; i < s.length(); i++) {
	    		char c = s.charAt(i);
	    		if (c == '\n') {
	    			newline = true;
	    			break;
	    		}
	    		if (Character.isWhitespace(c)) {
	    			quote = true;
	    		}
	    	}
    	}
    	if (newline) {
    		ss = "\n;\n"+s+";";
    	} else if (quote) {
    		ss = "'"+s+"'";
    	} else {
    		ss = s;
    	}
    	return ss;
    }
    
    /** finds longest common start string on nameList.
     * splits at "_"
     * this may be the category
     * 
     * @param nameList
     * @return the string ("" if nameList is of size 0,1)
     */
    public static String getGuessedCategory(List<String> nameList) {
    	String s = "";
    	if (nameList.size() > 1) {
    		List<String[]> splitList = new ArrayList<String[]>();
    		for (String name : nameList) {
    			splitList.add(name.split("_"));
    		}
    		int ip;
    		boolean ok = true;
    		for (ip = 0; true;ip++) {
    			if (ip >= splitList.get(0).length) {
    				break;
    			}
    			String chunk = splitList.get(0)[ip];
    			for (String[] split : splitList) {
        			if (ip >= split.length) {
        				ok = false;
        				break;
        			}
        			String ss = split[ip];
        			if (!ss.equals(chunk)) {
        				ok = false;
        				break;
        			}
    			}
    			if (!ok) {
    				break;
    			}
    		}
    		for (int i = 0; i < ip; i++) {
    			s += S_UNDER+splitList.get(0)[i];
    		}
    	}
    	// double underscore?
    	return (s.startsWith(S_UNDER+S_UNDER)) ? s.substring(1) : s;
    }
	/** creates String from value and su.
	 * example value=1.234 and su=0.012 and nDecimal= 3 gives 1.234(12)
	 * example value=1.234 and su=0.012 and nDecimal= 2 gives 1.23(1)
	 * uses round() for truncation
	 * @param value
	 * @param su error in SAME units as value. if Double.isNaN(su) omits it
	 * @param nDecimal < 0 allows n sig figs of precision; 0 omits point; else emits nDecimals after point
	 * @return su value as string
	 */
	static String createSuValue(double value, double su, int nDecimal) {
        // do the best we can for negative
        if (nDecimal < 0) {
            String sus = ""+su;
            int idx = sus.indexOf(S_PERIOD);
            if (idx != -1) {
                sus = sus.substring(idx+1);
                int ndec = 0;
                while (sus.startsWith("0")) {
                    sus = sus.substring(1);
                    ndec++;
                }
                nDecimal = ndec +(-nDecimal);
            } else {
                nDecimal = -nDecimal;
            }
        }
		double scale = Math.pow(10., (double) nDecimal);
		long iScale = Math.round(scale);
		long iv = Math.round(value * scale);
		String ds = ""+((double)iv / (double)iScale);
		if (nDecimal <= 0) {
			int idx = ds.indexOf(S_PERIOD);
			if (idx != -1) {
				ds = ds.substring(0, idx);
			}
		}
        String s =  ""+ ds;
        if (!Double.isNaN(su)) {
            long is = Math.round(su * scale);
            s += "(" + is + ")";
        }
        return s;
	}
	/** split CIF number and su to doubles.
	 * process field of form double(integer) or double
	 * note: there are value fields such as "I>2\s(I)"
	 * which spuriously fit this form.
	 * returns either null (not parsable or two doubles. The first
	 * is the value; if there is sd the second is the su, else 0
	 * @param value string of form 1.234(12) or 1.234; if null returns null
	 * @return double[2]numericValue 1.234 and su 0.012 or 0; (or null)
	 */
	public static double[] getNumericValueAndSu(String value) {
		double[] values = null;
		if (value != null) {
			int idx0 = value.indexOf("(");
			int idx1 = value.indexOf(")");
			// brackets?
			if (idx0 != -1 && idx0 < idx1 && idx1 == value.length()-1) {
				String sv = null;
				try {
					values = new double[2];
					sv = value.substring(0, idx0);
					values[0] = new Double(sv);
					values[1] = new Double(value.substring(idx0+1, idx1));
				} catch (NumberFormatException e) {
					throw new RuntimeException("cannot parse as value(su): "+e);
				}
				int idx = sv.indexOf(S_PERIOD);
				if (idx != -1) {
					int ndec = sv.length()-idx-1;
					values[1] /= Math.pow(10., (double) ndec);
				}
			} else {
				try {
					double d = new Double(value);
					values = new double[2];
					values[0] = d;
					values[1] =0;
				} catch (NumberFormatException e) {
					; // not a number
				}
			}
		}
		return values;
	}
	/** index of Strng in list.
	 * 
	 * @param name to search for
	 * @param names
	 * @param ignoreCase if true ignores case
	 * @return index or -1 if not found
	 */
	public static int indexOf(String name, String[] names, boolean ignoreCase) {
		int idx = -1;
		int i = 0;
		for (String a : names) {
			boolean matches = 
				(ignoreCase) ? a.equalsIgnoreCase(name) : a.equals(name);
			if (matches) {
				idx = i;
				break;
			}
			i++;
		}
		return idx;
	}
	/** is string in list of strings.
	 * 
	 * @param name
	 * @param names
	 * @param ignoreCase
	 * @return index of string in list (-1 if not found)
	 */
	public static int isStringInList(String name, List<String> names, boolean ignoreCase) {
		int idx = -1;
		int i = 0;
		for (String a : names) {
			boolean matches = 
				(ignoreCase) ? a.equalsIgnoreCase(name) : a.equals(name);
			if (matches) {
				idx = i;
				break;
			}
			i++;
		}
		return idx;
	}
    
    /** simple reroute to system.out.
     * 
     * @param s
     */
    public static void outputString(String s) {
        System.out.println(s);
    }
    
    /** creates resource from filename.
     * uses ClassLoader.getResource()
     * @param filename name relative to classroot
     * @return url or null
     */
    public static URL getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("NULL resource filename ");
        }
        ClassLoader l = Thread.currentThread().getContextClassLoader();
        URL url = l.getResource(filename);
        if (url == null) {
            throw new RuntimeException("No resource with name " + filename);
        }
        return url;
    }
    
    /** gets file from build path components.
     * pm286 is not quite sure how it does this...
     * @author ojd20@cam.ac.uk
     * @param path
     * @return file or null
     * @throws URISyntaxException
     */
    public static File getResourceFile(String... path) throws URISyntaxException {
    	LOG.debug("RF "+path[0]);
        String buildPath = CIFUtil.buildPath(Separator.URL, path);
        LOG.debug("BP "+buildPath);
        URL url =CIFUtil.class.getClassLoader()
                .getResource(buildPath);
        LOG.debug("U "+url);
        File f = (url == null) ? null : new File(url.toURI());
        if (f != null) {
        	LOG.debug("file "+f.getAbsolutePath());
        }
        return f;
    }

    /** gets build path from its components.
     * @param separator
     * @param parts
     * @return build path concatenated with File.separatorChar
     */
    public static String buildPath(Separator separator, String... parts) {
        StringBuilder sb = new StringBuilder(parts.length * 20);
        for (String part : parts) {
            sb.append(part).append(separator.value);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    /**
     * get temporary directory - mainly for testing methods with ouputs. calls
     * mkdirs() if does not exist
     * 
     * @return tempaorary directory.
     */
    public static File getTEMP_DIRECTORY() {
        if (!TEMP_DIRECTORY.exists()) {
            boolean ok = TEMP_DIRECTORY.mkdirs();
            if (!ok) {
                throw new RuntimeException("Cannot create temporary directory : "
                        + TEMP_DIRECTORY.getAbsolutePath());
            }
        }
        return TEMP_DIRECTORY;
    }

    /** are all values "?".
     * @param values
     * @return true if all values are "?"
     */
    public static boolean isIndeterminate(List<String> values) {
    	boolean isIndeterminate = true;
    	for (String value : values) {
    		if (!S_QUERY.equals(value.trim())) {
    			isIndeterminate = false;
    			break;
    		}
    	}
    	return isIndeterminate;
    }

    /** are all values ".".
     * @param values
     * @return true if all values are "."
     */
    public static boolean isDefault(List<String> values) {
    	boolean isDefault = true;
    	for (String value : values) {
    		if (!S_PERIOD.equals(value)) {
    			isDefault = false;
    			break;
    		}
    	}
    	return isDefault;
    }

    /** is value ".".
     * @param value
     * @return true if value is "."
     */
    public static boolean isDefault(String value) {
    	value = (value == null) ? null : value.trim();
    	return (S_PERIOD.equals(value));
    }

    /** is value "?".
     * @param value
     * @return true if value is "?"
     */
    public static boolean isIndeterminateValue(String value) {
    	value = (value == null) ? null : value.trim();
    	return (S_QUERY.equals(value));
    }
    
	/**
	 * remove all control (non-printing) characters
	 * 
	 * @param s Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String stripISOControls(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int l = s.length();
		for (int i = 0; i < l; i++) {
			char ch = s.charAt(i);
			if (Character.isWhitespace(ch) || !Character.isISOControl(ch)) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Replace a character in a String with a supplied String.
	 * 
	 * @param value - the String containing the char to be replaced.
	 * @param pos - the position of the char to be replaced.
	 * @param replacementChar - the char to add <code>value</code> as
	 * a replacement.
	 * 
	 * @return the altered version of <code>value</code>
	 */
	public static String replaceChar(String value, int pos, String replacementStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(value.substring(0,pos));
		sb.append(replacementStr);
		sb.append(value.substring(pos+1));
		return sb.toString();
	}
	
	public static String trimRight(String s) {
		StringBuffer sb = new StringBuffer(s);
		int l = sb.length();
		while (l > 0 && Character.isWhitespace(sb.charAt(--l))) {
			sb.deleteCharAt(l);
		}
		return sb.toString();
	}
}
