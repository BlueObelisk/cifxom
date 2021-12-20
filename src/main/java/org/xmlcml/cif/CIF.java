package org.xmlcml.cif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Holds a CIF document as an object
 * 
 * @author Peter Murray-Rust, 2000, 2003
 */
public class CIF extends CIFElement {

	private static Logger LOG = LogManager.getLogger(CIF.class);

	/**
	 * tag.
	 * 
	 */
	public final static String TAG = "cif";

	// if a duplicate datablock name is found this string is
	// appended and changed to blockIdPrefix+"I" as seems to 
	// be standard
	String blockIdPrefix = "I";

	private List<CIFDataBlock> dataBlockList;

	/**
	 * default constructor
	 */
	public CIF() {
		super(TAG);
		LOG.info("CIF INIT");
	}

	/**
	 * create from XML Document.
	 * 
	 * @param xmlCIFDoc document to create from
	 * @param failOnError if violates schema
	 * @throws CIFException unknown or bad elements
	 */
	public CIF(Document xmlCIFDoc, boolean failOnError) throws CIFException {
		this(xmlCIFDoc.getRootElement(), failOnError);
	}

	/**
	 * create recursively from an XML element.
	 * 
	 * @param element with name "cif"
	 * @param failOnError fail if violates schema
	 * @throws CIFException if this and descendants violate schema.
	 */
	public CIF(Element element, boolean failOnError) throws CIFException {
		this();
		validateName(TAG);
		copyAttributes(element, new String[] {"id"}, failOnError);
		copyChildren(element,
				new String[] { CIFDataBlock.TAG, CIFComment.TAG }, failOnError);
	}

	public CIF(CIF cif) {
		super(cif);
		this.dataBlockList = new ArrayList<CIFDataBlock>();
		for (CIFDataBlock dataBlock : cif.dataBlockList) {
			this.dataBlockList.add(new CIFDataBlock(dataBlock));
		}
	}
	/**
	 * create from file.
	 * 
	 * @param xmlFilename file to create from
	 * @param failOnError
	 * @return new CIF
	 * @throws CIFException
	 * @throws IOException
	 */
	public static CIF createFromXMLFile(String xmlFilename, boolean failOnError)
	throws CIFException, IOException {
		Document xmlDoc = null;
		try {
			xmlDoc = new Builder().build(new File(xmlFilename));
		} catch (Exception e) {
			throw new CIFException("", e);
		}
		return new CIF(xmlDoc, failOnError);
	}

	/**
	 * create from file.
	 * 
	 * @param xmlFilename file to create from
	 * @param failOnError
	 * @return new CIF
	 * @throws CIFException
	 * @throws IOException
	 */
	public static CIF createFromXML(Element xml, boolean failOnError) {
		CIF cif = null;
		try {
			cif = new CIF(xml, failOnError);
		} catch (CIFException e) {
			throw new RuntimeException("CIF parse", e);
		}
		return cif;
	}

	/**
	 * create from CIF file.
	 * 
	 * tested in {@link org.xmlcml.cif.CIFTest#testCreateFromFile()}.
	 * @param cifFile
	 *            file to create from
	 * @return new CIFDocument
	 * @throws CIFException
	 * @throws IOException
	 */
	public static CIF createFromFile(String cifFile) throws CIFException,
	IOException {
		URL url = new URL(CIFUtil.makeAbsoluteURL(cifFile));
		return createFromURL(url);
	}

	/**
	 * create from CIF file.
	 * 
	 * tested in {@link org.xmlcml.cif.CIFTest#testCreateFromFile()}.
	 * @param url to create from
	 * @return new CIFDocument
	 * @throws CIFException
	 * @throws IOException
	 */
	public static CIF createFromURL(URL url) throws CIFException,
	IOException {
		CIFParser parser = new CIFParser();
		Document doc = parser.parse(url);
		return (doc == null) ? null : (CIF) doc.getRootElement();
	}

