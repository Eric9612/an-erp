package com.springmvc.service;

import com.springmvc.dao.MaterialDAO;
import com.springmvc.dao.SupplierDAO;
import com.springmvc.dao.SupplierMaterialDAO;
import com.springmvc.dto.*;
import com.springmvc.exception.BadRequestException;
import com.springmvc.pojo.MaterialQuery;
import com.springmvc.pojo.SupplierMaterialQuery;
import com.springmvc.pojo.SupplierQuery;
import com.springmvc.utils.ParamUtils;
import com.springmvc.utils.RequestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("SupplierService")
@Transactional
public class SupplierService extends BaseService {
    @Resource
    private SupplierDAO supplierDAO;

    @Resource
    private SupplierMaterialDAO suppliermaterialDAO;

    @Resource
    private MaterialDAO materialDAO;

    private static final String SUPPLIER_NAME_EXIST = "供应商已存在";
    private static final String SUPPLIERMATERIAL_NAME_EXIST = "商品已存在";
    private static final String MATERIAL_NO_EXIST = "BOM表中无当前商品";
    /**
     * 新增供应商信息
     *
     * 检查供应商是否存在
     * 供应商信息保存：Supplier
     * 添加日志信息  LogType.SUPPLIER, Operate.ADD
     * @param supplierName
     * @param contact
     * @param contactPhone
     * @param region
     * @param address
     * @return
     */
    public Supplier addSupplier( String supplierName, String contact,String contactPhone,String region,String address) {
        SupplierQuery supplierQuery = new SupplierQuery();
        supplierQuery.or().andSupplierNameEqualTo(supplierName);
        if (supplierDAO.countByExample(supplierQuery) > 0) {
            throw new BadRequestException(SUPPLIER_NAME_EXIST);
        }
        Admin loginAdmin = RequestUtils.getLoginAdminFromCache();
        Supplier supplier = new Supplier();
        supplier.setAddress(address);
        supplier.setContact(contact);
        supplier.setContactPhone(contactPhone);
        supplier.setRegion(region);
        supplier.setSupplierName(supplierName);
        supplier.setCreateAt(new Date());
        supplier.setCreateBy(loginAdmin.getAdminId());
        supplier.setUpdateAt(new Date());
        supplier.setUpdateBy(loginAdmin.getAdminId());
        supplierDAO.insertSelective(supplier);

        // 添加日志
        addLog(LogType.SUPPLIER, Operate.ADD, supplier.getSupplierId());
        return getSupplierById(supplier.getSupplierId());
    }



    /**
     * 查询供应商信息
     *
     * 将主表信息取出：Supplier
     * @param supplierId
     * @return
     */
    public Supplier getSupplierById(int supplierId) {
        Supplier supplier = supplierDAO.selectByPrimaryKey(supplierId);
        return supplier;
    }


    /**
     * 查询供应商信息（分页）
     *
     * 将主表信息取出：supplier（同时包含总记录数）
     * 搜索字段：公司姓名
     * @param current  当前页数
     * @param limit   每页限制
     * @param sortColumn   排序字段
     * @param sort           升降序
     * @param searchKey
     * @return
     */
    public PageMode<Supplier> pageSupplier(Integer current, Integer limit, String sortColumn,String sort,
                                     String searchKey) {
        SupplierQuery supplierQuery = new SupplierQuery();
        supplierQuery.setOffset((current-1) * limit);
        supplierQuery.setLimit(limit);
        if (!ParamUtils.isNull(sortColumn)) {
            supplierQuery.setOrderByClause(ParamUtils.camel2Underline(sortColumn) + " " + sort);
        }

        // TODO: 目前对searchKey支持比较机械
        // 搜索登录名
        SupplierQuery.Criteria criteria = supplierQuery.or();
        if (!ParamUtils.isNull(searchKey)) {
            criteria.andSupplierNameEqualTo(searchKey);
        }


        List<Supplier> result = supplierDAO.selectByExample(supplierQuery)  ;

        return new PageMode<Supplier>(result, supplierDAO.countByExample(supplierQuery));
    }


    /**
     * 删除供应商信息
     *
     * 删除主表信息：supplier
     * 删除关联的从表信息：admin_role、log
     * 添加日志信息：SUPPLIER, Operate.REMOVE
     *
     * @param idList
     */
    public void removeSupplier(List<Integer> idList) {
        // 删除 admin
        SupplierQuery supplierQuery = new SupplierQuery();
        supplierQuery.or().andSupplierIdIn(idList);
        supplierDAO.deleteByExample(supplierQuery);
        // 添加日志
        addLog(LogType.SUPPLIER, Operate.REMOVE, idList);
    }

