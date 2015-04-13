package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.XACML3;

/**
 * Container class that maps attributes to predefined XACML AccessSubject category.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public class Subject extends CategoryContainer {

	public static final String SUBJECT_ID_KEY = "SUBJECT_ID_KEY";
	
	private String subjectIdValue;
	
	private Subject() {
		super(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
	}
	
	/**
	 * Creates a new Subject instance
	 * 
	 * @return
	 */
	public static Subject newInstance() {
		return new Subject();
	}
	
	/**
	 * Creates a new Subject instance containing a single default attribute with the given String value.
	 * 
	 * @param subjectIdValue
	 * @return
	 */
	public static Subject newInstance(String subjectIdValue) {
		Subject s = new Subject();
		s.subjectIdValue = subjectIdValue;
		s.addAttribute(SUBJECT_ID_KEY, subjectIdValue);
		return s;
	}
	
	/**
	 * Returns the value of the default subjectIdValue attribute
	 * 
	 * @return
	 */
	public String getSubjectIdValue() {
		return subjectIdValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("subject-id value : " + subjectIdValue);
		builder.append("\n");
		builder.append(super.toString());
		return builder.toString();
	}
}
