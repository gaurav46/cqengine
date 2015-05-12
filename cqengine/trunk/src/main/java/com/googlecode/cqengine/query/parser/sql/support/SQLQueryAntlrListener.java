/**
 * Copyright 2012-2015 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.cqengine.query.parser.sql.support;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.query.parser.antlr4.cqsql.CQEngineSQLBaseListener;
import com.googlecode.cqengine.query.parser.antlr4.cqsql.CQEngineSQLParser;
import com.googlecode.cqengine.query.parser.common.QueryParser;
import com.googlecode.cqengine.query.parser.common.ValueParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

/**
 * @author Niall Gallagher
 */
public class SQLQueryAntlrListener<O> extends CQEngineSQLBaseListener {

    /*
        NOTE: this class depends on classes auto-generated by the antlr4-maven-plugin.
        Run "mvn clean compile" to generate those classes.
    */

    final QueryParser<O> queryParser;

    // A map of parent context, to parsed child queries belonging to that context...
    final Map<ParserRuleContext, Collection<Query<O>>> childQueries = new HashMap<ParserRuleContext, Collection<Query<O>>>();

    int numQueriesEncountered = 0;
    int numQueriesParsed = 0;

    public SQLQueryAntlrListener(QueryParser<O> queryParser) {
        this.queryParser = queryParser;
    }

    // ======== Handler methods for each type of query defined in the antlr grammar... ========

    @Override
    public void exitAndQuery(CQEngineSQLParser.AndQueryContext ctx) {
        addParsedQuery(ctx, QueryFactory.and(childQueries.get(ctx)));
    }

    @Override
    public void exitOrQuery(CQEngineSQLParser.OrQueryContext ctx) {
        addParsedQuery(ctx, QueryFactory.or(childQueries.get(ctx)));
    }

    @Override
    public void exitNotQuery(CQEngineSQLParser.NotQueryContext ctx) {
        addParsedQuery(ctx, QueryFactory.not(childQueries.get(ctx).iterator().next()));
    }

    @Override
    public void exitEqualQuery(CQEngineSQLParser.EqualQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Object> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Object value = valueParser.validatedParse(queryParameter.getText());
        addParsedQuery(ctx, QueryFactory.equal(attribute, value));
    }

    @Override
    public void exitNotEqualQuery(CQEngineSQLParser.NotEqualQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Object> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Object value = valueParser.validatedParse(queryParameter.getText());
        addParsedQuery(ctx, QueryFactory.not(QueryFactory.equal(attribute, value)));
    }

    @Override
    public void exitLessThanOrEqualToQuery(CQEngineSQLParser.LessThanOrEqualToQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Comparable value = valueParser.validatedParse(queryParameter.getText());

        addParsedQuery(ctx, QueryFactory.lessThanOrEqualTo(attribute, value));
    }

    @Override
    public void exitLessThanQuery(CQEngineSQLParser.LessThanQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Comparable value = valueParser.validatedParse(queryParameter.getText());

        addParsedQuery(ctx, QueryFactory.lessThan(attribute, value));
    }

    @Override
    public void exitGreaterThanOrEqualToQuery(CQEngineSQLParser.GreaterThanOrEqualToQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Comparable value = valueParser.validatedParse(queryParameter.getText());

        addParsedQuery(ctx, QueryFactory.greaterThanOrEqualTo(attribute, value));
    }

    @Override
    public void exitGreaterThanQuery(CQEngineSQLParser.GreaterThanQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterContext queryParameter = ctx.queryParameter();
        Comparable value = valueParser.validatedParse(queryParameter.getText());

        addParsedQuery(ctx, QueryFactory.greaterThan(attribute, value));
    }

    @Override
    public void exitBetweenQuery(CQEngineSQLParser.BetweenQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        List<CQEngineSQLParser.QueryParameterContext> queryParameters = ctx.queryParameter();
        validateNumberOfParameters(2, queryParameters);

        Comparable lowerValue = valueParser.validatedParse(queryParameters.get(0).getText());
        Comparable upperValue = valueParser.validatedParse(queryParameters.get(1).getText());

        addParsedQuery(ctx, QueryFactory.between(attribute, lowerValue, upperValue));
    }

