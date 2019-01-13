package com.jwxt.service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author me@nitmali.com
 * @date 2019/1/13 23:31
 */
public interface IGetClassScheduleService {

    List<Map<String, String>> getClassSchedule(HttpServletRequest request, String year,String yearNumber) throws IOException;

}
