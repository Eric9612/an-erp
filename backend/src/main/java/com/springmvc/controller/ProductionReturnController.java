package com.springmvc.controller;

import com.springmvc.dto.DrawMaterialBill;
import com.springmvc.dto.ReturnMaterialBill;
import com.springmvc.service.ProductionDrawService;
import com.springmvc.service.ProductionReturnService;
import com.springmvc.utils.ParamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/production-return")
public class ProductionReturnController {

    @Resource
    ProductionReturnService productionReturnService;

    @RequestMapping(value = "/deletebills",method =RequestMethod.POST)
    @ResponseBody
    public String deletebills(String bill_id){
        System.out.println("_______deletebill___________");
        System.out.print(bill_id);
        this.productionReturnService.deleteBILL(ParamUtils.toIntList(bill_id));
        return "success";
    }
    @RequestMapping(value = "/searchbill",method =RequestMethod.POST)
    @ResponseBody
    public List<ReturnMaterialBill> searchBill(String search){
        System.out.println(search);
        return this.productionReturnService.getByMap(search);
    }

}
