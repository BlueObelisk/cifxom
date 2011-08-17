package org.xmlcml.star;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Serializer;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cif.CIF;
import org.xmlcml.cif.CIFParser;

public class StarTest {

	@Test
	public void testCaffeine() throws Exception {
		runTest("simple star", "src/test/resources/org/xmlcml/star/caffeine.str",
				"src/test/resources/org/xmlcml/star/caffeine.xml", false);
	}

	@Test
	public void testCIFLoop() throws Exception {
		runTest("simple cif loop", "src/test/resources/org/xmlcml/star/loop.cif",
				"src/test/resources/org/xmlcml/star/loop.cif.xml", false);
	}

	@Test
	public void testLoop() throws Exception {
		runTest("simple loop", "src/test/resources/org/xmlcml/star/loop.str",
				"src/test/resources/org/xmlcml/star/loop.xml", false);
	}

	@Test
	public void testLoop1() throws Exception {
		runTest("simple loops", "src/test/resources/org/xmlcml/star/loop1.str",
				"src/test/resources/org/xmlcml/star/loop1.xml", false);
	}

	private void runTest(String test, String filename, String xmlfile, boolean dump) throws Exception {
		CIF cif = new CIF();
		CIFParser parser = new CIFParser();
		parser.setUseStar(true);
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Document doc = parser.parse(reader);
		if (dump){
			Serializer serializer = new Serializer(System.err);
			serializer.setIndent(0);
			serializer.write(doc);
		}
		Document refDoc = new Builder().build(new File(xmlfile));
		
		Assert.assertEquals(test, refDoc.toXML(), doc.toXML());
	}
}
