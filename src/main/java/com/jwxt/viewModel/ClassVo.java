package com.jwxt.viewModel;

import lombok.Data;

/**
 * @author me@nitmali.com
 * @date 2019/4/17 23:37
 */
@Data
public class ClassVo {
    String name;
    String time;
    String teacher;
    String classroom;

    public ClassVo() {
    }

    public ClassVo(String name, String time, String teacher, String classroom) {
        this.name = name;
        this.time = time;
        this.teacher = teacher;
        this.classroom = classroom;
    }
}