	/**
	 * create from CIF file.
	 * 
	 * @param cifS
	 *            string to create from
	 * @return new CIFDocument
	 * @throws CIFException
	 */
	public static CIF createFromString(String cifS) throws CIFException {
		CIFParser parser = new CIFParser();
		BufferedReader br = new BufferedReader(new StringReader(cifS));
		Document doc = null;
		try {
			doc = parser.parse(br);
		} catch (IOException e) {
			CIFUtil.BUG(e);
		}
		return (doc == null) ? null : (CIF) doc.getRootElement();
	}

	/**
	 * add a datablock
	 * 
	 * @param dataBlock
	 * @exception CIFException
	 */
	public void add(CIFDataBlock dataBlock) throws CIFException {
		String dataId = dataBlock.getId();
		if (dataId == null) {
			throw new CIFException("dataBlockId is null");
		}
		if (getDataBlockById(dataId) != null) {
			boolean finished = false;
			while (!finished) {
				String oldId = dataId;
				dataId = dataId+"I";
				if (getDataBlockById(dataId) == null) {
					finished = true;
					dataBlock.resetId(dataId);
					LOG.warn("Renamed duplicate dataBlockId "+oldId+" to "+dataId);
				}
			}
		}
		this.appendChild(dataBlock);
	}

	/**
	 * Gets the list of data_ block
	 * 
	 * @return the blocks
	 */
	public List<CIFDataBlock> getDataBlockList() {
		dataBlockList = new ArrayList<CIFDataBlock>();
		Elements elements = this.getChildElements(CIFDataBlock.TAG);
		for (int i = 0; i < elements.size(); i++) {
			dataBlockList.add((CIFDataBlock) elements.get(i));
		}
		return dataBlockList;
	}

	/** recursively normalize datablocks
	 */
	public void normalize() {
		getDataBlockList();
		for (CIFDataBlock dataBlock : dataBlockList) {
			dataBlock.normalize();
		}
	}

	/**
	 * Gets the dataBlock with given id. case is ignored
	 * 
	 * @param id
	 *            the identifier (i.e. "foo" in data_foo)
	 * @return The dataBlock (null if not found)
	 */
	public CIFDataBlock getDataBlockById(String id) {
		List<CIFDataBlock> dataBlockList = this.getDataBlockList();
		CIFDataBlock dataBlock = null;
		for (CIFDataBlock b : dataBlockList) {
			if (b.getId().equalsIgnoreCase(id)) {
				dataBlock = b;
				break;
			}
		}
		return dataBlock;
	}

	/**
	 * Gets the list of dataBlock ids
	 * 
	 * @return the ids in document order
	 */
	public List<String> getDataBlockIds() {
		List<CIFDataBlock> dataBlockList = this.getDataBlockList();
		List<String> idList = new ArrayList<String>();
		for (CIFDataBlock block : dataBlockList) {
			idList.add(block.getId());
		}
		return idList;
	}


	/**
	 * write CIF in CIF format.
	 * 
	 * @param doc
	 * @param w
	 *            the writer
	 * @throws IOException
	 */
	public static void writeCIF(Document doc, Writer w) throws IOException {
		((CIF) doc.getRootElement()).writeCIF(w);
	}

	/**
	 * writes a CIF. does NOT close writer
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeCIF(Writer w) throws IOException {
		List<CIFDataBlock> dataBlockList = this.getDataBlockList();
		for (CIFDataBlock dataBlock : dataBlockList) {
			dataBlock.writeCIF(w);
		}
	}

	/**
	 * write CIF in XML format.
	 * 
	 * @param doc
	 * @param w
	 *            the writer
	 * @throws IOException
	 */
	public static void writeXML(Document doc, Writer w) throws IOException {
		// FIXME
		w.write(doc.toXML());
		w.close();
	}

