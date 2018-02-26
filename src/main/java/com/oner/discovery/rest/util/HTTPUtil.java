package com.oner.discovery.rest.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public final class HTTPUtil {

    private HTTPUtil(){
    };

    public static List<Map<String, String>> parseResponse(InputStream is) throws IOException {
        Type type = new TypeToken<List<LinkedHashMap<String, String>>>(){}.getType();
        return new Gson().fromJson(readInputStream(is), type);
    }

    public static String readInputStream(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        return response.toString();
    }

    public static String escapeParams(final String params) throws UnsupportedEncodingException {
        String[] attributes = params.split("&");
        StringJoiner joiner = new StringJoiner("&");
        for (int i = 0; i < attributes.length; i++) {
            String[] kv = attributes[i].split("=", 2);
            StringBuilder sb = new StringBuilder();
            sb.append(URLEncoder.encode(kv[0], "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(kv[1], "UTF-8"));
            joiner.add(sb.toString());
        }
        return joiner.toString();
    }
}
