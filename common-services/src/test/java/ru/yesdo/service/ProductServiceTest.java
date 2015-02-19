package ru.yesdo.service;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.GeneralCommonServiceDbTest;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.Product;
import ru.yesdo.model.data.ProductData;

/**
 * Created by lameroot on 19.02.15.
 */
public class ProductServiceTest extends GeneralCommonServiceDbTest {

    @Test
    @Transactional
    public void testCreateProducts() {
        createActivities();
        createMerchants();
        for (ProductData productData : productDatas) {
            Product product = createProduct(productData);
            assertNotNull(product);
        }

        assertEquals(productDatas.size(),productRepository.count());
        assertEquals(productDatas.size(),productGraphRepository.count());

        Product p21Found = productGraphRepository.findByCode(findProductDataByTitle("p21").getCode());
        assertNotNull(p21Found);
        Merchant merchant = p21Found.getMerchant();
        assertNotNull(merchant);
        neo4jTemplate.fetch(merchant);
        System.out.println(merchant);
        assertEquals("m21",merchant.getName());

        Product p21DbFound = productRepository.findByCode(findProductDataByTitle("p21").getCode());
        assertNotNull(p21DbFound);
        Merchant merchantDb = p21DbFound.getMerchant();
        assertNotNull(merchantDb);
        assertEquals("m21",merchantDb.getName());

    }
}
