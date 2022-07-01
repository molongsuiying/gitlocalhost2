<template>
 <div class="box-select-first">
 <select v-model.lazy="resCity">
    <option v-for="sc in dataSource" v-bind:value="sc.id">{{sc.name}}</option>
 </select>
 </div>
 <!-- <div class="box-select-second">
 <select v-model="resArea">
    <option v-for="sa in list2" v-bind:value="sa.id">{{sa.name}}</option>
 </select>
 </div> -->
</template>
<script>
import api from '../../api';
 export default {
 data: function () {
 return {
 resCity: null,
 resArea: null,
 url: {
          list: "/app/permission/getSystemMenuList",
          list2:"/app/permission/getSystemSubmenu",
          delete: "/honour/auth/deleteByUserId",
          deleteBatch: "/sys/position/deleteBatch",
          exportXlsUrl: "/sys/position/exportXls",
          importExcelUrl: "/sys/position/importExcel",
          getMyCollegeNames: "/college/queryCollegeNames"
        },
 }
 },
 created: function() {
 let vm = this;
 vm.getAppPermissionListParent(); //获取城市下拉列表
 },
 watch: {
 resCity: 'list2' //获取城市对应辖区的下拉列表
 },
 methods: {
    getAppPermissionListParent: function() {
         getAction(this.url.list).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
            console.log(this.dataSource)
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
    },
    list2: function(){}
 }
 }
</script>
