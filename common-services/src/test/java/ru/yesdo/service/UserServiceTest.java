package ru.yesdo.service;

import org.junit.Test;
import ru.yesdo.db.GeneralCommonServiceDbTest;
import ru.yesdo.model.User;
import ru.yesdo.model.data.UserData;

/**
 * Created by lameroot on 19.02.15.
 */
public class UserServiceTest extends GeneralCommonServiceDbTest {

    @Test
    public void testCreateUser() {
        createActivities();
        createMerchants();
        createProducts();
        for (UserData userData : userDatas) {
            User user = createUser(userData);
            assertNotNull(user);
            assertNotNull(user.getId());
        }

        assertEquals(userDatas.size(),userRepository.count());
        assertEquals(userDatas.size(),userGraphRepository.count());

    }
}
