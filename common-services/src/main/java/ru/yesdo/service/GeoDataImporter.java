package ru.yesdo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.graph.repository.ContactGraphRepository;
import ru.yesdo.model.Contact;
import ru.yesdo.model.ContactParam;
import ru.yesdo.model.data.GeoData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by lameroot on 25.02.15.
 */
@Service
public class GeoDataImporter {

    @javax.annotation.Resource
    private ContactGraphRepository contactGraphRepository;
    @javax.annotation.Resource
    private Neo4jTemplate neo4jTemplate;

    public void importUnderground(Resource resource, String splitter, SplitMethod splitMethod) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(resource.getFile()));
            String line = null;
            while ( null != (line = reader.readLine()) ) {
                String[] ar = line.split(splitter);
                GeoData geoData = splitMethod.split(ar);
                importData(geoData);
            }
        } finally {
            if ( null != reader ) {
                reader.close();
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void importData(GeoData...geoDatas) throws IOException {
        for (GeoData geoData : geoDatas) {
            Contact contact = new Contact().setLocation(geoData.getLon(),geoData.getLat())
                    .addContactParam(new ContactParam("name",geoData.getName(), ContactParam.Type.ADDRESS,false))
                    .addContactParam(new ContactParam("title",geoData.getTitle(), ContactParam.Type.ADDRESS,false));
            contactGraphRepository.save(contact);
        }
    }

    public static interface SplitMethod {
        GeoData split(String[] ar);
    }

    public static void main(String[] args) throws IOException {
        GeoDataImporter importer = new GeoDataImporter();
        importer.importUnderground(new ClassPathResource("metro_stations_msk.csv"), ",", new SplitMethod() {
            @Override
            public GeoData split(String[] ar) {
                return new GeoData(ar[1],ar[10],Double.parseDouble(ar[3]),Double.parseDouble(ar[4]));
            }
        });
    }
}
