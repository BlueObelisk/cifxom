package org.xmlcml.cif;

import org.junit.Before;
import org.junit.Test;

/**
 * tests examples.
 * 
 * @author pm286
 * 
 */
public class ExamplesTest extends CIFTestBase {

    private static int nTest = 0;
    /**
     */
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("ExamplesTest");
        }
    }
    

	/**
	 * Test method for parsing all example in directory
	 * 	
	 * @todo use getResourceFile paradigm
	 */
	@Test
	public void testParseOtherCIFFiles() {
		boolean checkDuplicates = true;
		// check 1 in 10 runs... (DONT)
//		if (Math.random() < 0.1) {
            int maxCount = 10;
			CIFUtil.outputString("======================= camb ========================");
			ParserTest.parseFiles(CIFUtil.getResource(CAMB_RESOURCE), checkDuplicates, maxCount);
//            CIFUtil.outputString("======================== in ===========================");
//			ParserTest.parseFiles(INDIRECTORY, checkDuplicates, maxCount);
//            CIFUtil.outputString("======================== ley ========================");
//			ParserTest.parseFiles(LEYDIRECTORY, checkDuplicates, maxCount);
//            CIFUtil.outputString("======================== rsc ========================");
//			ParserTest.parseFiles(RSCDIRECTORY, checkDuplicates, maxCount);
//            CIFUtil.outputString("======================= soton ======================");
//			ParserTest.parseFiles(SOTONDIRECTORY, checkDuplicates, maxCount);
//		}
	}

}
