<template>
 <div class="box-select-first">
 <select v-model.lazy="resCity">
    <option v-for="sc in scLists" v-bind:value="sc.areacode">{{sc.cityname}}</option>
 </select>
 </div>
 <div class="box-select-second">
 <select v-model="resArea">
 <option v-for="sa in saLists" v-bind:value="sa.id">{{sa.areaname}}</option>
 </select>
 </div>
</template>
<script>
 export default {
 data: function () {
 return {
 resCity: null,
 resArea: null
 }
 },
 created: function() {
 let vm = this;
 vm.getSelectLists(); //获取城市下拉列表
 },
 watch: {
 resCity: 'getSecondSelectLists' //获取城市对应辖区的下拉列表
 },
 methods: {
 getSelectLists: function() {},
 getSecondSelectLists: function(){}
 }
 }
</script>
