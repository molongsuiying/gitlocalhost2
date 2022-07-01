<template>
  <a-card :bordered="false">

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

        <template slot="projectType" slot-scope="text,record">
          <template v-if="record.studentName != ''">
            <font>学生姓名:{{ record.studentName }}</font><br />
            <font>指导教师:{{ record.teacherName }}</font>
          </template>
          <template v-else-if="record.teacherName != '' ">
            <font>教师:{{ record.teacherName }}</font>
          </template>
        </template>
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

          <a v-has="'user:honourEdit'" @click="handleEdit(record)" v-show="record.status!=1 && record.status!=2">编辑</a>
          <a-divider type="vertical" v-has="'user:honourEdit'" v-show="record.status!=1 && record.status!=2"/>

          <a v-has="'user:honourSubmit'" @click="submit(record)" v-show="record.status!=1 && record.status!=2">提交</a>
          <a-divider type="vertical" v-has="'user:honourSubmit'" v-show="record.status!=1 && record.status!=2"/>

          <a @click="handlePass(record)" v-has="'user:examine'" v-show="record.status==1">审核</a>
          <a-divider type="vertical" v-has="'user:examine'" v-show="record.status==1"/>

          <a v-has="'user:adminEdit'" @click="handleEdit(record)" v-show="record.status==2">编辑</a>
          <a-divider type="vertical" v-has="'user:adminEdit'" v-show="record.status==2"/>

          <template v-if="honourAuth.downloadAuth == 1">
            <a @click="downloadAppendix(record)">下载附件</a>
          </template>
          <template v-else-if="honourAuth.downloadAuth == 2">
            <template v-if="moment(honourAuth.currentTime) - moment(honourAuth.downloadDeadline) < 0">
              <a-tooltip>
                <template slot="title">
                  到期时间:{{ honourAuth.downloadDeadline }}
                </template>
                <a @click="downloadAppendix(record)">下载附件</a>
              </a-tooltip>
            </template>
            <template v-else>
              <a-tooltip>
                <template slot="title">
                  <font color="white">已过期:{{ honourAuth.downloadDeadline }}</font>
                </template>
                <font style="cursor: not-allowed;">下载附件</font>
              </a-tooltip>
            </template>
          </template>
          <template v-else>
            <a-tooltip>
              <template slot="title">
                <font color="white">无权下载</font>
              </template>
              <font style="cursor: not-allowed;">下载附件</font>
            </a-tooltip>
          </template>
          <a-divider type="vertical" v-has="'user:deleteHonour'" v-show="record.status < 1"/>

          <a-popconfirm v-has="'user:deleteHonour'" v-show="record.status< 1" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- table区域-end -->
    <honour-project-modal ref="modalForm" @ok="modalFormOk"></honour-project-modal>
    <imag-preview ref="imgPerview" @ok="modalFormOk"></imag-preview>
    <honour-detail-modal ref="detail" @ok="modalFormOk"></honour-detail-modal>
  </a-card>
</template>

<script>
  import moment from 'moment'
  import Vue from 'vue'
  import HonourProjectModal from './HonourProjectModal'
  import ImagPreview from './ImagPreview'
  import HonourDetailModal from './HonourDetailModal'
  import { checkDuplicateHonourByTitle } from '@/api/api'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    getAction,
    getFileAccessHttpUrl,
    postAction
  } from '@/api/manage'
