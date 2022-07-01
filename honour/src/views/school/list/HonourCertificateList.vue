<template>
  <a-card :bordered="false">

    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="24">
            <a-form-item :label="compLabel">
              <!-- <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input> -->
              <jeecg-select fields="title" tableId="2" v-model="queryParam.title" placeholder="请输入标题"></jeecg-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="归属专业">
              <jtree-select-major v-model="queryParam.majorId" placeholder="请选择归属专业"></jtree-select-major>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="证书类型">

              <a-input-group compact>
                <a-select default-value="studentName" @change="handleChange">
                  <a-select-option value="studentName">
                    学生
                  </a-select-option>
                  <a-select-option value="teacherName">
                    教师
                  </a-select-option>
                  <a-select-option value="departmentName">
                    部门
                  </a-select-option>
                  <a-select-option value="schoolName">
                    学校
                  </a-select-option>
                  <a-select-option value="otherName">
                    其他
                  </a-select-option>
                </a-select>
                <a-input
                  style="width: 65%"
                  v-if="selectName=='studentName'"
                  placeholder="请输入学生姓名"
                  v-model="queryParam.studentName" />
                <a-input
                  style="width: 65%"
                  v-if="selectName=='departmentName'"
                  placeholder="请输入部门名称"
                  v-model="queryParam.departmentName" />
                <a-input
                  style="width: 65%"
                  v-if="selectName=='schoolName'"
                  placeholder="请输入学校名称"
                  v-model="queryParam.schoolName" />
                <a-input
                  style="width: 65%"
                  v-if="selectName=='teacherName'"
                  placeholder="请输入教师姓名"
                  v-model="queryParam.teacherName" />
                <a-input
                  style="width: 65%"
                  v-if="selectName=='otherName'"
                  placeholder="其他"
                  v-model="queryParam.otherName" />
              </a-input-group>
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
        </a-row>
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label=" 负   责  人 ">
              <!-- <a-input placeholder="请输入发文字号" v-model="queryParam.articleNum"></a-input> -->
              <jeecg-select fields="leader" tableId="1" v-model="queryParam.leader" placeholder="请输入负责人"></jeecg-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="团队成员">
              <!-- <a-input placeholder="请输入发文字号" v-model="queryParam.articleNum"></a-input> -->
              <jeecg-select fields="team_persons" tableId="1" v-model="queryParam.teamPersons" placeholder="请输入团队成员"></jeecg-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <!-- <a-col :md="6" :sm="24">
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

          <a-col :md="12" :sm="24">
            <a-form-item label="颁发机关">
              <j-dict-select-tag v-model="queryParam.authorityType" type="radio" dictCode="certificate_authority" />
            </a-form-item>
          </a-col>

          <a-col :md="12" :sm="24">
            <a-form-item label="荣誉等级">
              <j-dict-select-tag v-model="queryParam.honourLevel" type="radio" dictCode="honour_level" />
            </a-form-item>
          </a-col>

        </a-row>
        <a-row :gutter="24">

          <a-col :md="24" :sm="24">
            <a-form-item label="成果类别">
              <j-dict-select-tag v-model="queryParam.achievementType" type="radio" dictCode="achievement_type" />
            </a-form-item>
          </a-col>
          <a-col :md="16" :sm="24">
            <a-form-item label="成果级别">
              <j-dict-select-tag v-model="queryParam.achievementLevel" type="radio" dictCode="achievement_level" />
            </a-form-item>
          </a-col>

          <a-col :md="8" :sm="24">
            <span style="float: right;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuerySecond" icon="search">查询</a-button>
              <!-- <a-button type="primary" @click="searchQuerySecond" icon="search" style="margin-left: 8px">二次查询</a-button> -->
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>

    </div>
    <!-- 操作按钮区域 -->
    <div class="table-operator" :md="24" :sm="24" style="margin-bottom:10px">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增荣誉</a-button>
      <a-button @click="importXls" type="primary" icon="import">导入EXCEL</a-button> -->
      <template v-if="honourAuth.exportAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.exportDeadline) < 0">
        <a-button type="primary" @click="exportXls" icon="downLoad" style="margin-left: 8px">导出EXCEL</a-button>
      </template>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <!-- <a-menu-item key="2" @click="batchSubmit">
            <a-icon type="upload" />
            提交审核
          </a-menu-item> -->
          <template v-if="honourAuth.exportAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.exportDeadline) < 0">
            <a-menu-item key="4" @click="exportXls">
              <a-icon type="download" />
              导出EXCEL
            </a-menu-item>
          </template>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="float:right;">
          <a-popover title="自定义列" trigger="click" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group @change="onColSettingsChange" v-model="settingColumns" :defaultValue="settingColumns">
                <a-row>
                  <template v-for="(item,index) in reportDefColumns">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <a-icon type="setting" />设置
            </a>
          </a-popover>
        </span>

      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="prizeType" slot-scope="text,record">
          <template v-if="text == 1">
            <template v-if="record.studentName != ''">
              <font>学生姓名:{{ record.studentName }}</font><br />
              <font>指导教师:{{ record.teacherName }}</font>
            </template>
            <template v-else-if="record.teacherName != '' ">
              <font>教师:{{ record.teacherName }}</font>
            </template>
            <template v-else-if="record.schoolName != '' ">
              <font>学校:{{ record.schoolName }}</font>
            </template>
            <template v-else>
              <font>部门:{{ record.departmentName }}</font>
            </template>
          </template>
          <template v-else>
            <font>{{ record.otherName }}</font>
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

          <a @click="showInfo(record)">
            <a-icon type="file-search" />详细信息
          </a>
          <a-divider type="vertical" />

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
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->
    <!-- table区域-end -->
    <honour-certificate-modal ref="modalForm" @ok="modalFormOk"></honour-certificate-modal>
    <imag-preview ref="imgPerview" @ok="modalFormOk"></imag-preview>
    <query-cer-list-modal ref="queryList" @close="queryClose"></query-cer-list-modal>
    <cer-excel-modal ref="cerExcelModal" @ok="modalFormOk"></cer-excel-modal>
  </a-card>
