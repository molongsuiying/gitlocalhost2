<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">

      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">

            <a-col :md="8" :sm="24">
              <a-form-item :label="compLabel">
                <!-- <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input> -->
                <jeecg-select fields="title" tableId="1" v-model="queryParam.title" placeholder="请输入标题"></jeecg-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="发文字号">
                <!-- <a-input placeholder="请输入发文字号" v-model="queryParam.articleNum"></a-input> -->
                <jeecg-select fields="article_num" tableId="1" v-model="queryParam.articleNum" placeholder="请输入发文字号"></jeecg-select>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24">
              <a-form-item label="归属专业">
                <jtree-select-major v-model="queryParam.majorId" placeholder="请选择归属专业"></jtree-select-major>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24">
              <a-form-item label=" 负   责  人 ">
                <!-- <a-input placeholder="请输入发文字号" v-model="queryParam.articleNum"></a-input> -->
                <jeecg-select fields="leader" tableId="1" v-model="queryParam.leader" placeholder="请输入负责人"></jeecg-select>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24">
              <a-form-item label="团队成员">
                <!-- <a-input placeholder="请输入发文字号" v-model="queryParam.articleNum"></a-input> -->
                <jeecg-select fields="team_persons" tableId="1" v-model="queryParam.teamPersons" placeholder="请输入团队成员"></jeecg-select>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24">
              <a-form-item label="作品名称">
                <!-- <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input> -->
                <jeecg-select fields="work_name" tableId="1" v-model="queryParam.workName" placeholder="请输入作品名称"></jeecg-select>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24" style="margin-bottom: 10px;">
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

            <a-col :md="24" :sm="24">
              <a-form-item label="荣誉等级">
                <j-dict-select-tag v-model="queryParam.honourLevel" type="radio" dictCode="honour_level" />
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

            <a-col :md="16" :sm="24">
              <a-form-item label="发文机关">
                <j-dict-select-tag v-model="queryParam.authorityType" type="radio" dictCode="certificate_authority" />
              </a-form-item>
            </a-col>

          </a-row>
          <a-row :gutter="24">

            <a-col :md="24" :sm="24">
              <a-form-item label="成果类别">
                <j-dict-select-tag v-model="queryParam.achievementType" type="radio" dictCode="achievement_type" />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <j-dict-select-tag v-model="queryParam.achievementLevel" type="radio" dictCode="achievement_level" />
              </a-form-item>
            </a-col>

          </a-row>
          <a-row :gutter="24">

            <a-col :md="8" :sm="24">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                <template
                  v-if="honourAuth.exportAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.exportDeadline) < 0">
                  <a-button type="primary" @click="exportXls" icon="download" style="margin-left: 8px">导出EXCEL
                  </a-button>
                </template>
                <a-dropdown v-if="selectedRowKeys.length > 0">
                  <a-menu slot="overlay">
                    <!-- <a-menu-item key="1" @click="batchDel">
                      <a-icon type="delete" />
                      批量删除
                    </a-menu-item>
                    <a-menu-item key="2" @click="batchSubmit">
                      <a-icon type="upload" />
                      提交审核
                    </a-menu-item> -->
                    <template
                      v-if="honourAuth.downloadAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.downloadDeadline) < 0">
                      <a-menu-item key="3" @click="batchDownLoadAppendixs">
                        <a-icon type="download" />
                        下载附件
                      </a-menu-item>
                    </template>

                    <template
                      v-if="honourAuth.exportAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.exportDeadline) < 0">
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
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <!-- 操作按钮区域 -->
      <div class="table-operator">

      </div>

      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <span style="float:right;">
          <a-popover title="自定义列" trigger="click" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group @change="onColSettingsChange" v-model="settingColumns" :defaultValue="settingColumns">
                <a-row>
                  <template v-for="item in reportDefColumns">
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
          <template
            v-if="honourAuth.downloadAuth > 0 && moment(honourAuth.currentTime) - moment(honourAuth.downloadDeadline) < 0">
            <a @click="download(record)">下载附件</a>
          </template>
          <template v-else>
            <a-tooltip>
              <template slot="title">
                <font color="white">权限已到期或无权下载</font>
              </template>
              <font style="cursor: not-allowed;">下载附件</font>
            </a-tooltip>
          </template>
        </span>

      </a-table>
      </div>
    </a-spin>

  </a-modal>
</template>

