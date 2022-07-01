<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="名称">
              <a-input placeholder="请输入搜索关键词" v-model="queryParam.mc"></a-input>
            </a-form-item>
          </a-col>
          <!-- <a-col :md="6" :sm="8">
            <a-form-item label="是否最新">
              <a-switch checkedChildren="是" unCheckedChildren="否" defaultChecked v-model="queryParam.zx" />
            </a-form-item>
          </a-col> -->

          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="12">
              <a-button type="primary" style="left: 10px" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px;left: 10px">重置</a-button>
            </a-col>
          </span>
          <span style="float: right;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="12" :sm="12">
              <a-button type="primary" style="left: 10px" @click="build()" icon="search">创建模型</a-button>
            </a-col>
          </span>

        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <a-table :scroll="{x:1570,y:500}" bordered ref="table" size="middle" rowKey="id" :dataSource="dataSource"
      :pagination="ipagination" :loading="loading" @change="handleTableChange">

      <a-table-column title="#" :width="50" fixed='left' key="a">
        <template slot-scope="t,r,i">
          <span> {{i+1}} </span>
        </template>
      </a-table-column>
      <a-table-column title="名称" dataIndex="name" :width="200" align="center" fixed='left'>
        <template slot-scope="t,r,i">
          <span> {{t}} </span>
        </template>
      </a-table-column>
      <a-table-column title="版本" dataIndex="version" key="asa" :width="100" align="center" :sorter="(a, b) => a.version - b.version">
        <template slot-scope="t,r,i">
          <span> v.{{t}} </span>
        </template>
      </a-table-column>
      <a-table-column title="所属分类" dataIndex="categoryId" :width="200" align="center" key="categoryId_" :filters="lcTypeF"
        @filter="filter_categoryId">
        <template slot-scope="t,r,i">
          <span> {{filterDictText(dictOptions, t)}} </span>
        </template>
      </a-table-column>
      <a-table-column title="表单路由" dataIndex="routeName" :width="150" align="center">
        <template slot-scope="t,r,i">
          <span @click="viewForm(t)" style="color: blue;cursor: pointer;"> {{getFormComponent(t).text}} </span>
        </template>
      </a-table-column>
      <a-table-column title="状态" dataIndex="status" :width="100" align="center">
        <template slot-scope="t,r,i">
          <span v-if="t==1" style="color: #00A0E9;"> 已启用 </span>
          <span v-if="t!=1" style="color: #999;"> 未启用 </span>
        </template>
      </a-table-column>
      <a-table-column title="备注说明" dataIndex="description" :width="200" align="center">
        <template slot-scope="t,r,i">
          <j-ellipsis :value="t" :length="10" />
        </template>
      </a-table-column>
      <a-table-column title="部署时间" dataIndex="createTime" :width="190" align="center">
        <template slot-scope="t,r,i">
          <span> {{t}} </span>
        </template>
      </a-table-column>
      <a-table-column title="更新时间" dataIndex="updateTime" :width="190" align="center">
        <template slot-scope="t,r,i">
          <span> {{t}} </span>
        </template>
      </a-table-column>
      <a-table-column title="操作" dataIndex="" align="center" :width="250" fixed='right'>
        <template slot-scope="t,r,i">
          <a href="javascript:void(0);" v-if="r.status!=1" @click="editStatus(1,r)" style="color: rgb(144,96,255);">启用</a>
          <a href="javascript:void(0);" v-if="r.status==1" @click="editStatus(0,r)" style="color: #cb892d">禁用</a>
          <a-divider type="vertical" />
          <a href="javascript:void(0);" @click="edit(r)">编辑</a>
          <a-divider type="vertical" />
          <a href="javascript:void(0);" style="color: red;" @click="remove(r)">删除</a>


        </template>
      </a-table-column>
    </a-table>
    <!-- table区域-end -->
    <!--编辑-->
    <a-modal width="900px" :confirmLoading="confirmLoading" :title="editObj.title" :visible="editObj.visible" @ok="editObjOk"
      @cancel="editObj.visible = false">
      <a-form :form="editForm" v-if="editObj.visible">

        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="模型名称">
          <a-input v-decorator="[ 'name', {initialValue:editObj.name, rules: []}]" placeholder="模型名称" />
        </a-form-item>

        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="投诉分类">
          <component :is="LcDict" :trigger-change="true" v-decorator="[ 'categoryId', {initialValue:editObj.categoryId, rules: [{ required: true, message: '不能为空' }] },]"
            placeholder="请选择投诉分类" dictCode="complain_type"></component>
        </a-form-item>

        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="关联表单">
          <a-select @change="change_routeName" placeholder="请选择关联表单" :trigger-change="true" v-decorator="[ 'routeName', {initialValue:editObj.routeName, rules: [{ required: true, message: '不能为空' }] },]">
            <a-select-option value="">请选择</a-select-option>
            <a-select-option v-for="(item, i) in allFormComponent" :key="item.routeName" :value="item.routeName">
              <span style="display: inline-block;width: 100%" :title=" item.text">
                {{ item.text}}
              </span>
            </a-select-option>
          </a-select>
          <a href="javascrip:void(0)" @click="viewForm()">预览表单</a>
        </a-form-item>
        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="角色授权">
          <j-select-role placeholder="不选择则所有人可用" v-decorator="[ 'roles', {initialValue:editObj.roles, rules: []}]" />
        </a-form-item>

        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="是否启用">
          <a-switch  v-model='status' />
        </a-form-item>

        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="版本">
          <a-input-number v-decorator="[ 'version', {initialValue:editObj.version, rules: []}]" placeholder="版本" />
        </a-form-item>
        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="排序">
          <a-input-number v-decorator="[ 'sort', {initialValue:editObj.sort, rules: []}]" placeholder="排序" />
        </a-form-item>
        <a-form-item :label-col="labelCol" :wrapper-col="wrapperCol" label="备注描述">
          <a-textarea v-decorator="[ 'description', {initialValue:editObj.description, rules: []}]" placeholder="备注描述"
            :autoSize="{ minRows: 3, maxRows: 5 }" />
        </a-form-item>
      </a-form>
    </a-modal>
    <a-modal :title="viewTitle" width="90%" :visible="viewImage" :footer="null" @cancel="viewImage = false">
      <div style="min-height: 400px">
        <img :src="diagramUrl" :alt="viewTitle">
      </div>
    </a-modal>
    <!--表单 预览-->
    <a-modal :title="lcModa.title" v-model="lcModa.visible" :footer="null" :maskClosable="false" width="80%">
      <component :is="lcModa.formComponent" :disabled="true"></component>
    </a-modal>
  </a-card>

