package com.example.intelligentirrigation.entity;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

import lombok.Data;

@Data
@SmartTable(name="灌溉信息列表")
public class Record {
    @SmartColumn(id = 1,name = "温度")
    private String temperature;
    @SmartColumn(id = 2,name = "湿度")
    private String humidity;
    @SmartColumn(id = 3,name = "时间",autoMerge = true)
    private String currentTime;
    @SmartColumn(id = 4,name = "备注",autoMerge = true)
    private String remark;
}