    @Override
    public void exitNotBetweenQuery(CQEngineSQLParser.NotBetweenQueryContext ctx) {
        Attribute<O, Comparable> attribute = getComparableAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Comparable> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        List<CQEngineSQLParser.QueryParameterContext> queryParameters = ctx.queryParameter();
        validateNumberOfParameters(2, queryParameters);

        Comparable lowerValue = valueParser.validatedParse(queryParameters.get(0).getText());
        Comparable upperValue = valueParser.validatedParse(queryParameters.get(1).getText());

        addParsedQuery(ctx, QueryFactory.not(QueryFactory.between(attribute, lowerValue, upperValue)));
    }

    @Override
    public void exitInQuery(CQEngineSQLParser.InQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Object> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        List<CQEngineSQLParser.QueryParameterContext> queryParameters = ctx.queryParameter();
        validateMinimumNumberOfParameters(1, queryParameters);

        Collection<Object> values = new ArrayList<Object>(queryParameters.size());
        for (CQEngineSQLParser.QueryParameterContext queryParameter : queryParameters) {
            Object value = valueParser.validatedParse(queryParameter.getText());
            values.add(value);
        }

        addParsedQuery(ctx, QueryFactory.in(attribute, values));
    }

    @Override
    public void exitNotInQuery(CQEngineSQLParser.NotInQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<Object> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        List<CQEngineSQLParser.QueryParameterContext> queryParameters = ctx.queryParameter();
        validateMinimumNumberOfParameters(1, queryParameters);

        Collection<Object> values = new ArrayList<Object>(queryParameters.size());
        for (CQEngineSQLParser.QueryParameterContext queryParameter : queryParameters) {
            Object value = valueParser.validatedParse(queryParameter.getText());
            values.add(value);
        }

        addParsedQuery(ctx, QueryFactory.not(QueryFactory.in(attribute, values)));
    }

    @Override
    public void exitStartsWithQuery(CQEngineSQLParser.StartsWithQueryContext ctx) {
        Attribute<O, String> attribute = getStringAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<String> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterTrailingPercentContext queryParameter = ctx.queryParameterTrailingPercent();
        String value = valueParser.validatedParse(queryParameter.getText());
        value = value.substring(0, value.length() - 1);

        addParsedQuery(ctx, QueryFactory.startsWith(attribute, value));
    }

    @Override
    public void exitEndsWithQuery(CQEngineSQLParser.EndsWithQueryContext ctx) {
        Attribute<O, String> attribute = getStringAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<String> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterLeadingPercentContext queryParameter = ctx.queryParameterLeadingPercent();
        String value = valueParser.validatedParse(queryParameter.getText());
        value = value.substring(1, value.length());

        addParsedQuery(ctx, QueryFactory.endsWith(attribute, value));
    }

    @Override
    public void exitContainsQuery(CQEngineSQLParser.ContainsQueryContext ctx) {
        Attribute<O, String> attribute = getStringAttribute(getAttributeName(ctx.attributeName()));
        ValueParser<String> valueParser = queryParser.getValueParser(attribute.getAttributeType());

        CQEngineSQLParser.QueryParameterLeadingAndTrailingPercentContext queryParameter = ctx.queryParameterLeadingAndTrailingPercent();
        String value = valueParser.validatedParse(queryParameter.getText());
        value = value.substring(1, value.length() - 1);

        addParsedQuery(ctx, QueryFactory.contains(attribute, value));
    }

    @Override
    public void exitHasQuery(CQEngineSQLParser.HasQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        addParsedQuery(ctx, QueryFactory.has(attribute));
    }

    @Override
    public void exitNotHasQuery(CQEngineSQLParser.NotHasQueryContext ctx) {
        Attribute<O, Object> attribute = getObjectAttribute(getAttributeName(ctx.attributeName()));
        addParsedQuery(ctx, QueryFactory.not(QueryFactory.has(attribute)));
    }

    /** This handler is called for all queries, allows us to validate that no handlers are missing. */
    @Override
    public void exitQuery(CQEngineSQLParser.QueryContext ctx) {
        numQueriesEncountered++;
        ensureAllQueriesParsed(numQueriesEncountered, numQueriesParsed);
    }

    // ======== Utility methods... ========

    /**
     * Adds the given query to a list of child queries which have not yet been wrapped in a parent query.
     */
    void addParsedQuery(ParserRuleContext currentContext, Query<O> parsedQuery) {
        // Retrieve the possibly null parent query...
        ParserRuleContext parentContext = getParentContextOfType(currentContext, CQEngineSQLParser.AndQueryContext.class, CQEngineSQLParser.OrQueryContext.class, CQEngineSQLParser.NotQueryContext.class);
        Collection<Query<O>> childrenOfParent = this.childQueries.get(parentContext);
        if (childrenOfParent == null) {
            childrenOfParent = new ArrayList<Query<O>>();
            this.childQueries.put(parentContext, childrenOfParent); // parentContext will be null if this is root query
        }
        childrenOfParent.add(parsedQuery);
        numQueriesParsed++;
    }

