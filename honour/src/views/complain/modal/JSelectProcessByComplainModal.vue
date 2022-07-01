<template>
  <j-modal
    :width="modalWidth"
    :visible="visible"
    :title="title"
    switchFullscreen
    @ok="handleSubmit"
    @cancel="close"
    style="top:50px"
    cancelText="关闭"
  >
    <a-row :gutter="10" style="background-color: #ececec; padding: 10px; margin: -10px">
      
      <a-col :md="24" :sm="24">
        <a-card :bordered="false">
          流程名称:
          <a-input-search
            :style="{width:'150px',marginBottom:'15px'}"
            placeholder="请流程名称"
            v-model="queryParam.username"
            @search="onSearch"
          ></a-input-search>
          <a-button @click="searchReset(1)" style="margin-left: 20px" icon="redo">重置</a-button>
          <!--用户列表-->
          <a-table
            ref="table"
            :scroll="scrollTrigger"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: getType}"
            :loading="loading"
            @change="handleTableChange">
            <span slot="pic" slot-scope="text, record">
              <span @click="showResource(record)" style="color: blue;cursor: pointer;">{{text}}</span>
            </span>
          </a-table>
          
        </a-card>
      </a-col>
    </a-row>
    <a-modal
      :title="viewTitle" width="90%"
      :visible="viewImage" :footer="null"
      @cancel="viewImage = false"
    >
      <div style="min-height: 400px">
        <img :src="diagramUrl" :alt="viewTitle">
      </div>
    </a-modal>
  </j-modal>
  <!--查看图片-->
  
</template>

<script>
  import {filterObj} from '@/utils/util'
  import {queryDepartTreeList, getUserList, queryUserByDepId} from '@/api/api'
  import {initDictOptions, filterDictText} from '@/components/dict/JDictSelectUtil'
  export default {
    name: 'JSelectProcessByComplainModal',
    components: {},
    props: ['modalWidth', 'multi', 'userIds','customReturnField'],
    data() {
      return {
        viewImage:false,
        viewTitle:"",
        diagramUrl:"",
        queryParam: {
          name: "",
          tableName:'',
          pageNo:0,
          pageSize:10,
        },
        columns: [
          {
            title: '流程名称',
            align: 'center',
            dataIndex: 'name'
          },
          {
            title: '流程标识',
            align: 'center',
            dataIndex: 'processKey'
          },
          {
            title: '流程图片',
            align: 'center',
            dataIndex: 'diagramName',
            scopedSlots: { customRender: 'pic' }
          },
          {
            title: '备注说明',
            align: 'center',
            dataIndex: 'description'
          },
          {
            title: '部署时间',
            align: 'center',
            dataIndex: 'createTime'
          }
        ],
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectProcessRows: [],
        selectProcessId: [],
        title: '根据表单选择所属流程',
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        departTree: [],
        visible: false,
        form: this.$form.createForm(this),
        loading: false,
        expandedKeys: [],
        url: {
          listDataByPage: '/activiti_process/listDataByPage',
          img: "/activiti/models/export",
        },
      }
    },
    computed: {
      // 计算属性的 getter
      getType: function () {
        return this.multi == true ? 'checkbox' : 'radio';
      }
    },
    watch: {
      processId: {
        immediate: true,
        handler() {
          this.initProcessList();
        }
      },
    },
    created() {
      // 该方法触发屏幕自适应
      this.resetScreenSize();
      this.loadData()
    },
    methods: {
      initDictConfig() {
        //初始化字典 - 流程分类
        initDictOptions('bpm_process_type').then((res) => {
          if (res.success) {
            var lcTypes = [];
            this.dictOptions = res.result||[];
            for (const dict of this.dictOptions) {
              lcTypes.push({text:dict.text,value:dict.value})
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
      initProcessList(){
        var url = this.url.listDataByPage;
        this.getAction(url,this.queryParam).then((res) => {
          if (res.success) {
            let selectedRowKeys = []
            let names = []
            res.result.records.forEach(proData => {
              names.push(proData['name'])
              selectedRowKeys.push(proData['id'])
            })
            this.selectedRowKeys = selectedRowKeys
            this.$emit('initComp', names.join(','))
          }
        })
      },
      
      async loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
       this.loading = true
       let params = this.getQueryParams()//查询条件
       if(params.name) params.name = `*${params.name}*`;
       this.getAction(this.url.listDataByPage,params).then((res) => {
         if (res.success) {
           this.dataSource = res.result.records
           this.ipagination.total = res.result.total
         }
       }).finally(() => {
         this.loading = false
       })
      },
      // 触发屏幕自适应
      resetScreenSize() {
        let screenWidth = document.body.clientWidth;
        if (screenWidth < 500) {
          this.scrollTrigger = {x: 800};
        } else {
          this.scrollTrigger = {};
        }
      },
      showModal(param) {
        this.visible = true;
        this.queryParam = param;
        //this.initProcessList();
        this.loadData();
        this.form.resetFields();
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      getQueryField() {
        let str = 'id,';
        for (let a = 0; a < this.columns.length; a++) {
          str += ',' + this.columns[a].dataIndex;
        }
        return str;
      },
      searchReset(num) {
        let that = this;
        if (num !== 0) {
          that.queryParam.name=='';
          that.queryParam.pageNo=0;
          that.queryParam.pageSize=10;
          that.loadData(1);
        }
        that.selectedRowKeys = [];
        that.selectProcessId = '';
      },
      close() {
        this.searchReset(0);
        this.visible = false;
      },
      handleTableChange(pagination, filters, sorter) {
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc';
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleSubmit() {
        let that = this;
        this.getSelectProcessRows();
        that.$emit('ok', that.selectProcessRows, that.selectProcessId);
        that.searchReset(0)
        that.close();
      },
      //获取选择用户信息
      getSelectProcessRows(rowId) {
        let dataSource = this.dataSource;
        console.log(dataSource);
        let processId = "";
        this.selectProcessRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectProcessRows = [];
            this.selectProcessRows.push(dataSource[i]);
            processId = dataSource[i][this.customReturnField]
          }
        }
        this.selectProcessId = processId;
      },
      
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onSearch() {
        this.loadData(1);
      },
      modalFormOk() {
        this.loadData();
      },
      showResource(row) {
          this.viewTitle = "流程图片预览(" + row.diagramName + ")";
          this.diagramUrl = `${window._CONFIG['domianURL']}${this.url.img}?id=${row.id}`;
          this.viewImage = true;
      },
    }
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
  }

  #components-layout-demo-custom-trigger .trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color .3s;
  }
</style>