/*
 Created By Siva Reddy Puli
 */
package com.anf.core.listeners;

import com.anf.core.utils.ResourceUtil;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(
        service = {
                EventHandler.class,
                JobConsumer.class
        },
        immediate = true,
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        property = {
                "event.topics=" + PageEvent.EVENT_TOPIC,
                JobConsumer.PROPERTY_TOPICS + "=" + "aem/pagecreation/event"
        }
)

public class PageCreationListener implements EventHandler, JobConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageCreationListener.class);
    private static final String PN_PAGE_CREATED = "pageCreated";
    private static final String PN_ROOT_PATH = "/content/anf-code-challenge/us/en";

    @Reference
    JobManager jobManager;
    @Reference
    ResourceUtil resourceUtil;

    @Override
    public void handleEvent(Event event) {
        PageEvent pageEvent = PageEvent.fromEvent(event);
        Map<String, Object> properties = new HashMap<>();
        properties.put("pageEvent", pageEvent);
        jobManager.addJob("aem/pagecreation/event", properties);
    }

    @Override
    public JobResult process(Job job) {
        PageEvent pageEvent = (PageEvent) job.getProperty("pageEvent");
            if (pageEvent != null) {
                Iterator<PageModification> modificationsIterator = pageEvent.getModifications();
                while (modificationsIterator.hasNext()) {
                    PageModification modification = modificationsIterator.next();

                    /* Checks if event path is under the root path and event type is page created */
                    if (modification.getPath() != null && modification.getPath().contains(PN_ROOT_PATH) && PageModification.ModificationType.CREATED.equals(modification.getType())) {
                        addPropertyToPage(modification.getPath());
                    }
                }
            }
        return JobResult.OK;
    }

    /**
     * addPropertyToPage
     * @param pagePath
     *
     * Adds property pageCreated with value Boolean true
     */
    private void addPropertyToPage(String pagePath) {

        try (ResourceResolver resolver = resourceUtil.getServiceResourceResolver()) {
            Optional.of(resolver)
                    .map(r -> resolver.getResource(pagePath + "/jcr:content"))
                    .map(res -> res.adaptTo(ModifiableValueMap.class))
                    .map(m -> m.put(PN_PAGE_CREATED, "{Boolean}true"));
            resolver.commit();
            LOGGER.info("Added property pageCreated to page", pagePath);
        } catch (PersistenceException | LoginException e) {
            LOGGER.error("Can't add property pageCreated to page {}", pagePath);
        }
    }

}