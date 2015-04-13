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

package com.att.research.xacmlatt.pdp.eval;

/**
 * Evaluatable is the interface objects implement to indicate they can be evaluated with an {@link com.att.research.xacmlatt.pdp.eval.EvaluationContext}
 * and return an {@link com.att.research.xacmlatt.pdp.eval.EvaluationResult}.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public interface Evaluatable {
	public EvaluationResult evaluate(EvaluationContext evaluationContext) throws EvaluationException;
}
