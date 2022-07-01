<template>
  <a-card :bordered="false">
    <!-- 抽屉 -->
    <a-drawer title="权限列表" :width="screenWidth" @close="onClose" :visible="visible">
      <!-- 抽屉内容的border -->
      <div
        :style="{
          padding:'10px',
          border: '1px solid #e9e9e9',
          background: '#fff',
        }">

        <div class="table-page-search-wrapper">

          <a-button style="margin-bottom: 10px" type="primary" @click="handleAdd">新增</a-button>
        </div>
        <div>
          <a-table
            ref="table"
            rowKey="id"
            size="middle"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            @change="handleTableChange">

            <template slot="honourTypeTags" slot-scope="text">
              <template v-if="text == 1">
                <font>文件类</font>
              </template>
              <template v-else-if="text == 2">
                <font>证书类</font>
              </template>
              <template v-else-if="text == 3">
                <font>物图类</font>
              </template>
              <template v-else-if="text == 4">
                <font>报道类</font>
              </template>
              <template v-else-if="text == 5">
                <font>协议类</font>
              </template>
              <template v-else-if="text == 6">
                <font>人才类</font>
              </template>
              <template v-else-if="text == 7">
                <font>课题类</font>
              </template>
            </template>

            <span slot="auths" slot-scope="text, record">

              <template v-if="record.showAuth == 0">
                <a-tag color="#CCCCCC">显示</a-tag>
              </template>
              <template v-else-if="record.showAuth == 1">
                <a-tag color="blue">显示</a-tag>
              </template>
              <template v-else>
                <a-tooltip placement="top">
                  <template slot="title">
                    <span>到期时间:{{ record.showDeadline }}</span>
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
                    <span>到期时间:{{ record.editDeadline }}</span>
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
                    <span>到期时间:{{ record.downloadDeadline }}</span>
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
                    <span>到期时间:{{ record.exportDeadline }}</span>
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
                    <span>到期时间:{{ record.examineDeadline }}</span>
                  </template>
                  <a-tag color="orange">审核:限时</a-tag>
                </a-tooltip>
              </template>

            </span>

            <span slot="action" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
              </a-popconfirm>
            </span>
            <template slot="status" slot-scope="text">
              <template v-if="text == 1">
                <font>正常</font>
              </template>
              <template v-else>
                <font color="red">禁用</font>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </a-drawer>
    <auth-modal ref="modalForm" @ok="modalFormOk"></auth-modal> <!-- 字典数据 -->
  </a-card>
</template>

<script>
  import pick from 'lodash.pick'
  import {
    filterObj
  } from '@/utils/util'
  import {
    getAction,
    deleteAction
  } from '@/api/manage'
  import AuthModal from './modules/AuthModal'
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'

  export default {
    name: 'DictItemList',
    mixins: [JeecgListMixin],
    components: {
      AuthModal
    },
    data() {
      return {
        columns: [{
            title: '荣誉类型',
            align: 'center',
            dataIndex: 'honourType',
            scopedSlots: {
              customRender: 'honourTypeTags'
            }
          },
          {
            title: '拥有权限',
            align: 'center',
            dataIndex: 'auths',
            scopedSlots: {
              customRender: 'auths'
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
        queryParam: {},
        title: '操作',
        visible: false,
        screenWidth: 800,
        model: {},
        userId: '',
        status: 1,
        labelCol: {
          xs: {
            span: 5
          },
          sm: {
            span: 5
          }
        },
        wrapperCol: {
          xs: {
            span: 12
          },
          sm: {
            span: 12
          }
        },
        url: {
          list: '/honour/auth/queryHonourAuthItemList',
          delete: '/honour/auth/deleteItem'
        }
      }
    },
    created() {
      // 当页面初始化时,根据屏幕大小来给抽屉设置宽度
      this.resetScreenSize()
    },
    methods: {
      add(authId) {
        this.queryParam.authId = authId
        this.visible = true
        this.edit({})
      },
      edit() {
        this.loadData()
      },
      // 添加字典数据
      handleAdd() {
        var types = [0, 0, 0, 0, 0, 0, 0]
        for (var i = 0; i < this.dataSource.length; i++) {
          var auth = this.dataSource[i]
          types[auth.honourType] = 1
        }
        this.$refs.modalForm.add(this.queryParam.authId, types)
        this.$refs.modalForm.title = '新增权限'
      },
      handleEdit: function(record) {
        var types = [0, 0, 0, 0, 0, 0, 0]
        for (var i = 0; i < this.dataSource.length; i++) {
          var auth = this.dataSource[i]
          types[auth.honourType] = 1
        }
        this.$refs.modalForm.edit(record, types)
        this.$refs.modalForm.title = '编辑权限'
        this.$refs.modalForm.disableSubmit = false
      },
      onClose() {
        this.visible = false
        this.$emit('ok')
      },
      // 抽屉的宽度随着屏幕大小来改变
      resetScreenSize() {
        let screenWidth = document.body.clientWidth
        if (screenWidth < 600) {
          this.screenWidth = screenWidth
        } else {
          this.screenWidth = 600
        }
      }
    }
  }
</script>
<style scoped>
</style>
