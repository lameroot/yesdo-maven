package ru.yesdo.db;

import org.junit.Test;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.db.repository.ActivityRepository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 16:32
 */
public class GeneralCommonServiceDbTest extends GeneralCommonServiceTest {

    @PersistenceContext
    protected EntityManager entityManager;
    @Resource
    protected ActivityRepository activityRepository;

    @Test
    public void testExist1() {
        assertNotNull(entityManager);
        assertNotNull(activityRepository);
    }
}
