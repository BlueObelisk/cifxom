package org.xmlcml.cif;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cif.CIFUtil.Separator;

/**
 * test CIF.
 * 
 * @author pm286
 * 
 */
public class CIFTest extends CIFTestBase {

    private static int nTest = 0;
    /**
     */
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("CIFTest");
        }
    }

    static String cifS = "" +
    "# a test CIF\n" +
    "data_I\n" +
    "_foo bar\n" +
    "_plugh xyzzy\n" +
    "loop_\n" +
    "_a\n" +
    "_b\n" +
    "1 2 3 4 5 6\n"+
    "data_II\n" +
    "_foo2 bar2\n" +
    "_plugh2 xyzzy2\n" +
    "loop_\n" +
    "_x\n" +
    "_y\n" +
    "21 22 23 24 25 26\n"+
    "loop_\n" +
    "_P\n" +
    "_qX\n" +
    "21 " +
    "\n; 22a  22b\n" +
    " 22c    22d\n" +
    ";" +
    " ' 23a  23b ' 24 25 26\n";
    
    static CIF createCIF() {
        CIF cif = null;
        try {
            cif = CIF.createFromString(cifS);
        } catch (CIFException e) {
            CIFUtil.BUG(e);
        }
        return cif;
    }

	Document readXML(String fileroot) {
		Document doc = null;
		try {
			doc = new Builder().build(new FileReader(CIFUtil.getResourceFile(SIMPLE_RESOURCE,
					fileroot + ".xml")));
		} catch (Exception e) {
			Assert.fail("reading " + fileroot + " should not throw " + e);
		}
		return doc;
	}

	Document readXML(File file) {
		Assert.assertNotNull("file not null", file);
		Assert.assertTrue("file should exist "+file.getAbsolutePath(), file.exists());
		Document doc = null;
		try {
			doc = new Builder().build(new FileReader(file));
		} catch (Exception e) {
			Assert.fail("reading " + file + " should not throw " + e);
		}
		return doc;
	}

	static CIF readCIF(String fileroot) throws CIFException, IOException,
		URISyntaxException {
		CIF cif = null;
		File file = CIFUtil.getResourceFile(SIMPLE_RESOURCE, fileroot + ".cif");
		Assert.assertNotNull("test cif file should exist in : "+SIMPLE_RESOURCE+"" +
				" = "+fileroot, file);
		cif = CIF.createFromFile(file.getAbsolutePath());
		return cif;
	}

	static CIF readCIF(File file) throws CIFException, IOException {
		CIF cif = null;
		Assert.assertNotNull("cif file should not be null: "+file, file);
		Assert.assertTrue("cif file should exist: "+file.getAbsolutePath(), file.exists());
		cif = CIF.createFromFile(file.getAbsolutePath());
		return cif;
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF(Document, boolean)'
	 * 
	 * @todo use getResourceFile paradigm
	 */
	@Test
	public void testCIFDocumentBoolean() {
		Document doc = null;
		try {
			doc = readXML(acta1_xml);
		} catch (Exception e) {
			Assert.fail("reading acta1.xml should not throw " + e);
		}
		CIF cif = null;
		try {
			cif = new CIF(doc, true);
		} catch (Exception e) {
			CIFUtil.BUG("reading acta1.xml should not throw ", e);
		}
		try {
			Writer w = new FileWriter(CIFUtil.createNewFile(CIFUtil.getTEMP_DIRECTORY() +
					File.separator + "acta1.xml.cif.out"));
			cif.writeCIF(w);
			w.close();
		} catch (Exception e) {
            e.printStackTrace();
			CIFUtil.BUG(e);
		}
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.createFromXML(String)'
	 * 
	 * @throws IOException
	 * @throws CIFException
	 * @throws URISyntaxException
	 */
	@Test
	public void testCreateFromXMLFile() throws Exception {
//		String s = new Builder().build(acta1_xml).toXML();
		CIF cif = CIF.createFromXMLFile(acta1_xml.toString(), true);
		Assert.assertNotNull("cif not null ", cif);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.createFromCIF(String)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testCreateFromFile() throws CIFException, IOException,
			URISyntaxException {
		CIF cif = CIF.createFromFile(acta1_cif.getAbsolutePath());
		Assert.assertNotNull("acta1.cif", cif);
		Assert.assertEquals("acta1 block count", 2, cif.getDataBlockList()
				.size());
		List<CIFDataBlock> blockList = cif.getDataBlockList();
		CIFDataBlock dataBlock0 = blockList.get(0);
		Assert.assertEquals("block should be global ", "global", dataBlock0
				.getId());
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.add(CIFDataBlock)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testAddDataBlock() throws CIFException, IOException,
			URISyntaxException {
		CIF cif = CIF.createFromFile(acta1_cif.getAbsolutePath());
		Assert.assertEquals("acta1 block count", 2, cif.getDataBlockList()
				.size());
		Assert.assertEquals("acta1 block1 id ", "global", cif
				.getDataBlockList().get(0).getId());
		CIFDataBlock dataBlock = null;
		try {
			dataBlock = new CIFDataBlock("foo");
		} catch (CIFException e) {
            CIFUtil.BUG(e);
		}
		try {
			cif.add(dataBlock);
		} catch (CIFException e) {
			Assert.fail("should not throw " + e);
		}
		Assert.assertEquals("acta1 block count", 3, cif.getDataBlockList()
				.size());
		Assert.assertEquals("acta1 block1 id ", "foo", cif.getDataBlockList()
				.get(2).getId());
		try {
			dataBlock = new CIFDataBlock("foo");
		} catch (CIFException e) {
            CIFUtil.BUG(e);
		}
		/** fails until parser.setFoo updated
		try {
			cif.add(dataBlock);
			Assert.fail("should catch duplicate block id");
		} catch (CIFException e) {
			Assert.assertEquals("acta1 add duplicate fails",
					"Duplicate dataBlockId: foo", e.getMessage());
		}
		Assert.assertEquals("acta1 block count", 3, cif.getDataBlockList()
				.size());
		Assert.assertEquals("acta1 block1 id ", "foo", cif.getDataBlockList()
				.get(2).getId());
		try {
			dataBlock = new CIFDataBlock("a b");
			Assert.fail("should catch bad block id");
		} catch (CIFException e) {
			Assert.assertEquals("acta1 bad block id fails",
					"Whitespace ( ) in id [a b]", e.getMessage());
		}
		*/
	}

	/**
	 * test for writeHTML(Writer w)
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testWriteHTMLWriter() throws Exception {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		Assert.assertNotNull("cif not null", cif);
//		FileUtils.touch(acta1_html);
		FileWriter w = new FileWriter(acta1_html);
		cif.writeHTML(w);
		w.close();
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.getDataBlockList()'
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testGetDataBlockList() throws Exception {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		List<CIFDataBlock> dataBlockList = cif.getDataBlockList();
		Assert.assertEquals("dataBlock count for acta1", 2, dataBlockList
				.size());
	}

//	CIF parseExample(String dir, String fileRoot) throws URISyntaxException {
//		CIF cif = null;
//		File file = CIFUtil.getResourceFile(dir, fileRoot + ".cif");
//		Assert.assertNotNull("not null: "+fileRoot, file);
//		try {
//			cif = (CIF) new CIFParser().parse(file).getRootElement();
//		} catch (Exception e) {
//			Assert.fail("parseExample " + fileRoot + " should not throw" + e);
//		}
//		return cif;
//	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.getDataBlockById(String)'
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testGetDataBlockById() throws Exception {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		CIFDataBlock block1 = cif.getDataBlockById("global");
		Assert.assertNotNull("global for acta1", block1);
		block1 = cif.getDataBlockById("GlObAl");
		Assert.assertNotNull("global ignore case for acta1", block1);
		CIFDataBlock block0 = cif.getDataBlockById("7");
		Assert.assertNotNull("main block for acta1", block0);
		CIFDataBlock blockx = cif.getDataBlockById("junk");
		Assert.assertNull("non-existent block for acta1", blockx);
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.getDataBlockIds()'
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testGetDataBlockIds() throws Exception {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		List<String> idList = cif.getDataBlockIds();
		Assert.assertEquals("acta1 block count ", 2, idList.size());
		Assert.assertEquals("acta 1 block 1", "global", idList.get(0));
		Assert.assertEquals("acta 1 block 2", "7", idList.get(1));
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.writeCIF(Writer)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testWriteCIFWriter() throws CIFException, IOException,
			URISyntaxException {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		Assert.assertNotNull("acta1.cif", cif);
		Writer w = new FileWriter(
				new File(CIFUtil.buildPath(Separator.FILE, "target", "acta1.cif.out")));
		cif.writeCIF(w);
		w.close();
	}

	/**
	 * Test method for 'uk.co.demon.ursus.cif.CIF.writeXML(Writer)'
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testWriteXMLWriter() throws Exception {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		Assert.assertNotNull("acta1.cif", cif);
		Writer w = new FileWriter(new File(CIFUtil.buildPath(Separator.FILE, "target", "acta1.xml")));
		cif.writeXML(w);
		w.close();
		cif.processSu(false);
		Writer w2 = new FileWriter(new File(CIFUtil.buildPath(Separator.FILE, "target",
				"acta1.split.xml")));
		cif.writeXML(w2);
		w2.close();
	}

	/**
	 * test su.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	public void testProcessSu() throws CIFException, IOException,
			URISyntaxException {
		CIF cif = CIF.createFromFile(acta1_cif.toString());
		boolean failOnError = false;
		try {
			cif.processSu(failOnError);
		} catch (RuntimeException e) {
			Assert.fail("acta1 should not throw" + e);
		}
		failOnError = true;
		try {
			cif.processSu(failOnError);
			Assert.fail("acta1 should throw NumberFormat");
		} catch (RuntimeException e) {
			Assert
					.assertEquals(
							"acta1 should throw",
							"child 2 (datablock) : child 67 (item) : cannot parse as value(su): java.lang.NumberFormatException: For input string: \"I>2\\s\" in _reflns_threshold_expression/I>2\\s(I)",
							e.getMessage());
		}
	}

	/**
	 * test dictionary.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws CIFException
	 */
	@Test
	@Ignore
	public void testCoreDictionary() throws CIFException, IOException,
			URISyntaxException {
		CIF dic = CIF.createFromFile(CIFUtil.getResourceFile(DICT_RESOURCE, "cif_core[1].dic")
				.getAbsolutePath());
		Assert.assertNotNull("core dict not null", dic);
		Assert.assertEquals("core dict blocks", 533, dic.getDataBlockList()
				.size());
		/*--
		 data_atom_site_fract_
		 loop_ _name                '_atom_site_fract_x'
		 '_atom_site_fract_y'
		 '_atom_site_fract_z'
		 _category                    atom_site
		 _type                        numb
		 _type_conditions             esd
		 _related_item              '_atom_site_Cartn_'
		 _related_function            alternate
		 _list                        yes
		 _list_reference            '_atom_site_label'
		 _definition
		 ;              Atom-site coordinates as fractions of the _cell_length_ values.
		 ;
		 --*/
		CIFDataBlock block = dic.getDataBlockById("atom_site_fract_");
		Assert.assertNotNull("block not null", block);
		Assert.assertEquals("block children", 9, block.getChildElements()
				.size());
		List<CIFLoop> loopList = block.getLoopList();
		Assert.assertEquals("block loop count", 1, loopList.size());
		CIFLoop loop = block.getLoopContainingName("_name");
		Assert.assertNotNull("loop not null", loop);
		Assert.assertEquals("name loop length", 3, loop.getValueList().size());

		// HTML
		FileWriter w = null;
		w = new FileWriter(new File(CIFUtil.buildPath(Separator.FILE, "target", "cif_core[1].html")));
		dic.writeHTML(w);
		w.close();
	}

	/**
	 * merges files from John Davies into output.
	 * 
	 * @throws IOException
	 * 
	 * 
	 */
	@Test
	public void testMerge() throws IOException {
		CIF cif = null;
		CIFDataBlock cifBlock = null;
		List<CIFItem> cifItemList = null;
		try {
			cif = CIF.createFromFile(sl1_cif.toString());
			cifBlock = cif.getDataBlockList().get(0);
			cifItemList = cifBlock.getItemList();
		} catch (Exception e) {
			Assert.fail("acta1 should not throw" + e);
		}
		Assert.assertEquals("item count ", 100, cifItemList.size());
		CIF sortav = null;
		CIFDataBlock sortavBlock = null;
		List<CIFItem> sortavItemList = null;
		try {
			sortav = CIF.createFromFile(sortav_cif.toString());
			Assert.assertNotNull("sortav not null", sortav);
			sortavBlock = sortav.getDataBlockById("sortav");
			sortavItemList = sortavBlock.getItemList();
		} catch (Exception e) {
			Assert.fail("sortav should not throw" + e);
		}
		Assert.assertEquals("item count ", 4, sortavItemList.size());
		for (CIFItem sortavItem : sortavItemList) {
			String sortavName = sortavItem.getName();
			CIFItem cifItem = cifBlock.getChildItem(sortavName);
			if (cifItem == null) {
				cifBlock.add(sortavItem, true);
			} else {
				cifItem.setTextValue(sortavItem.getValue());
			}
		}
		
		Writer w = new FileWriter(CIFUtil.createNewFile(CIFUtil.getTEMP_DIRECTORY() +File.separator+ JED + File.separator + "cifbit.cif"));
		cif.writeCIF(w);
		w.close();

//		CIF cifbit = null;
//		try {
//			cifbit = CIF.createFromFile(CIFUtil.getResourceFile(JED_RESOURCE, "cifbit.cif")
//					.getAbsolutePath());
//		} catch (Exception e) {
//			Assert.fail("cifbit should not throw" + e);
//		}

	}

}
