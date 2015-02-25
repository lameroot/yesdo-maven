package ru.yesdo.service;

import org.junit.Test;
import org.neo4j.cypher.internal.compiler.v1_9.commands.ShortestPath;
import org.springframework.core.io.ClassPathResource;
import ru.yesdo.GeneralCommonServiceTest;
import ru.yesdo.model.data.GeoData;

import java.io.IOException;

/**
 * Created by lameroot on 25.02.15.
 */
public class GeoDataImporterTest extends GeneralCommonServiceTest {

    @Test
    public void testImportUnderground() {
        createUnderground();
    }

    @Test
    public void findShortestPath() {
        //99,M-PEROVO, 55.751090, 37.788540,37463.9765400, 49, 1, 6, 0, 12632256,Метро Перово, 0, 0, 0, 502, 7, 0,17,0,10.0,2,,,
        //86,M-NOVOGIREEVO, 55.751110, 37.815640,37463.9765200, 49, 1, 6, 0, 12632256,Метро Новогиреево, 0, 0, 0, 531, 7, 0,17,0,10.0,2,,,
        //235,M-NOVOKOSINO, 55.740539, 37.856347,38372.5148108, 38, 1, 6, 0, 13158342,Метро Новокосино (стр.), 0, 0, 0, 512, 7, 0,17,0,10.0,2,,,
    }
}
