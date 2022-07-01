<template>
  <a-card :bordered="false">
    <!--导航区域-->
    <div>
      <a-tabs defaultActiveKey="1" @change="callback">
        <a-tab-pane tab="查询日志" key="1"></a-tab-pane>
        <a-tab-pane tab="下载日志" key="2"></a-tab-pane>
      </a-tabs>
    </div>

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <template v-if="tabKey==2">
              <a-form-item label="标题">
                <a-input placeholder="请输入标题" v-model="queryParam.keyWord"></a-input>
              </a-form-item>
            </template>
            <template v-else>
              <a-form-item label="搜索参数">
                <a-input placeholder="请输入搜索关键词" v-model="queryParam.keyWord"></a-input>
                <!-- <a-select
                  show-search
                  :value="queryParam.keyWord"
                  placeholder="请输入搜索关键词"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="false"
                  :not-found-content="null"
                  @search="handleSearch"
                  @change="handleChange">
                  <a-select-option v-for="d in data" :key="d">
                    {{ d }}
                  </a-select-option>
                </a-select> -->
                <!-- <jeecg-select fields="title" tableId="1" v-model="queryParam.keyWord" placeholder="请输入标题"></jeecg-select> -->
              </a-form-item>
            </template>

          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="荣誉类型">
              <j-dict-select-tag v-model="queryParam.honourType" placeholder="请选择荣誉类型" dictCode="honour_class" />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="10">
            <a-form-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-range-picker
                style="width: 210px"
                v-model="queryParam.createTimeRange"
                format="YYYY-MM-DD"
                :placeholder="['开始时间', '结束时间']"
                @change="onDateChange"
                @ok="onDateOk" />
            </a-form-item>
          </a-col>

          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="24">
              <a-button type="primary" style="left: 10px" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px;left: 10px">重置
              </a-button>
            </a-col>
          </span>

        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <a-table
      ref="table"
      size="middle"
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      @change="handleTableChange">

      <!-- 字符串超长截取省略号显示-->
      <span slot="queryParam" slot-scope="text">
        <j-ellipsis :value="text" :length="80" />
      </span>
      <span slot="action" slot-scope="text, record">
        <a @click="downloadAppendix(record)">查看附件</a>
      </span>
      <template v-if="tabKey == 2">

      </template>
    </a-table>
    <imag-preview ref="imgPerview" @ok="modalFormOk"></imag-preview>

    <!-- table区域-end -->
  </a-card>
</template>

<script>
  import {
    filterObj
  } from '@/utils/util'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    getAction
  } from '@/api/manage'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import ImagPreview from './modules/ImagPreview'
  export default {
    name: 'QueryHistories',
    mixins: [JeecgListMixin],
    components: {
      JEllipsis,
      ImagPreview
    },
    data() {
      return {
        description: '这是日志管理页面',
        // 查询条件
        queryParam: {
          ipInfo: '',
          createTimeRange: [],
          keyWord: ''
        },
        tabKey: '1',
        // 表头
        columns: [{
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '荣誉类型',
            dataIndex: 'honourType_dictText',
            align: 'center'
          },
          {
            title: '查询参数',
            align: 'left',
            dataIndex: 'queryParam',
            scopedSlots: {
              customRender: 'queryParam'
            },
            sorter: true
          },
          {
            title: '操作人名称',
            dataIndex: 'createBy',
            align: 'center',
            sorter: true
          },
          {
            title: '查询时间',
            dataIndex: 'createTime',
            align: 'center',
            sorter: true
          }
        ],
        queryColumns: [{
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '荣誉类型',
            dataIndex: 'honourType_dictText',
            align: 'center'
          },
          {
            title: '查询参数',
            align: 'left',
            dataIndex: 'queryParam',
            scopedSlots: {
              customRender: 'queryParam'
            },
            sorter: true
          },
          {
            title: '操作人名称',
            dataIndex: 'createBy',
            align: 'center',
            sorter: true
          },
          {
            title: '查询时间',
            dataIndex: 'createTime',
            align: 'center',
            sorter: true
          }
        ],
        downColumns: [{
            title: '#',
            dataIndex: '',
            key: 'rowIndex',
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '荣誉类型',
            dataIndex: 'honourType_dictText',
            align: 'center'
          },
          {
            title: '标题',
            align: 'left',
            dataIndex: 'honourTitle'
          },
          {
            title: '操作人名称',
            dataIndex: 'createBy',
            align: 'center',
            sorter: true
          },
          {
            title: '操作时间',
            dataIndex: 'createTime',
            align: 'center',
            sorter: true
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        labelCol: {
          xs: {
            span: 1
          },
          sm: {
            span: 2
          }
        },
        wrapperCol: {
          xs: {
            span: 10
          },
          sm: {
            span: 16
          }
        },
        data: [],
        url: {
          list: '/honour/query/list',
          queryList: '/honour/query/list',
          downList: '/honour/query/downList'
        }
      }
    },
    methods: {
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter)
        param.field = this.getQueryField()
        param.pageNo = this.ipagination.current
        param.pageSize = this.ipagination.pageSize
        delete param.createTimeRange // 时间参数不传递后台
        if (this.superQueryParams) {
          param['superQueryParams'] = encodeURI(this.superQueryParams)
          param['superQueryMatchType'] = this.superQueryMatchType
        }
        return filterObj(param)
      },
      downloadAppendix(record) {
        record.honourClass = record.honourType
        this.$refs.imgPerview.add(record)
        this.$refs.imgPerview.visible = true
      },

      // 重置
      searchReset() {
        var that = this
        that.queryParam = {} // 清空查询区域参数
        that.loadData(this.ipagination.current)
      },
      // 日志类型
      callback(key) {
        this.queryParam = {}
        // 动态添加操作类型列
        if (key == 2) {
          this.tabKey = '2'
          this.columns = this.downColumns
          this.url.list = this.url.downList
        } else {
          this.tabKey = '1'
          this.columns = this.queryColumns
          this.url.list = this.url.queryList
        }

        let that = this
        that.loadData()
      },
      onDateChange: function(value, dateString) {
        console.log(dateString[0], dateString[1])
        this.queryParam.createTime_begin = dateString[0]
        this.queryParam.createTime_end = dateString[1]
      },
      onDateOk(value) {
        console.log(value)
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        var params = this.getQueryParams() // 查询条件
        this.loading = true
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
            for (var i = 0; i < this.dataSource.length; i++) {
              if (this.dataSource[i].honourType === 8) {
                this.dataSource[i].honourType_dictText = '汇总类'
              }
            }
            this.ipagination.total = res.result.total
          }
          if (res.code === 510) {
            this.$message.warning(res.message)
          }
          this.loading = false
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>
