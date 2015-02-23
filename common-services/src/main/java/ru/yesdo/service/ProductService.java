package ru.yesdo.service;

import org.neo4j.cypherdsl.grammar.Execute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.db.repository.ProductRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.graph.repository.ProductGraphRepository;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.Product;
import ru.yesdo.model.SearchProductCriteria;
import ru.yesdo.model.data.ProductData;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class ProductService {

    @Resource
    private ProductRepository productRepository;
    @Resource
    private ProductGraphRepository productGraphRepository;
    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private MerchantGraphRepository merchantGraphRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product create(ProductData productData) {
        Product product = new Product();
        product.setTitle(productData.getTitle());
        product.setCreatedAt(productData.getCreatedAt());
        product.setCode(productData.getCode());
        Merchant merchant = merchantRepository.findByName(productData.getMerchant().getName());
        if ( null == merchant ) throw new IllegalArgumentException("Unable to find merchant with name: " + productData.getMerchant().getName());
        if ( productData.isPartial() ) {
            if ( null == merchantGraphRepository.findByName(productData.getMerchant().getName()) )
                throw new IllegalArgumentException("Unable to find merchant with name: " + productData.getMerchant().getName() + " in graph database");

        }
        product.setMerchant(merchant);

        productRepository.save(product);
        if ( productData.isPartial() ) {
            productGraphRepository.save(product);
        }

        return product;
    }

    public List<Product> findByCriteria(SearchProductCriteria criteria) {
        //Execute query =

        return null;
    }

//    Execute query =
//            start(lookup(identifier("activity"),identifier(Activity.INDEX_FOR_NAME),identifier("name"),param("activityRootName") )).
//                    match(path("p", node("activity")
//                            .out("ACTIVITY").hops(0, 1).node("ch"))).returns(identifier("ch"))
//            ;
}
