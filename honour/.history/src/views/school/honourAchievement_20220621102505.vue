<template>
  <div class="app-container">
    <span style="margin-left:120px;margin-right: 20px;width: 150px;display: inline-block;">大类：</span>
    <el-select v-model="big" placeholder="请选择" @change="getSuppliesType(big)" style="width: 19%;">
      <el-option
        v-for="item in bigTypes"
        :key="item.bigCategoryName"
        :label="item.bigCategoryName"
        :value="item.id">
      </el-option>
    </el-select>
    <span style="margin-left:120px;margin-right: 20px; width: 150px;display: inline-block;">中类：</span>
    <el-select v-model="middle" placeholder="请选择" @change="getSuppliesType(middle)" style="width: 19%;">
      <el-option
        v-for="item in middleTypes"
        :key="item.middleCategoryName"
        :label="item.middleCategoryName"
        :value="item.id">
      </el-option>
    </el-select>
    <br>
    <br>
    <br>
    <span style="margin-left:120px;margin-right: 20px;width: 150px; margin-top:20px; display: inline-block;">小类：</span>
    <el-select v-model="small" placeholder="请选择" style="width: 19%;">
      <el-option
        v-for="item in smallTypes"
        :key="item.smallCategoryName"
        :label="item.smallCategoryName"
        :value="item.id">
      </el-option>
    </el-select>
    <br>
    <br>
    <br>
    <el-button type="primary" round style="margin-left:280px" @click.native.prevent="commit">添加</el-button>
    <el-button type="primary" round style="margin-left:100px" @click.native.prevent="cancel">取消</el-button>
  </div>
</template>
 
<script>
    export default {
        filters: {
            parseTime(timestamp) {
                return parseTime(timestamp, null)
            }
        },
        data() {
            return {
                big: '',
                bigTypes: null,
                middle: '',
                middleTypes: null,
                small: '',
                smallTypes: null,
                dataList: [
                    {"id":1,"categoryType":"BIG","bigCategoryName":"1.现场管理与保障","middleCategoryName":null,"smallCategoryName":null,"parentId":0,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":"2021-07-04T13:34:31.000+0000","isDeleted":false},
                    {"id":27,"categoryType":"BIG","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":null,"smallCategoryName":null,"parentId":0,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":2,"categoryType":"MIDDLE","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":null,"parentId":1,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":10,"categoryType":"MIDDLE","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.2现场安全","smallCategoryName":null,"parentId":1,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":3,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.1气象监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":4,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.2地震监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":5,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.3地质灾害监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":6,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.4水文监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":7,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.5环境监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":8,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.6疫病监测","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":9,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.1现场监测","smallCategoryName":"1.1.7观察测量","parentId":2,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":11,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.2现场安全","smallCategoryName":"1.2.1现场照明","parentId":10,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":12,"categoryType":"SMALL","bigCategoryName":"1.现场管理与保障","middleCategoryName":"1.2现场安全","smallCategoryName":"1.2.2现场警戒","parentId":10,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":28,"categoryType":"MIDDLE","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":null,"parentId":27,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":34,"categoryType":"MIDDLE","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":null,"parentId":27,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":"2021-07-04T13:03:23.000+0000","isDeleted":false},
                    {"id":29,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":"2.1.1卫生防疫","parentId":28,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":30,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":"2.1.2消防防护","parentId":28,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":31,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":"2.1.3化学与放射","parentId":28,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":32,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":"2.1.4防高空坠落","parentId":28,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":33,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.1人员安全防护","smallCategoryName":"2.1.5通用防护","parentId":28,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":35,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":"2.2.1生命搜索","parentId":34,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":36,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":"2.2.2攀岩营救","parentId":34,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":37,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":"2.2.3破拆起重","parentId":34,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":38,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":"2.2.4水下营救","parentId":34,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false},
                    {"id":39,"categoryType":"SMALL","bigCategoryName":"2.生命救援与生活救助","middleCategoryName":"2.2生命搜救与营救","smallCategoryName":"2.2.5通用工具","parentId":34,"createTime":"2021-07-04T11:13:36.000+0000","createUserName":null,"updateTime":null,"isDeleted":false}
                    ]
            }
        },
        created() {
            this.getSuppliesType(0)
        },
        methods: {
            getSuppliesType(id) {
                const queryData = {
                    parentId: id
                }
                //此处为js模拟，真实数据的获取还需要后台接口的支持
                getSuppliersType(JSON.stringify(queryData)).then(response => {
                    console.log(response)
                    console.log(response.data[0].categoryType)
                    //存放此次查询结果
                    let tmpList = []
                    this.dataList.forEach((item, index) => {
                        if(item.parentId === id){
                            tmpList.push(item)
                        }
                    })
                    if (tmpList[0].categoryType === 'BIG') {
                        this.bigTypes = tmpList
                    } else if (response.data[0].categoryType === 'MIDDLE') {
                        this.middleTypes = tmpList
                    } else {
                        this.smallTypes = tmpList
                    }
                }).catch(function (error) {
                    console.log(error)
                })
            },
            commit() {
                console.log("点击了提交按钮")
            },
            cancel() {
                this.$router.go(-1)
            }
        }
    }
</script>