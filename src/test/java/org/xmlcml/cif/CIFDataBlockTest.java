package org.xmlcml.cif;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * test CIFBlock.
 * 
 * @author pm286
 * 
 */
public class CIFDataBlockTest extends CIFTest {

    
    private static int nTest = 0;
    /** set up*/
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("CIFDataBlockTest");
        }
    }

	static CIFDataBlock readBlock(String fileroot, int blockNum) throws CIFException,
		IOException, URISyntaxException {
		CIF cif = readCIF(fileroot);
		return cif.getDataBlockList().get(blockNum);
	}

	static CIFDataBlock readBlock(File file, int blockNum) throws CIFException,
	IOException {
		CIF cif = readCIF(file);
		return cif.getDataBlockList().get(blockNum);
	}

    static CIFDataBlock createBlock() {
        CIF cif = createCIF();
        List<CIFDataBlock> blockList = cif.getDataBlockList();
        Assert.assertEquals("blocks ", 2, blockList.size());
        return blockList.get(0);
    }


	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.writeHTML(Writer)'
	 */
	@Test
	public void testWriteHTML() {
        StringWriter w = new StringWriter();
        CIFDataBlock block = createBlock();
        try {
        block.writeHTML(w);
        w.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        String s = w.toString();
        String expect = 
            "" +
            "<h3>I</h3><br/><br/>\n"+
            "_foo&nbsp;<i>bar</i><br/>\n"+
            "_plugh&nbsp;<i>xyzzy</i><br/>\n"+
            "<br/>\n"+
            "<table border=\"1\">\n" +
//            "<br/>"+
            "<tr>\n"+
            "<th>_a</th>\n"+
            "<th>_b</th>\n"+
            "</tr>\n"+
            "<tr>\n"+
            "<td>1</td>\n"+
            "<td>2</td>\n"+
            "</tr>\n"+
            "<tr>\n"+
            "<td>3</td>\n"+
            "<td>4</td>\n"+
            "</tr>\n"+
            "<tr>\n"+
            "<td>5</td>\n"+
            "<td>6</td>\n"+
            "</tr>\n"+
            "</table>\n"+
            "<br/>\n"+
            "";
        Assert.assertEquals("to HTML", expect, s);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.normalize()'
	 */
	@Test
	public void testNormalize() {
        CIFDataBlock block = createBlock();
        block.normalize();
        String expected = 
            "data_I\n"+
            "_foo bar\n"+
            "_plugh xyzzy\n"+
            "loop_\n"+
            "_a\n"+
            "_b\n"+
            "1 2\n"+
            "3 4\n"+
            "5 6\n";
        Assert.assertEquals("normalize", expected, block.toCIFString());
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.canonicalize()'
	 */
	@Test
	public void testCanonicalize() {
        CIFDataBlock block = createBlock();
        try {
        block.canonicalize();
        String expected =
            "data_I\n"+
            "_foo bar\n"+
            "_plugh xyzzy\n"+
            "loop_\n"+
            "_a\n"+
            "_b\n"+
            "1 2\n"+
            "3 4\n"+
            "5 6\n";
        Assert.assertEquals("canonicalize", expected, block.toCIFString());
        } catch (RuntimeException e) {
            Assert.assertEquals("canonicalize", CIFUtil.Message.NYI.value, e.getMessage());
        }
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.add(CIFComment)'
	 */
	@Test
	public void testAddCIFComment() {
        CIFDataBlock block = createBlock();
        String block0S = block.toCIFString();
        String COMMENT = "this is a comment";
        CIFComment comment = new CIFComment(COMMENT);
        try {
            block.add(comment);
        } catch (CIFException e) {
            Assert.fail("should never throw "+e);
        }
        String expected = block0S + "#"+COMMENT+"\n";
        Assert.assertEquals("add comment", expected, block.toCIFString());
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.add(CIFItem)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testAddCIFItem() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		List<CIFItem> itemList = block.getItemList();
		Assert.assertEquals("simple1 block1 item count", 6, itemList.size());
		CIFItem item = block.getChildItem("_journal_year");
		Assert.assertNotNull("simple1 block1 known item", item);
		Assert.assertEquals("simple1 block1 item value", "2002", item
				.getValue());
		CIFItem newItem = null;
		newItem = new CIFItem("_foo", "bar");
		block.add(newItem, true);
		itemList = block.getItemList();
		Assert.assertEquals("simple1 block1 item count", 7, itemList.size());
		// retrieve added item?
		item = block.getChildItem("_foo");
		Assert.assertNotNull("simple1 block1 new item", item);
		Assert.assertEquals("simple1 block1 item value", "bar", item
				.getValue());
		// and check we are still OK
		item = block.getChildItem("_journal_year");
		Assert.assertNotNull("simple1 block1 known item", item);
		Assert.assertEquals("simple1 block1 item value", "2002", item
				.getValue());
		// add date?
		try {
			block.add(newItem, true);
			// FIXME
//			Assert.fail("should throw date item");
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
		// add date?
		try {
			block.add(new CIFItem("_journal_year", "foo"), true);
//			Assert.fail("should throw duplicate item");
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.add(CIFLoop)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testAddCIFLoop() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		List<CIFLoop> loopList = block.getLoopList();
		Assert.assertEquals("simple1 block1 loop count", 1, loopList.size());
		CIFLoop loop = block.getChildLoop("_publ_author_name");
		Assert.assertNotNull("simple1 block1 known loop", loop);
		Assert.assertEquals("simple1 block1 loop value", "_publ_author_name",
				loop.getNameList().get(0));
		CIFLoop newLoop = null;
		try {
			newLoop = new CIFLoop(new String[] { "_foo", "_bar" },
					new String[] { "foo1", "bar1", "foo2", "bar2" });
		} catch (CIFException e) {
			throw new RuntimeException("should not throw " + e);
		}
		try {
			block.add(newLoop);
		} catch (CIFException e) {
			throw new RuntimeException("should not throw " + e);
		}
		loopList = block.getLoopList();
		Assert.assertEquals("simple1 block1 loop count", 2, loopList.size());
		// retrieve added loop?
		loop = block.getChildLoop("_foo");
		Assert.assertNotNull("simple1 block1 new loop", loop);
		Assert.assertEquals("simple1 block1 loop value", "_foo", loop
				.getNameList().get(0));
		// and check we are still OK
		CIFLoop pubLoop = block.getChildLoop("_publ_author_name");
		Assert.assertNotNull("simple1 block1 known loop", pubLoop);
		Assert.assertEquals("simple1 block1 loop value", "_publ_author_name",
				pubLoop.getNameList().get(0));
		// add date?
		try {
			block.add(newLoop);
			// FIXME
			Assert.fail("should throw duplicate loop");
		} catch (CIFException e) {
			Assert.assertEquals("simple1 block1 duplicate loop",
					"Duplicate column name in loop: _foo", e.getMessage());
		}
		// add date?
		try {
			block.add(pubLoop);
			Assert.fail("should throw date loop");
		} catch (CIFException e) {
			Assert.assertEquals("simple1 block1 date loop",
					"Duplicate column name in loop: _publ_author_name", e
							.getMessage());
		}

	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.getItemList()'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetItemList() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		List<CIFItem> itemList = block.getItemList();
		Assert.assertEquals("simple1 block1 item count", 6, itemList.size());
		Assert.assertEquals("simple1 block1 item 0", "_journal_date_accepted",
				itemList.get(0).getName());
		Assert.assertEquals("simple1 block1 item 5",
				"_publ_contact_author_fax", itemList.get(5).getName());

	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.getLoopList()'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetLoopList() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 1);
		Assert.assertNotNull("simple1 block2 not null", block);
		List<CIFLoop> loopList = block.getLoopList();
		Assert.assertEquals("simple1 block1 item count", 3, loopList.size());
		Assert.assertEquals("simple1 block1 loop 0",
				"_symmetry_equiv_pos_as_xyz", loopList.get(0).getNameList()
						.get(0));
		Assert.assertEquals("simple1 block1 item 2", "_atom_site_label",
				loopList.get(2).getNameList().get(0));
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.getChildItem(String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetChildItemString() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		CIFItem item = block.getChildItem("_publ_contact_author_email");
		Assert.assertNotNull("simple1 block1 item not null", item);
		Assert.assertEquals("simple1 block1 item ", "alough@chem.utoronto.ca",
				item.getValue());
		item = block.getChildItem("_publ_contact_author_http");
		Assert.assertNull("simple1 block1 item null", item);

	}

	/**
	 * Test method for
	 * 'uk.co.demon.ursus.cif.CIFBlock.getChildItemValue(String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetChildItemValue() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		String itemValue = block
				.getChildItemValue("_publ_contact_author_email");
		Assert.assertNotNull("simple1 block1 itemValue not null", itemValue);
		Assert.assertEquals("simple1 block1 item ", "alough@chem.utoronto.ca",
				itemValue);
		itemValue = block.getChildItemValue("_publ_contact_author_http");
		Assert.assertNull("simple1 block1 item null", itemValue);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.getChildItem(String,
	 * String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetChildItemStringString() throws CIFException,
			IOException, URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 0);
		Assert.assertNotNull("simple1 block1 not null", block);
		CIFItem item = block.getChildItem("_publ_contact_author_email",
				"alough@chem.utoronto.ca");
		Assert.assertNotNull("simple1 block1 item not null", item);
		item = block.getChildItem("_publ_contact_author_email",
				"blough@chem.utoronto.ca");
		Assert.assertNull("simple1 block1 item null", item);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.getChildLoop(String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testGetChildLoop() throws CIFException, IOException,
			URISyntaxException {
		CIFDataBlock block = readBlock(file1_cif, 1);
		Assert.assertNotNull("simple1 block2 not null", block);
		CIFLoop loop = block.getChildLoop("_atom_type_symbol");
		Assert.assertNotNull("simple1 block1 loop not null", loop);
		Assert.assertEquals("loop column count ", 5, loop.getNameList().size());
		loop = block.getChildLoop("_atom_type_scat_source");
		Assert.assertNotNull("simple1 block1 loop not null", loop);
		Assert.assertEquals("loop column count ", 5, loop.getNameList().size());
		loop = block.getChildLoop("_atom_type_scatttering_source");
		Assert.assertNull("simple1 block1 loop null", loop);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIFBlock.setId(String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testSetId() throws CIFException, IOException,
			URISyntaxException {
		CIF cif = readCIF(file1_cif);
		CIFDataBlock dataBlock = cif.getDataBlockById("7");
		Assert.assertNotNull("get data block by old id", dataBlock);
		try {
			dataBlock.setId("2,3-Dihydro-2-hydroxybenzofuran-3-one");
		} catch (CIFException e) {
			Assert.fail("should not throw exception " + e);
		}
		dataBlock = cif.getDataBlockById("7");
		Assert.assertNull("get data block by old id", dataBlock);
		dataBlock = cif
				.getDataBlockById("2,3-Dihydro-2-hydroxybenzofuran-3-one");
		Assert.assertNotNull("get data block by new id", dataBlock);
		// bad id (not allowed spaces)
		try {
			dataBlock.setId("2,3-Dihydro-2-hydroxybenzofuran-3-oic acid");
			Assert.fail("should throw bad id exception ");
		} catch (CIFException e) {
			Assert.assertEquals(
							"bad id exception",
							"Whitespace ( ) in id [2,3-Dihydro-2-hydroxybenzofuran-3-oic acid]",
							e.getMessage());
		}
	}

}