<script>
  import JSZip from 'jszip'
  import FileSaver from 'file-saver'
  import Vue from 'vue'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import moment from 'moment'
  import {
    getAction,
    postAction,
    getFileAccessHttpUrl,
    deleteAction,
    downFile
  } from '@/api/manage'
  import {
    initDictOptions
  } from '@/components/dict/JDictSelectUtil'
  import JtreeSelectMajor from '../modules/JtreeSelectMajor'
  import JeecgSelect from '../modules/JeecgSelect'
  export default {
    name: 'HonourPersonModal',
    mixins: [JeecgListMixin],
    components: {
      JtreeSelectMajor,
      JeecgSelect
    },
    data() {
      return {
        title: '操作',
        major: '',
        compLabel: '标\xa0\xa0\xa0\xa0\xa0\xa0\xa0题',
        confirmLoading: false,
        visible: false,
        statusType: [],
        queryParam: {},
        disableMixinCreated: true,
        selectedRowKeys: [],
        description: '人才类荣誉',
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
            dataIndex: 'authorityType_dictText',
            scopedSlots: {
              customRender: 'authority'
            }
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText',
            customRender: function(t, r, index) {
              if (r.achievementType === 99) {
                return r.achievementType_dictText + '(' + r.achievementTypeTxt + ')'
              } else {
                return r.achievementType_dictText
              }
            }
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText',
            customRender: function(t, r, index) {
              if (r.achievementLevelType === 99) {
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
          list: '/honour/file/queryHonourFileList',
          getAppendix: '/honour/findAppendixByIds',
          batchSubmit: '/honour/file/batchSubmit',
          submit: '/honour/file/submitFile',
          saveHistory: '/honour/saveHistoriesByIds'
        }
      }
    },
    created() {
      this.initDictConfig()
      this.initColumns()
    },
    methods: {
      addAuth(honourAuth) {
        this.honourAuth = honourAuth
      },
      add(queryParam) {
        this.queryParam = {}
        console.log(this.queryParam)
        this.queryParam = queryParam
        if (this.queryParam.status != null) {
          this.statusType = []
          this.statusType.push(parseInt(this.queryParam.status))
        }
        this.visible = true
        this.loadData()
      },
      moment,
      getAppendix(appendixIds) {
        var that = this
        var params = {
          appendixIds: appendixIds
        }
        this.fileList = []
        getAction(this.url.getAppendix, params).then((res) => {
          if (res.success) {
            var appendixs = res.result

            for (var i = 0; i < appendixs.length; i++) {
              var temp = {
                path: appendixs[i].fileUrl,
                appendixId: appendixs[i].id,
                type: appendixs[i].fileType
              }
              this.fileList.push(temp)
            }
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      searchReset() {
        this.queryParam = {}
        this.queryParam.majorId = null
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
      initDictConfig() {
        // 初始化字典 - 性别
        initDictOptions('certificate_type').then((res) => {
          if (res.success) {
            this.cerDictOptions = res.result
          }
        })
      },
      onLoadMajor(txt, label) {

      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
        console.log(this.selectionRows)
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
      download(record) {
        this.downloadAppendix(record.appendixIds)
        var vo = {
          appendixIds: record.appendixIds,
          title: record.title,
          honourClass: 1
        }
        var formData = []
        formData.push(vo)
        this.saveHistory(formData)
      },
      batchDownLoadAppendixs() {
        if (this.selectionRows.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          var ids = ''
          var formData = []
          for (var a = 0; a < this.selectionRows.length; a++) {
            ids += this.selectionRows[a].appendixIds + ','
            var vo = {
              appendixIds: this.selectionRows[a].appendixIds,
              title: this.selectionRows[a].title,
              honourClass: 1
            }
            formData.push(vo)
          }
          var that = this
          console.log(ids)
          that.downloadAppendix(ids)
          that.saveHistory(formData)
        }
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
            var fileName = '人才荣誉表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      saveHistory(formData) {
        postAction(this.url.saveHistory, formData).then((res) => {})
      },
      downloadAppendix: function(ids) {
        var params = {
          appendixIds: ids
        }
        var that = this
        getAction(this.url.getAppendix, params).then((res) => {
          if (res.success) {
            var appendixs = res.result

            var data = []
            for (var i = 0; i < appendixs.length; i++) {
              var url = getFileAccessHttpUrl(appendixs[i].fileUrl)

              data.push(url)
            }

            const zip = new JSZip()
            const cache = {}
            const promises = []
            data.forEach(item => {
              const promise = downFile(item, {}).then(data => { // 下载文件, 并存成ArrayBuffer对象
                var arr_name = item.split('/')
                var file_name = arr_name[arr_name.length - 1] // 获取文件名
                zip.file(file_name, data, {
                  binary: true
                }) // 逐个添加文件
                cache[file_name] = data
              })
              promises.push(promise)
            })

            Promise.all(promises).then(() => {
              zip.generateAsync({
                type: 'blob'
              }).then(content => { // 生成二进制流
                FileSaver.saveAs(content, '附件.zip') // 利用file-saver保存文件
              })
            })
          } else {
            that.$message.warning(res.message)
          }
        })
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

      onChange(e) {
        if (e.target.value == '99') {
          this.txtFlag = true
        } else {
          this.txtFlag = false
        }
      },
      close() {
        this.queryParam = {}
        this.statusType = []
        this.$emit('close')
        this.visible = false
      },
      handleCancel() {
        this.close()
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

        if (!params.beginTime) {
          params.beginTime = ''
        } else {
          params.beginTime = params.beginTime.format('YYYY-MM-DD')
        }
        if (!params.endTime) {
          params.endTime = ''
        } else {
          params.endTime = params.endTime.format('YYYY-MM-DD')
        }

        getAction(this.url.list, params).then((res) => {
          if (res.success && res.result) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total

            var personNum = 0

            if (this.ipagination.total !== 0) {
              personNum = 1
            }

            this.$emit('personFn', personNum)
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>

</style>
