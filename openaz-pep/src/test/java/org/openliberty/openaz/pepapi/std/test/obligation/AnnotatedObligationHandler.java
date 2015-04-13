package org.openliberty.openaz.pepapi.std.test.obligation;

import org.openliberty.openaz.pepapi.Attribute;
import org.openliberty.openaz.pepapi.MatchAllObligationAttributes;
import org.openliberty.openaz.pepapi.MatchAnyObligation;

@MatchAnyObligation({"jpmc:obligation:one","jpmc:obligation:two","jpmc:obligation:three"})
@MatchAllObligationAttributes({
	@Attribute(id="jpmc:obligation:obligation-type", anyValue={"FILTERING","REDACTION"}),
	@Attribute(id="jpmc:resource:attribute:resource-type", anyValue={"Card"}),
	@Attribute(id="jpmc:obligation:attribute:attribute-1")
})
public class AnnotatedObligationHandler {
	
	public void enforce() {
		
	}
	
}
