package org.openliberty.openaz.pepapi;


/**
 * Container that holds <code>ObjectMapper</code> instances registered with the framework.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public interface MapperRegistry {
	
	/**
	 * Registers the provided ObjectMapper instance 
	 * 
	 * @param mapper
	 */
	public void registerMapper(ObjectMapper mapper);

	/**
	 * Registers the provided ObjectMapper instances
	 *
	 * @param mappers
	 */
	public void registerMappers(Iterable<? extends ObjectMapper> mappers);

	/**
	 * Returns the ObjectMapper instance registered for the given Class.
	 * 
	 * @param clazz 
	 * @return an ObjectMapper instance
	 * @throws org.openliberty.openaz.pepapi.PepException if no ObjectMapper could be found for class clazz;
	 */
	public ObjectMapper getMapper(Class<?> clazz);
	
}
