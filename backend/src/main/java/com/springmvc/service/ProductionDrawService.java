package com.springmvc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springmvc.dao.DrawMaterialBillDAO;
import com.springmvc.dao.DrawMaterialBillMaterialDAO;
import com.springmvc.dto.DrawMaterialBill;
import com.springmvc.pojo.DrawMaterialBillQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("ProductionDrawService")
@Transactional
public class ProductionDrawService extends BaseService {
    @Resource
    private DrawMaterialBillDAO drawMaterialBillDAO;
    @Resource
    private DrawMaterialBillMaterialDAO drawMaterialBillMaterialDAO;
    public String deleteBILL(List<Integer> bill_id) {
        // 检查是否被log引用,直接粘贴过来的
        System.out.println("deleteBILL");
        //先删除主表
        int l = bill_id.size();
        System.out.print(l);
        for (int i = 0; i < l; i++) {
            DrawMaterialBill tmp = drawMaterialBillDAO.selectByPrimaryKey(bill_id.get(i));
            if (tmp.getBillState() == 2) {
                //已审核，无法更改
                return "failure";
            } else {
                drawMaterialBillDAO.deleteByPrimaryKey(bill_id.get(i));
                drawMaterialBillMaterialDAO.deleteByPrimaryKey(bill_id.get(i));
                return "success";

            }
        }
        return"success";
    }
    public List<DrawMaterialBill> getByMap(String search){
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonObject = JSONObject.parseObject(search);
        Iterator<String> keys = jsonObject.keySet().iterator();
        String key = null;
        Object value = null;
        while(keys.hasNext()){
            key = keys.next();
            value = jsonObject.get(key);
            map.put(key, value);
        }
        DrawMaterialBillQuery drawMaterialBillQuery=new DrawMaterialBillQuery();
        DrawMaterialBillQuery.Criteria searc=drawMaterialBillQuery.createCriteria();
        searc.andDrawReasonEqualTo(1);
        String toprincipal=map.get("toPrincipal").toString();
        if(toprincipal.length()!=0)
        {
            searc.andToPrincipalEqualTo(Integer.parseInt(toprincipal));
            System.out.println("principal");
        }
        String billTime=map.get("billTime").toString();
        JSONArray jsonArray = JSONArray.parseArray(billTime);
        String datestart=jsonArray.getString(0);
        String dateend=jsonArray.getString(1);
        if(dateend!=null&&datestart!=null){
            System.out.println("date");
            System.out.println("*************************");
            System.out.println(Long.parseLong(datestart));
            java.sql.Date billstart=new java.sql.Date(Long.parseLong(datestart));
            System.out.println(billstart);
            java.sql.Date billend=new java.sql.Date(Long.parseLong(dateend));
            searc.andCreateAtBetween(billstart,billend);
            System.out.println(billstart);
            System.out.println(billend);
            System.out.println("*************************");
        }
        String billno=map.get("billNo").toString();
        if(billno.length()!=0){
            searc.andBillNoEqualTo(billno);
            System.out.println("billno");
        }
        String billstate=map.get("billState").toString();
        if(billstate.trim().length()!=0){
            int state=Integer.parseInt(billstate);
            searc.andBillStateEqualTo(state);
            System.out.println("billstate");
        }
        List<DrawMaterialBill>all=this.drawMaterialBillDAO.selectByExample(drawMaterialBillQuery);
        return all;
    }
    public List<DrawMaterialBill> getAll(){
        DrawMaterialBillQuery drawMaterialBillQuery=new DrawMaterialBillQuery();
        drawMaterialBillQuery.or().andBillStateEqualTo(1);
        return this.drawMaterialBillDAO.selectByExample(drawMaterialBillQuery);
    }
}
