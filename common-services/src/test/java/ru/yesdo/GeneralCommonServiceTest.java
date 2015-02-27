package ru.yesdo;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import ru.yesdo.db.JpaConfigTest;
import ru.yesdo.db.repository.*;
import ru.yesdo.graph.GraphConfigTest;
import ru.yesdo.graph.repository.*;
import ru.yesdo.model.*;
import ru.yesdo.model.data.*;
import ru.yesdo.service.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;

/**
 * User: Krainov
 * Date: 19.02.2015
 * Time: 16:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
        JpaConfigTest.class,
        GraphConfigTest.class,
        CommonServicesConfig.class
})
@ActiveProfiles(value = "test")
public class GeneralCommonServiceTest extends TestCase {

    @Resource
    protected ApplicationContext applicationContext;
    @Autowired(required = false)
    protected Neo4jTemplate neo4jTemplate;
    @Resource
    protected PlatformTransactionManager transactionManager;
    @Resource
    protected GraphDatabaseService graphDatabaseService;
    @Resource
    protected SpatialDatabaseService spatialDatabaseService;
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
    @Resource
    protected OfferRepository offerRepository;
    @Resource
    protected OfferGraphRepository offerGraphRepository;
//    @Resource
//    protected GeoDataImporter geoDataImporter;
    @Resource
    protected WeekDayGraphRepository weekDayGraphRepository;
    @Resource
    protected ContactGraphRepository contactGraphRepository;


    protected List<ActivityData> activityDatas = new ArrayList<>();
    protected List<MerchantData> merchantDatas = new ArrayList<>();
    protected List<ProductData> productDatas = new ArrayList<>();
    protected List<UserData> userDatas = new ArrayList<>();
    protected List<Object[]> offerDatas = new ArrayList<>();
    protected List<WeekDay> weekDays = new ArrayList(){{
        for (WeekDay.Days days : WeekDay.Days.values()) {
            add(new WeekDay(days));
        }
    }};

    @BeforeTransaction
    public void cleanGraphDb() {
        System.out.println("---- clean graph db");
        Neo4jHelper.cleanDb(graphDatabaseService);
    }

    @Before
    public void initData() {
        Transaction transaction = graphDatabaseService.beginTx();
        try {
            Neo4jHelper.cleanDb(graphDatabaseService);
            transaction.success();
        } catch (Exception e) {
            transaction.failure();
            e.printStackTrace();
        } finally {
            transaction.close();
        }


        ActivityData a0 = createActivityData(Activity.ROOT_NAME);
        ActivityData a11 = createActivityData("a11", Activity.ROOT_NAME);
        ActivityData a12 = createActivityData("a12", Activity.ROOT_NAME);
        ActivityData a13 = createActivityData("a13", Activity.ROOT_NAME);
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

        ContactParam contactParam = new ContactParam("name", "stas", ContactParam.Type.PROFILE);
        offerDatas.add(createOfferDataAsArray("m11","p11",100L,5.0,23.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m11","p12",110L,10.0,23.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m12","p13",120L,32.0,24.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m12","p14",140L,35.0,35.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m21","p21",200L,20.0,18.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m21","p22",210L,30.0,19.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m22","p23",220L,35.0,20.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m22","p24",230L,38.0,22.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m31","p31",300L,3.0,3.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m31","p32",310L,5.0,4.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m32","p33",320L,10.0,4.5,contactParam));
        offerDatas.add(createOfferDataAsArray("m32","p34",330L,15.0,6.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m33","p35",340L,20.0,5.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m33","p36",350L,25.0,7.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m34","p37",360L,29.0,8.0,contactParam));
        offerDatas.add(createOfferDataAsArray("m34","p38",370L,35.0,9.0,contactParam));
    }

    protected ActivityData createActivityData(String name, String... parents) {
        ActivityData activityData = new ActivityData().setName(name).setTitle("title for: " + name);
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

    protected OfferData createOfferData(Long amount, double lon, double lat, ContactParam...contactParams) {
        return new OfferData().setAmount(amount).setPublicity(Publicity.PUBLIC).setProductType(ProductType.SERVICE).setContactData(
                new ContactData().setLocation(lon, lat).addContactParams(contactParams)
        );
    }

    protected Object[] createOfferDataAsArray(String merchantName, String productTitle, Long amount, double lon, double lat, ContactParam...contactParams) {
        List<Object> list = new ArrayList<>();
        list.add(merchantName);
        list.add(productTitle);
        list.add(createOfferData(amount, lon, lat, contactParams));
        return list.toArray(new Object[]{});
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

    protected Offer createOffer(Merchant merchant, Product product, OfferData offerData) {
        return merchantService.concludeOffer(merchant, product, offerData);
    }

    protected User createUser(UserData userData) {
        return userService.create(userData);
    }

    protected void createWeekDays() {
        for (WeekDay weekDay : weekDays) {
            neo4jTemplate.save(weekDay);
        }
    }


    protected void createUnderground() {
        /*
        try {
            geoDataImporter.importUnderground(new ClassPathResource("metro_stations_msk.csv"), ",", new GeoDataImporter.SplitMethod() {
                @Override
                public GeoData split(String[] ar) {
                    return new GeoData(ar[1],ar[10],Double.parseDouble(ar[3]),Double.parseDouble(ar[4]));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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

    protected void createOffers() {
        offerDatas.stream().forEach(offerDataAsArray -> {
                    String merchantName = (String) offerDataAsArray[0];
                    String productTitle = (String) offerDataAsArray[1];
                    OfferData offerData = (OfferData) offerDataAsArray[2];
                    offerData.addOfferTimes(WeekDay.Days.FRIDAY, new OfferTimeData().start(1000).finish(2000), new OfferTimeData().interval(1200, 1400))
                            .addOfferTimes(WeekDay.Days.MONDAY, new OfferTimeData().interval(1200, 1400));

                    Merchant merchant = merchantRepository.findByName(merchantName);
                    assertNotNull(merchant);
                    Product product = productRepository.findByCode(findProductDataByTitle(productTitle).getCode());
                    assertNotNull(product);

                    Offer offer = createOffer(merchant,product,offerData);
                    assertNotNull(offer);
                }
        );
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
