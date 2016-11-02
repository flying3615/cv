package com.gabriel.service.util;

/**
 * Created by liuyufei on 2/11/16.
 */
public class ElasticSearchUtil {


    //for finding empty string using term/match
    //http://stackoverflow.com/questions/25561981/find-documents-with-empty-string-value-on-elasticsearch/25562877#25562877
    //for the sake of elasticsearch deals with "" as the way of dealing null
    public static String getNullIfEmptyStr(String input){
        if("".equals(input)){
            return null;
        }else{
            return input;
        }
    }
}
