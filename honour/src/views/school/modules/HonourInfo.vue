<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper" style="margin-bottom: 0px;">
      <!-- 搜索区域 -->
      <a-form layout="inline">
        <a-row :gutter="10">
          <a-col :md="8" :sm="12">
            <a-form-item label="姓名/学号" style="margin-left:8px">
              <a-input placeholder="请输入姓名/学号" v-model="queryParam.keyword"></a-input>
            </a-form-item>

          </a-col>
          <a-col :md="8" :sm="12">
            <a-form-item label="入学年份" style="margin-left:8px">
              <a-date-picker
                mode="year"
                allowClear
                style="width: 100%"
                placeholder="请选择入学年份"
                format="YYYY"
                :open="yearShowOne"
                @openChange="openChangeOne"
                @panelChange="panelChangeOne"
                v-model="queryParam.year" />
            </a-form-item>

          </a-col>

          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
            <a-col :md="6" :sm="24">
              <a-button type="primary" @click="searchQuery" icon="search" style="margin-left: 18px">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </a-col>
          </span>
        </a-row>
      </a-form>
    </div>
    <!-- 操作按钮区域 -->
    <div class="table-operator" :md="24" :sm="24">
      <a-button @click="handleAdd" type="primary" icon="plus">导入荣誉</a-button>
      <a-button @click="handleAdd" type="primary" icon="import">批量导入</a-button>
      <a-button @click="handleAdd" type="primary" icon="download">导出荣誉</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />
            批量删除
          </a-menu-item>
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
                  <template v-for="(item) in defColumns">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12"><a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox></a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a><a-icon type="setting" />设置</a>
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
            <font color="orange">审核中</font>
          </template>
          <template v-else-if="text == 1">
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
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />

          <a-dropdown>
            <a class="ant-dropdown-link">
              更多
              <a-icon type="down" />
            </a>
            <a-menu slot="overlay">

              <a-menu-item>
                <a href="javascript:;" @click="handleDetail(record)">详情</a>
              </a-menu-item>

            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <student-modal ref="modalForm" @ok="modalFormOk"></student-modal>
  </a-card>
</template>

