import { http } from '../libs/http';

var addmaterial = (material_id,quantity,remark)=> http.post('production-draw/addmaterial', {material_id:material_id,quantity:quantity,remark:remark});
var allBills = () => http.post('production-draw/allBills',);
var getBill = (id) => http.post('production-draw/billdetail', {bill_id:id});
var addBill=(billno,remark,reason) => http.post('production-draw/addbill',{billno:billno,remark:remark,reason:reason})
var deletebills= (bill_id) => http.post('production-draw/deletebills', {bill_id:bill_id});
var getAllBillMaterials=(id) => http.post('production-draw/allMaterial', {bill_id:id});
var deleteitem=(id) => http.post('production-draw/deletematerial', {material_id:id});
var searchBill = (search) => http.post('production-draw/searchbill', {search:search});
var changematerials= (bill_id,material,quantity,remark)=> http.post('production-draw/changematerials', {bill_id:bill_id,material:material,quantity:quantity,remark:remark});
var deletematerials= (bill_id,material)=>http.post('production-draw/deletematerials', {bill_id:bill_id,material:material});
var changestatus=(bill_id,status)=>http.post('production-draw/cahngestate', {bill_id:bill_id,status:status});

export default { addmaterial, allBills,getBill,addBill,deletebills,getAllBillMaterials,deleteitem,searchBill,changematerials,deletematerials,changestatus};
