package ru.yesdo.db;

import org.junit.Before;
import org.junit.Test;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.db.repository.ActivityRepository;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.db.repository.ProductRepository;
import ru.yesdo.db.repository.UserRepository;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.graph.repository.ProductGraphRepository;
import ru.yesdo.graph.repository.UserGraphRepository;
import ru.yesdo.model.Activity;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.Product;
import ru.yesdo.model.User;
import ru.yesdo.model.data.ActivityData;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.ProductData;
import ru.yesdo.model.data.UserData;
import ru.yesdo.service.ActivityService;
import ru.yesdo.service.MerchantService;
import ru.yesdo.service.ProductService;
import ru.yesdo.service.UserService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @Resource
    protected ActivityGraphRepository activityGraphRepository;
    @Resource
    protected ActivityService activityService;
    @Resource
    protected MerchantRepository merchantRepository;
    @Resource
    protected MerchantGraphRepository merchantGraphRepository;
    @Resource
    protected MerchantService merchantService;
    @Resource
    protected ProductService productService;
    @Resource
    protected ProductRepository productRepository;
    @Resource
    protected ProductGraphRepository productGraphRepository;
    @Resource
    protected UserRepository userRepository;
    @Resource
    protected UserGraphRepository userGraphRepository;
    @Resource
    protected UserService userService;


    protected List<ActivityData> activityDatas = new ArrayList<>();
    protected List<MerchantData> merchantDatas = new ArrayList<>();
    protected List<ProductData> productDatas = new ArrayList<>();
    protected List<UserData> userDatas = new ArrayList<>();
    @Before
    public void initData() {
        ActivityData a0 = createActivityData(Activity.ROOT_TITLE);
        ActivityData a11 = createActivityData("a11", Activity.ROOT_TITLE);
        ActivityData a12 = createActivityData("a12", Activity.ROOT_TITLE);
        ActivityData a13 = createActivityData("a13", Activity.ROOT_TITLE);
        ActivityData a21 = createActivityData("a21", "a11", "a13");
        ActivityData a22 = createActivityData("a22", "a13");
        ActivityData a31 = createActivityData("a31", "a21");
        ActivityData a32 = createActivityData("a32", "a21");

        activityDatas.add(a0);
        activityDatas.add(a11);
        activityDatas.add(a12);
        activityDatas.add(a13);
        activityDatas.add(a21);
        activityDatas.add(a22);
        activityDatas.add(a31);
        activityDatas.add(a32);

        MerchantData m11 = createMerchantData("m11", "a11");
        MerchantData m12 = createMerchantData("m12", "a13");
        MerchantData m21 = createMerchantData("m21", "a21", "a22");
        MerchantData m22 = createMerchantData("m22", "a22");
        MerchantData m31 = createMerchantData("m31", "a31");
        MerchantData m32 = createMerchantData("m32", "a31");
        MerchantData m33 = createMerchantData("m33", "a32");
        MerchantData m34 = createMerchantData("m34","a32");

        merchantDatas.add(m11);
        merchantDatas.add(m12);
        merchantDatas.add(m21);
        merchantDatas.add(m22);
        merchantDatas.add(m31);
        merchantDatas.add(m32);
        merchantDatas.add(m33);
        merchantDatas.add(m34);

        ProductData p11 = createProductData("p11", "m11");
        ProductData p12 = createProductData("p12", "m11");
        ProductData p13 = createProductData("p13", "m12");
        ProductData p14 = createProductData("p14", "m12");
        ProductData p21 = createProductData("p21", "m21");
        ProductData p22 = createProductData("p22", "m21");
        ProductData p23 = createProductData("p23", "m22");
        ProductData p24 = createProductData("p24", "m22");
        ProductData p31 = createProductData("p31", "m31");
        ProductData p32 = createProductData("p32", "m31");
        ProductData p33 = createProductData("p33", "m32");
        ProductData p34 = createProductData("p34", "m32");
        ProductData p35 = createProductData("p35", "m33");
        ProductData p36 = createProductData("p36", "m33");
        ProductData p37 = createProductData("p37", "m34");
        ProductData p38 = createProductData("p38", "m34");

        productDatas.add(p11);
        productDatas.add(p12);
        productDatas.add(p13);
        productDatas.add(p14);
        productDatas.add(p21);
        productDatas.add(p22);
        productDatas.add(p23);
        productDatas.add(p24);
        productDatas.add(p31);
        productDatas.add(p32);
        productDatas.add(p33);
        productDatas.add(p34);
        productDatas.add(p35);
        productDatas.add(p36);
        productDatas.add(p37);
        productDatas.add(p38);

        UserData u11 = createUserData("u11", "m11");
        UserData u12 = createUserData("u12", "m11");
        UserData u13 = createUserData("u13", "m12");
        UserData u14 = createUserData("u14", "m12");

        userDatas.add(u11);
        userDatas.add(u12);
        userDatas.add(u13);
        userDatas.add(u14);

    }

    protected ActivityData createActivityData(String title, String... parents) {
        ActivityData activityData = new ActivityData().setName(title).setTitle(title);
        for (String parent : parents) {
            Activity parentFound = new Activity(parent);
            activityData.addParent(parentFound);
        }
        return activityData;
    }

    protected MerchantData createMerchantData(String title, String... activityTitles) {
        MerchantData merchantData = new MerchantData().setName(title).setTitle(title);
        for (String activityTitle : activityTitles) {
            Activity activity = new Activity(activityTitle);
            merchantData.addActivity(activity);
        }
        return merchantData;
    }

    protected ProductData createProductData(String title, String merchantName) {
        String code = UUID.randomUUID().toString();
        ProductData productData = new ProductData().setTitle(title).setCreatedAt(new Date()).setMerchant(new Merchant(merchantName)).setCode(code);
        return productData;
    }

    protected UserData createUserData(String login, String merchantName) {
        return new UserData().setLogin(login).setMerchant(new Merchant(merchantName));
    }

    protected ProductData findProductDataByTitle(String title) {
        return productDatas.stream().filter(f -> f.getTitle().equals(title)).findFirst().get();
    }

    protected Activity createActivity(ActivityData activityData) {
        return activityService.create(activityData);
    }

    protected Merchant createMerchant(MerchantData merchantData) {
        return merchantService.create(merchantData);
    }

    protected Product createProduct(ProductData productData) {
        return productService.create(productData);
    }

    protected User createUser(UserData userData) {
        return userService.create(userData);
    }

    protected void createActivities() {
        activityDatas.stream().forEach(activityData -> createActivity(activityData));
    }

    protected void createMerchants() {
        merchantDatas.stream().forEach(merchantData -> createMerchant(merchantData));
    }

    protected void createProducts() {
        productDatas.stream().forEach(productData -> createProduct(productData));
    }

    protected void createUsers() {
        userDatas.stream().forEach(userData -> createUser(userData));
    }

    @Test
    public void testExist1() {
        assertNotNull(entityManager);
        assertNotNull(activityRepository);
        assertNotNull(activityGraphRepository);
    }

    protected Activity findActivityByName(String name) {
        return activityRepository.findByName(name);
    }
}
