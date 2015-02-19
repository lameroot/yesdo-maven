package ru.yesdo.service;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.GeneralCommonServiceDbTest;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import java.util.Set;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 17:28
 */
public class ActivityServiceTest extends GeneralCommonServiceDbTest {


    @Test
    @Transactional
    public void testCreateActivity() {
        for (ActivityData activityData : activityDatas) {
            Activity activity = createActivity(activityData);
            assertNotNull(activity);
        }


        assertEquals(activityDatas.size(), activityRepository.count());
        assertEquals(activityDatas.size(), activityGraphRepository.count());

        Activity a21Found = activityGraphRepository.findByName("a21");
        assertNotNull(a21Found);
        Set<Activity> parentOfa21 = a21Found.getParents();
        assertNotNull(parentOfa21);
        assertEquals(2,parentOfa21.size());
        neo4jTemplate.fetch(parentOfa21);
        assertEquals(1, parentOfa21.stream().filter(f -> f.getName().equals("a11")).count());
        assertEquals(1, parentOfa21.stream().filter(f -> f.getName().equals("a13")).count());

        Activity a21DbFound = activityRepository.findByName("a21");
        assertNotNull(a21DbFound);
        assertNotNull(a21DbFound.getParents());
        assertEquals(2,a21DbFound.getParents().size());

    }
}