    /**
     * Can be called when parsing has finished, to retrieve the parsed query.
     */
    public Query<O> getParsedQuery() {
        Collection<Query<O>> rootQuery = childQueries.get(null);
        if (rootQuery == null) {
            // There was no WHERE clause...
            return QueryFactory.all(this.queryParser.getObjectType());
        }
        validateChildQueries(1, rootQuery.size());
        return rootQuery.iterator().next();
    }

    /**
     * Examines the parent rule contexts of the given context, and returns the first parent context which is assignable
     * from (i.e. is a, or is a subclass of) one of the given context types.
     * @param currentContext The starting context whose parent contexts should be examined
     * @param parentContextTypes The types of parent context sought
     * @return The first parent context which is assignable from one of the given context types,
     * or null if there is no such parent in the tree
     */
    static ParserRuleContext getParentContextOfType(ParserRuleContext currentContext, Class<?>... parentContextTypes) {
        while (currentContext != null) {
            currentContext = currentContext.getParent();
            if (currentContext != null) {
                for (Class<?> parentContextType : parentContextTypes) {
                    if (parentContextType.isAssignableFrom(currentContext.getClass())) {
                        return currentContext;
                    }
                }
            }
        }
        return null;
    }

    Attribute<O, Comparable> getComparableAttribute(String attributeName) {
        Attribute<O, ?> attribute = queryParser.getRegisteredAttribute(attributeName);
        if (!Comparable.class.isAssignableFrom(attribute.getAttributeType())) {
            throw new IllegalStateException("Non-Comparable attribute used in a query which requires a Comparable attribute: " + attribute.getAttributeName());
        }
        @SuppressWarnings("unchecked")
        Attribute<O, Comparable> result = (Attribute<O, Comparable>) attribute;
        return result;
    }

    Attribute<O, String> getStringAttribute(String attributeName) {
        Attribute<O, ?> attribute = queryParser.getRegisteredAttribute(attributeName);
        if (!String.class.isAssignableFrom(attribute.getAttributeType())) {
            throw new IllegalStateException("Non-String attribute used in a query which requires a String attribute: " + attribute.getAttributeName());
        }
        @SuppressWarnings("unchecked")
        Attribute<O, String> result = (Attribute<O, String>) attribute;
        return result;
    }

    Attribute<O, Object> getObjectAttribute(String attributeName) {
        Attribute<O, ?> attribute = queryParser.getRegisteredAttribute(attributeName);
        @SuppressWarnings("unchecked")
        Attribute<O, Object> result = (Attribute<O, Object>) attribute;
        return result;
    }

    String getAttributeName(CQEngineSQLParser.AttributeNameContext ctx) {
        ValueParser<String> valueParser = queryParser.getValueParser(String.class);
        return valueParser.validatedParse(ctx.getText());
    }

    static void validateObjectTypeParameter(Class<?> expectedType, String actualType) {
        if (!expectedType.getSimpleName().equals(actualType)) {
            throw new IllegalStateException("Unexpected object type parameter, expected: " + expectedType.getSimpleName() + ", found: " + actualType);
        }
    }

    static void validateNumberOfParameters(int expectedNumber, List<?> actualParameters) {
        int size = actualParameters.size();
        if (size != expectedNumber) {
            throw new IllegalStateException("Unexpected number of parameters, expected: " + expectedNumber + ", found: " + size);
        }
    }

    static void validateMinimumNumberOfParameters(int expectedNumber, List<CQEngineSQLParser.QueryParameterContext> actualParameters) {
        int size = actualParameters.size();
        if (size < expectedNumber) {
            throw new IllegalStateException("Unexpected number of parameters, expected minimum: " + expectedNumber + ", found: " + size);
        }
    }

    static void validateChildQueries(int expected, int actual) {
        if (actual != expected) {
            throw new IllegalStateException("Unexpected number of child queries, expected: " + expected + ", actual: " + actual);
        }
    }

    static void ensureAllQueriesParsed(int numQueriesEncountered, int numQueriesParsed) {
        if (numQueriesEncountered != numQueriesParsed) {
            throw new IllegalStateException("A query declared in the antlr grammar, was not parsed by the listener. If a new query is added in the grammar, a corresponding handler must also be added in the listener.");
        }
    }
}