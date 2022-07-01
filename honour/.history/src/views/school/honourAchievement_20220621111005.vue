<template>
 <div class="box-select-first">
 <select v-model.lazy="list1">
    <option v-for="sc in list1" v-bind:value="sc.id">{{sc.name}}</option>
 </select>
 <a-cascader :options="options" placeholder="Please select" @change="onChange" />
 </div>
 <!-- <div class="box-select-second">
 <select v-model="resArea">
    <option v-for="sa in list2" v-bind:value="sa.id">{{sa.name}}</option>
 </select>
 </div> -->
</template>
<script>
import {
    getAction,
    deleteAction
  } from "@/api/manage"
 export default {
 data: function () {
 return {
 list1: [],
 resArea: [],
  options: [
        {
          value: 'zhejiang',
          label: 'Zhejiang',
          children: [
            {
              value: 'hangzhou',
              label: 'Hangzhou',
              children: [
                {
                  value: 'xihu',
                  label: 'West Lake',
                },
              ],
            },
          ],
        },
        {
          value: 'jiangsu',
          label: 'Jiangsu',
          children: [
            {
              value: 'nanjing',
              label: 'Nanjing',
              children: [
                {
                  value: 'zhonghuamen',
                  label: 'Zhong Hua Men',
                },
              ],
            },
          ],
        },
      ],
    
//  url: {
//           list: "/app/permission/getSystemMenuList",
//           list2:"/app/permission/getSystemSubmenu",
//           delete: "/honour/auth/deleteByUserId",
//           deleteBatch: "/sys/position/deleteBatch",
//           exportXlsUrl: "/sys/position/exportXls",
//           importExcelUrl: "/sys/position/importExcel",
//           getMyCollegeNames: "/college/queryCollegeNames"
//         },
 }
 },
//  created: function() {
//  let vm = this;
//  vm.getAppPermissionListParent(); //获取城市下拉列表
//  },
 watch: {
 resCity: 'list2' //获取城市对应辖区的下拉列表
 },
 methods: {
    onChange(value) {
      console.log(value);
    },
      getlist(){
 fetch("http://localhost:8084/jeecg-boot/app/permission/getSystemMenuList")
        .then((response) => {
            return response.json();
            })
        .then((res) => {
        console.log(res.result,111);
        this.list1 = [...res.result]
        })
        .catch((err) => {
        console.log(err);
        })
      }   
 },
 created(){
    this.getlist()
 }
 }
</script>
