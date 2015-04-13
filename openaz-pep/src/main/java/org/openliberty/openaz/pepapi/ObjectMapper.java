package org.openliberty.openaz.pepapi;



/**
 * Converts a Java Class (typically an application Domain Object) into request attributes of some Category. 
 * Applications are expected to provide only a single ObjectMapper instance per Domain Type.
 * 
 * Typically, there is a one-to-one relationship between the Domain Type and Attribute Category. The interface, however, takes
 * a general approach allowing a Domain Type to be mapped to multiple categories.
 * 
 * The conversion for the most part involves obtaining a <code>CategoryAttributes</code> instance for a specific category from the
 * request context and then mapping Object properties as name-value pairs using one of the overloaded <code>setAttribute</code>
 * methods.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public interface ObjectMapper {

	/**
	 * Returns a Class that represents the mapped domain type.
	 * 
	 * @return a Class object
	 */
	public Class<?> getMappedClass();
	
	/**
	 * Maps Object properties to attributes
	 * 
	 * @param o - an instance of the domain object to be mapped
	 * @param pepRequest - the current Request Context
	 */
	public void map(Object o, PepRequest pepRequest);

	/**
	 *
	 * @param mapperRegistry
	 */
	public void setMapperRegistry(MapperRegistry mapperRegistry);

	/**
	 *
	 * @param pepConfig
	 */
	public void setPepConfig(PepConfig pepConfig);
}
