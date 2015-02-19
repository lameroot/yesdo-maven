package ru.yesdo.service;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.db.GeneralCommonServiceDbTest;
import ru.yesdo.model.Activity;
import ru.yesdo.model.data.ActivityData;

import javax.annotation.Resource;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 17:28
 */
public class ActivityServiceTest extends GeneralCommonServiceDbTest {

    @Resource
    private ActivityService activityService;

    private ActivityData create(String title,Activity...parents) {
        ActivityData activityData = new ActivityData().setName(title).setTitle(title);
        for (Activity parent : parents) {
            activityData.addParent(parent);
        }
        return activityData;
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testCreateActivity() {
        ActivityData a0Data = create(Activity.ROOT_TITLE + ":").setPartial(false);
        Activity a0 = activityService.create(a0Data);
        assertNotNull(a0);
        assertNotNull(a0.getId());

        for (Activity activity : neo4jTemplate.findAll(Activity.class)) {
            System.out.println("graph = " + activity);
        }

        for (Activity activity : activityRepository.findAll()) {
            System.out.println("db = " + activity);
        }
    }
}