</template>

<script>
  import moment from 'moment'
  import Vue from 'vue'
  import HonourCertificateModal from '../modules/HonourCertificateModal'
  import ImagPreview from '../modules/ImagPreview'
  import CerExcelModal from '../excel/cerExcelModal'
  import QueryCerListModal from './QueryCerListModal'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    getAction,
    postAction,
    deleteAction,
    getFileAccessHttpUrl
  } from '@/api/manage'
  import JtreeSelectMajor from '../modules/JtreeSelectMajor'
  import JeecgSelect from '../modules/JeecgSelect'
  export default {
    name: 'HonourCertificate',
    mixins: [JeecgListMixin],
    components: {
      HonourCertificateModal,
      ImagPreview,
      QueryCerListModal,
      CerExcelModal,
      JtreeSelectMajor,
      JeecgSelect
    },
    data() {
      return {
        selectName: 'studentName',
        compLabel: '标\xa0\xa0\xa0\xa0\xa0\xa0\xa0题',
        statusType: [],
        selectedRowKeys: [],
        description: '证书类荣誉',
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
            title: '证书类别',
            align: 'center',
            dataIndex: 'prizeType',
            scopedSlots: {
              customRender: 'prizeType'
            }

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
            width: 170,
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
            title: '证书类别',
            align: 'center',
            dataIndex: 'prizeType',
            scopedSlots: {
              customRender: 'prizeType'
            }

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
            width: 170,
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        url: {
          list: '/honour/certificate/queryHonourCertificateList',
          batchSubmit: '/honour/certificate/batchSubmit',
          submit: '/honour/certificate/submitCertificate',
          exportXls: '/honour/certificate/exportXls',
          auth: '/honour/auth/getMyAuth'
        }
      }
    },
    created() {
      this.initColumns()
      this.getMyAuth(2)
    },
    methods: {
      queryClose() {
        console.log(this.queryParam)
        delete this.queryParam.majorId
      },
      searchReset() {
        this.queryParam = {}
        this.statusType = []
        this.loadData(1)
      },
      handleChange(value) {
        this.selectName = value
      },
      searchQuerySecond() {
        if (this.statusType != null && this.statusType.length > 0) {
          var status = this.statusType[0]
          this.queryParam.status = ''
          this.queryParam.status = status + ''
        }
        this.queryParam.selectName = this.selectName
        this.$refs.queryList.addAuth(this.honourAuth)
        this.$refs.queryList.add(this.queryParam)
        this.$refs.queryList.title = '二次查询荣誉'
        // this.loadData(1)
      },
      searchQuery() {
        if (this.statusType != null && this.statusType.length > 0) {
          var status = this.statusType[0]
          this.queryParam.status = ''
          this.queryParam.status = status + ''
        }
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

        getAction(this.url.list, params).then((res) => {
          if (res.success && res.result) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total

            var certificateNum = 0

            if (this.ipagination.total !== 0) {
              certificateNum = 1
            }

            this.$emit('certificateFn', certificateNum)
          }
        })
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
        params.customizeCells = cells

        if (ids.length > 0) {
          params.ids = ids
        }
        delete params.pageSize
        delete params.pageNo
        getAction(this.url.exportXls, params).then((res) => {
          if (res.success && res.result) {
            console.log(res)
            var url = getFileAccessHttpUrl(res.result)
            var fileName = '证书类荣誉表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      downloadAppendix(record) {
        record.honourClass = 2
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
      submit(record) {
        var that = this
        console.log(record)
        if (record.status > 0) {
          this.$message.warning('请选择未提交或未通过的荣誉！')
          return false
        }
        this.$confirm({
          title: '确认提交',
          content: '是否提交选中数据?',
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
      },
      batchSubmit() {
        var that = this
        if (this.selectionRows.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          var ids = ''
          for (var a = 0; a < this.selectionRows.length; a++) {
            if (this.selectionRows[a].status == 0 || this.selectionRows[a].status == -1) {
              ids += this.selectionRows[a].id + ','
            }
          }

          if (ids.length <= 0) {
            this.$message.warning('请选择一条未提交或未通过的荣誉！')
            return false
          }
          this.$confirm({
            title: '确认提交',
            content: '是否提交选中数据?',
            onOk: function() {
              that.loading = true
              deleteAction(that.url.batchSubmit, {
                ids: ids
              }).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadData()
                  that.selectionRows = []
                  that.selectedRowKeys = []
                } else {
                  that.$message.warning(res.message)
                }
              }).finally(() => {
                that.loading = false
              })
            }
          })
        }
      },
      handlePass(record) {
        this.$refs.modalForm.pass(record)
        this.$refs.modalForm.title = '审核证书类荣誉'
        this.$refs.modalForm.disableSubmit = false
      },
      importXls() {
        this.$refs.cerExcelModal.add()
        this.$refs.cerExcelModal.title = '导入证书类荣誉'
        this.$refs.cerExcelModal.disableSubmit = false
      },
      showInfo(record) {
        this.$refs.modalForm.showInfo(record)
        this.$refs.modalForm.title = '查看荣誉详情'
        this.$refs.modalForm.disableSubmit = false
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
    margin-left: -20px;
    margin-right: -20px;
  }

  .table-page-search-wrapper {
    margin-top: -16px;
    margin-bottom: 16px;
  }
</style>
