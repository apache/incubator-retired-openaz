/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.openaz.xacml.admin.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class MatchFunctionQueryDelegate implements FreeformStatementDelegate {
	private static final long serialVersionUID = 1L;
    private List<Filter> filters = null;
    private List<OrderBy> orderBys = null;

	public MatchFunctionQueryDelegate() {
	}

	@Override
	public String getQueryString(int offset, int limit)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("use getQueryStatement");
	}

	@Override
	public String getCountQuery() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("getCountStatement");
	}

	@Override
	public void setFilters(List<Filter> filters)
			throws UnsupportedOperationException {
		this.filters = filters;
	}

	@Override
	public void setOrderBy(List<OrderBy> orderBys)
			throws UnsupportedOperationException {
		this.orderBys = orderBys;
	}

	@Override
	public int storeRow(Connection conn, RowItem row)
			throws UnsupportedOperationException, SQLException {
		throw new UnsupportedOperationException("Cannot store anything");
	}

	@Override
	public boolean removeRow(Connection conn, RowItem row)
			throws UnsupportedOperationException, SQLException {
		throw new UnsupportedOperationException("Cannot remove anything");
	}

	@Override
	public String getContainsRowQueryString(Object... keys)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("use getContainsRowQueryStatement");
	}

	@Override
	public StatementHelper getQueryStatement(int offset, int limit)
			throws UnsupportedOperationException {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer("SELECT * FROM match_functions ");
		if (this.filters != null && this.filters.isEmpty() == false) {
			query.append(QueryBuilder.getWhereStringForFilters(this.filters, sh));
		}
		query.append(getOrderByString());
		if (offset != 0 || limit != 0) {
			query.append(" LIMIT ").append(limit);
			query.append(" OFFSET ").append(offset);
		}
		sh.setQueryString(query.toString());
		return sh;
	}

	@Override
	public StatementHelper getCountStatement()
			throws UnsupportedOperationException {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM match_functions ");
		if (this.filters != null && this.filters.isEmpty() == false) {
			query.append(QueryBuilder.getWhereStringForFilters(this.filters, sh));
		}
		query.append(getOrderByString());
		sh.setQueryString(query.toString());
		return sh;
	}

	@Override
	public StatementHelper getContainsRowQueryStatement(Object... keys)
			throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer("SELECT * FROM match_functions WHERE ARGUMENT_ID = ?");
        sh.addParameterValue(keys[0]);
        sh.setQueryString(query.toString());
        return sh;
	}

    private String getOrderByString() {
        StringBuffer orderBuffer = new StringBuffer("");
        if (this.orderBys != null && !this.orderBys.isEmpty()) {
            orderBuffer.append(" ORDER BY ");
            OrderBy lastOrderBy = this.orderBys.get(this.orderBys.size() - 1);
            for (OrderBy orderBy : this.orderBys) {
                orderBuffer.append(SQLUtil.escapeSQL(orderBy.getColumn()));
                if (orderBy.isAscending()) {
                    orderBuffer.append(" ASC");
                } else {
                    orderBuffer.append(" DESC");
                }
                if (orderBy != lastOrderBy) {
                    orderBuffer.append(", ");
                }
            }
        }
        return orderBuffer.toString();
    }
}
