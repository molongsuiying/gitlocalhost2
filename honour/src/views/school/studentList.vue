<template>
  <a-row :gutter="10">
    <a-col :md="8" :sm="24">
      <a-card :bordered="false">
        <a-row style="margin-left: 16px">
          <a-button @click="handleAdd()" type="primary" :disabled="addDisabled">添加班级</a-button>
          <a-button @click="handleEdit()" style="margin-left: 14px" type="primary" :disabled="editDisabled">编辑班级</a-button>
          <a-button title="删除当前班级" @click="editDisabled" style="margin-left: 14px" type="default" :disabled="editDisabled">删除班级</a-button>
        </a-row>
        <div style="background: #fff;padding-left:16px;height: 100%; margin-top: 10px">

          <a-alert type="info" :showIcon="true">
            <div slot="message">
              当前选择：<span v-if="this.currSelected.title">{{ getCurrSelectedTitle() }}</span>
              <a v-if="this.currSelected.title" style="margin-left: 10px" @click="onClearSelected">取消选择</a>
            </div>
          </a-alert>
          <!-- 树-->

          <template v-if="departTree.length>0">

            <!--组织机构-->
            <a-tree
              showLine
              :loadData="onLoadData"
              :treeData="departTree"
              :selectedKeys="selectedKeys"
              :default-expanded-keys="defaultExpandedKeys"
              @select="onSelect" />

          </template>
          <div style="margin-top: 24px;" v-else>
            <h3><span>您的账号下暂无有效学院信息</span></h3>
          </div>
        </div>
      </a-card>
    </a-col>
    <a-col :md="16" :sm="24">
      <a-card :bordered="false">
        <div style="padding: 10px;">
          <bunch-student-info ref="studentInfo" @clearSelectedDepartKeys="clearSelectedDepartKeys"></bunch-student-info>
        </div>

      </a-card>
    </a-col>
    <bunch-modal ref="modalForm" @ok="modalFormOk"></bunch-modal>
  </a-row>
