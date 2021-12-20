package org.xmlcml.cif;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.canonical.Canonicalizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.Assert;
import org.junit.Before;

/**
 * 
 * <p>
 * superclass for manage common methods for unit tests
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class CIFTestBase implements CIFConstants {

    /** root of tests. */
    public final static String TEST_RESOURCE = "src/test/resources";

//    /** root of examples. */
//    public final static String EXAMPLES_RESOURCE = TEST_RESOURCE + C_SLASH
//            + "examples";
    /** root of examples. */
    public final static String EXAMPLES_RESOURCE = "examples";

    /** root of examples. */
    public final static String SIMPLE_RESOURCE = EXAMPLES_RESOURCE + C_SLASH
            + "simple";

    /** root of complex examples. */
    public final static String ACTA_RESOURCE = EXAMPLES_RESOURCE + C_SLASH
            + "acta";

    /** root of complex examples. */
    public final static String CAMB_RESOURCE = EXAMPLES_RESOURCE + C_SLASH
            + "camb";

    /** root of JED examples. */
    public final static String JED = "jed";

    /** root of complex examples. */
    public final static String JED_RESOURCE = EXAMPLES_RESOURCE + C_SLASH
            + JED;

    /** root of dictionary examples. */
    public final static String DICT_RESOURCE = EXAMPLES_RESOURCE + C_SLASH
            + "dict";

	static Logger LOG = LogManager.getLogger(CIFTestBase.class);
	
//	public final static String TEST_DIR = "simple";

	public final static File file1_cif  = new File(TEST_RESOURCE+"/cif/test1.cif");
	public final static File acta1_cif  = new File(TEST_RESOURCE+"/cif/acta1.cif");
	public final static File acta1_html = new File(TEST_RESOURCE+"/html/acta1.html");
	public final static File acta1_xml  = new File(TEST_RESOURCE+"/xml/acta1.xml");
	public final static File sl1_cif    = new File(TEST_RESOURCE+"/cif/sl1.cif");
	public final static File sortav_cif    = new File(TEST_RESOURCE+"/cif/sortav.cif");
	/** */
	public final static String FILE1 = "acta1";

	/**
	 * error in numerics.
	 * 
	 */
	public final static double EPS = 1.0E-10;

	/**
	 * tests equality of doubles.
	 * 
	 * @param a
	 * @param b
	 * @param eps
	 *            margin of identity
	 * @return true if a == b within eps
	 */
	public static boolean equals(double a, double b, double eps) {
		return (Math.abs(a - b) < Math.abs(eps));
	}

	/**
	 * tests euality of double arrays. arrays must be of same length
	 * 
	 * @param a
	 *            first array
	 * @param b
	 *            second array
	 * @param eps
	 *            margin of identity
	 * @return array elements equal within eps
	 */
	public static boolean equals(double[] a, double[] b, double eps) {
		boolean result = false;
		if (a.length == b.length) {
			result = true;
			for (int i = 0; i < a.length; i++) {
				if (Math.abs(a[i] - b[i]) > Math.abs(eps)) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * used by Assert.assert routines. copied from Assert.assert
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @return message
	 */
	protected static String format1(String message, Object expected,
			Object actual) {
		String formatted = "";
		if (message != null) {
			formatted = message + " ";
		}
		return formatted + "expected:<" + expected + "> but was:<" + actual
				+ ">";
	}

	/**
	 * Assert.asserts equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a expected array
	 * @param b actual array
	 * @param eps tolerance for agreement
	 */
	public static void assertEquals(String message, double[] a, double[] b,
			double eps) {
		if (a == null || b == null) {
			Assert.fail(format1(message, "double[]", "null"));
		}
		if (a.length != b.length) {
			Assert.fail(format1(message + "; unequal arrays", "" + a.length, ""
					+ b.length));
		}
		for (int i = 0; i < a.length; i++) {
			if (!equals(a[i], b[i], eps)) {
				Assert.fail(format1(message + "; unequal element (" + i + ")",
						"" + a[i], "" + b[i]));
			}
		}
	}

	/**
	 * Assert.asserts equality of int arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 */
	public static void assertEquals(String message, int[] a, int[] b) {
		if (a == null || b == null) {
			Assert.fail(format1(message, "int[]", "null"));
		}
		if (a.length != b.length) {
			Assert.fail(format1(message + "; unequal arrays", "" + a.length, ""
					+ b.length));
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				Assert.fail(format1(message + "; unequal element (" + i + ")",
						"" + a[i], "" + b[i]));
			}
		}
	}

	/**
	 * Assert.asserts equality of String arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * equality if individual elements are equal or both elements are null
	 * 
	 * @param message
	 * @param a
	 *            expected array may include nulls
	 * @param b
	 *            actual array may include nulls
	 */
	public static void assertEquals(String message, String[] a, String[] b) {
		if (a == null || b == null) {
			Assert.fail(format1(message, "int[]", "null"));
		}
		if (a.length != b.length) {
			Assert.fail(format1(message + "; unequal arrays", "" + a.length, ""
					+ b.length));
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] == null && b[i] == null) {
				// null == null
			} else if (!a[i].equals(b[i])) {
				Assert.fail(format1(message + "; unequal element (" + i + ")",
						"" + a[i], "" + b[i]));
			}
		}
	}

	/**
	 * tests 2 XML objects for equality using canonical XML.
	 * 
	 * @param message
	 * @param elem1
	 *            first element
	 * @param elem2
	 *            second element
	 */
	public static void assertEqualsCanonically(String message, Element elem1,
			Element elem2) {
		if (elem1 == null || elem2 == null) {
			Assert.fail(format1(message, "element", "null"));
		}
		assertEqualsCanonically(message, elem1, elem2, true);
	}

	/**
	 * tests 2 XML objects for non-equality using canonical XML.
	 * 
	 * @param message
	 * @param elem1
	 *            first element
	 * @param elem2
	 *            second element
	 */
	public static void assertNotEqualsCanonically(String message,
			Element elem1, Element elem2) {
		if (elem1 == null || elem2 == null) {
			Assert.fail(format1(message, "element", "null"));
		}
		assertEqualsCanonically(message, elem1, elem2, false);
	}

	/**
	 * adds boolean to Assert.assertEqualsCanonically.
	 * 
	 * @param message
	 * @param elem1
	 * @param elem2
	 * @param assertx
	 *            true/false => Assert.assert Assert.assertNot
	 */
	private static void assertEqualsCanonically(String message, Element elem1,
			Element elem2, boolean assertx) {
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		Canonicalizer canon1 = new Canonicalizer(baos1);
		Canonicalizer canon2 = new Canonicalizer(baos2);
		Document doc1 = new Document((Element) elem1.copy());
		Document doc2 = new Document((Element) elem2.copy());
		try {
			canon1.write(doc1);
			canon2.write(doc2);
		} catch (IOException e) {
			Assert.fail("should never throw IOEXception " + e);
		}
		String s1 = baos1.toString();
		String s2 = baos2.toString();
		if (assertx) {
			Assert.assertEquals(message, s1, s2);
		} else {
			if (s1.equals(s2)) {
				Assert.fail(message + ":  " + s1 + " and " + s2
						+ " should not be equal");
			}
		}
	}
    
    private static int nTest = 0;
    /** set up*/
    @Before
    public void setUp() {
        if (nTest++ == 0) {
            header("BaseTest");
        }
    }
    
    static protected long millis = System.currentTimeMillis();
    protected void header(String s) {
        long newTime = System.currentTimeMillis();
        LOG.info("== Running: "+s+" == "+(newTime - millis));
        System.out.flush();
        millis = newTime;
    }

}
