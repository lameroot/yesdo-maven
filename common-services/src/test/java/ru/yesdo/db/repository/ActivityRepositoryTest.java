package ru.yesdo.db.repository;

import org.junit.Test;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.Activity;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 17:22
 */
public class ActivityRepositoryTest extends GeneralCommonServiceTest {

    @Test
    public void testCreateActivity() {
        Activity activity = new Activity(Activity.ROOT_NAME);
        activityRepository.save(activity);
        assertNotNull(activity);
        assertNotNull(activity.getId());
    }
}
