import { http } from '../libs/http';

var addmaterial = (material_id,quantity,remark)=> http.post('production-return/addmaterial', {material_id:material_id,quantity:quantity,remark:remark});
var allBills = () => http.post('production-return/allBills',);
var getBill = (id) => http.post('production-return/billdetail', {bill_id:id});
var addBill=(billno,remark,reason) => http.post('production-return/addbill',{billno:billno,remark:remark,reason:reason})
var deletebills= (bill_id) => http.post('production-return/deletebills', {bill_id:bill_id});
var getAllBillMaterials=(id) => http.post('production-return/allMaterial', {bill_id:id});
var deleteitem=(id) => http.post('production-return/deletematerial', {material_id:id});
var searchBill = (search) => http.post('production-return/searchbill', {search:search});
var changematerials= (bill_id,material,quantity,remark)=> http.post('production-return/changematerials', {bill_id:bill_id,material:material,quantity:quantity,remark:remark});
var deletematerials= (bill_id,material)=>http.post('production-return/deletematerials', {bill_id:bill_id,material:material});
var changestatus=(bill_id,status)=>http.post('production-return/cahngestate', {bill_id:bill_id,status:status});

export default { addmaterial, allBills,getBill,addBill,deletebills,getAllBillMaterials,deleteitem,searchBill,changematerials,deletematerials,changestatus};
