<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="荣誉类型">
              <j-dict-select-tag v-model="queryParam.honourClass" placeholder="请选择荣誉类型" dictCode="honour_class" />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="负责人">
              <a-input placeholder="请输入负责人" v-model="queryParam.leader"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="团队成员">
              <a-input placeholder="请输入团队成员" v-model="queryParam.teamPersons"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="归属专业">
              <jtree-select-major v-model="queryParam.majorId" placeholder="请选择归属专业"></jtree-select-major>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24" style="margin-bottom: 10px;">
            <a-form-item label="时间范围" style="margin-bottom:0;">
              <a-form-item :style="{ display: &quot;inline-block&quot;, width: &quot;calc(50% - 12px)&quot; }">
                <a-date-picker style="width: 100%" v-model="queryParam.beginTime" />
              </a-form-item>
              <span
                :style="{ display: &quot;inline-block&quot;, width: &quot;24px&quot;, textAlign: &quot;center&quot; }">
                -
              </span>
              <a-form-item :style="{ display: &quot;inline-block&quot;, width: &quot;calc(50% - 12px)&quot; }">
                <a-date-picker style="width: 100%" v-model="queryParam.endTime" />
              </a-form-item>
            </a-form-item>
          </a-col>

          <!-- <a-col :md="8" :sm="24">
            <a-form-item label="审核状态">
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="0" name="未提交">
                  未提交
                </a-checkbox>
              </a-checkbox-group>
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="1" name="审核中">
                  审核中
                </a-checkbox>
              </a-checkbox-group>
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="2" name="已通过">
                  已通过
                </a-checkbox>
              </a-checkbox-group>
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="-1" name="已驳回">
                  已驳回
                </a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col> -->

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>

              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" @click="exportAll" icon="export" style="margin-left: 8px">导出总表</a-button>
    </div>

    <!-- table区域-begin -->
    <div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
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

        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail(record)">
            <a-icon type="file-search" />详细信息
          </a>
          <a-divider type="vertical" />

          <template v-if="downAuthList[record.honourClass] == 1">
            <a @click="downloadAppendix(record)">下载附件</a>
          </template>
          <template v-else>
            <a-tooltip>
              <template slot="title">
                <font color="white">无权下载或已过期</font>
              </template>
              <font style="cursor: not-allowed;">下载附件</font>
            </a-tooltip>
          </template>
        </span>

      </a-table>
    </div>

    <!-- table区域-end -->

    <!-- 表单区域 -->
    <imag-preview ref="imgPerview" @ok="modalFormOk"></imag-preview>
    <honour-detail-modal ref="detail" @ok="modalFormOk"></honour-detail-modal>
  </a-card>
</template>

