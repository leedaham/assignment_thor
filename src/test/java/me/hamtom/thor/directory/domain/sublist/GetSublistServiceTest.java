package me.hamtom.thor.directory.domain.sublist;

import me.hamtom.thor.directory.domain.common.DirectoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
class GetSublistServiceTest {

    @Autowired
    DirectoryService directoryService;
    @Test
    void test(){
        //given
        List<String> list = new ArrayList<>();
        list.add("/abc/ads");
        list.add("/abc/ssd/asd");
        list.add("/abc/ads/asd");
        list.add("/abc/seq/asd");
        list.add("/abc/aaa/asd");
        list.add("/abc/aaa/asd/ase");


        Map<Integer, List<String>> map = new HashMap<>();
        for (String childDirectory : list) {
            int layerNum = directoryService.getLayerNum(childDirectory);
            map.computeIfAbsent(layerNum, v -> new ArrayList<>()).add(childDirectory);
        }
        System.out.println("collect = " + map);
        //when

        //then
    }


}