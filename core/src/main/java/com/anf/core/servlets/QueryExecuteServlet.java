/*
 Created By Siva Reddy Puli
 */
package com.anf.core.servlets;

import com.anf.core.utils.ResourceUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Query Execute Servlet",
        "sling.servlet.paths=" + "/bin/queryexecute", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class QueryExecuteServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecuteServlet.class);
    private static final String PN_ROOT_PATH = "/content/anf-code-challenge/us/en";
    private static final String PN_PROPERTY = "anfCodeChallenge";


    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceUtil resourceUtil;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            resourceResolver = resourceUtil.getServiceResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);
            generateQueryBuilderQuery(session);
            generateSQLQuery(session);

        } catch (LoginException e) {
            LOGGER.error("Login exception {}", e.getMessage());
        }
    }

    /**
     *
     * generateQueryBuilderQuery
     * @param session
     *
     * This method generates 10 results by running the query builder query
     * under a root path /content/anf-code-challenge/us/en
     * checks if property anfCodeChallenge exists
     *
     */
    private void generateQueryBuilderQuery(Session session) {
        Map<String, String> map = new HashMap<>();
        map.put("path", PN_ROOT_PATH);
        map.put("type", "cq:PageContent");
        map.put("property", PN_PROPERTY);
        map.put("property.operation", "exists");
        map.put("p.limit", "10");
        try {
            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();
            for (final Hit hit : result.getHits()) {
                LOGGER.info("Result Pages: {}", hit.getPath());
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository exception {}", e.getMessage());
        }
    }

    /**
     * generateSQLQuery
     * @param session
     *
     * This method generates 10 results by running the SQL2 query
     * under a root path /content/anf-code-challenge/us/en
     * checks if property anfCodeChallenge exists
     */
    private void generateSQLQuery(Session session) {
        String sqlStatement = "SELECT * FROM [cq:PageContent] as a where [" + PN_PROPERTY + "] is not null and isdescendantnode(a," + PN_ROOT_PATH + ")";
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");
            query.setLimit(10);
            QueryResult result = query.execute();
            NodeIterator nodeIterator = result.getNodes();
            while ( nodeIterator.hasNext() ) {
                LOGGER.info("Result Pages: {}", nodeIterator.nextNode().getPath());
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository exception {}", e.getMessage());
        }
    }

}