<script>
  import ImagPreview from './modules/ImagPreview'
  import HonourDetailModal from './modules/HonourDetailModal'
  import JtreeSelectMajor from './modules/JtreeSelectMajor'
  import ColumnJson from './json/columns'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    getAction,
    postAction,
    getFileAccessHttpUrl
  } from '@/api/manage'
  import store from '../../store'
  export default {
    name: 'HonourAuthList',
    mixins: [JeecgListMixin],
    components: {
      JtreeSelectMajor,
      ImagPreview,
      HonourDetailModal
    },
    data() {
      return {
        statusType: [],
        description: '汇总查询',
        isMultiple: true,
        uploading: false,
        queryParam: {},
        downAuthList: [],
        // 表头
        columns: [{
            title: '荣誉类型',
            align: 'center',
            dataIndex: 'honourClass_dictText'
          }, {
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
            title: '作品名称',
            align: 'center',
            dataIndex: 'workName'
          },
          {
            title: '荣誉等级',
            align: 'center',
            dataIndex: 'honourLevel_dictText'
          },
          {
            title: '发表/授权时间',
            align: 'center',
            dataIndex: 'acquireTime'
          },
          {
            title: '团队成员',
            width: 200,
            align: 'center',
            dataIndex: 'teamPersons'
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
          },
          {
            title: '操作',
            dataIndex: 'action',
            width: 170,
            align: 'center',
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        url: {
          list: '/honour/queryHonourAllList',
          exportAllXls: '/honour/exportAllXls',
          authList: '/honour/auth/getMyAuthList'
        }

      }
    },
    created() {
      this.getMyAuthList()
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      getMyAuthList() {
        var that = this
        var downAuthList = [0, 0, 0, 0, 0, 0, 0, 0]
        getAction(this.url.authList, {}).then((res) => {
          if (res.success && res.result) {
            var data = res.result
            var now = new Date().getTime()
            for (var i = 0; i < data.length; i++) {
              data[i].currentTime = now
              var index = data[i].honourType
              if (data[i].downloadAuth == 1) {
                downAuthList[index] = 1
              } else if (data[i].downloadAuth == 2) {
                var downTime = Date.parse(new Date(data[i].downloadDeadline))
                if (now - downTime < 0) {
                  downAuthList[index] = 1
                }
              }
            }
            that.downAuthList = downAuthList
            console.log(downAuthList)
          }
        })
      },
      downloadAppendix(record) {
        if (record.appendixIds && record.appendixIds != null) {
            this.$refs.imgPerview.add(record)
            this.$refs.imgPerview.visible = true
        } else {
          this.$message.error('该荣誉无附件内容')
        }
      },
      handleChange(value) {
        console.log(`selected ${value}`)
        this.loadData()
      },
      handleDetail(record) {
        this.$refs.detail.title = record.honourClass_dictText + '详情'
        this.$refs.detail.detail(record)
        this.$refs.detail.visible = true
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
        params.createBy = store.getters.usereInfo.username
        this.loading = true
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
      },
      searchReset() {
        this.queryParam = {}
        this.statusType = []
        this.loadData(1)
      },
      searchQuery() {
        if (this.statusType != null && this.statusType.length > 0) {
          var status = this.statusType[0]
          this.queryParam.status = ''
          this.queryParam.status = status + ''
        }
        this.loadData(1)
      },
      exportAll() {
        var fieldsNames = ['', '', '', '', '', '', '']
        var fileCells = this.getCell(ColumnJson.fileColumns)
        fieldsNames[0] = fileCells
        var cerCells = this.getCell(ColumnJson.cerColumns)
        fieldsNames[1] = cerCells
        var articleCells = this.getCell(ColumnJson.articleColumns)
        fieldsNames[2] = articleCells
        var reportCells = this.getCell(ColumnJson.reportColumns)
        fieldsNames[3] = reportCells
        var agreeCells = this.getCell(ColumnJson.agreeColumns)
        fieldsNames[4] = agreeCells
        var personCells = this.getCell(ColumnJson.personColumns)
        fieldsNames[5] = personCells
        var projectCells = this.getCell(ColumnJson.projectColumns)
        fieldsNames[6] = projectCells

        let params = this.getQueryParams() // 查询条件
        params.status = 2
       
        var formData = {}
        formData.fieldsNames = fieldsNames
        formData.queryParam = params
        console.log(formData)
        delete params.pageSize
        delete params.pageNo
        postAction(this.url.exportAllXls, formData).then((res) => {
          if (res.success && res.result) {
            console.log(res)
            var url = getFileAccessHttpUrl(res.result)
            var fileName = '荣誉总表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      getCell(columns) {
        var cells = ''
        if (columns.length > 0) {
          for (var i = 0; i < columns.length; i++) {
            if (columns[i].indexOf('_dictText') != -1) {
              var cell = columns[i].replace('_dictText', '')
              cells += cell + ','
            } else if (columns[i].indexOf('action') != -1) {

            } else {
              cells += columns[i] + ','
            }
          }
        }
        return cells
      }
    }
  }
</script>
<style scoped>
  @import "~@assets/less/common.less"
</style>
