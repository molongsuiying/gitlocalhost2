<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="流程名称">
              <a-input-search v-model="queryParam.name"
                placeholder="输入流程名称模糊查询" />
            </a-form-item>
          </a-col>
          <a-col :xl="4" :lg="4" :md="8" :sm="24">
            <a-form-item label="操作类型">
              <j-dict-select-tag v-model="queryParam.operaType" placeholder="操作类型" dictCode="process_operation"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="6" :md="8" :sm="24">
            <a-form-item label="操作时间">
              <a-range-picker v-model="timeRange"
                              format="YYYY-MM-DD"
                              :placeholder="['开始时间', '结束时间']"
                              @change="onTimeChange" />
            </a-form-item>
          </a-col>
          <a-button type="primary" @click="searchQuery" icon="search" style='margin-right: 18px;'>查询</a-button>
          <a-button type="primary" icon="download" @click="handleExportXls('操作历史')">导出</a-button>
        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey='id'
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        
        @change="handleTableChange">
        <span slot="action" slot-scope="text, record">
          <!-- <a href="javascript:void(0);" style="color: green;">流程历史</a> -->
          <!-- <a-divider type="vertical" /> -->
          <a href="javascript:void(0);" style="color: green;" @click="detail(record)">查看详情</a>

        </span>

      </a-table>
    </div>
    <!--流程表单-->
    <a-modal :destroyOnClose="true" :title="lcModa.title" v-model="lcModa.visible" :footer="null" :maskClosable="false"
      width="80%">
      <component :disabled="lcModa.disabled" v-if="lcModa.visible" :is="lcModa.formComponent" :processData="lcModa.processData"
        :isNew="lcModa.isNew" @close="lcModa.visible=false,lcModa.disabled = false"></component>
    </a-modal>
    
    <!-- table区域-end -->


  </a-card>
</template>

<script>
  import {initDictOptions, filterDictText,filterDictTextByCache} from '@/components/dict/JDictSelectUtil'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { filterObj } from '@/utils/util';
  import {
    getAction
  } from '@/api/manage'
  import {
    activitiMixin
  } from '@/views/activiti/mixins/activitiMixin'
  export default {
    name: "processQuery",
    mixins:[activitiMixin,JeecgListMixin],
    components: {
    },
    data() {
      return {
        //列设置
        settingColumns:[],
        lcModa: {
          title: '',
          disabled: false,
          visible: false,
          formComponent: null,
          isNew: false
        },
        //列定义
        columns: [
          {
            title: 'ID',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '流程名称',
            align: "center",
            dataIndex: 'name'
          },
          {
            title: '申请人',
            align: "center",
            dataIndex: 'creator'
          },
          {
            title: '个人操作',
            align: "center",
            dataIndex: 'operaType',
            customRender: (text) => {
              var val = '';
              if(text == 'starter'){
                val =  '申请操作'
              }else if(text == 'urgent'){
                val =  '催办'
              }else if(text == 'supervise'){
                val =  '督办'
              }else if(text == 'unite'){
                val =  '申请联合'
              }else if(text == 'unite_agree'){
                val =  '同意联合'
              }else if(text == 'postpone'){
                val =  '申请延期'
              }else if(text == 'actualExecutor_p'){
                val =  '同意申请'
              }else if(text == 'actualExecutor_b'){
                val =  '驳回申请'
              }
              return val;
            }
          },
          {
            title: '操作时间',
            align: "center",
            dataIndex: 'beginTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            scopedSlots: {
              filterDropdown: 'filterDropdown',
              filterIcon: 'filterIcon',
              customRender: 'action'},
          },
          
        ],
        timeRange:null,
        url: {
          getBusinessOperationQuery: "/actData/getBusinessOperationQuery",
          exportXlsUrl: "/test/jeecgDemo/exportXls"
        },
      }
    },
    methods: {
      loadData(pageNum) {
        this.loading = true
        if (pageNum === 1) {
          this.ipagination.current = 1
        }
        //加载数据 若传入参数1则加载第一页的内容
        getAction(this.url.getBusinessOperationQuery, this.queryParam).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records;
            this.ipagination.total = res.result.total
          }
        }).finally(() => {
          this.loading = false
          this.cardLoading = false
        })
      },
      onTimeChange: function (value, dateString) {
        console.log(dateString[0],dateString[1]);
        this.queryParam.beginTime=dateString[0];
        this.queryParam.endTime=dateString[1];
      },
      detail(r) {
        if (!r.routeName) {
          this.$message.warning(
            "该流程信息未配置表单，请联系开发人员！"
          );
          return;
        }
        this.lcModa.disabled = true;
        this.lcModa.title = '查看流程业务信息：' + r.name;
        this.lcModa.formComponent = this.getFormComponent(r.routeName).component;
        this.lcModa.processData = r;
        this.lcModa.isNew = false;
        this.lcModa.visible = true;
      },
      handleTableChange(pagination, filters, sorter) {
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc'
        }
        this.ipagination = pagination
        console.log(pagination);
        this.queryParam.pageNo = pagination.current;
        this.loadData(null)
      },
    },
    created() {
      
    },
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>