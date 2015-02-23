package ru.yesdo.service;

import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import java.util.Set;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 17:28
 */
public class ActivityServiceTest extends GeneralCommonServiceTest {

    @Test
    public void testCreateWeeDays() {
        createWeekDays();
    }


    @Test
    @Transactional
    @Rollback(false)
    public void testCreateActivity() {
        createWeekDays();
        for (ActivityData activityData : activityDatas) {
            Activity activity = createActivity(activityData);
            assertNotNull(activity);
        }

        assertEquals(activityDatas.size(), activityRepository.count());
        for (Activity activity : activityGraphRepository.findAll()) {
            System.out.println(activity);
        }
        assertEquals(activityDatas.size(), activityGraphRepository.count());


        Activity rootActivity = activityRepository.findByName(Activity.ROOT_NAME);
        assertNotNull(rootActivity);
        assertNotNull(rootActivity.getChild());
        assertTrue(null == rootActivity.getParents() || 0 == rootActivity.getParents().size());
        assertTrue(rootActivity.getChild().size() == 3);


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
        assertEquals(2, a21DbFound.getChild().size());

    }

    @Test
    public void testFindAll() {
        createActivities();
        Page<Activity> page = activityService.findAll(new PageRequest(0,activityDatas.size()));
        assertNotNull(page);
        assertEquals(page.getTotalElements(),activityDatas.size());
        assertEquals(page.getTotalPages(),1);

        Page<Activity> page1 = activityService.findAll(new PageRequest(0,activityDatas.size() - 1));
        assertNotNull(page1);
        assertEquals(page1.getSize(),activityDatas.size() - 1);
        assertEquals(page1.getTotalPages(),2);
    }



    @Test
    @Transactional
    public void testFindChildByDeep() {
        createActivities();
        Result<Activity> child = activityService.findChildByDeep(Activity.ROOT_NAME,1);
        assertNotNull(child);

        for (Activity activity : child) {
            System.out.println(activity);
        }

    }


}