<script>
  import StudentModal from './StudentModal'
  import moment from 'moment'
    import Vue from 'vue'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    getAction,
    postAction,
    deleteAction
  } from '@/api/manage'

  export default {
    name: 'BunchStudentInfo',
    mixins: [JeecgListMixin],
    components: {
      StudentModal
    },
    data() {
      return {
        format: 'YYYY',
        selectedRowKeys: [],
        description: '用户信息',
        currentBunchId: '',
        currentBunch: {},
        settingColumns: [],
        yearShowOne: false,
        defColumns: [{
            title: '学院',
            align: 'center',
            dataIndex: 'collegeName'
          },
          {
            title: '院系',
            align: 'center',
            dataIndex: 'departmentName'
          }, {
            title: '专业',
            align: 'center',
            dataIndex: 'majorName'
          },
          {
            title: '班级',
            align: 'center',
            dataIndex: 'bunchName'
          }, {
            title: '学生名称',
            align: 'center',
            dataIndex: 'studentName'
          }, {
            title: '荣誉名称',
            align: 'center',
            dataIndex: 'honourName'
          },
          {
            title: '荣誉类型',
            align: 'center',
            dataIndex: 'honourType_dictText'
          },
          {
            title: '获奖日期',
            align: 'center',
            dataIndex: 'acquireTime'
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
            scopedSlots: {
              customRender: 'action'
            },
            align: 'center',
            width: 150
          }
        ],
        // 表头
        columns: [{
            title: '学生名称',
            align: 'center',
            dataIndex: 'studentName'
          }, {
            title: '荣誉名称',
            align: 'center',
            dataIndex: 'honourName'
          },
          {
            title: '荣誉类型',
            align: 'center',
            dataIndex: 'honourType_dictText'
          },
          {
            title: '获奖日期',
            align: 'center',
            dataIndex: 'acquireTime'
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
            scopedSlots: {
              customRender: 'action'
            },
            align: 'center',
            width: 150
          }
        ],
        columnsExtr: [{
            title: '学院',
            align: 'center',
            dataIndex: 'collegeName'
          },
          {
            title: '院系',
            align: 'center',
            dataIndex: 'departmentName'
          }, {
            title: '专业',
            align: 'center',
            dataIndex: 'majorName'
          },
          {
            title: '班级',
            align: 'center',
            dataIndex: 'bunchName'
          }
        ],
        url: {
          list: '/honour/queryHonourFileList',
          edit: '/student/edit'
        }
      }
    },
    created() {
      this.initColumns()
    },
    methods: {
      searchReset() {
        this.queryParam = {}
        this.loadData(1)
      },
      moment,
      initData() {
        this.columns = [{
            title: '学生名称',
            align: 'center',
            dataIndex: 'studentName'
          }, {
            title: '荣誉名称',
            align: 'center',
            dataIndex: 'honourName'
          },
          {
            title: '荣誉类型',
            align: 'center',
            dataIndex: 'honourType_dictText'
          },
          {
            title: '获奖日期',
            align: 'center',
            dataIndex: 'acquireTime'
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
            scopedSlots: {
              customRender: 'action'
            },
            align: 'center',
            width: 150
          }
        ]
        if (JSON.stringify(this.currentBunch) != '{}') {
          var level = this.currentBunch.level
          var id = this.currentBunch.key
          var arr = ['collegeName', 'departmentName', 'majorName', 'bunchName']
          var arr2 = []
          if (level === 0) {
            this.queryParam.collegeId = id
            this.columns.splice(0, 0, this.columnsExtr[1], this.columnsExtr[2], this.columnsExtr[3])
            arr2 = arr.slice(1, 4)
            arr = arr.slice(0, 1)
          } else if (level === 1) {
            this.queryParam.departmentId = id
            this.columns.splice(0, 0, this.columnsExtr[2], this.columnsExtr[3])
            arr2 = arr.slice(2, 4)
            arr = arr.slice(0, 2)
          } else if (level === 2) {
            this.queryParam.majorId = id
            this.columns.splice(0, 0, this.columnsExtr[3])
            arr2 = arr.slice(3, 4)
            arr = arr.slice(0, 3)
          } else if (level === 3) {
            this.queryParam.bunchId = id
          }
          this.settingColumns = this.settingColumns.filter(function (val) { return arr.indexOf(val) === -1 })
          this.settingColumns.push(...arr2)
          this.settingColumns = [...new Set(this.settingColumns)]
        } else {
          this.columns = this.columnsExtr.concat(this.columns)
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
        this.initData()

        let params = this.getQueryParams() // 查询条件
        if (!params.year) {
          params.year = ''
        } else {
          params.year = params.year.format('YYYY')
        }
        getAction(this.url.list, params).then((res) => {
          if (res.success && res.result) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          }
        })
      },
      batchDel: function() {
        if (!this.url.deleteBatch) {
          this.$message.error('请设置url.deleteBatch属性!')
          return
        }
        if (!this.currentBunchId) {
          this.$message.error('未选中任何部门，无法取消部门与用户的关联!')
          return
        }

        if (this.selectedRowKeys.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          var ids = ''
          for (var a = 0; a < this.selectedRowKeys.length; a++) {
            ids += this.selectedRowKeys[a] + ','
          }
          var that = this
          console.log(this.currentBunchId)
          this.$confirm({
            title: '确认取消',
            content: '是否取消用户与选中部门的关联?',
            onOk: function() {
              deleteAction(that.url.deleteBatch, {
                depId: that.currentBunchId,
                userIds: ids
              }).then((res) => {
                if (res.success) {
                  that.$message.success('删除用户与选中部门关系成功！')
                  that.loadData()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
            }
          })
        }
      },
      handleDelete: function(id) {
        if (!this.url.delete) {
          this.$message.error('请设置url.delete属性!')
          return
        }
        if (!this.currentBunchId) {
          this.$message.error('未选中任何部门，无法取消部门与用户的关联!')
          return
        }

        var that = this
        deleteAction(that.url.delete, {
          depId: this.currentBunchId,
          userId: id
        }).then((res) => {
          if (res.success) {
            that.$message.success('删除用户与选中部门关系成功！')
            if (this.selectedRowKeys.length > 0) {
              for (let i = 0; i < this.selectedRowKeys.length; i++) {
                if (this.selectedRowKeys[i] == id) {
                  this.selectedRowKeys.splice(i, 1)
                  break
                }
              }
            }
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      onColSettingsChange (checkedValues) {
        var key = this.$route.name + ':colsettings'
        Vue.ls.set(key, checkedValues, 7 * 24 * 60 * 60 * 1000)

        this.settingColumns = checkedValues

        const cols = this.defColumns.filter(item => {
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

        var key = this.$route.name + ':colsettings'
        let colSettings = Vue.ls.get(key)
        if (colSettings == null || colSettings == undefined) {
          let allSettingColumns = []
          this.defColumns.forEach(function (item, i, array) {
            allSettingColumns.push(item.dataIndex)
          })
          this.settingColumns = allSettingColumns
          this.columns = this.defColumns
        } else {
          this.settingColumns = colSettings
          const cols = this.defColumns.filter(item => {
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
        this.currentBunch = record
        this.queryParam = {}
        this.loadData(1)
      },
      clearList() {
        this.currentBunchId = ''
        this.dataSource = []
      },
      hasSelectDept() {
        if (this.currentBunchId == '') {
          this.$message.error('请选择一个部门!')
          return false
        }
        return true
      },
      handleEdit: function(record) {
        this.$refs.modalForm.title = '编辑学生'
        this.$refs.modalForm.disableSubmit = false
        this.$refs.modalForm.edit(record)
      },
      handleAdd: function() {
        console.log(this.currentBunch)
        if (this.currentBunch.level !== 3) {
          this.$message.error('请选择一个班级!')
        } else {
          this.$refs.modalForm.add(this.currentBunch)
          this.$refs.modalForm.title = '新增学生'
        }
      },
      selectOK(data) {
        let params = {}
        params.userIdList = []
        for (var a = 0; a < data.length; a++) {
          params.userIdList.push(data[a])
        }
        console.log(params)
        postAction(this.url.edit, params).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.loadData()
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      openChangeOne(status) {
        // status是打开或关闭的状态
        if (status) {
          this.yearShowOne = true
        }
      },
      // 得到年份选择器的值
      panelChangeOne(value) {
        this.queryParam.year = value
        this.yearShowOne = false
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
