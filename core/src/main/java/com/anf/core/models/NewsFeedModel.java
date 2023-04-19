package com.anf.core.models;

import com.anf.core.beans.NewsFeed;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy= DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

    private static final Logger log = LoggerFactory.getLogger(NewsFeedModel.class);
    @ValueMapValue(name="newsContentRoot", injectionStrategy= InjectionStrategy.OPTIONAL)
    @Default(values="/var/commerce/products/anf-code-challenge/newsData")
    protected String newsContentRoot;

    @SlingObject
    private ResourceResolver resourceResolver;

    String currentDate;

    @PostConstruct
    protected void init() {
        //B
    }

    public List<NewsFeed> getNewsFeedList() {
        List<NewsFeed> newsFeedList = new ArrayList<>();
        try {
            Resource newsFeedRoot = resourceResolver.getResource(newsContentRoot);
            if (newsFeedRoot != null) {
                Iterator<Resource> resourceIterator = newsFeedRoot.listChildren();
                while (resourceIterator.hasNext()) {
                    NewsFeed newsFeed = new NewsFeed();
                    Resource newsResource = resourceIterator.next();
                    ValueMap valueMap = newsResource.getValueMap();
                    newsFeed.setTitle(validateProperty(valueMap.get("title", String.class)));
                    newsFeed.setAuthor(validateProperty(valueMap.get("author", String.class)));
                    newsFeed.setDescription(validateProperty(valueMap.get("description", String.class)));
                    newsFeed.setImage(validateProperty(valueMap.get("urlImage", String.class)));
                    newsFeedList.add(newsFeed);
                }
            }
        } catch (RuntimeException e) {
            log.error("Error fetching the news data {}",e.getMessage());
        }

        return newsFeedList;
    }

    protected String validateProperty(String propValue) {
        return propValue != null ? propValue : EMPTY;
    }

    public String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        return formatter.format(date).replaceAll("/",".");
    }
}
