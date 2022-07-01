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
      <div style="margin-bottom: 10px;">
        <a-alert
          v-if="visible"
          message="温馨提示:请先下载荣誉类模板后不要随意更改模板信息,再进行相关数据导入,以防出错"
          type="warning"
          closable
          banner
          style />
      </div>
      <!-- 操作按钮区域 -->
      <div class="table-operator">

        <a-button type="primary" @click="exportXlsTemp" icon="downLoad" style="margin-right: 8px">下载EXCEL模板</a-button>
        <a-upload
          name="file"
          :showUploadList="false"
          :multiple="false"
          :headers="tokenHeader"
          :action="importExcelUrl"
          @change="handleImportExcel">
          <a-button type="primary" icon="import">导入EXCEL</a-button>
        </a-upload>
      </div>

      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;margin-top: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
          selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空勾选</a>
        <a style="margin-left: 24px" @click="reData">复原数据</a>
        <a style="margin-left: 24px" @click="clearData">清空数据</a>
      </div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :data-source="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="action" slot-scope="text, record">
          <div class="editable-row-operations">
            <a class="delete" @click="() => del(record)">删除</a>
          </div>
        </template>

      </a-table>
      </div>

    </a-spin>

    <template slot="footer">
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
      <a-button key="save" type="primary" :loading="confirmLoading" @click="handleSave">
        仅保存
      </a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
        上传提交
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
    httpAction
  } from '@/api/manage'
  import JtreeSelectMajor from '../modules/JtreeSelectMajor'

  export default {
    name: 'FileExcelModal',
    mixins: [JeecgListMixin],
    components: {
      JtreeSelectMajor
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
        selectedRowKeys: [],
        editingKey: false,
        description: '文件类荣誉',
        settingColumns: [],
        yearShowOne: false,
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
            title: '合作类别',
            align: 'center',
            dataIndex: 'cooperationType'
          },
          {
            title: '合作单位',
            align: 'center',
            dataIndex: 'cooperationUnit'
          },
          {
            title: '签约时间',
            align: 'center',
            dataIndex: 'acquireTime'
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
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            scopedSlots: {
              customRender: 'action'
            }
          }
        ],
        url: {
          importExcelUrl: '/honour/agree/importExcel',
          saveListByExcel: '/honour/agree/saveListByExcel'
        }
      }
    },
    created() {

    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      add() {
        this.visible = true
      },
      moment,

      searchReset() {

      },
      searchQuery() {

      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
        console.log(this.selectionRows)
      },
      handleSave() {
        var that = this
        if (this.selectionRows.length == 0) {
          this.$message.warning('请选择数据!')
          return false
        }
        that.confirmLoading = true
        let httpurl = this.url.saveListByExcel
        let method = 'post'

        let formData = Object.assign(this.selectionRows, {})
        httpAction(httpurl, formData, method).then((res) => {
          if (res.success) {
            that.dataSource = that.arrChange(that.dataSource, that.selectionRows)
            that.selectionRows = []
            that.selectedRowKeys = []
            that.$message.success(res.message)
            that.$emit('exportDownSearch')
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          that.confirmLoading = false
          // that.close()
        })
      },
      clearData() {
        this.dataSource = []
        this.cacheData = []
        this.selectionRows = []
        this.selectedValue = []
      },
      handleOk() {
        if (this.selectionRows.length == 0) {
          this.$message.warning('请选择数据!')
          return false
        }
        for (var i = 0; i < this.selectionRows.length; i++) {
          this.selectionRows[i].status = 1
        }
        this.handleSave()
      },
      arrChange(a, b) {
         for (var i = 0; i < b.length; i++) {
          for (var j = 0; j < a.length; j++) {
           if (a[ j ].id == b[ i ].id) {
            a.splice(j, 1)
            j = j - 1
           }
          }
         }
         return a
      },
      close() {
        this.dataSource = []
        this.$emit('exportDownSearch')
        this.visible = false
      },
      handleCancel() {
        this.close()
      },
      loadData(arg) {},
      handleImportExcel(info) {
        if (info.file.status !== 'uploading') {
          console.log(info.file, info.fileList)
        }
        if (info.file.status === 'done') {
          if (info.file.response.success) {
            var data = info.file.response.result
            for (var i = 0; i < data.length; i++) {
              data[i].id = Math.random().toString(36).substr(-10)
              this.dataSource = data
              this.cacheData = data
            }
            this.loadData()
          } else {
            this.$message.error(`${info.file.name} ${info.file.response.message}.`)
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`文件上传失败: ${info.file.msg} `)
        }
      },
      exportXlsTemp() {
        var url = getFileAccessHttpUrl('excel/temp/课题类荣誉模板.xlsx')
        var fileName = '课题类荣誉模板'
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', fileName)
        document.body.appendChild(link)
        link.click()
      },
      handleChange (value, key, column) {
        console.log(value, key, column)
      },
      edit (row) {

      },
      // eslint-disable-next-line
      del (row) {
        var that = this
        this.$confirm({
          title: '警告',
          content: '真的要删除吗?',
          okText: '删除',
          okType: 'danger',
          cancelText: '取消',
          onOk() {
            console.log('OK')
            // 在这里调用删除接口
            that.dataSource = that.dataSource.filter(d => d.id != row.id)
            return new Promise((resolve, reject) => {
              setTimeout(Math.random() > 0.5 ? resolve : reject, 100)
            }).catch(() => console.log('Oops errors!'))
          },
          onCancel() {
            console.log('Cancel')
          }
        })
      },
      reData() {
        this.dataSource = this.cacheData
      }
    }
  }
</script>

<style lang="less" scoped>

</style>
