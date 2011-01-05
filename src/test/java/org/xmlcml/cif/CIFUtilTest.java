/**
 * 
 */
package org.xmlcml.cif;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pm286
 *
 */
public class CIFUtilTest extends CIFTestBase {

    private static int nTest = 0;
    /**
     */
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("CIFUtilTest");
        }
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#getSU(java.lang.String)}.
     */
    @Test
    public final void testGetSU() {
        String s = "123(4)";
        String su = CIFUtil.getSU(s);
        Assert.assertEquals("get su ", "4", su);
        s = "123.1(4)";
        su = CIFUtil.getSU(s);
        Assert.assertEquals("get su ", "4", su);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#decodeEscapedChar(java.lang.String)}.
     */
    @Test
    public final void testDecodeEscapedChar() {
        String s = "\\a"; 
        String ss = CIFUtil.decodeEscapedChar(s);
        Assert.assertEquals("decode", "[alpha]", ss);
        s = "\\b"; 
        ss = CIFUtil.decodeEscapedChar(s);
        Assert.assertEquals("decode", "[beta]", ss);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#encodeEscapedChar(java.lang.String)}.
     */
    @Test
    public final void testEncodeEscapedChar() {
        String s = "[alpha]"; 
        String ss = CIFUtil.encodeEscapedChar(s);
        Assert.assertEquals("encode alpha", "\\a", ss);
        s = "a"; 
        ss = CIFUtil.encodeEscapedChar(s);
        Assert.assertNull("encode nonencodable", ss);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#isOrdinaryChar(char)}.
     */
    @Test
    public final void testIsOrdinaryChar() {
        char c = 'a'; 
        Assert.assertTrue("is ordinary", CIFUtil.isOrdinaryChar(c));
        c = 0; 
        Assert.assertFalse("is ordinary", CIFUtil.isOrdinaryChar(c));
        c = 7; 
        Assert.assertFalse("is ordinary", CIFUtil.isOrdinaryChar(c));
        c = '\\'; 
        Assert.assertTrue("is ordinary", CIFUtil.isOrdinaryChar(c));
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#isNonBlankChar(char)}.
     */
    @Test
    public final void testIsNonBlankChar() {
        char c = 'a'; 
        Assert.assertTrue("is non-blank", CIFUtil.isNonBlankChar(c));
        c = ' '; 
        Assert.assertFalse("is blank", CIFUtil.isNonBlankChar(c));
        c = '\t'; 
        Assert.assertFalse("is blank", CIFUtil.isNonBlankChar(c));
        c = '\n'; 
        Assert.assertFalse("is blank", CIFUtil.isNonBlankChar(c));
        c = '\r'; 
        Assert.assertFalse("is blank", CIFUtil.isNonBlankChar(c));
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#isTextLeadChar(char)}.
     */
    @Test
    public final void testIsTextLeadChar() {
        char c = 'a'; 
        Assert.assertTrue("is textLead car", CIFUtil.isTextLeadChar(c));
        c = '['; 
        Assert.assertTrue("is textLead [", CIFUtil.isTextLeadChar(c));
        c = '~'; 
        Assert.assertTrue("is textLead ~", CIFUtil.isTextLeadChar(c));
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#isAnyPrintChar(char)}.
     */
    @Test
    public final void testIsAnyPrintChar() {
        char c = 'a'; 
        Assert.assertTrue("a", CIFUtil.isAnyPrintChar(c));
        c = '['; 
        Assert.assertTrue("[", CIFUtil.isAnyPrintChar(c));
        c = '\t'; 
        Assert.assertTrue("tab", CIFUtil.isAnyPrintChar(c));
        c = '\n'; 
        Assert.assertFalse("NL", CIFUtil.isAnyPrintChar(c));
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#translateCIF2ISO(java.lang.String)}.
     */
    @Test
    public final void testTranslateCIF2ISO() {
        String s = "\\a"; 
        String ss = CIFUtil.translateCIF2ISO(s);
        Assert.assertEquals("translate alpha", "&alpha;", ss);
        s = "\\b"; 
        ss = CIFUtil.translateCIF2ISO(s);
        Assert.assertEquals("translate beta", "&beta;", ss);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#normalize(java.lang.String)}.
     */
    @Test
    public final void testNormalize() {
        String s = " a    b ";
        String ss = CIFUtil.normalize(s);
        Assert.assertEquals("normalize", "a b", ss);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#getQuotedString(java.lang.String)}.
     */
    @Test
    public final void testGetQuotedString() {
        String s = "abc";
        String ss = CIFUtil.getQuotedString(s);
        Assert.assertEquals("to string", "abc", ss);
        s = "a b c";
        ss = CIFUtil.getQuotedString(s);
        Assert.assertEquals("to string", "'a b c'", ss);
        s = " a b c ";
        ss = CIFUtil.getQuotedString(s);
        Assert.assertEquals("to string", "' a b c '", ss);
        s = "";
        ss = CIFUtil.getQuotedString(s);
        Assert.assertEquals("to string", "''", ss);
        s = "a b\nc";
        ss = CIFUtil.getQuotedString(s);
        Assert.assertEquals("to string", "\n;\na b\nc;", ss);
   }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#translateCIF2HTML(java.lang.String)}.
     */
    @Test
    public final void testTranslateCIF2HTML() {
        String s = "a~1~ is b^1^";
        String ss = CIFUtil.translateCIF2HTML(s);
        Assert.assertEquals("html ", "a<sub>1</sub> is b<sup>1</sup>", ss);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#getGuessedCategory(java.util.List)}.
     */
    @Test
    public final void testGetCategory() {
        List<String> nameList = new ArrayList<String>();
        for (String s : new String[]{"_atom_site_x", "_atom_site_aniso_a11"}) {
            nameList.add(s);
        }
        String cat = CIFUtil.getGuessedCategory(nameList);
        Assert.assertEquals("category", cat, "_atom_site");
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFUtil#getNumericValueAndSu(java.lang.String)}.
     */
    @Test
    public final void testGetNumericValueAndSu() {
        String s = "123(4)";
        double[] vsu = CIFUtil.getNumericValueAndSu(s);
        Assert.assertEquals("get value su ", 2, vsu.length);
        Assert.assertEquals("get value su ", 123, vsu[0], 0.000001);
        Assert.assertEquals("get value su ", 4, vsu[1], 0.000001);
        s = "123.1(4)";
        vsu = CIFUtil.getNumericValueAndSu(s);
        Assert.assertEquals("get value su ", 2, vsu.length);
        Assert.assertEquals("get value su ", 123.1, vsu[0], 0.000001);
        Assert.assertEquals("get value su ", 0.4, vsu[1], 0.000001);
    }

}