</template>

<script>
  import {
    filterObj
  } from '@/utils/util';
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    activitiMixin
  } from '@/views/activiti/mixins/activitiMixin'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import {
    deleteAction,
    getAction,
    downFile
  } from '@/api/manage'
  import pick from "lodash.pick";
  import JTreeSelect from '@/components/jeecg/JTreeSelect'
  import JTreeDict from '@/components/jeecg/JTreeDict'
  import {
    initDictOptions,
    filterDictText
  } from '@/components/dict/JDictSelectUtil'
  import JSelectUserByDep from '@/components/jeecgbiz/JSelectUserByDep'
  import JSelectRole from '@/components/jeecgbiz/JSelectRole'
  import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
  export default {
    name: "ProcessModelList",
    mixins: [activitiMixin, JeecgListMixin],
    components: {
      JEllipsis,
      JSelectUserByDep,
      JSelectRole,
      JSelectDepart,
      JTreeSelect,
      JTreeDict
    },
    data() {
      return {
        viewImage: false,
        viewTitle: "",
        diagramUrl: "",
        /*编辑流程*/
        editObj: {
          visible: false,
          title: '创建模型',
        },
        status:true,
        editForm: this.$form.createForm(this),

        description: '已部署模型',
        // 查询条件
        queryParam: {
          createTimeRange: [],
          keyWord: '',
        },
        tabKey: "1",
        // 表头
        labelCol: {
          xs: {
            span: 4
          },
          sm: {
            span: 4
          },
        },
        wrapperCol: {
          xs: {
            span: 20
          },
          sm: {
            span: 20
          },
        },
        confirmLoading: false,
        current: 0,
        dictOptions: [],
        url: {
          list: "/pro_complain/listData",
          updateStatus: "/pro_complain/updateStatus",
          delByIds: "/activiti_process/delByIds",
          updateInfo: "/pro_complain/updateInfo",
          buildInfo: "/pro_complain/buildInfo",
        },
     
        lcModa: {
          title: '表单预览',
          visible: false,
          formComponent: null
        },
        lcTypeF: [],
        dataList: [],
        updateRow: {}
      }
    },
    computed: {
      //可行性测试，根据文件路径动态加载组件
      LcDict: function() {
        var myComponent = () => import(`@/components/dict/JDictSelectTag`);
        return myComponent;
      },
    },
    methods: {
      initDictConfig() {
        //初始化字典 - 流程分类
        initDictOptions('complain_type').then((res) => {
          if (res.success) {
            var lcTypes = [];
            this.dictOptions = res.result || [];
            for (const dict of this.dictOptions) {
              lcTypes.push({
                text: dict.text,
                value: dict.value
              })
            }
            this.lcTypeF = lcTypes;
            console.log(lcTypes)
          }
        });
      },
      filterDictText(dictOptions, text) {
        if (dictOptions instanceof Array) {
          for (let dictItem of dictOptions) {
            if (text === dictItem.value) {
              return dictItem.text
            }
          }
        }
        return text
      },

      /*编辑信息*/
      edit(row) {
        this.editObj = Object.assign(this.editObj, row)
        
        if(this.editObj.status == 1){
          this.status = true;
        }else{
          this.status = false;
        }
        
        this.editObj.visible = true;
        this.editObj.title = '编辑模型';
      },
      build() {
        this.editObj = {
          visible: true,
          title: '创建模型',
        }
      },
      editObjOk() {
        var _this = this;
        this.editForm.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.editObj, values)
            console.log("formData", formData)
            var url = this.url.updateInfo;
            if (formData.id == null || formData.id == '') {
              url = this.url.buildInfo;
            }
            
            if(this.status){
              formData.status = 1;
            }else{
              formData.status = 0;
            }
            this.confirmLoading = true;
            this.postFormAction(url, formData).then(res => {
              if (res.success) {
                _this.$message.success("操作成功");
                
                _this.editObj = {
                  visible: false,
                  title: '创建模型',
                  status:true,
                },
                _this.loadData();
                
              } else {
                _this.$message.error(res.message);
              }
            }).finally(() => _this.confirmLoading = false);;
          }
        })
      },
      change_routeName() {
        this.$nextTick(() => {
          let routeName = this.editForm.getFieldValue('routeName');
          console.log("routeName", routeName)
          var route = this.getFormComponent(routeName);
          this.editObj.businessTable = route.businessTable;
          this.editObj.routeName = route.routeName;
          console.log("this.editObj", this.editObj)
        })
      },
      viewForm(routeName) {
        if (!routeName) routeName = this.editObj.routeName;
        if (!routeName) {
          this.$message.warning(
            "请先选择表单！"
          );
          return;
        }
        let formComponent = this.getFormComponent(routeName);
        console.log(formComponent)
        this.lcModa.formComponent = formComponent.component;
        this.lcModa.title = '表单预览：' + formComponent.text;
        this.lcModa.visible = true;
      },

      remove(row) {
        var _this = this;
        _this.$confirm({
          title: "确认删除",
          content: "您确认要删除模型 " + row.name + " ?",
          loading: true,
          onOk: () => {
            _this.postFormAction(_this.url.delByIds, {
              ids: row.id
            }).then(res => {
              if (res.success) {
                _this.$message.success("操作成功");
                _this.loadData();
              } else {
                _this.$message.error(res.message);
              }
            });
          }
        });
      },
      editStatus(status, row) {
        var _this = this;
        let operation = "";
        if (status == 0) {
          operation = "不启用";
        } else {
          operation = "启用";
        }
        this.$confirm({
          title: "确认" + operation + "?",
          content: `您确认要${operation}模型${row.name}?`,
          onOk() {
            let params = {
              status: status,
              id: row.id
            };
            _this.postFormAction(_this.url.updateStatus, params).then(res => {
              if (res.success) {
                _this.$message.success("操作成功");
                _this.loadData();
              } else {
                _this.$message.error(res.message);
              }
            });
          },
          onCancel() {},
        });
      },
      /*删除模型*/
      deletelc(y, row) {
        console.log(y, row);
        if (y) {
          getAction(this.url.delete + row.id).then((res) => {
            if (res.success) {
              this.$message.success(res.message);
            } else {
              this.$message.error(res.message);
            }
            this.loadData();
          });
        }
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        console.log(pagination, filters, sorter)
        /*if (Object.keys(filters).length>0&&this.dataList.length>0){
          for (const filterField in filters) {
            let fiterVals = filters[filterField]||[];

          }
        }*/
        // this.loadData();
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error("请设置url.list属性!")
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams(); //查询条件
        this.loading = true;
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            let records = res.result || [];
            this.dataSource = records;
            this.dataList = records;
            this.ipagination.total = records.length;
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false;
        })
      },
      getQueryParams() {
        //this.queryParam.tableName = 'act_z_complain';
        var param = Object.assign({}, this.queryParam, this.isorter);
        delete param.createTimeRange; // 时间参数不传递后台
        //param.tableName = 'act_z_complain';
        return filterObj(param);
      },

      // 重置
      searchReset() {
        var that = this;
        var logType = that.queryParam.logType;
        that.queryParam = {}; //清空查询区域参数
        that.queryParam.logType = logType;
        that.loadData(this.ipagination.current);
      },
      onDateChange: function(value, dateString) {
        console.log(dateString[0], dateString[1]);
        this.queryParam.createTime_begin = dateString[0];
        this.queryParam.createTime_end = dateString[1];
      },
      onDateOk(value) {
        console.log(value);
      },

      filter_categoryId(v, r) {
        // console.log(v,r)
        return r.categoryId == v;
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';

  .nodespan {
    color: #999;
  }

  .ant-checkbox-wrapper+.ant-checkbox-wrapper {
    margin-left: 0;
    margin-right: 8px;
  }
</style>
