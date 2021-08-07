package com.random.things.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class QueryUtility {

    Random random = new Random();
    Logger log = LoggerFactory.getLogger(QueryUtility.class);
    public String randomQueryGenerator(int maxChar){
        String characters = "abcdefghijklmnoprstuvyzqwx";
        int inputSize = random.nextInt(10)+1;
        String query = "";
        for (int i = 0; i< inputSize; i++){
            query = query.concat(String.valueOf(characters.charAt(random.nextInt(characters.length()))));
        }

        log.info("Search Query:" + query);

        return query;

    }
}
