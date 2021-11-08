package com.dragon.activiti.demo.controller;

import com.dragon.activiti.demo.dto.ModelForm;
import com.dragon.activiti.demo.service.ModelService;
import com.dragon.activiti.demo.util.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/model")
@AllArgsConstructor
public class ModelController {

    private final ModelService modelService;

    //新增模型
    @PostMapping(value = "/createModel")
    public R<Boolean> createModel(@RequestBody @Valid ModelForm form) {
        modelService.create(form.getName(), form.getKey(), form.getDesc(), form.getCategory());
        return new R<>(Boolean.TRUE);
    }

    @GetMapping(value = "/getModelPage")
    public R getModelPage(@RequestParam Map<String, Object> params) {
        return new R<>(modelService.getModelPage(params));
    }

    @DeleteMapping("/removeModelById/{id}")
    public R removeModelById(@PathVariable("id") String id) {
        return new R<>(modelService.removeModelById(id));
    }

    //部署模型
    @PostMapping("/deploy/{id}")
    public R deploy(@PathVariable("id") String id) {
        return new R<>(modelService.deploy(id));
    }

}
