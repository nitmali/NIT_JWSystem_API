package com.jwxt.viewModel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author me@nitmali.com
 * @date 2019/4/17 23:28
 */
@Data
public class ClassScheduleVo {
    String name;
    String className;
    String userId;

    List<ClassVo> monday = new ArrayList<>();
    List<ClassVo> tuesday = new ArrayList<>();
    List<ClassVo> wednesday = new ArrayList<>();
    List<ClassVo> thursday = new ArrayList<>();
    List<ClassVo> friday = new ArrayList<>();
    List<ClassVo> saturday = new ArrayList<>();
    List<ClassVo> sunday = new ArrayList<>();
}
