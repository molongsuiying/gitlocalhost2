<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="用户账号">
              <a-input placeholder="请输入用户账号" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="用户姓名">
              <a-input placeholder="请输入用户姓名" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="8">
            <a-form-item label="账号权限">
              <j-dict-select-tag v-model="queryParam.authType" placeholder="请选择账号权限" dictCode="honour_auth" />
              
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
      <a-button @click="handleAdd" type="primary" icon="plus">添加权限</a-button>
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

        <span slot="tags" slot-scope="text, record">

          <template v-if="record.showAuth == 0">
            <a-tag color="#CCCCCC">显示</a-tag>
          </template>
          <template v-else-if="record.showAuth == 1">
            <a-tag color="blue">显示</a-tag>
          </template>
          <template v-else>           
            <a-tooltip placement="top">
              <template slot="title">
                <span>到期时间:{{record.showDeadline}}</span>
              </template>
              <a-tag color="orange">显示:限时</a-tag>
            </a-tooltip> 
          </template>

          <template v-if="record.editAuth == 0">
            <a-tag color="#CCCCCC">编辑</a-tag>
          </template>
          <template v-else-if="record.editAuth == 1">
            <a-tag color="blue">编辑</a-tag>
          </template>
          <template v-else>
            
            <a-tooltip placement="top">
              <template slot="title">
                <span>到期时间:{{record.editDeadline}}</span>
              </template>
              <a-tag color="orange">编辑:限时</a-tag>
            </a-tooltip>  
          </template>

          <template v-if="record.downloadAuth == 0">
            <a-tag color="#CCCCCC">附件</a-tag>
          </template>
          <template v-else-if="record.downloadAuth == 1">
            <a-tag color="blue">附件</a-tag>
          </template>
          <template v-else>
            
            <a-tooltip placement="top">
              <template slot="title">
                <span>到期时间:{{record.downloadDeadline}}</span>
              </template>
              <a-tag color="orange">附件:限时</a-tag>
            </a-tooltip>  
          </template>

          <template v-if="record.exportAuth == 0">
            <a-tag color="#CCCCCC">导出</a-tag>
          </template>
          <template v-else-if="record.exportAuth == 1">
            <a-tag color="blue">导出</a-tag>
          </template>
          <template v-else>
            <a-tooltip placement="top">
              <template slot="title">
                <span>到期时间:{{record.exportDeadline}}</span>
              </template>
              <a-tag color="orange">导出:限时</a-tag>
            </a-tooltip>  
            
          </template>

          <template v-if="record.examineAuth == 0">
            <a-tag color="#CCCCCC">审核</a-tag>
          </template>
          <template v-else-if="record.examineAuth == 1">
            <a-tag color="blue">审核</a-tag>
          </template>
          <template v-else>
            <a-tooltip placement="top">
              <template slot="title">
                <span>到期时间:{{record.examineDeadline}}</span>
              </template>
              <a-tag color="orange">审核:限时</a-tag>
            </a-tooltip>   
          </template>

          <!-- <template v-if="record.showAuth == 0">
            <a-tag color="#CCCCCC">显示</a-tag>
          </template>
          <template v-else>
            <a-tag color="blue">显示</a-tag>
          </template>

          <template v-if="record.editAuth == 0">
            <a-tag color="#CCCCCC">编辑</a-tag>
          </template>
          <template v-else>
            <a-tag color="blue">编辑</a-tag>
          </template>

          <template v-if="record.downloadAuth == 0">
            <a-tag color="#CCCCCC">附件</a-tag>
          </template>
          <template v-else>
            <a-tag color="blue">附件</a-tag>
          </template>

          <template v-if="record.exportAuth == 0">
            <a-tag color="#CCCCCC">导出</a-tag>
          </template>
          <template v-else>
            <a-tag color="blue">导出</a-tag>
          </template>

          <template v-if="record.examineAuth == 0">
            <a-tag color="#CCCCCC">审核</a-tag>
          </template>
          <template v-else>
            <a-tag color="blue">审核</a-tag>
          </template> -->

        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail(record)"> <a-icon type="setting"/>详细配置</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.userId)">
            <a>删除</a>
          </a-popconfirm>

        </span>

      </a-table>
    </div>

    <!-- table区域-end -->

    <!-- 表单区域 -->
    <honour-auth-modal ref="modalForm" @ok="modalFormOk"></honour-auth-modal>
    <honour-detail-list ref="detailList" @ok="modalFormOk"></honour-detail-list>
  </a-card>
</template>

<script>
  import HonourAuthModal from "./modules/HonourAuthModal"
  import HonourDetailList from "./honourDetailList"
  import {
    JeecgListMixin
  } from "@/mixins/JeecgListMixin"
  import {
    getAction,
    deleteAction
  } from "@/api/manage"
  export default {
    name: "HonourAuthList",
    mixins: [JeecgListMixin],
    components: {
      HonourAuthModal,
      HonourDetailList
    },
    data() {
      return {
        description: "院系专业",
        isMultiple: true,
        uploading: false,
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['5', '10', '20'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        // 表头
        columns: [
          {
            title: "用户账号",
            align: "center",
            dataIndex: "username"
          },
          {
            title: "用户姓名",
            align: "center",
            dataIndex: "realname"
          },
         {
            title: "账号权限",
            align: "center",
            dataIndex: "authType_dictText",
          },
/*          {
            title: "创建时间",
            align: "center",
            dataIndex: "createTime"
          }, */
          {
            title: "拥有权限",
            align: "center",
            dataIndex: "auths",
            scopedSlots: {
              customRender: "tags"
            }
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
          list: "/honour/auth/queryHonourAuthList",
          delete: "/honour/auth/deleteByUserId",
          deleteBatch: "/sys/position/deleteBatch",
          exportXlsUrl: "/sys/position/exportXls",
          importExcelUrl: "/sys/position/importExcel",
          getMyCollegeNames: "/college/queryCollegeNames"
        },
        queryParam: {
        },
        collegeList: []
      }
    },
    created() {

    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG["domianURL"]}/${this.url.importExcelUrl}`
      }
    },
    methods: {

      handleDetail(record) {
        this.$refs.detailList.add(record.id)
        this.$refs.detailList.title = "权限详情"
        this.$refs.detailList.disableSubmit = false
      },
      handleChange(value) {
        console.log(`selected ${value}`)
        this.loadData()
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
        var params = this.getQueryParams() // 查询条件
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
      handleAdd: function() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = "新增权限"
        this.$refs.modalForm.disableSubmit = false
      },
      handleEdit: function(record) {
        this.$refs.modalForm.edit(this.collegeList, record)
        this.$refs.modalForm.title = "编辑权限"
        this.$refs.modalForm.disableSubmit = false
      },
      handleDelete: function (userId) {
        if (!this.url.delete) {
          this.$message.error("请设置url.delete属性!")
          return
        }
        var that = this
        deleteAction(that.url.delete, { userId: userId }).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.loadData()
          } else {
            that.$message.warning(res.message)
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import "~@assets/less/common.less"
</style>