</template>
<script>
  import BunchModal from "./modules/BunchModal"
  import BunchStudentInfo from "./modules/BunchStudentInfo"
  import {
    getAction
  } from "@/api/manage"
  export default {
    name: "honourList",
    components: {
      BunchStudentInfo,
      BunchModal
    },
    data() {
      return {
        iExpandedKeys: [],
        loading: false,
        autoExpandParent: true,
        currParam: {},
        addDisabled: true,
        editDisabled: true,
        disable: true,
        treeData: [],
        visible: false,
        departTree: [],
        defaultExpandedKeys: [],
        defaultSelectedKeys: [],
        hiding: true,
        model: {},
        dropTrigger: "",
        depart: {},
        disableSubmit: false,
        selectedKeys: [],
        autoIncr: 1,
        currSelected: {},
        form: this.$form.createForm(this),
        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 5
          }
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 16
          }
        },
        graphDatasource: {
          nodes: [],
          edges: []
        },
        url: {
          queryMyColleges: "/college/queryMyColleges",
          queryMyDeparts: "/department/queryMyDeparts",
          queryMyMajors: "/major/queryMyMajorList",
          queryMyBunchList: "/bunch/queryMyBunchList"
        }
      }
    },
    methods: {
      callback(key) {

      },
      loadData() {
        if ("ids" in this.currParam) {
          var ids = this.currParam.ids.split("-")
          this.defaultExpandedKeys.push(ids[0])
          this.defaultExpandedKeys.push(ids[1])
        }
        this.refresh()
      },
      clearSelectedDepartKeys() {
        this.selectedKeys = []
      },
      onClearSelected() {
        this.hiding = true
        this.addDisabled = true
        this.editDisabled = true
        this.currSelected = {}
        this.selectedKeys = []
        this.$refs.studentInfo.open({})
      },
      getCurrSelectedTitle() {
        return !this.currSelected.title ? "" : this.currSelected.title
      },
      loadTree() {
        var that = this
        that.treeData = []
        that.departTree = []
        var params = {}

        if ("ids" in this.currParam) {
          params = this.currParam
        }
        getAction(this.url.queryMyColleges, params).then(res => {
          if (res.success && res.result) {
            for (let i = 0; i < res.result.length; i++) {
              let temp = res.result[i]
              var t = {
                id: temp.key,
                value: temp.value,
                title: temp.title,
                isLeaf: temp.isLeaf,
                key: temp.key,
                level: 0,
                rootLevel: temp.key
              }
              if (temp.children && temp.children.length > 0) {
                t.children = temp.children
              }
              that.treeData.push(t)
              that.departTree.push(t)
            }
            this.loading = false
          }
        })
      },

      refresh() {
        this.loading = true
        this.loadTree()
      },
      onLoadData(treeNode) {
        var level = treeNode.dataRef.level
        var param = {}
        var url = ""
        if (level === 0) {
          param = {
            collegeId: treeNode.dataRef.key
          }
          url = this.url.queryMyDeparts
        } else if (level === 1) {
          param = {
            departmentId: treeNode.dataRef.key
          }
          url = this.url.queryMyMajors
        } else if (level === 2) {
          param = {
            majorId: treeNode.dataRef.key
          }
          url = this.url.queryMyBunchList
        }
        return getAction(url, param).then(res => {
          if (treeNode.dataRef.children) {
            return
          }
          setTimeout(() => {
            for (let i = 0; i < res.result.length; i++) {
              res.result[i].rootLevel = treeNode.dataRef.rootLevel + "-" + res.result[i].key
            }
            treeNode.dataRef.children = res.result
            this.treeData = [...this.treeData]
            this.departTree = [...this.departTree]
          }, 100)
        })
      },

      onSelect(selectedKeys, e) {
        if (this.selectedKeys[0] !== selectedKeys[0]) {
          this.selectedKeys = [selectedKeys[0]]
        }
        let record = e.node.dataRef
        this.currSelected = record
        if (record.level === 2) {
          this.addDisabled = false
          this.editDisabled = true
        } else if (record.level === 3) {
          this.addDisabled = true
          this.editDisabled = false
        } else {
          this.addDisabled = true
          this.editDisabled = true
        }
        this.$refs.studentInfo.open(record)
      },
      handleAdd: function() {
        var level = this.currSelected.level
        if (level !== 2) {
          this.$message.error("请先选择专业!")
        } else {
          var majorList = []
          var major = {
            majorId: this.currSelected.key,
            majorName: this.currSelected.title
          }
          majorList.push(major)
          this.$refs.modalForm.add(majorList, this.currSelected)
          this.$refs.modalForm.title = "新增班级"
          this.$refs.modalForm.disableSubmit = false
        }
      },
      handleEdit: function() {
        var current = this.currSelected
        var bunch = {
          id: current.key,
          bunchName: current.title,
          majorId: current.parentId,
          sort: current.sort,
          describe: current.slotTitle,
          majorName: current.label
        }
        var majorList = []
        var major = {
          majorId: bunch.majorId,
          majorName: bunch.majorName
        }
        majorList.push(major)
        this.$refs.modalForm.edit(majorList, bunch)
        this.$refs.modalForm.title = "编辑班级"
        this.$refs.modalForm.disableSubmit = false
      },
      modalFormOk(data, type) {
        this.searchOption(data, this.departTree, type)
      },
      searchOption(option, arr, type) {
        if (type === "add") {
          for (var i = 0; i < arr.length; i++) {
            if (arr[i].key === option.majorId) {
              var item = {
                key: option.id,
                level: 3,
                title: option.bunchName,
                slotTitle: option.describe,
                value: option.id,
                isLeaf: true,
                rootLevel: this.currSelected.rootLevel + "-" + option.id,
                parentId: option.majorId,
                label: this.currSelected.majorName,
                sort: option.sort
              }
              if (!arr[i].children || arr[i].children.length <= 0) {
                arr[i].children = []
              }
              arr[i].isLeaf = false
              this.currSelected.isLeaf = false
              arr[i].children.push(item)
              break
            } else if (arr[i].children && arr[i].children.length > 0) { // 递归条件
              this.searchOption(option, arr[i].children, type)
            } else {
              continue
            }
          }
        } else {
          for (var s = 0; s < arr.length; s++) {
            if (arr[s].key === option.id) {
              if (type === "delect") {
                arr.splice(s, 1)
              } else {
                arr[s].slotTitle = option.describe
                arr[s].sort = option.sort
                arr[s].title = option.bunchName
                this.currSelected = arr[s]
              }
              break
            } else if (arr[s].children && arr[s].children.length > 0) { // 递归条件
              this.searchOption(option, arr[s].children)
            } else {
              continue
            }
          }
        }
      }
    },
    created() {
      this.currParam = this.$route.query
      this.loadData()
    }
  }
</script>
<style scoped>
  @import "~@assets/less/common.less"
</style>
