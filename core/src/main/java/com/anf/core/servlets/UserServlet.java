/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.servlets;

import com.anf.core.services.ContentService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Random;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

//@Component(service = { Servlet.class })
//@SlingServletPaths(
//        value = "/bin/saveUserDetails"
//)


@Component(service = Servlet.class,
        property = {
            "sling.servlet.methods=" + HttpConstants.METHOD_POST,
            "sling.servlet.paths=" + "/bin/saveUserDetails"
})
public class UserServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ContentService contentService;

    @Override
    protected void doPost(final SlingHttpServletRequest req,
                          final SlingHttpServletResponse resp) throws ServletException, IOException {

        try {
            int age = Integer.parseInt(req.getParameter("age"));
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String country = req.getParameter("country");
            ResourceResolver resourceResolver = req.getResourceResolver();
            Resource resource = resourceResolver.getResource("/etc/age");
            ValueMap valueMap = resource.getValueMap();

            int minAge = Integer.parseInt(valueMap.get("minAge", EMPTY));
            int maxAge = Integer.parseInt(valueMap.get("maxAge", EMPTY));

            JSONObject obj = new JSONObject();

            JSONObject childObj = new JSONObject();

            if(age < minAge || age > maxAge) {
                childObj.put("status", "ERROR");
                childObj.put("field", "AGE");
            } else {
                Resource desResource = resourceResolver.getResource("/var");

                Node node = desResource.adaptTo(Node.class);

                /**
                 * Create a new node with name and primary type and add it below the path specified by the resource
                 */
                Node newNode = null;
                if(!node.hasNode("anf-code-challenge")) {
                    newNode = node.addNode("anf-code-challenge", "nt:unstructured");
                } else {
                    newNode = node.getNode("anf-code-challenge");
                }
                Node userNode = null;
                if(newNode!=null) {
                    if(newNode.hasNode(lastName)) {
                        userNode = newNode.addNode(lastName + String.valueOf(new Random().nextInt(100)));
                    } else {
                        userNode = newNode.addNode(lastName);
                    }
                }

                userNode.setProperty("firstName", firstName);
                userNode.setProperty("lastName", lastName);
                userNode.setProperty("country", country);
                userNode.setProperty("age", age);
                childObj.put("status", "SUCCESS");
            }
            resourceResolver.commit();
            obj.put("data", childObj);
            resp.getWriter().println(obj.toString());
        } catch (RuntimeException e) {
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (LockException e) {
            throw new RuntimeException(e);
        } catch (ItemExistsException e) {
            throw new RuntimeException(e);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException(e);
        } catch (PathNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchNodeTypeException e) {
            throw new RuntimeException(e);
        } catch (VersionException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }


    }
}