	/**
	 * write HTML header
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	void writeHTMLHeader(Writer w) throws IOException {
		w.write("<html>\n<head>\n\t<title>");
		String name = this.getLocalName();
		if ((name != null) && (name.length() > 0)) {
			w.write(name);
		}
		w.write("</title>\n</head>\n<body>\n");
	}

	/**
	 * write HTML footer
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	void writeHTMLFooter(Writer w) throws IOException {
		w.write("</body>\n</html>\n");
	}

	/**
	 * write CIF to html
	 * 
	 * @param w
	 *            writer
	 * @throws IOException
	 */
	public void writeHTML(Writer w) throws IOException {
		writeHTMLHeader(w);
		Elements childElements = this.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element elem = childElements.get(i);
			if (elem instanceof CIFElement) {
				((CIFElement) elem).writeHTML(w);
			} else {
				throw new RuntimeException("Unknown node: " + elem.getLocalName());
			}
		}
		writeHTMLFooter(w);
	}

	/**
	 * main program.
	 * 
	 * @param args
	 *            the arguments
	 */
	@SuppressWarnings("unused")
	public static void main(String args[]) {

		if (args.length == 0) {
			System.err
			.println("Usage: java org.xmlcml.cif.CIFDocumentImpl [options]");
			System.err.println("     -DEBUG                //full debug print");
			System.err
			.println("     -CIFIN     cifFile    //the input file - required unless -DOMIN");
			System.err.println("     -CIFOUT    cifFile    //write CIF");
			// System.err.println(" -DICTIN dicFile //the dictionary file -
			// optional");
			// System.err.println(" -DICTOUT dicoutFile //the dictionary output
			// file");
			System.err
			.println("     -DOMIN     domFile    //read CIFDOM (in xml) NYI");
			System.err
			.println("     -DOMOUT    xmlFile    //the output dom file");
			System.err.println("     -ECHOIN               //echo the input");
			System.err
			.println("     -SU                   //parse and save standard uncertainties");
			System.err
			.println("     -TEST      num        //run test num (all and help also allowed)");
			System.err
			.println("     -VALID                //validate document against dictionary (NYI)");
			System.exit(1);
		}
		try {
			String cifInFile = "";
			String cifOutFile = "";
			String domInFile = "";
			String domOutFile = "";
			boolean debug = false;
			boolean echoin = false;
			boolean su = false;
			CIF cif = null;

			int i = 0;
			while (i < args.length) {
				if (false) {
					;
				} else if (args[i].equalsIgnoreCase("-CIFIN")) {
					cifInFile = args[++i];
					i++;
				} else if (args[i].equalsIgnoreCase("-CIFOUT")) {
					cifOutFile = args[++i];
					i++;
				} else if (args[i].equalsIgnoreCase("-DEBUG")) {
					debug = true;
					i++;
				} else if (args[i].equalsIgnoreCase("-DOMIN")) {
					domInFile = args[++i];
					i++;
				} else if (args[i].equalsIgnoreCase("-DOMOUT")) {
					domOutFile = args[++i];
					i++;
				} else if (args[i].equalsIgnoreCase("-ECHOIN")) {
					echoin = true;
					i++;
				} else if (args[i].equalsIgnoreCase("-SU")) {
					su = true;
					i++;
				} else {
					throw new RuntimeException("Bad arg: " + args[i]);
				}
			}
			if (!cifInFile.equals("")) {
				CIFParser parser = new CIFParser();
				parser.setEchoInput(echoin);
				parser.setDebug(debug);
				URL url = new URL(CIFUtil.makeAbsoluteURL(cifInFile));
				cif = (CIF) parser.parse(url).getRootElement();
			}
			if (cif != null && !cifOutFile.equals("")) {
				cif.writeCIF(new FileWriter(cifOutFile));
			}
			if (cif != null && !domOutFile.equals("")) {
				cif.writeXML(new FileWriter(domOutFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}
	
	/**
	 * get block id. normally the data_id
	 * 
	 * @return the id
	 */
	public String getId() {
		return this.getAttributeValue(ATTRIBUTE_ID);
	}
	
	/**
	 * sets the block id. checks for valid and invalid characters. I don't know
	 * what these are, so this may fail on some Ids. At least whitespace is
	 * forbidden
	 * 
	 * @param id
	 * @throws CIFException
	 */
	public void setId(String id) throws CIFException {
		if (id == null || id.length() == 0) {
			throw new CIFException("Bad id: " + id);
		}
		addAttribute(new Attribute(ATTRIBUTE_ID, id));
	}

    @Override
    public CIF copy() {
        return new  CIF(this);
    }
    
}
