<template>
  <div>
    <a-card style="margin-top: 24px" :bordered="false" title="院校列表">

      <div slot="extra">

        <a-input-search
          placeholder="请输入学院名称或院校代码"
          v-model="queryParam.keyWord"
          style="margin-left: 16px; width: 272px;"
          @search="onSearch" />
      </div>

      <div class="operate">
        <a-button type="dashed" style="width: 100%" icon="plus" @click="handleAdd">添加学院</a-button>
      </div>

      <a-list size="large" :pagination="ipagination">
        <a-list-item :key="index" v-for="(item, index) in dataSource">
          <a-list-item-meta :description="item.motto">
            <a-avatar slot="avatar" size="large" shape="square" :src="getAvatarView(item.icon)" />
            <a slot="title" @click="gotoMajor(item.id)">{{ item.collegeName }}
              <font v-if="item.englishName!=null">({{ item.englishName }})</font>
            </a>
          </a-list-item-meta>
          <div slot="actions">
            <a @click="gotoMajor(item.id)">进入</a>
          </div>
          <div slot="actions">
            <a @click="handleEdit(item)">详情</a>
          </div>
          <div slot="actions">
            <a-dropdown>
              <a-menu slot="overlay">
                <a-menu-item><a @click="handleEdit(item)">编辑</a></a-menu-item>
                <a-menu-item><a @click="handleDelete(item)">删除</a></a-menu-item>
              </a-menu>
              <a>更多
                <a-icon type="down" />
              </a>
            </a-dropdown>
          </div>
          <div class="list-content">
            <div class="list-content-item">
              <span>办学性质</span>
              <p>{{ item.natureOfRunning }}</p>
            </div>
            <div class="list-content-item">
              <span>学院代号</span>
              <p>{{ item.collegeCode }}</p>
            </div>

            <div class="list-content-item">
              <span>创办时间</span>
              <p>{{ item.buildTime }}</p>
            </div>
          </div>
        </a-list-item>
      </a-list>

      <college-modal ref="modalForm" @ok="modalFormOk"></college-modal>
    </a-card>
  </div>
</template>

<script>
  import CollegeModal from './modules/CollegeModal'
  import {
    getAction,
    getFileAccessHttpUrl,
    deleteAction
  } from '@/api/manage'

  export default {
    name: 'CollegeList',
    components: {
      CollegeModal
    },
    data() {
      return {
        dataSource: [],
        queryParam: {
          keyWord: ''
        },
        url: {
          list: '/college/list',
          findDepartments: '/department/getDepartmentsByCollegeId',
          delete: 'college/delete'
        },
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'id',
          order: 'asc'
        }
      }
    },
    mounted() {
      this.loadData()
    },
    methods: {
      getQueryParams() {

      },
      loadData() {
        var params = Object.assign(this.queryParam, this.isorter)
        params.pageNo = this.ipagination.current
        params.pageSize = this.ipagination.pageSize
        getAction(this.url.list, params).then(res => {
          if (res.success) {
            console.log(res.result)
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      getAvatarView: function(avatar) {
        return getFileAccessHttpUrl(avatar)
      },
      handleAdd: function() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
        this.$refs.modalForm.disableSubmit = false
      },
      handleEdit: function(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
      },
      handleDelete(record) {
        var params = {
          collegeId: record.id
        }
        var that = this
        getAction(this.url.findDepartments, params).then(res => {
          if (res.success) {
            console.log(res.result)
            if (res.result.length == 0) {
              this.$confirm({
                title: '确认删除该院校',
                content: '确删除该院校?',
                onOk: function() {
                  that.loading = true
                  deleteAction(that.url.delete, {
                    id: record.id
                  }).then((res) => {
                    if (res.success) {
                      that.$message.success(res.message)
                      that.loadData()
                    } else {
                      that.$message.warning(res.message)
                    }
                  })
                }
              })
            } else {
              this.$message.error('该院校下存在院校专业，无法删除!')
            }
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      onSearch() {
        this.loadData()
      },
      modalFormOk() {
        this.loadData()
      },
      gotoMajor(collegeId) {
        this.$router.push({
          path: '/school/departmentList',
          query: {
            collegeId: collegeId
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .ant-avatar-lg {
    width: 48px;
    height: 48px;
    line-height: 48px;
  }

  .list-content-item {
    color: rgba(0, 0, 0, .45);
    display: inline-block;
    vertical-align: middle;
    font-size: 14px;
    margin-left: 40px;

    span {
      line-height: 20px;
    }

    p {
      margin-top: 4px;
      margin-bottom: 0;
      line-height: 22px;
    }
  }
</style>
