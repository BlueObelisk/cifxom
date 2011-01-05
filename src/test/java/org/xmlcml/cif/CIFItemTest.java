/**
 * 
 */
package org.xmlcml.cif;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import nu.xom.Attribute;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cif.CIFElement.CIFAttribute;
import org.xmlcml.cif.CIFElement.DataType;

/**
 * @author pm286
 *
 */
public class CIFItemTest extends CIFTestBase {

    CIFItem item0 = new CIFItem();

    CIFItem item1 = null;

    static CIFItem ITEM = null;
    static String cifS = "" +
            "data_I\n" +
            "_FoO    BaR\n" +
            "   _foo1   '  bar2  bar21'\n" +
            "_foo2\n" +
            "  '_bar2'\n"+
    "_foo3\n" +
    "; first line\n" +
    ";\n"+
    "_foo4\n" +
    "; first line\n" +
    "     second line\n" +
    "third line\n" +
    ";\n";
    static CIFDataBlock BLOCK = null;
    
    private static int nTest = 0;
    
    
    /** setup.
     */
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("CIFItemTest");
        }
        ITEM = CIFDataBlockTest.createBlock().getItemList().get(0);
        try {
            BLOCK = CIF.createFromString(cifS).getDataBlockList().get(0);
            item1 = new CIFItem("_foo", "bar");
        } catch (CIFException e) {
            CIFUtil.BUG(e);
        }
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#writeHTML(java.io.Writer)}.
     * @exception IOException
     */
    @Test
    public final void testWriteHTML() throws IOException {
        StringWriter w = new StringWriter();
        ITEM.writeHTML(w);
        w.close();
        Assert.assertEquals("html item", "_foo&nbsp;<i>bar</i><br/>\n", w.toString());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#normalize()}.
     */
    @Test
    public final void testNormalize() {
        CIFItem item = BLOCK.getItemList().get(0);
        item.normalize();
        String s = item.toCIFString();
        Assert.assertEquals("normalize", "_foo BaR\n", s);
        item = BLOCK.getItemList().get(1);
        item.normalize();
        s = item.toCIFString();
        Assert.assertEquals("normalize", "_foo1 'bar2 bar21'\n", s);
        item = BLOCK.getItemList().get(2);
        item.normalize();
        s = item.toCIFString();
        Assert.assertEquals("normalize", "_foo2 '_bar2'\n", s);
        item = BLOCK.getItemList().get(3);
        item.normalize();
        s = item.toCIFString();
        Assert.assertEquals("normalize", "_foo3 'first line'\n", s);
        item = BLOCK.getItemList().get(4);
        item.normalize();
        s = item.toCIFString();
        Assert.assertEquals("normalize", "_foo4 \n;\nfirst line\nsecond line\nthird line;\n", s);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#canonicalize()}.
     */
    @Test
    public final void testCanonicalize() {
        CIFItem item = BLOCK.getItemList().get(0);
        item.canonicalize();
        String s = item.toCIFString();
        Assert.assertEquals("canonicalize", "_foo BaR\n", s);
        item = BLOCK.getItemList().get(1);
        item.canonicalize();
        s = item.toCIFString();
        Assert.assertEquals("canonicalize", "_foo1 'bar2 bar21'\n", s);
        item = BLOCK.getItemList().get(2);
        item.canonicalize();
        s = item.toCIFString();
        Assert.assertEquals("canonicalize", "_foo2 '_bar2'\n", s);
        item = BLOCK.getItemList().get(3);
        item.canonicalize();
        s = item.toCIFString();
        Assert.assertEquals("canonicalize", "_foo3 'first line'\n", s);
        item = BLOCK.getItemList().get(4);
        item.canonicalize();
        s = item.toCIFString();
        Assert.assertEquals("canonicalize", "_foo4 \n;\nfirst line\nsecond line\nthird line;\n", s);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#writeCIF(java.io.Writer)}.
     * @exception IOException
     */
    @Test
    public final void testWriteCIF() throws IOException {
        CIFItem item = BLOCK.getItemList().get(0);
        StringWriter w = new StringWriter();
        item.writeCIF(w);
        w.close();
        Assert.assertEquals("writeCIF", "_foo BaR\n", w.toString());
        
        item = BLOCK.getItemList().get(1);
        w = new StringWriter();
        item.writeCIF(w);
        w.close();
        Assert.assertEquals("writeCIF", "_foo1 '  bar2  bar21'\n", w.toString());
        
        item = BLOCK.getItemList().get(2);
        w = new StringWriter();
        item.writeCIF(w);
        w.close();
        Assert.assertEquals("writeCIF", "_foo2 '_bar2'\n", w.toString());
        
        item = BLOCK.getItemList().get(3);
        w = new StringWriter();
        item.writeCIF(w);
        w.close();
        Assert.assertEquals("writeCIF", "_foo3 \n"+
";\n first line\n"+
";\n", w.toString());
        
        item = BLOCK.getItemList().get(4);
        w = new StringWriter();
        item.writeCIF(w);
        w.close();
        Assert.assertEquals("writeCIF", "_foo4 \n"+
";\n first line\n"+
"     second line\n"+
"third line\n"+
";\n", w.toString());
        

    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#CIFItem()}.
     */
    @Test
    public final void testCIFItem() {
        CIFItem item = new CIFItem();
        Assert.assertNull("item name null", item.getName());
        Assert.assertEquals("item value empty", "", item.getValue());
        StringWriter w = new StringWriter();
        try {
            item.writeCIF(w);
            Assert.fail("should throw null name");
        } catch (IOException e) {
            CIFUtil.BUG(e);
        } catch (RuntimeException e) {
            Assert.assertEquals("null name", CIFItem.Message.NULL_NAME.value, e.getMessage());
        }
        item.setName("_foo");
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value empty", "", item.getValue());
        try {
            item.writeCIF(w);
        } catch (IOException e) {
            CIFUtil.BUG(e);
        } catch (RuntimeException e) {
            Assert.assertEquals("null value", CIFItem.Message.NULL_VALUE.value, e.getMessage());
        }
        item.setTextValue("bar");
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value", "bar", item.getValue());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#CIFItem(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testCIFItemStringString() {
        CIFItem item = null;
        item = new CIFItem("_foo", "bar");
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value", "bar", item.getValue());
        StringWriter w = new StringWriter();
        try {
            item.writeCIF(w);
            w.close();
        } catch (IOException e) {
            CIFUtil.BUG(e);
        } catch (RuntimeException e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("CIFItem", "_foo bar\n", w.toString());
        try {
            item = new CIFItem("foo", "bar");
            Assert.fail("should trap bad name");
        } catch (RuntimeException e1) {
            Assert.assertEquals("bad name", CIFItem.Message.NO_UNDER.value+"foo", e1.getMessage());
        }
        String s = "_bar \n bar1   bar2";
        try {
            item = new CIFItem("_foo", s);
        } catch (RuntimeException e1) {
            Assert.assertEquals("bad name", CIFItem.Message.NO_UNDER.value+"foo", e1.getMessage());
        }
        Assert.assertEquals("item value", s, item.getValue());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#CIFItem(java.lang.String, double, double, int)}.
     */
    @Test
    public final void testCIFItemStringDoubleDoubleInt() {
        CIFItem item = null;
        try {
            item = new CIFItem("_foo", 23.12, 0.11, 2);
        } catch (CIFException e1) {
            CIFUtil.BUG(e1);
        }
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value", "23.12(11)", item.getValue());
        try {
            item = new CIFItem("_foo", 23.12, 0.11, 1);
        } catch (CIFException e1) {
            CIFUtil.BUG(e1);
        }
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value", "23.1(1)", item.getValue());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#CIFItem(nu.xom.Element, boolean)}.
     */
    @Test
    public final void testCIFItemElementBoolean() {
        Element elem = new Element("item");
        elem.addAttribute(new Attribute(CIFAttribute.NAME.value, "_foo"));
        elem.addAttribute(new Attribute(CIFAttribute.DATATYPE.value, DataType.NUMB.value));
        elem.addAttribute(new Attribute(CIFAttribute.SU.value, "0.12"));
        elem.addAttribute(new Attribute(CIFAttribute.NUMERICVALUE.value, "23.45"));
        
        boolean failOnError = true;
        CIFItem item = null;
        item = new CIFItem(elem, failOnError);
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertNotNull("item value not null", item.getNumericValue());
        Assert.assertEquals("item value", 23.45, item.getNumericValue(), 0.00001);
        Assert.assertEquals("item value", "23.45(12)", item.getValue());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFItem#setName(java.lang.String)}.
     */
    @Test
    public final void testSetName() {
        CIFItem item = new CIFItem("_foo", "bar");
        Assert.assertEquals("item name", "_foo", item.getName());
        Assert.assertEquals("item value", "bar", item.getValue());
        item .setName("_plugh");
        Assert.assertEquals("item name", "_plugh", item.getName());
        Assert.assertEquals("item value", "bar", item.getValue());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFElement#processSu(boolean)}.
     */
    @Test
    public final void testProcessSu() {
        CIFItem item = new CIFItem("_foo", "23.12(5)");
        boolean failOnError = true;
        item.processSu(failOnError);
        Assert.assertEquals("su", 0.05, item.getSu(), 0.0001);
        Assert.assertEquals("value", 23.12, item.getNumericValue(), 0.0001);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.writeHTML(Writer)'
     */
    @Test
    public void testWriteHTML1() {
        StringWriter w = new StringWriter();
        try {
            item1.writeHTML(w);
            w.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("item html", w.toString(),
                "_foo&nbsp;<i>bar</i><br/>\n");
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.toCIFString()'
     */
    @Test
    public void testToCIFString() {
        CIFItem item = new CIFItem("_foo", "bar");
        Assert.assertEquals("name", item.getName(), "_foo");
        Assert.assertEquals("value", "_foo bar\n", item.toCIFString());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.writeCIF(Writer)'
     */
    @Test
    public void testWriteCIF1() {
        StringWriter w = new StringWriter();
        CIFItem item = new CIFItem("_foo", "bar");
        w = new StringWriter();
        try {
	        item.writeCIF(w);
	        w.close();
        } catch (IOException e) {
        	Assert.fail("Should never throw "+e);
        }
        Assert.assertEquals("name", "_foo bar\n", w.toString());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.CIFItem(String, String)'
     */
    @Test
    public void testProcessSu2() {
        CIFItem item = new CIFItem("_foo", "1.234(12)");
        Assert.assertEquals("name", "_foo", item.getName());
        Assert.assertEquals("value", "1.234(12)", item.getValue());
        boolean failOnError = true;
        item.processSu(failOnError);
        Assert.assertEquals("name", "_foo", item.getName());
        Assert.assertEquals("value", "1.234(12)", item.getValue());
        Assert.assertEquals("numericValue", 1.234, item.getNumericValue(),
                0.0001);
        Assert.assertEquals("su", 0.012, item.getSu(), 0.0001);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.CIFItem(String, String)'
     */
    @Test
    public void testCIFItemStringString1() {
        CIFItem item = new CIFItem("_foo", "bar");
        Assert.assertEquals("name", item.getName(), "_foo");
        Assert.assertEquals("value", item.getValue(), "bar");
        try {
            item = new CIFItem("foo", "bar");
            Assert.fail("should throw: "+CIFItem.Message.NO_UNDER.value+"foo");
        } catch (RuntimeException e) {
            Assert.assertEquals("name",
                    CIFItem.Message.NO_UNDER.value+"foo", e.getMessage());
        }
        item = new CIFItem("_foo", "_bar");
        Assert.assertEquals("value", "_bar", item.getValue());
        Assert.assertEquals("value", "_foo '_bar'\n", item.toCIFString());
        item = new CIFItem("_foo", "bar plugh");
        Assert.assertEquals("value", "bar plugh", item.getValue());
        Assert.assertEquals("value", "_foo 'bar plugh'\n", item
                .toCIFString());
        item = new CIFItem("_foo", "");
        Assert.assertEquals("value", "", item.getValue());
        Assert.assertEquals("value", "_foo ''\n", item.toCIFString());
        try {
            item = new CIFItem("_", "bar plugh");
            Assert
                    .fail("should throw: "+CIFItem.Message.NO_UNDER.value+"_");
        } catch (RuntimeException e) {
            Assert.assertEquals("name",
                    CIFItem.Message.NO_UNDER.value+"_", e
                            .getMessage());
        }
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.CIFItem(String, double,
     * double, int)'
     */
    @Test
    public void testCIFItemStringDoubleDoubleInt1() {
        CIFItem item = null;
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 3);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert.assertEquals("value", "1.234(12)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 2);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert.assertEquals("value", "1.23(1)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            item = new CIFItem("_foo", 1234, 12, 0);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert.assertEquals("value", "1234(12)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.setName(String)'
     */
    @Test
    public void testSetName1() {
        CIFItem item = new CIFItem();
        item.setName("_foo");
        Assert.assertEquals("item name", "_foo", item.getName());
        try {
            item.setName("foo");
            Assert.fail("should throw "
                    + CIFItem.Message.NO_UNDER.value+"foo");
        } catch (RuntimeException e) {
            Assert.assertEquals("should throw ",
                    CIFItem.Message.NO_UNDER.value+"foo", e
                            .getMessage());
        }
        try {
            item.setName("_");
            Assert.fail("should throw "
                    + CIFItem.Message.NO_UNDER.value+"_");
        } catch (RuntimeException e) {
            Assert.assertEquals("should throw ",
                    CIFItem.Message.NO_UNDER.value+"_", e
                            .getMessage());
        }
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.setTextValue(String)'
     */
    @Test
    public void testSetTextValueString() {
        CIFItem item = new CIFItem();
        item.setTextValue("_foo");
        Assert.assertEquals("item name", "_foo", item.getValue());
        item.setTextValue("a b");
        Assert.assertEquals("item name", "a b", item.getValue());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.getName()'
     */
    @Test
    public void testGetName() {
        CIFItem item = new CIFItem();
        Assert.assertNull("item name", item.getName());
        item.setName("_foo");
        Assert.assertEquals("item name", "_foo", item.getName());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.getNumericValue()'
     */
    @Test
    public void testGetNumericValue() {
        CIFItem item = null;
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 3);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 2);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
            Assert.assertEquals("value", 1.23, item.getNumericValue(), 0.01);
            Assert.assertEquals("su", 0.01, item.getSu(), 0.01);
            Assert.assertEquals("value", "1.23(1)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.getSu()'
     */
    @Test
    public void testGetSu() {
        CIFItem item = null;
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 3);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 2);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
            Assert.assertEquals("value", 1.23, item.getNumericValue(), 0.01);
            Assert.assertEquals("su", 0.01, item.getSu(), 0.01);
            Assert.assertEquals("value", "1.23(1)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.getValue()'
     */
    @Test
    public void testGetTextValue() {
        CIFItem item = null;
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 3);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
            Assert.assertEquals("value", "1.234(12)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        try {
            item = new CIFItem("_foo", 1.234, 0.012, 2);
            Assert.assertEquals("name", item.getName(), "_foo");
            Assert
                    .assertEquals("value", 1.234, item.getNumericValue(),
                            0.00001);
            Assert.assertEquals("su", 0.012, item.getSu(), 0.00001);
            Assert.assertEquals("value", 1.23, item.getNumericValue(), 0.01);
            Assert.assertEquals("su", 0.01, item.getSu(), 0.01);
            Assert.assertEquals("value", "1.23(1)", item.getValue());
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
    }

    CIFItem readItem(File file, int blockNum, int itemNum)
            throws CIFException, IOException, URISyntaxException {
        CIFDataBlock block = CIFDataBlockTest.readBlock(file, blockNum);
        return block.getItemList().get(itemNum);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.processSu()'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testProcessSu1() throws CIFException, IOException,
            URISyntaxException {
        CIFItem item = readItem(acta1_cif, 1, 12);
        Assert.assertNotNull("simple1 block1 not null", item);
        Assert.assertEquals("item name", "_cell_length_c", item.getName());
        Assert.assertEquals("item value", "15.7861(7)", item.getValue());
        Assert.assertNull("numeric value", item.getNumericValue());
        Assert.assertNull("su value", item.getSu());
        boolean failOnError = true;
        item.processSu(failOnError);
        Assert.assertEquals("item name", "_cell_length_c", item.getName());
        Assert.assertEquals("item value", "15.7861(7)", item.getValue());
        Assert.assertNotNull("numeric value", item.getNumericValue());
        Assert.assertNotNull("su value", item.getSu());
        Assert.assertEquals("numeric value", 15.7861, item.getNumericValue()
                .doubleValue(), 0.00001);
        Assert.assertEquals("su value", 0.0007, item.getSu().doubleValue(),
                0.00001);

        item.setTextValue("foo");
        Assert.assertNotNull("simple1 block1 not null", item);
        Assert.assertEquals("item name", "_cell_length_c", item.getName());
        Assert.assertEquals("item value", "foo", item.getValue());
        Assert.assertNull("numeric value", item.getNumericValue());
        Assert.assertNull("su value", item.getSu());
    }
}
