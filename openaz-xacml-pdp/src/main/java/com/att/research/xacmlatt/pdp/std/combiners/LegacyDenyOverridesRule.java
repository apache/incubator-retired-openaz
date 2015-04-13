/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacmlatt.pdp.std.combiners;

import java.util.Iterator;
import java.util.List;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.eval.EvaluationResult;
import com.att.research.xacmlatt.pdp.policy.CombinerParameter;
import com.att.research.xacmlatt.pdp.policy.CombiningElement;
import com.att.research.xacmlatt.pdp.policy.Rule;
import com.att.research.xacmlatt.pdp.policy.RuleEffect;

/**
 * DenyOverrides implements the XACML 1.0 "deny-overrides" combining algorithm for rules.
 * 
 * @author car
 *
 * @param <T> the java class for the {@link com.att.research.xacmlatt.pdp.eval.Evaluatable}
 * @param <U> the java class for the identifier
 */
public class LegacyDenyOverridesRule extends CombiningAlgorithmBase<Rule> {

	public LegacyDenyOverridesRule(Identifier identifierIn) {
		super(identifierIn);
	}

	@Override
	public EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<Rule>> elements, List<CombinerParameter> combinerParameters) throws EvaluationException {
		boolean atLeastOnePermit						= false;
		boolean potentialDeny							= false;

		EvaluationResult combinedResult					= new EvaluationResult(Decision.PERMIT);
		EvaluationResult evaluationResultIndeterminate	= null;
		
		Iterator<CombiningElement<Rule>> iterElements	= elements.iterator();
		while (iterElements.hasNext()) {
			CombiningElement<Rule> combiningElement		= iterElements.next();
			EvaluationResult evaluationResultElement	= combiningElement.evaluate(evaluationContext);
			
			assert(evaluationResultElement != null);
			switch(evaluationResultElement.getDecision()) {
			case DENY:
				return evaluationResultElement;
			case INDETERMINATE:
			case INDETERMINATE_DENYPERMIT:
			case INDETERMINATE_DENY:
			case INDETERMINATE_PERMIT:
				if (evaluationResultIndeterminate == null) {
					evaluationResultIndeterminate	= evaluationResultElement;
				} else {
					evaluationResultIndeterminate.merge(evaluationResultElement);
				}
				if (combiningElement.getEvaluatable().getRuleEffect() == RuleEffect.DENY) {
					potentialDeny	= true;
				}
			case NOTAPPLICABLE:
				break;
			case PERMIT:
				atLeastOnePermit	= true;
				combinedResult.merge(evaluationResultElement);
				break;
			default:
				throw new EvaluationException("Illegal Decision: \"" + evaluationResultElement.getDecision().toString());
			}
		}
		
		if (potentialDeny) {
			return evaluationResultIndeterminate;
		} else if (atLeastOnePermit) {
			return combinedResult;
		} else if (evaluationResultIndeterminate != null) {
			return evaluationResultIndeterminate;
		} else {
			return new EvaluationResult(Decision.NOTAPPLICABLE);
		}
	}

}
