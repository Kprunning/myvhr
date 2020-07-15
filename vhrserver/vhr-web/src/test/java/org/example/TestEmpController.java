package org.example;



import org.javaboy.vhr.service.HttpClientService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestEmpController {


    public static void main(String[] args) throws IOException {
        test1();
    }

    public static void test1(){
        HttpClientService httpClient = new HttpClientService();
        String url = "localhost:8080/employee/basic/";
        Map<String,String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "10");
        params.put("employee", null);
        params.put("beginDateScope", null);
        String result = httpClient.doPost(url);
        System.out.println(result);
    }
}
