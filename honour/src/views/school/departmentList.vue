<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="学院名称">
              <a-select
                show-search
                v-model="queryParam.collegeId"
                placeholder="请输入学院名称"
                option-filter-prop="children"
                :filter-option="filterOption"
                @change="handleChange">
                <a-select-option v-for="c in collegeList" :key="c.id">
                  {{ c.text }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="院系名称">
              <a-input placeholder="请输入院系名称" v-model="queryParam.departmentName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="院系编号">
              <a-input placeholder="请输入院系编号" v-model="queryParam.departmentCode"></a-input>
            </a-form-item>
          </a-col>

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
      <a-button @click="handleAdd" type="primary" icon="plus">新增院系</a-button>
      <!-- <a-button type="primary" icon="download" @click="handleExportXls("职务表")">导出</a-button>
      <a-upload
        name="file"
        :showUploadList="false"
        :multiple="false"
        :headers="tokenHeader"
        :action="importExcelUrl"
        @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload> -->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />
            删除
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
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
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

        <span slot="tags" slot-scope="majorNames">
          <a-tag
            v-for="tag in majorNames"
            :key="tag"
          >
            {{ tag }}
          </a-tag>
        </span>
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a @click="editMajorList(record)"><a-icon type="setting"/> 专业配置</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>

        </span>

      </a-table>
    </div>

    <!-- table区域-end -->

    <!-- 表单区域 -->
    <department-modal ref="modalForm" @ok="modalFormOk"></department-modal>
    <major-list-modal ref="majorList"></major-list-modal>
  </a-card>
</template>

<script>
  import DepartmentModal from "./modules/DepartmentModal"
  import MajorListModal from "./majorList"
  import {
    JeecgListMixin
  } from "@/mixins/JeecgListMixin"
  import {
    getAction
  } from "@/api/manage"
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  export default {
    name: "DepartmentList",
    mixins: [JeecgListMixin],
    components: {
      DepartmentModal,
      MajorListModal,
      JDictSelectTag
    },
    data() {
      return {
        description: "院系专业",
        fileList: [],
        maxPicNum: 10,
        isMultiple: true,
        uploading: false,
        previewVisible: false,
        previewImage: "",
        // 表头
        columns: [{
            title: "#",
            dataIndex: "",
            key: "rowIndex",
            width: 60,
            align: "center",
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: "学院名称",
            align: "center",
            dataIndex: "collegeName"
          },
          {
            title: "院系名称",
            align: "center",
            dataIndex: "departmentName"
          },
          {
            title: "院校编号",
            align: "center",
            dataIndex: "departmentCode"
          },
          {
            title: "成立日期",
            align: "center",
            dataIndex: "buildTime"
          },
          {
            title: "院系介绍",
            align: "center",
            width: 200,
            dataIndex: "describe"
          },
          {
            title: "下属专业",
            align: "center",
            dataIndex: "majorNames",
            scopedSlots: { customRender: "tags" }
          },
          {
            title: "操作",
            dataIndex: "action",
            align: "center",
            scopedSlots: {
              customRender: "action"
            }
          }
        ],
        url: {
          list: "/department/list",
          delete: "/department/delete",
          deleteBatch: "/department/deleteBatch",
          getMyCollegeNames: "/college/queryCollegeNames"
        },
        queryParam: {
          collegeId: "",
          departmentCode: "",
          departmentName: ""
        },
        collegeList: []
      }
    },
    created() {
      var query = this.$route.query

      if (query.hasOwnProperty("collegeId")) {
        this.queryParam.collegeId = query.collegeId
      }
      this.initCollege()
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG["domianURL"]}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      initCollege() {
        getAction(this.url.getMyCollegeNames, {}).then(res => {
          if (res.success) {
            console.log(res.result)
            this.collegeList = res.result
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      handleChange(value) {
        console.log(`selected ${value}`)
        this.loadData()
      },
      filterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error("请设置url.list属性!")
          return
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        var params = this.getQueryParams()
        
        //var params = this.queryParam// 查询条件
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
      handleAdd: function () {
        this.$refs.modalForm.add(this.collegeList, this.queryParam.collegeId)
        this.$refs.modalForm.title = "新增院系"
        this.$refs.modalForm.disableSubmit = false
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(this.collegeList, record)
        this.$refs.modalForm.title = "编辑院系"
        this.$refs.modalForm.disableSubmit = false
      },
      editMajorList(record) {
        this.$refs.majorList.edit(record)
      }

    }
  }
</script>
<style scoped>
  @import "~@assets/less/common.less"
</style>
