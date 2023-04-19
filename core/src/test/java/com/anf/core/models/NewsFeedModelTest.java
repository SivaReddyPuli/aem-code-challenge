package com.anf.core.models;

import com.anf.core.beans.NewsFeed;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

    NewsFeedModel newsFeedModel;

    private final AemContext context = new AemContext();

    @BeforeEach
    public void setup() throws Exception {
        context.addModelsForClasses(NewsFeedModel.class);
        context.load().json("/simple-page-data.json", "/content/anf-code-challenge/us/en/page");
        Resource resource = context.resourceResolver().getResource("/content/anf-code-challenge/us/en/page/jcr:content/root/container/newsfeed");
        newsFeedModel = resource.adaptTo(NewsFeedModel.class);
    }

    @Test
    void testGetCurrentDate() throws Exception {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        String currentDate = formatter.format(date).replaceAll("/",".");
        assertEquals(newsFeedModel.getCurrentDate(), currentDate);
    }

    @Test
    void testGetNewsFeedList() throws Exception {

        newsFeedModel.getNewsFeedList();

        int index = 0;
        for (NewsFeed newsFeed : newsFeedModel.getNewsFeedList()) {
            if(index == 0) {
                assertEquals(newsFeed.getAuthor(),"Caroline Fox");
            } else {
                assertEquals(newsFeed.getAuthor(),"Leah Askarinam");
            }
            index++;
        }
    }

    @Test
    void testValidateProperty() throws Exception {
        assertEquals(newsFeedModel.validateProperty("Caroline Fox"), "Caroline Fox");
        assertEquals(newsFeedModel.validateProperty(null), StringUtils.EMPTY);
    }

}