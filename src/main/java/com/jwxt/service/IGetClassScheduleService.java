package com.jwxt.service;

import com.jwxt.viewModel.ClassScheduleVo;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/**
 * @author me@nitmali.com
 * @date 2019/1/13 23:31
 */
public interface IGetClassScheduleService {

    ClassScheduleVo getClassSchedule(HttpServletRequest request, String year, String yearNumber) throws IOException;

}
