package org.xmlcml.cif;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** tests comment class.
 * 
 * @author pm286
 *
 */
public class CIFCommentTest extends CIFTestBase {

    private String COMMENT = "I am a comment";
    private String COMMENT1 = "new comment";
    private String[] COMMENTS = new String[] {
            "comment 1",
            "comment 2",
            "comment 3",
    };
    
    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.writeHTML(Writer)'
     */
    @Test
    public void testWriteHTML() {
        CIFComment comment = new CIFComment(COMMENT);
        StringWriter w = new StringWriter();
        try {
            comment.writeHTML(w);
            w.close();
        } catch (Exception e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("html", "<span class='comment'>"+COMMENT+"</span>\n", w.toString());
        // strings
        comment = new CIFComment(COMMENTS);
        w = new StringWriter();
        try {
            comment.writeHTML(w);
            w.close();
        } catch (Exception e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("html", 
                "<span class='comment'>"+COMMENTS[0]+"</span>\n" +
                "<span class='comment'>"+COMMENTS[1]+"</span>\n" +
                "<span class='comment'>"+COMMENTS[2]+"</span>\n" +
                        "", w.toString());
    }

    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.toCIFString()'
     */
    @Test
    public void testToCIFString() {
        CIFComment comment = new CIFComment(COMMENT);
        String s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#"+COMMENT+"\n", s);
        // strings
        comment = new CIFComment(COMMENTS);
        s = comment.toCIFString();
        Assert.assertEquals("cif comment", 
                "#"+COMMENTS[0]+"\n" +
                "#"+COMMENTS[1]+"\n" +
                "#"+COMMENTS[2]+"\n" +
                        "", s);
    }

    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.CIFComment()'
     */
    @Test
    public void testCIFComment() {
        CIFComment comment = new CIFComment();
        String s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#\n", s);
    }

    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.CIFComment(String)'
     */
    @Test
    public void testCIFCommentString() {
        CIFComment comment = new CIFComment(COMMENT);
        String s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#"+COMMENT+"\n", s);
    }

    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.CIFComment(String[])'
     */
    @Test
    public void testCIFCommentStringArray() {
        CIFComment comment = new CIFComment(COMMENTS);
        String s = comment.toCIFString();
        Assert.assertEquals(
            "cif comment", 
            "#comment 1\n" +
            "#comment 2\n" +
            "#comment 3" +
            "\n", s);
    }

    /** Test method for 'uk.co.demon.ursus.cif.CIFComment.setValue(String)'
     */
    @Test
    public void testSetValue() {
        CIFComment comment = new CIFComment(COMMENT);
        String s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#"+COMMENT+"\n", s);
        comment.setValue(COMMENT1);
        s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#"+COMMENT1+"\n", s);
        
        comment = new CIFComment(COMMENTS);
        s = comment.toCIFString();
        Assert.assertEquals("cif comment", 
            "#comment 1\n" +
            "#comment 2\n" +
            "#comment 3\n" +
            "", s);
        comment.setValue(COMMENT1);
        s = comment.toCIFString();
        Assert.assertEquals("cif comment", "#"+COMMENT1+"\n", s);
    }
    
    private static int nTest = 0;
    /** set up*/
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("CIFCommentTest");
        }
    }
}
