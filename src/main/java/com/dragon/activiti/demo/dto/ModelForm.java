package com.dragon.activiti.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ModelForm {

    private String category;
    @NotBlank
    private String name;
    @NotBlank
    private String key;
    @NotBlank
    private String desc;

}
