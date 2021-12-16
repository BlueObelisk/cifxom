package org.xmlcml.cif;

import nu.xom.Attribute;

/**
 * provides support for value elements (cell and item).
 */
public abstract class AbstractValueElement extends AbstractTextElement {

	/**
	 * create value.
	 * 
	 * @param name
	 */
	public AbstractValueElement(String name) {
		super(name);
	}

	/** copy constructor.
	 * 
	 * @param abstractValueElement
	 */
	public AbstractValueElement(AbstractValueElement abstractValueElement) {
		super(abstractValueElement);
	}
	
	protected void clearNumericSu() {
		Attribute att = this.getAttribute(CIFAttribute.NUMERICVALUE.value);
		if (att != null) {
			this.removeAttribute(att);
		}
		att = this.getAttribute(CIFAttribute.SU.value);
		if (att != null) {
			this.removeAttribute(att);
		}
		att = this.getAttribute(CIFAttribute.DATATYPE.value);
		if (att != null) {
			this.removeAttribute(att);
		}
	}

	/**
	 * processes the SU of the value.
     *  only relevant for CIFItem and CIFLoop
	 * (otherwise passes to children) if the TextValue is of form 1.234(12)
	 * calculates numeric Value and SU
	 * 
	 * @param failOnError
	 *            abort if not of numeric form else no-op
	 * @throws RuntimeException
	 *             bad number(su) if failOnError == true
	 */
	public void processSu(boolean failOnError) {
		String value = this.getValue();
		double[] values = null;
		try {
			values = CIFUtil.getNumericValueAndSu(value);
		} catch (RuntimeException e) {
			if (failOnError) {
				throw new RuntimeException("" + e.getMessage() + " in " + getName()
						+ S_SLASH + getValue());
			}
		}
		if (values != null) {
			this.setNumericValue(values[0]);
			this.setSu(values[1]);
		}
	}

	public void setNumericValue(double value) {
		setDataType(DataType.NUMB);
		this.addAttribute(new Attribute(CIFAttribute.NUMERICVALUE.value, "" + value));
	}

	public void setValueAndSu(double value, double su, int nDecimal) {
		this.addAttribute(new Attribute(CIFAttribute.NUMERICVALUE.value, "" + value));
		this.addAttribute(new Attribute(CIFAttribute.SU.value, "" + su));
		this.setTextValue(CIFUtil.createSuValue(value, su, nDecimal));
	}

	/**
	 * set value.
	 * clears all numeric stuff (SU)
	 * @param s
	 */
	public void setTextValue(String s) {
		super.setTextValue(s);
		this.clearNumericSu();
	}

	public void setValue(double value) {
		this.setNumericValue(value);
	}

	/**
	 * set the value for a data item. normally used when building a CIFDOM.
	 * implementers may check the value to see whether it violates any CIF
	 * syntax or dictionary restrictions. the value and su must be on same
	 * scale. Thus 1.234(4) requires value=1.234 and su=.004 (NOT 4, which would
	 * give 1.234(4000)
	 * 
	 * @param value
	 * @param su
	 *            its mandatory uncertainty in same units as value
	 */
	public void setValue(double value, double su, int nDecimal) {
		String valueSu = CIFUtil.createSuValue(value, su, nDecimal);
		this.setTextValue(valueSu);
		this.setDataType(DataType.NUMB);
		this.setNumericValue(value);
		this.setSu(su);
	}

	public void setSu(double value) {
		setDataType(DataType.NUMB);
		this.addAttribute(new Attribute(CIFAttribute.SU.value, "" + value));
	}

    /** set data type.
     * 
     * @param dataType
     */
	public void setDataType(DataType dataType) {
		this.addAttribute(new Attribute(CIFAttribute.DATATYPE.value, dataType.value));
	}

    /** gets dataType as set by attributes.
     * 
     * @return datatype or null
     */
    public DataType getDataType() {
        String dt = this.getAttributeValue(CIFAttribute.DATATYPE.value);
        return DataType.getDataType(dt);
    }
    
    /** uses attributes to decide whether to set text value.
     * requires numb, value and optionally su.
     */
    public void setTextValueFromAttributes() {
        DataType dataType = this.getDataType();
        if (DataType.NUMB.equals(dataType)) {
            Double value = this.getNumericValue();
            Double su = this.getSu();
            double suval = (su == null) ? Double.NaN : su.doubleValue();
            if (value != null) {
                int ndec = -2;
                super.setTextValue(CIFUtil.createSuValue(value, suval, ndec));
            }
        }
    }

	/**
	 * get name of item.
	 * 
	 * @return name of item
	 */
	public String getName() {
		return this.getAttributeValue(CIFAttribute.NAME.value);
	}

	/**
	 * gets standard uncertainty.
	 * 
	 * @return value or null
	 */
	public Double getSu() {
		Double su = null;
		String dd = this.getAttributeValue(CIFAttribute.SU.value);
		if (dd != null) {
			try {
				su = Double.valueOf(dd);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("bad su value: " + nfe);
			}
		}
		return su;
	}

	/**
	 * get value as number.
	 * 
	 * @return value of null if missing or not numeric
	 */
	public Double getNumericValue() {
		Double d = null;
		if (DataType.NUMB.value.equals(this.getAttributeValue(CIFAttribute.DATATYPE.value))) {
			String value = this.getAttributeValue(CIFAttribute.NUMERICVALUE.value);
            if (value != null) {
    			try {
    				d = Double.valueOf(value);
    			} catch (NumberFormatException nfe) {
    				throw new RuntimeException("expected double: " + nfe);
    			}
            }
		}
		return d;
	}

}
