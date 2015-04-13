package org.openliberty.openaz.pepapi.std.test.mapper;

public class Document {
	
	private final Integer documentId;
	private final String documentName;
	private final String clientName;
	private final String documentOwner;
	
	public Document(Integer documentId, String documentName, String clientName, String documentOwner){
		this.documentId = documentId;
		this.documentName = documentName;
		this.clientName = clientName;
		this.documentOwner = documentOwner;
	}
	
	public Integer getDocumentId() {
		return documentId;
	}
	
	public String getDocumentName() {
		return documentName;
	}
	
	public String getDocumentOwner() {
		return documentOwner;
	}

	public String getClientName() {
		return clientName;
	}
}
