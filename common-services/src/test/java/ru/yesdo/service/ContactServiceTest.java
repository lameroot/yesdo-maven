package ru.yesdo.service;

import org.junit.Test;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.geo.Shape;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.graph.repository.ContactGraphRepository;
import ru.yesdo.model.Contact;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;

/**
 * Created by lameroot on 25.02.15.
 */
public class ContactServiceTest extends GeneralCommonServiceTest {

    @Resource
    private ContactGraphRepository contactGraphRepository;

    @Test
    @Transactional
    public void testGetContacts() {
        createActivities();
        createMerchants();
        createProducts();
        createOffers();

        assertTrue(offerDatas.size() <= contactGraphRepository.count());

        Box box = new Box(new Point(0.0,0.0),new Point(10.5,10.0));
        Result<Contact> contacts = contactGraphRepository.findWithinBoundingBox(Contact.LOCATION_INDEX_NAME, box);
        assertNotNull(contacts);
        int countInBox1 = 0;
        for (Contact contact : contacts) {
            countInBox1++;
        }
        assertEquals(3, countInBox1);

        Shape polygon = new Polygon(new Point(0.0,0.0),new Point(0.0,10.0),new Point(35.0,0.0));
        Result<Contact> contactsInPolygon = contactGraphRepository.findWithinShape(Contact.LOCATION_INDEX_NAME, polygon);
        assertNotNull(contactsInPolygon);
        int countInPolygon = 0;
        for (Contact contact : contactsInPolygon) {
            countInPolygon++;
        }
        assertEquals(3,countInPolygon);


    }
}
