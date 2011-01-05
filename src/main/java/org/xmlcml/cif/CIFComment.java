package org.xmlcml.cif;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * a comment in a CIF.
 * 
 * COMCIFS opinion differs as to whether comments are part of a CIF or should be
 * stripped on reading. This class allows comments to be held in the
 * CIFDocument.
 * 
 * this implementation allows for neighbouring comment lines to be concatenated
 * 
 * Comment values are stored WITHOUT leading hashes
 */
public class CIFComment extends AbstractTextElement {
	/**
	 * tag.
	 */
	public final static String TAG = "comment";

	static Logger logger = Logger.getLogger(CIFComment.class.getName());

//	protected String cc_value;

	protected List<String> cc_values;

	/**
	 * create an empty comment.
	 */
	public CIFComment() {
		super(TAG);
		cc_values = null;
	}

	/**
	 * create a comment with given value.
	 * 
	 * @param value
	 */
	public CIFComment(String value) {
		this();
		setValue(value);
	}

	/**
	 * create a comment with given value.
	 * 
	 * @param values
	 *            strings to be concatenated into a single comment separated by
	 *            newlines
	 */
	public CIFComment(String[] values) {
		this();
		StringBuffer sb = new StringBuffer();
		for (String v : values) {
			sb.append(v);
            sb.append(S_NL);
		}
		setValue(sb.substring(1).toString());
        cc_values = new ArrayList<String>();
        for (String v : values) {
            cc_values.add(v);
            sb.append(S_NL);
        }
	}

	/**
	 * sets value of comment.
	 * 
	 * @param value
	 *            does NOT include the leading #
	 */
	public void setValue(String value) {
        cc_values = null;
		this.setTextValue(value);
	}

	/** write comment to HTML.
	 * outputs as <span class="comment">...</span>
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeHTML(Writer w) throws IOException {
        if (cc_values != null) {
            for (String v : cc_values) {
                String s = toHTML(v);
                w.write(s);
            }
        } else {
            String cc_value = this.getValue();
    		if (cc_value != null) {
    			w.write(toHTML(cc_value));
    		}
		}
	}
        
    private String toHTML(String v) {
        v = CIFUtil.translateCIF2ISO(v);
        v = CIFUtil.translateCIF2HTML(v);
        return "<span class='comment'>" +
            v + 
            "</span>\n";
    }
    
    /** writes comment as CIF.
     * appends newline
     * @param w
     *            writer
     * @throws IOException
     */
    public void writeCIF(Writer w) throws IOException {
		if (cc_values != null) {
    		for (String v : cc_values) {
    			w.write(C_HASH);
                w.write(v);
                w.write(C_NL);
    		}
        } else {
            String cc_value = this.getValue();
    		if (cc_value != null) {
                w.write(C_HASH);
                w.write(cc_value);
                w.write(C_NL);
    		}
        }
	}
}
