package com.springmvc.controller;

import com.springmvc.dto.DrawMaterialBill;
import com.springmvc.service.ProductionDrawService;
import com.springmvc.utils.ParamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/production-draw")
public class ProductionDrawController {

    @Resource
    ProductionDrawService productionDrawService;
    @RequestMapping(value = "/deletebills",method =RequestMethod.POST)
    @ResponseBody
    public String deletebills(String bill_id){
        System.out.println("_______deletebill___________");
        System.out.print(bill_id);
        this.productionDrawService.deleteBILL(ParamUtils.toIntList(bill_id));
        return "success";
    }
    @RequestMapping(value = "/searchbill",method =RequestMethod.POST)
    @ResponseBody
    public List<DrawMaterialBill> searchBill(String search){
        System.out.println(search);
        return this.productionDrawService.getByMap(search);
    }
    @RequestMapping(value = "/getall",method =RequestMethod.POST)
    @ResponseBody
    public List<DrawMaterialBill> getAll(){
        return this.productionDrawService.getAll();
    }
}
