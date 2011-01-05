/**
 * 
 */
package org.xmlcml.cif;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlcml.cif.CIFElement.CIFAttribute;

/**
 * @author pm286
 * 
 */
public class CIFLoopTest extends CIFTestBase {

    CIFLoop LOOP = null;
    static CIFLoop LOOP1 = null;
    static String cifS = null;
    static String cifoutS = null;

    private static int nTest = 0;
    /**
     */
    @Before
    public void setUp() {
        LOOP = CIFDataBlockTest.createBlock().getLoopList().get(0);
        if (nTest++ == 0) {
            header("CIFLoopTest");
        }
    }

    static CIFLoop getNumericLoop() {
        List<String> names = new ArrayList<String>();
        names.add("_foo");
        names.add("_plugh");
        names.add("_bar");
        List<String> values = new ArrayList<String>();
        values.add("12.3(1)");
        values.add("23.4(2)");
        values.add("34.3(3)");
        values.add("45.6(4)");
        values.add("56.7(5)");
        values.add("67.8(6)");
        values.add("78.9(7)");
        values.add("89.0(8)");
        values.add("90.1(9)");
        values.add("101.2(10)");
        values.add("112.3(11)");
        values.add("123.4(12)");
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            CIFUtil.BUG(e);
        }
        boolean failOnError = true;
        loop.processSu(failOnError);
        return loop;
    }

    private static CIFLoop makeCIFLoop1() {
    	cifS =
    	"data_grot\n"+
    	"loop_\n"+
        "_atom_type_symbol\n"+
        "_atom_type_description\n"+
        "_atom_type_scat_dispersion_real\n"+
        "_atom_type_scat_dispersion_imag\n"+
        "_atom_type_scat_source\n"+
        "'C' 'C' 0.0033 0.0016\n"+
    "; International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
    ";\n"+
    "    'H' 'H' 0.0000 0.0000\n"+
    "; International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
    ";\n"+
    "   'N' 'N' 0.0061 0.0033\n"+
    "; International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
    ";\n"+
    "   'O' 'O' 0.0106 0.0060\n"+
    "; International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
    ";\n"+
    		"";
    	cifoutS =
        	"loop_\n"+
            "_atom_type_symbol\n"+
            "_atom_type_description\n"+
            "_atom_type_scat_dispersion_real\n"+
            "_atom_type_scat_dispersion_imag\n"+
            "_atom_type_scat_source\n"+
            "C C 0.0033 0.0016 \n"+
        ";\n" +
        " International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
        ";\n"+
        "H H 0.0000 0.0000 \n"+
        ";\n" +
        " International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
        ";\n"+
        "N N 0.0061 0.0033 \n"+
        ";\n" +
        " International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
        ";\n"+
        "O O 0.0106 0.0060 \n"+
        ";\n" +
        " International Tables Vol. C, Tables 4.2.6.8 and 6.1.1.4\n"+
        ";\n"+
        		"";
    	
            CIF cif = null;
            try {
                cif = CIF.createFromString(cifS);
            } catch (CIFException e) {
                CIFUtil.BUG(e);
            }
    	CIFLoop loop = cif.getDataBlockById("grot").getLoopList().get(0);
    	return loop;
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#writeHTML(java.io.Writer)}.
     */
    @Test
    public final void testWriteHTML() {
        StringWriter w = new StringWriter();
        try {
            LOOP.writeHTML(w);
            w.close();
        } catch (IOException e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("html", "<br/>\n" + "<table border=\"1\">\n" + "<tr>\n"
                + "<th>_a</th>\n" + "<th>_b</th>\n" + "</tr>\n" + "<tr>\n"
                + "<td>1</td>\n" + "<td>2</td>\n" + "</tr>\n" + "<tr>\n"
                + "<td>3</td>\n" + "<td>4</td>\n" + "</tr>\n" + "<tr>\n"
                + "<td>5</td>\n" + "<td>6</td>\n" + "</tr>\n" + "</table>\n"
                + "<br/>\n" + "", w.toString());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#normalize()}.
     */
    @Test
    public final void testNormalize() {
        LOOP.normalize();
        Assert.assertEquals("normalize", "loop_\n" + "_a\n" + "_b\n" + "1 2\n"
                + "3 4\n" + "5 6\n", LOOP.toCIFString());

        CIFLoop loop2 = CIFTest.createCIF().getDataBlockList().get(1)
                .getLoopList().get(1);
        loop2.normalize();

    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#canonicalize()}.
     */
    @Test
    public final void testCanonicalize() {
        try {
            LOOP.canonicalize();
            Assert.assertEquals("canonicalize", "loop_\n" + "_a\n" + "_b\n" + "1 2\n"
                    + "3 4\n" + "5 6\n", LOOP.toCIFString());
            CIFLoop loop2 = CIFTest.createCIF().getDataBlockList().get(1)
                    .getLoopList().get(1);
            loop2.canonicalize();
        } catch (RuntimeException e) {
            Assert.assertEquals("canonicalize", CIFUtil.Message.NYI.value, e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#writeCIF(java.io.Writer)}.
     */
    @Test
    public final void testWriteCIF() {
        StringWriter w = new StringWriter();
        try {
            LOOP.writeCIF(w);
            w.close();
        } catch (IOException e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("to string", 
        	"loop_ _a _b 1 2 3 4 5 6", 
        	w.toString().replaceAll("[\n\t ]+", " ").trim());
        
        CIFLoop loop1 = makeCIFLoop1();
        w = new StringWriter();
        try {
            loop1.writeCIF(w);
            w.close();
        } catch (IOException e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("to string", 
        		cifoutS.replaceAll("[\n\t ]+", " ").trim(), 
        		w.toString().replaceAll("[\n\t ]+", " ").trim());
        
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#CIFLoop()}.
     */
    @Test
    public final void testCIFLoop() {
        CIFLoop loop = new CIFLoop();
        Assert.assertNotNull("null name", loop.getNameList());
        Assert.assertEquals("null name", 0, loop.getNameList().size());
        Assert.assertNotNull("null values", loop.getValueList());
        Assert.assertEquals("null values", 0, loop.getValueList().size());
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#CIFLoop(java.util.List, java.util.List)}.
     */
    @Test
    public final void testCIFLoopListOfStringListOfString() {
        List<String> names = new ArrayList<String>();
        names.add("_foo");
        names.add("_bar");
        List<String> values = new ArrayList<String>();
        values.add("11 ");
        values.add("12");
        values.add("21 ");
        values.add("22");
        values.add("31 ");
        values.add("32");
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            CIFUtil.BUG(e);
        }
        Assert.assertEquals("loop size", 2, loop.getNameList().size());
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#CIFLoop(nu.xom.Element, boolean)}.
     */
    @Test
    public final void testCIFLoopElementBoolean() {
        Element elem = new Element("loop");
        elem.addAttribute(new Attribute(CIFAttribute.NAMES.value, "_foo _bar"));
        // elem.addAttribute(new Attribute(CIFAttribute.DATATYPE.value,
        // DataType.NUMB.value));
        // elem.addAttribute(new Attribute(CIFAttribute.SU.value, "0.12 0.13
        // 0.14 0.15"));
        elem.addAttribute(new Attribute(CIFAttribute.VALUES.value,
                "23.45 34.56 45.67 56.78"));

        boolean failOnError = true;
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(elem, failOnError);
        } catch (CIFException e) {
            CIFUtil.BUG(e);
        }
        List<String> names = loop.getNameList();
        Assert.assertNotNull("loop names not null", names);
        Assert.assertEquals("name size", 2, names.size());
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#getSU(int, int)}.
     */
    @Test
    public final void testGetSU() {
        CIFLoop loop = getNumericLoop();
        try {
            String s = loop.getSU(0, 0);
            Assert.assertEquals("su", "1", s);
        } catch (RuntimeException e) {
            Assert.assertEquals("get su", e.getMessage(), CIFUtil.Message.NYI.value);
        }
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#getColumnCount()}.
     */
    @Test
    public final void testGetColumnCount() {
        int cols = LOOP.getColumnCount();
        Assert.assertEquals("get columns", 2, cols);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#getRowCount()}.
     */
    @Test
    public final void testGetRowCount() {
        int rows = LOOP.getRowCount();
        Assert.assertEquals("get columns", 3, rows);
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#getNumericColumnValues(int)}.
     */
    @Test
    public final void testGetNumericColumnValuesInt() {
        CIFLoop loop = getNumericLoop();
        double[] values = loop.getNumericColumnValues(0);
        Assert.assertEquals("col values", 4, values.length);
        Assert.assertEquals("col values", 12.3, values[0], 0.00001);
        Assert.assertEquals("col values", 45.6, values[1], 0.00001);
        Assert.assertEquals("col values", 78.9, values[2], 0.00001);
        Assert.assertEquals("col values", 101.2, values[3], 0.00001);
        values = loop.getNumericColumnValues(3);
        Assert.assertNull("bad column", values);
        values = loop.getNumericColumnValues(-1);
        Assert.assertNull("bad column", values);
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#getNumericColumnValues(java.lang.String)}.
     */
    @Test
    public final void testGetNumericColumnValuesString() {
        CIFLoop loop = getNumericLoop();
        double[] values = loop.getNumericColumnValues("_foo");
        Assert.assertEquals("col values", 12.3, values[0], 0.00001);
        Assert.assertEquals("col values", 45.6, values[1], 0.00001);
        Assert.assertEquals("col values", 78.9, values[2], 0.00001);
        Assert.assertEquals("col values", 101.2, values[3], 0.00001);
        values = loop.getNumericColumnValues("_a");
        Assert.assertNull("bad column", values);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#getColumnCells(int)}.
     */
    @Test
    public final void testGetColumnCells() {
        CIFLoop loop = getNumericLoop();
        List<CIFTableCell> cellList = loop.getColumnCells(0);
        Assert.assertEquals("col values", 4, cellList.size());
        CIFTableCell cell = cellList.get(0);
        Assert.assertEquals("cell 0", 12.3, cell.getNumericValue().doubleValue(), 0.001);
        Assert.assertEquals("cell 0", 0.1, cell.getSu().doubleValue(), 0.001);
        Assert.assertEquals("cell 0", CIFElement.DataType.NUMB.value, cell.getDataType().value);
        Assert.assertEquals("cell 0", "12.3(1)", cell.getValue());
        cellList = loop.getColumnCells(3);
        Assert.assertNull("bad column", cellList);
        cellList = loop.getColumnCells(-1);
        Assert.assertNull("bad column", cellList);
    }

    /**
     * Test method for {@link uk.co.demon.ursus.cif.CIFLoop#getCell(int, int)}.
     */
    @Test
    public final void testGetCell() {
        CIFLoop loop = getNumericLoop();
        CIFTableCell cell = loop.getCell(0, 0);
        Assert.assertNotNull("cell 0 0", cell);
        Assert.assertEquals("cell 0 0", 12.3, cell.getNumericValue().doubleValue(), 0.001);
        Assert.assertEquals("cell 0 0", 0.1, cell.getSu().doubleValue(), 0.001);
        Assert.assertEquals("cell 0 0", CIFElement.DataType.NUMB.value, cell.getDataType().value);
        Assert.assertEquals("cell 0 0", "12.3(1)", cell.getValue());
        cell = loop.getCell(2,1);
        Assert.assertNotNull("cell 2 1", cell);
        Assert.assertEquals("cell 2 1", 89.0, cell.getNumericValue().doubleValue(), 0.001);
        Assert.assertEquals("cell 2 1", 0.8, cell.getSu().doubleValue(), 0.001);
        Assert.assertEquals("cell 2 1", CIFElement.DataType.NUMB.value, cell.getDataType().value);
        Assert.assertEquals("cell 2 1", "89.0(8)", cell.getValue());
        Assert.assertNotNull("cell 0 2", loop.getCell(0, 2));
        Assert.assertNotNull("cell 3 0", loop.getCell(3, 0));
        Assert.assertNotNull("cell 3 2", loop.getCell(3, 2));
        Assert.assertNull("cell -1 1", loop.getCell(-1, 1));
        Assert.assertNull("cell 4 1", loop.getCell(4, 1));
        Assert.assertNull("cell 1 -1", loop.getCell(1, -1));
        Assert.assertNull("cell 1 3", loop.getCell(1, 3));
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#getCanonicalizedNames()}.
     */
    @Test
    public final void testGetCanonicalizedNames() {
        CIFLoop loop = getNumericLoop();
        String[] names = loop.getCanonicalizedNames();
        Assert.assertEquals("canonical names", 3, names.length);
        Assert.assertEquals("canonical names", "_bar", names[0]);
        Assert.assertEquals("canonical names", "_foo", names[1]);
        Assert.assertEquals("canonical names", "_plugh", names[2]);
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFLoop#getCanonicalizedLoop()}.
     */
    @Test
    public final void testGetCanonicalizedLoop() {
        CIFLoop loop = getNumericLoop();
        String s = loop.toCIFString();
        Assert.assertEquals("pre-canonical loop", 
                "loop_\n"+
                "_foo\n"+
                "_plugh\n"+
                "_bar\n"+
                "12.3(1) 23.4(2) 34.3(3)\n" +
                "45.6(4) 56.7(5) 67.8(6)\n"+
                "78.9(7) 89.0(8) 90.1(9)\n"+
                "101.2(10) 112.3(11) 123.4(12)\n"+
                "", s);
        // note this sorts the columns into order
        CIFLoop loop1 = loop.getCanonicalizedLoop();
        s = loop1.toCIFString();
        Assert.assertEquals("canonical loop", 
            "loop_\n"+
            "_bar\n"+
            "_foo\n"+
            "_plugh\n"+
            "34.3(3) 12.3(1) 23.4(2)\n"+
            "67.8(6) 45.6(4) 56.7(5)\n"+
            "90.1(9) 78.9(7) 89.0(8)\n"+
            "123.4(12) 101.2(10) 112.3(11)\n"+
            "", s);
    }

    /**
     * Test method for
     * {@link uk.co.demon.ursus.cif.CIFElement#writeXML(java.io.Writer)}.
     */
    @Test
    public final void testWriteXML() {
        try {
    		Writer w = new FileWriter(CIFUtil.createNewFile(CIFUtil.getTEMP_DIRECTORY() + File.separator + "loop.xml"));
            loop.writeXML(w);
            w.close();
        } catch (IOException e) {
            CIFUtil.BUG(e);
        }
    }

    CIFLoop readLoop(String fileroot, int blockNum, int loopNum)
	    throws Exception {
		CIFDataBlock cifBlock = CIFDataBlockTest.readBlock(fileroot, blockNum);
		return cifBlock.getLoopList().get(loopNum);
	}

    CIFLoop readLoop(File file, int blockNum, int loopNum)
	    throws Exception {
		CIFDataBlock cifBlock = CIFDataBlockTest.readBlock(file, blockNum);
		return cifBlock.getLoopList().get(loopNum);
	}

    CIFLoop loop = CIFDataBlockTest.createBlock().getLoopList().get(0);

    /**
     * test get cell public CIFTableCell getCell(int rowNum, int colNum)
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void fooGetCell() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 1, 2);
        Assert.assertNotNull(FILE1+" loop not null", loop);
        CIFTableCell cell = loop.getCell(0, 0);
        Assert.assertEquals("cell 00 value", "O1", cell.getValue());
        cell = loop.getCell(1, 1);
        Assert.assertEquals("cell 11 value", "0.598(2)", cell.getValue());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFItem.processSu()'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testProcessSu1() throws Exception {
        CIFLoop loop = readLoop(acta1_cif, 1, 2);
        Assert.assertNotNull("simple1 block1 not null", loop);
        CIFTableCell cell = loop.getCell(1, 1);
        Assert.assertEquals("cell 11 value", "0.598(2)", cell.getValue());
        Assert.assertNull("cell 11", cell.getNumericValue());
        Assert.assertNull("cell 11", cell.getSu());
        boolean failOnError = true;
        loop.processSu(failOnError);
        Assert.assertEquals("cell 11 value", "0.598(2)", cell.getValue());
        Assert.assertEquals("cell 11", 0.598, cell.getNumericValue()
                .doubleValue(), 0.0001);
        Assert.assertEquals("cell 11", 0.002, cell.getSu().doubleValue(),
                0.0001);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.writeHTML(Writer)'
     */
    @Test
    public void testWriteHTML1() {
        String[] names = new String[] { "_a", "_b" };
        String[] values = new String[] { "a1", "b1", "a2", "b2", "a3", "b3" };
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        StringWriter w = new StringWriter();
        try {
            loop.writeHTML(w);
            w.close();
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("HTML", "<br/>\n" + "<table border=\"1\">\n"
                + "<tr>\n" + "<th>_a</th>\n" + "<th>_b</th>\n" + "</tr>\n"
                + "<tr>\n" + "<td>a1</td>\n" + "<td>b1</td>\n" + "</tr>\n"
                + "<tr>\n" + "<td>a2</td>\n" + "<td>b2</td>\n" + "</tr>\n"
                + "<tr>\n" + "<td>a3</td>\n" + "<td>b3</td>\n" + "</tr>\n"
                + "</table>\n" + "<br/>\n", w.toString());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.toCIFString()'
     */
    @Test
    public void testToCIFString() {
        CIFLoop loop = getNumericLoop();
        String s = loop.toCIFString();
        Assert.assertEquals("to CIFString", "" +
            "loop_\n"+
            "_foo\n"+
            "_plugh\n"+
            "_bar\n"+
            "12.3(1) 23.4(2) 34.3(3)\n"+
            "45.6(4) 56.7(5) 67.8(6)\n"+
            "78.9(7) 89.0(8) 90.1(9)\n"+
            "101.2(10) 112.3(11) 123.4(12)\n"+
            "", s);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.writeCIF(Writer)'
     */
    @Test
    public void testWriteCIF1() {
        String[] names = new String[] { "_a", "_b" };
        String[] values = new String[] { "a1", "b1", "a2", "b2",
                "a12345678901234567890123456789012345678901234567890",
                "b12345678901234567890123456789012345678901234567890", "a3",
                "b3" };
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        StringWriter w = new StringWriter();
        try {
            loop.writeCIF(w);
            w.close();
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        String out = w.toString();
        Assert.assertEquals("CIF", "loop_\n" + "_a\n" + "_b\n"
                + "a1 b1\n" + "a2 b2\n"
                + "a12345678901234567890123456789012345678901234567890\n"
                + "b12345678901234567890123456789012345678901234567890\n"
                + "a3 b3\n", out);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.CIFLoop(List<String>,
     * List<String>)'
     */
    @Test
    public void testCIFLoopListOfStringListOfString1() {
        String[] names = new String[] { "_a", "_b" };
        String[] values = new String[] { "a1", "b1", "a2", "b2", "a3", "b3" };
        List<String> nameList = new ArrayList<String>();
        for (String name : names) {
            nameList.add(name);
        }
        List<String> valueList = new ArrayList<String>();
        for (String value : values) {
            valueList.add(value);
        }
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(nameList, valueList);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        StringWriter w = new StringWriter();
        try {
            loop.writeCIF(w);
            w.close();
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("CIF", "loop_\n" + "_a\n" + "_b\n"
                + "a1 b1\n" + "a2 b2\n" + "a3 b3" +"\n", w.toString());
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.CIFLoop(String[],
     * String[])'
     */
    @Test
    public void testCIFLoopStringArrayStringArray() {
        String[] names = new String[] { "_a", "_b" };
        String[] values = new String[] { "a1", "b1", "a2", "b2", "a3", "b3" };
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        StringWriter w = new StringWriter();
        try {
            loop.writeCIF(w);
            w.close();
        } catch (Exception e) {
            Assert.fail("should not throw " + e);
        }
        Assert.assertEquals("CIF", "loop_\n" + "_a\n" + "_b\n"
                + "a1 b1\n" + "a2 b2\n" + "a3 b3" + "\n", w.toString());

    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getNameList()'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetNameList() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        List<String> nameList = loop.getNameList();
        Assert.assertEquals("names", 2, nameList.size());
        Assert.assertEquals("names 0", "_publ_author_name", nameList.get(0));
        Assert.assertEquals("names 1", "_publ_author_address", nameList.get(1));
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getValueList()'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetValueList() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        List<String> valueList = loop.getValueList();
        Assert.assertEquals("values", 6, valueList.size());
        Assert.assertEquals("values 0", "Kresge, A. J.", valueList.get(0));
        Assert.assertEquals("values 1", "\n" + "Department of Chemistry\n"
                + "University of Toronto\n" + "Toronto\n" + "Ontario\n"
                + "Canada M5S 3H6\n", valueList.get(1));
        Assert.assertEquals("values 2", "Lough, Alan J.", valueList.get(2));
        Assert.assertEquals("values 3", "\n" + "Department of Chemistry\n"
                + "University of Toronto\n" + "Toronto\n" + "Ontario\n"
                + "Canada M5S 3H6\n", valueList.get(1));
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getLoopValue(int, int)'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetLoopValue() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        Assert.assertEquals("values 0,0", "Kresge, A. J.", loop.getLoopValue(0,
                0));
        Assert.assertEquals("values 1,1", "\n" + "Department of Chemistry\n"
                + "University of Toronto\n" + "Toronto\n" + "Ontario\n"
                + "Canada M5S 3H6\n", loop.getLoopValue(1, 1));
        Assert.assertEquals("values 1,0", "Lough, Alan J.", loop.getLoopValue(
                1, 0));
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getColumnValues(String)'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetColumnValuesString() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        List<String> values = loop.getColumnValues("_publ_author_name");
        Assert.assertEquals("column 0", 3, values.size());
        Assert.assertEquals("column 0,2", "Meng, Q.", values.get(2));
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getColumnValues(int)'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetColumnValuesInt() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        List<String> values = loop.getColumnValues(0);
        Assert.assertEquals("column 0", 3, values.size());
        Assert.assertEquals("column 0,2", "Lough, Alan J.", values.get(1));
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.getColumnNumber(String)'
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws CIFException
     */
    @Test
    public void testGetColumnNumber() throws Exception {
        CIFLoop loop = readLoop(file1_cif, 0, 0);
        int col = loop.getColumnNumber("_publ_author_name");
        Assert.assertEquals("column 0", 0, col);
        col = loop.getColumnNumber("_publ_author_address");
        Assert.assertEquals("column 0", 1, col);
        col = loop.getColumnNumber("_publ_author_http");
        Assert.assertEquals("column 0", -1, col);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.selectColumns(int[])'
     */
    @Test
    public void testSelectColumns() {
        String[] names = new String[] { "_a", "_b", "_c" };
        String[] values = new String[] { "a1", "b1", "c1", "a2", "b2", "c2",
                "a3", "b3", "c3" };
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        int[] colSelect = { 2, 1 };
        CIFLoop newLoop = null;
        try {
            newLoop = loop.selectColumns(colSelect);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        String cif = newLoop.toCIFString();
        Assert.assertEquals("new loop", "loop_\n" + "_c\n" + "_b\n"
                + "c1 b1\n" + "c2 b2\n" + "c3 b3" + "\n", cif);
    }

    /**
     * Test method for 'uk.co.demon.ursus.cif.CIFLoop.selectRows(int[])'
     */
    @Test
    public void testSelectRows() {
        String[] names = new String[] { "_a", "_b", "_c" };
        String[] values = new String[] { "a1", "b1", "c1", "a2", "b2", "c2",
                "a3", "b3", "c3" };
        CIFLoop loop = null;
        try {
            loop = new CIFLoop(names, values);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        int[] rowSelect = { 2, 1 };
        CIFLoop newLoop = null;
        try {
            newLoop = loop.selectRows(rowSelect);
        } catch (CIFException e) {
            Assert.fail("should not throw " + e);
        }
        String cif = newLoop.toCIFString();
        Assert.assertEquals("new loop", "loop_\n" + "_a\n" + "_b\n"
                + "_c\n" + "a3 b3 c3\n" + "a2 b2 c2\n", cif);
    }


}
