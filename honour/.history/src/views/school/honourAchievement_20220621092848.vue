<template>
  <div>
    <a-select :default-value="provinceData[0]" style="width: 120px" @change="handleProvinceChange">
      <a-select-option v-for="province in provinceData" :key="province">
        {{ province }}
      </a-select-option>
    </a-select>
    <a-select v-model="secondCity" style="width: 120px">
      <a-select-option v-for="city in cities" :key="city">
        {{ city }}
      </a-select-option>
    </a-select>
  </div>
</template>
<script>
const provinceData = ['Zhejiang', 'Jiangsu'];
const cityData = {
  Zhejiang: ['Hangzhou', 'Ningbo', 'Wenzhou'],
  Jiangsu: ['Nanjing', 'Suzhou', 'Zhenjiang'],
};
export default {
  data() {
    return {
      provinceData,
      cityData,
      cities: cityData[provinceData[0]],
      secondCity: cityData[provinceData[0]][0],
       url: {
          list: '/app/permission/list',
          delete: '/app/permission/delete',
          deleteBatch: '/app/permission/deleteBatch'
        }
    };
  },
  methods: {
    handleProvinceChange(value) {
      this.cities = cityData[value];
      this.secondCity = cityData[value][0];
    },
  },
};
</script>