    /**
     * 更新供应商信息
     *
     * 进行必要的检查：供应商名称是存在
     * 更新主表信息：supplier
     * 添加日志信息：LogType.ADMIN, Operate.UPDATE
     * @param supplierName
     * @param contact
     * @param contactPhone
     * @param region
     * @param address
     * @return
     */

    public Supplier updatesupplier( Integer supplierId,String supplierName, String contact,String contactPhone,String region,String address) {
         //supplierQuery.or().andSupplierNameEqualTo(supplierName);
         //if (supplierDAO.countByExample(supplierQuery) > 0) {
         //    throw new BadRequestException(SUPPLIER_NAME_EXIST);
         //}
         Admin loginAdmin = RequestUtils.getLoginAdminFromCache();
         Supplier supplier = new Supplier();
         supplier.setSupplierId(supplierId);
         supplier.setAddress(address);
         supplier.setContact(contact);
         supplier.setContactPhone(contactPhone);
         supplier.setRegion(region);
         supplier.setSupplierName(supplierName);
         supplier.setUpdateAt(new Date());
         supplier.setUpdateBy(loginAdmin.getAdminId());
         supplierDAO.updateByPrimaryKeySelective(supplier);
         // 添加日志
         addLog(LogType.SUPPLIER, Operate.UPDATE, supplier.getSupplierId());
         int temp=supplier.getSupplierId();
         Supplier st=getSupplierById(temp);
         return st;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 新增供应商物料信息,需要改成按materialId来查的格式
     *
     * 根据供应商提供的no.检查供应商物料是否存在BOM表，检查现有供应商物料表中是否存咋
     * 供应商信息保存：SupplierMaterial
     * 添加日志信息  LogType.SUPPLIER, Operate.ADD
     * @param supplierId
     * @param materialNo
     * @param price
     * @param remark
     * @return
     */
    public SupplierMaterial addSupplierMaterial(Integer supplierId,String materialNo, BigDecimal price, String remark ){
        //如果按materialId查下面这部分可以删掉
        // 原本是为了通过MaterialNO获取Material对象
        MaterialQuery materialQuery = new MaterialQuery();
        materialQuery.or().andMaterialNoEqualTo(materialNo);
        //no转id，获取material对象
        List<Material> materialList = materialDAO.selectByExample(materialQuery);
        if (materialList.size() == 0) {
            throw new BadRequestException(MATERIAL_NO_EXIST);
        }
        Material material = materialList.get(0);
        //删除到此为止
        SupplierMaterial suppliermaterial = new SupplierMaterial();
        suppliermaterial.setPrice(price);
        suppliermaterial.setRemark(remark);
        suppliermaterial.setSupplierId(supplierId);
        //下面这句话参数直接改成ID
        suppliermaterial.setMaterialId(material.getMaterialId());
        suppliermaterialDAO.insertSelective(suppliermaterial);

        suppliermaterial.setMaterialName(material.getMaterialName());
        suppliermaterial.setMaterialNo(materialNo);
        // 添加日志
        addLog(LogType.SUPPLIER_MATERIAL, Operate.ADD, suppliermaterial.getSupplierMaterialId());
        //return getSupplierMaterialById(suppliermaterial.getSupplierMaterialId());
        return suppliermaterial;
    }

    /**
     * 查询供应商物料信息查询 单个
     *
     * 将主表信息取出：SupplierMaterial
     * @param supplierMaterialId
     * @return
     */
    public  SupplierMaterial getSupplierMaterialById(int supplierMaterialId) {
        SupplierMaterial suppliermaterial = suppliermaterialDAO.selectByPrimaryKey(supplierMaterialId);
        MaterialQuery materialQuery = new MaterialQuery();
        materialQuery.or().andMaterialIdEqualTo(suppliermaterial.getMaterialId());
        //no转id，获取material对象
        List<Material> materialList = materialDAO.selectByExample(materialQuery);
        if (materialList.size() == 0) {
            throw new BadRequestException(MATERIAL_NO_EXIST);
        }
        Material material = materialList.get(0);
        suppliermaterial.setMaterialName(material.getMaterialName());
        suppliermaterial.setMaterialNo(material.getMaterialNo());
        return suppliermaterial;
    }

    /**
     * 查询供应商物料信息（分页）
     *
     * 将主表信息取出：suppliermaterial（同时包含总记录数）
     * 搜索字段：编号
     * @param suppierId  所属supperID
     * @param current  当前页数
     * @param limit   每页限制
     * @param sortColumn   排序字段
     * @param sort           升降序
     * @param searchKey
     * @return
     */
    public PageMode<SupplierMaterial> pageSupplierMaterial(Integer suppierId,Integer current, Integer limit, String sortColumn,String sort, String searchKey) {
        SupplierMaterialQuery suppliermaterialQuery = new SupplierMaterialQuery();
        suppliermaterialQuery.setOffset((current-1) * limit);
        suppliermaterialQuery.setLimit(limit);
        if (!ParamUtils.isNull(sortColumn)) {
            suppliermaterialQuery.setOrderByClause(ParamUtils.camel2Underline(sortColumn) + " " + sort);
        }

        // TODO: 目前对searchKey支持比较机械
        // 搜索登录名
        SupplierMaterialQuery.Criteria criteria = suppliermaterialQuery.or();
        criteria.andSupplierIdEqualTo(suppierId);

        //根据materialid进行查询，但实际输入的是编号
        if (!ParamUtils.isNull(searchKey)) {
            MaterialQuery materialQuery = new MaterialQuery();
            materialQuery.or().andMaterialNoEqualTo(searchKey);
            List<Material> materialList = materialDAO.selectByExample(materialQuery);
            if (materialList.size() == 0) {
                return null;
            }
            Material material = materialList.get(0);
            criteria.andMaterialIdEqualTo(material.getMaterialId());
        }


        List<SupplierMaterial> resultSM = suppliermaterialDAO.selectByExample(suppliermaterialQuery)  ;
        List<SupplierMaterial> resultms=new ArrayList<SupplierMaterial>();
        for(SupplierMaterial sm:resultSM){
            SupplierMaterial ms=getSupplierMaterialById(sm.getSupplierMaterialId());
            resultms.add(ms);
        }
        return new PageMode<SupplierMaterial>(resultms, suppliermaterialDAO.countByExample(suppliermaterialQuery));
    }

    /**
     * 删除供应商物料信息
     *
     * 删除主表信息：supplier
     * 删除关联的从表信息：admin_role、log
     * 添加日志信息：LogType.SUPPLIER_MATERIAL, Operate.REMOVE
     *
     * @param idList
     */
    public void removeSupplierMaterial(List<Integer> idList) {
        // 删除 admin
        SupplierMaterialQuery supplierMaterialQuery = new SupplierMaterialQuery();
        supplierMaterialQuery.or().andSupplierIdIn(idList);
        suppliermaterialDAO.deleteByExample(supplierMaterialQuery);
        // 添加日志
        addLog(LogType.SUPPLIER_MATERIAL, Operate.REMOVE, idList);
    }

    /**
     * 更新供应商物料信息
     *
     * 进行必要的检查：当前物料是存在
     * 更新主表信息：suppliermaterial
     * 添加日志信息：LogType.SUPPLIER_MATERIAL, Operate.UPDATE
     * @param supplierId
     * @param materialNo
     * @param price
     * @param remark
     * @return
     */

    public SupplierMaterial updatesupplierMaterial(Integer supplierMaterialId,Integer supplierId,String materialNo, BigDecimal price, String remark) {
        MaterialQuery materialQuery = new MaterialQuery();
        materialQuery.or().andMaterialNoEqualTo(materialNo);
        if (materialDAO.countByExample(materialQuery) == 0) {
            throw new BadRequestException(MATERIAL_NO_EXIST);
        }
        //no转id，获取material对象
        List<Material> materialList = materialDAO.selectByExample(materialQuery);
        if (materialList.size() == 0) {
            return null;
        }
        Material material = materialList.get(0);

        SupplierMaterial suppliermaterial = new SupplierMaterial();
        suppliermaterial.setSupplierMaterialId(supplierMaterialId);
        suppliermaterial.setPrice(price);
        suppliermaterial.setRemark(remark);
        suppliermaterial.setSupplierId(supplierId);
        suppliermaterial.setMaterialId(material.getMaterialId());

        suppliermaterial.setMaterialName(material.getMaterialName());
        suppliermaterial.setMaterialNo(materialNo);

        // 添加日志
        addLog(LogType.SUPPLIER_MATERIAL, Operate.UPDATE, suppliermaterial.getSupplierMaterialId());
        suppliermaterialDAO.updateByPrimaryKeySelective(suppliermaterial);
        return suppliermaterial;
    }

}
