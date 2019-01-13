package com.jwxt.service;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IGetResultService {

     List<Map<String, String>> getResult(HttpServletRequest request, String key) throws IOException;

}
