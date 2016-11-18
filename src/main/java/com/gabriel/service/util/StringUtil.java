package com.gabriel.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by liuyufei on 2/11/16.
 */
public class StringUtil {


    //for finding empty string using term/match
    //http://stackoverflow.com/questions/25561981/find-documents-with-empty-string-value-on-elasticsearch/25562877#25562877
    //for the sake of elasticsearch deals with "" as the way of dealing null
    public static String getNullIfEmptyStr(String input) {
        return "".equals(input) ? null : input;
    }

    public static Map<String, String> splitQuery(String url) {

        return Arrays.asList(url.split("&")).stream()
            .map(s -> Arrays.copyOf(s.split("="), 2))
            .collect(Collectors.toMap(o->decode(o[0]),o->decode(o[1])));
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
        }
    }
}
