package com.anf.core.utils;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;

@Component(service = ResourceUtil.class, immediate = true)
public class ResourceUtil {

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private static final String DEFAULT_SERVICE_NAME = "anfServiceUser";

	/**
	 * getServiceResourceResolver
	 *
	 * Gets the resource resolver by service.
	 * @return the resource resolver by service
	 * @throws LoginException the login exception
	 */
	public ResourceResolver getServiceResourceResolver() throws LoginException {

		HashMap<String, Object> resolverParamMap = new HashMap<>();

		resolverParamMap.put(ResourceResolverFactory.SUBSERVICE, DEFAULT_SERVICE_NAME);

		return resourceResolverFactory.getServiceResourceResolver(resolverParamMap);
	}


}

