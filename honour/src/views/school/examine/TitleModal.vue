<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">

        <template slot="status" slot-scope="text,record">
          <template v-if="text == 0">
            <font color="blue">未提交</font>
          </template>
          <template v-else-if="text == 1">
            <font color="orange">审核中</font>
          </template>
          <template v-else-if="text == 2">
            <font color="green">已通过</font>
          </template>
          <template v-else>
            <a-tooltip>
              <template slot="title">
                驳回理由:{{ record.remarks }}
              </template>
              <font color="red">已驳回</font>
            </a-tooltip>
          </template>
        </template>

      </a-table>
      </div>

    </a-spin>

    <template slot="footer">
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
    </template>

  </a-modal>
</template>

<script>
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import moment from 'moment'
  import {
    getFileAccessHttpUrl,
    httpAction,
    getAction
  } from '@/api/manage'
  import ColumnJson from '../json/columns'

  export default {
    name: 'FileExcelModal',
    mixins: [JeecgListMixin],
    components: {
    },
    data() {
      return {
        cacheData: [],
        dataSource: [],
        title: '操作',
        compLabel: '标\xa0\xa0\xa0\xa0\xa0\xa0\xa0题',
        confirmLoading: false,
        visible: false,
        passFlag: false,
        queryParam: {},
        model: {},
        selectedRowKeys: [],
        editingKey: false,
        description: '文件类荣誉',
        settingColumns: [],
        yearShowOne: false,
        // 表头
        columns: [],
        defColumns: [{
            title: '标题',
            align: 'center',
            dataIndex: 'title'
          },
          {
            title: '负责人',
            align: 'center',
            dataIndex: 'leader'
          },
          {
            title: '发文字号',
            align: 'center',
            dataIndex: 'articleNum'
          },
          {
            title: '发文时间',
            align: 'center',
            dataIndex: 'acquireTime'

          },
          {
            title: '发文机关',
            align: 'center',
            dataIndex: 'authorityType_dictText'
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText'
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText'
          },
          {
            title: '归属专业',
            align: 'center',
            dataIndex: 'majorName'
          },
          {
            title: '审核状态',
            align: 'center',
            dataIndex: 'status',
            scopedSlots: {
              customRender: 'status'
            }
          }
        ],
        url: {
            list: '/honour/file/queryHonourFileList',
            defList: '/honour/file/queryHonourFileList'
        }
      }
    },
    created() {

    },
    computed: {

    },
    methods: {
      add(record, honourType) {
        this.title = record.title + ' 相同项'
        this.visible = true
        this.model = record
        if (honourType == 1) {
          this.columns = this.defColumns
          this.url.list = this.url.defList
        } else if (honourType == 2) {
          this.columns = ColumnJson.defCerColumns
          this.url.list = '/honour/certificate/queryHonourCertificateList'
        } else if (honourType == 3) {
          this.columns = ColumnJson.defArticleColumns
          this.url.list = '/honour/article/queryHonourArticleList'
        } else if (honourType == 4) {
          this.columns = ColumnJson.defAgreeColumns
          this.url.list = '/honour/agree/queryHonourAgreeList'
        } else if (honourType == 5) {
          this.columns = ColumnJson.defReportColumns
          this.url.list = '/honour/report/queryHonourReportList'
        } else if (honourType == 6) {
          this.columns = ColumnJson.defPersonColumns
          this.url.list = '/honour/person/queryHonourPersonList'
        } else if (honourType == 7) {
          this.columns = ColumnJson.defProjectColumns
          this.url.list = '/honour/project/queryHonourProjectList'
        }
        this.queryParam = {
          title: record.title,
          honourId: record.id
        }
        this.loadData()
      },
      moment,

      searchReset() {

      },
      searchQuery() {

      },
      close() {
        this.dataSource = []
        this.$emit('ok')
        this.visible = false
      },
      handleCancel() {
        this.close()
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
        params.create = 'examine'
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
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

<style lang="less" scoped>

</style>
