package ru.yesdo.graph.service;

import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import ru.yesdo.graph.repository.ActivityGraphRepository;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.graph.repository.ProductGraphRepository;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.MerchantProductRelationship;
import ru.yesdo.model.Offer;
import ru.yesdo.model.Product;
import ru.yesdo.model.data.MerchantData;
import ru.yesdo.model.data.OfferData;

import javax.annotation.Resource;

/**
 * Created by lameroot on 25.01.15.
 */
@Service
public class MerchantGraphService {

    @Resource
    private MerchantGraphRepository merchantGraphRepository;
    @Resource
    private ActivityGraphRepository activityGraphRepository;
    @Resource
    private ProductGraphRepository productGraphRepository;

    @Resource
    private Neo4jTemplate neo4jTemplate;


    public Merchant create(MerchantData merchantData) {
        Merchant merchant = new Merchant(merchantData.getTitle());//test
        merchant.setTitle(merchantData.getTitle());
        merchant.setName(merchantData.getName());
        merchant.getActivities().addAll(merchantData.getActivities());
        merchantGraphRepository.save(merchant);
        return merchant;
    }



    public void joinToMerchant(Merchant merchant,Product...products) {
//        for (Product product : products) {
//            MerchantProductRelationship merchantProductRelationship = merchant.addMerchantProduct(product,product.getRelationship().getAmount());
//        }
//        merchantGraphRepository.save(merchant);
    }

    public MerchantProductRelationship joinToMerchant(Merchant merchant,Product product) {
//        MerchantProductRelationship merchantProductRelationship = merchant.addMerchantProduct(product, product.getRelationship().getAmount());//amount for test
//        merchantGraphRepository.save(merchant);
//        return merchantProductRelationship;
        return null;
    }

}