import store from '../../../store'
  export default {
    name: 'HonourCertificate',
    mixins: [JeecgListMixin],
    components: {
      HonourProjectModal,
      HonourDetailModal,
      ImagPreview
    },
    data() {
      return {

        selectedRowKeys: [],
        description: '课题类荣誉',
        settingColumns: [],
        yearShowOne: false,
        reportDefColumns: [{
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
            title: '课题类别',
            align: 'center',
            dataIndex: 'projectType',
            scopedSlots: {
              customRender: 'projectType'
            }

          },
          {
            title: '立项负责人',
            align: 'center',
            dataIndex: 'buildProjectCharge'

          },
          {
            title: '结项负责人',
            align: 'center',
            dataIndex: 'overProjectCharge'
          },
          {
            title: '荣誉级别',
            align: 'center',
            dataIndex: 'honourLevel_dictText',
            customRender: function(t, r, index) {
              if (r.honourLevel === 99) {
                return t + '(' + r.honourLevelTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '颁发时间',
            align: 'center',
            dataIndex: 'acquireTime'
          },
          {
            title: '颁发机关',
            align: 'center',
            dataIndex: 'authorityType_dictText',
            customRender: function(t, r, index) {
              if (r.authorityType === 99) {
                return t + '(' + r.authorityTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText',
            customRender: function(t, r, index) {
              if (r.achievementType === 99) {
                return t + '(' + r.achievementTypeTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText',
            customRender: function(t, r, index) {
              if (r.achievementLevel === 99) {
                return t + '(' + r.achievementLevelTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '团队成员',
            width: 100,
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
            align: 'center',
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        // 表头
        columns: [{
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
            title: '课题类别',
            align: 'center',
            dataIndex: 'projectType',
            scopedSlots: {
              customRender: 'projectType'
            }

          },
          {
            title: '立项负责人',
            align: 'center',
            dataIndex: 'buildProjectCharge'

          },
          {
            title: '结项负责人',
            align: 'center',
            dataIndex: 'overProjectCharge'
          },
          {
            title: '荣誉级别',
            align: 'center',
            dataIndex: 'honourLevel_dictText',
            customRender: function(t, r, index) {
              if (r.honourLevel === 99) {
                return t + '(' + r.honourLevelTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '颁发时间',
            align: 'center',
            dataIndex: 'acquireTime'
          },
          {
            title: '颁发机关',
            align: 'center',
            dataIndex: 'authorityType_dictText',
            customRender: function(t, r, index) {
              if (r.authorityType === 99) {
                return t + '(' + r.authorityTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText',
            customRender: function(t, r, index) {
              if (r.achievementType === 99) {
                return t + '(' + r.achievementTypeTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText',
            customRender: function(t, r, index) {
              if (r.achievementLevel === 99) {
                return t + '(' + r.achievementLevelTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '团队成员',
            width: 100,
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
            align: 'center',
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        url: {
          list: '/honour/project/queryHonourProjectList',
          submit: '/honour/project/submitProject',
          exportXls: '/honour/project/exportXls',
          auth: '/honour/auth/getMyAuth',
          delete: 'honour/project/delete'
        }
      }
    },
    created() {
      this.getMyAuth(7)
      this.initColumns()
    },
    methods: {
      searchReset() {
        this.queryParam = {}
        this.loadData(1)
      },
      moment,
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        let params = this.getQueryParams() // 查询条件
        params.create = 'create'
          // params.createBy = store.getters.userInfo.username
        getAction(this.url.list, params).then((res) => {
          if (res.success && res.result) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total

            var projectNum = 0

            if (this.ipagination.total !== 0) {
              projectNum = 1
            }

            this.$emit('projectFn', projectNum)
          }
        })
      },
      downloadAppendix(record) {
        record.honourClass = 7
        this.$refs.imgPerview.add(record)
        this.$refs.imgPerview.visible = true
      },
      onColSettingsChange(checkedValues) {
        var key = this.$route.name + ':colsettings'
        Vue.ls.set(key, checkedValues, 7 * 24 * 60 * 60 * 1000)

        this.settingColumns = checkedValues

        const cols = this.reportDefColumns.filter(item => {
          if (item.key == 'rowIndex' || item.dataIndex == 'action') {
            return true
          }
          if (this.settingColumns.includes(item.dataIndex)) {
            return true
          }
          return false
        })
        console.log(this.settingColumns)
        this.columns = cols
      },
      initColumns() {
        // 权限过滤（列权限控制时打开，修改第二个参数为授权码前缀）
        // this.defColumns = colAuthFilter(this.defColumns,"testdemo:");

        var key = this.description + ':colsettings'
        let colSettings = Vue.ls.get(key)
        if (colSettings == null || colSettings == undefined) {
          let allSettingColumns = []
          this.reportDefColumns.forEach(function(item, i, array) {
            allSettingColumns.push(item.dataIndex)
          })
          this.settingColumns = allSettingColumns
          this.columns = this.reportDefColumns
        } else {
          this.settingColumns = colSettings
          const cols = this.reportDefColumns.filter(item => {
            if (item.key == 'rowIndex' || item.dataIndex == 'action') {
              return true
            }
            if (colSettings.includes(item.dataIndex)) {
              return true
            }
            return false
          })
          this.columns = cols
        }
      },
      open(record) {
        this.queryParam = record
        this.loadData(1)
      },
      exportXls() {
        var cells = ''
        if (this.settingColumns.length > 0) {
          for (var i = 0; i < this.settingColumns.length; i++) {
            if (this.settingColumns[i].indexOf('_dictText') != -1) {
              var cell = this.settingColumns[i].replace('_dictText', '')
              cells += cell + ','
            } else if (this.settingColumns[i].indexOf('action') != -1) {

            } else {
              cells += this.settingColumns[i] + ','
            }
          }
        }

        var ids = ''
        for (var a = 0; a < this.selectionRows.length; a++) {
          ids += this.selectionRows[a].id + ','
        }
        console.log(cells)
        let params = this.getQueryParams() // 查询条件
        cells += ',studentName,teacherName'
        params.customizeCells = cells

        if (ids.length > 0) {
          params.ids = ids
        }
        params.create = 'create'
        delete params.pageSize
        delete params.pageNo
        getAction(this.url.exportXls, params).then((res) => {
          if (res.success && res.result) {
            console.log(res)
            var url = getFileAccessHttpUrl(res.result)
            var fileName = '课题类荣誉表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      submit(record) {
        var that = this
        console.log(record)
        if (record.status > 0) {
          this.$message.warning('请选择未提交或未通过的荣誉！')
          return false
        }


/*        record.title = record.title.replace("[","");
        record.title = record.title.replace("]",""); */
        var query = {
          id: record.id,
          fields: 'title',
          title: ""+record.title,
          tableId: 7
        }
        let formData = Object.assign({}, query)
        checkDuplicateHonourByTitle(formData).then((res) => {
            if (res.success) {
              var title = '确认提交'
              var content = '是否提交选中数据?'
              if (res.result) {
                title = '当前系已提交过 [' + record.title + ']'
                content = '是否确认提交?'
              }
              that.$confirm({
                title: title,
                content: content,
                onOk: function() {
                  that.loading = true
                  postAction(that.url.submit, record).then((res) => {
                    if (res.success) {
                      that.$message.success(res.message)
                      that.loadData()
                    } else {
                      that.$message.warning(res.message)
                    }
                  }).finally(() => {
                    that.loading = false
                  })
                }
              })
            } else {
              that.$message.warning(res.message)
            }
        })
      },
      handlePass(record) {
        this.$refs.modalForm.pass(record)
        this.$refs.modalForm.title = '审核证书类荣誉'
        this.$refs.modalForm.disableSubmit = false
      },
      handleDetail(record) {
        record.honourClass = 7
        record.honourClass_dictText = '课题类'
        this.$refs.detail.title = record.honourClass_dictText + '详情'
        this.$refs.detail.detail(record)
        this.$refs.detail.visible = true
      }

    }
  }
</script>
<style scoped>
  /** Button按钮间距 */
  .ant-btn {
    margin-left: 3px
  }

  .ant-card {
    margin-left: -30px;
    margin-right: -30px;
  }

  .table-page-search-wrapper {
    margin-top: -16px;
    margin-bottom: 16px;
  }
</style>
