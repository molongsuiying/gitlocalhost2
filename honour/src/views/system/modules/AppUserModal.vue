<template>
  <a-drawer
    :title="title"
    :maskClosable="true"
    width=650
    placement="right"
    :closable="true"
    @close="close"
    :visible="visible"
    style="overflow: auto;padding-bottom: 53px;">

    <a-form>
      <a-form-item label='拥有该系统权限的角色'>
        <a-tree
          checkable
          @check="onCheck"
          :checkedKeys="checkedKeys"
          :treeData="treeData"
          @expand="onExpand"
          @select="onTreeNodeSelect"
          :selectedKeys="selectedKeys"
          :expandedKeys="expandedKeysss"
          :checkStrictly="checkStrictly">
          <span slot="hasDatarule" slot-scope="{slotTitle,ruleFlag}">
            {{ slotTitle }}<a-icon v-if="ruleFlag" type="align-left" style="margin-left:5px;color: red;"></a-icon>
          </span>
        </a-tree>
      </a-form-item>
    </a-form>

    <div class="drawer-bootom-button">
      <a-dropdown style="float: left" :trigger="['click']" placement="topCenter">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="switchCheckStrictly(1)">父子关联</a-menu-item>
          <a-menu-item key="2" @click="switchCheckStrictly(2)">取消关联</a-menu-item>
          <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
          <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
          <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
          <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
        </a-menu>
        <a-button>
          树操作 <a-icon type="up" />
        </a-button>
      </a-dropdown>
      <a-popconfirm title="确定放弃编辑？" @confirm="close" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit(false)" type="primary" :loading="loading" ghost style="margin-right: 0.8rem">仅保存</a-button>
      <a-button @click="handleSubmit(true)" type="primary" :loading="loading">保存并关闭</a-button>
    </div>

    <role-datarule-modal ref="datarule"></role-datarule-modal>

  </a-drawer>

</template>
<script>
  import {queryTreeListForUser,queryUserPermission,saveUserPermission} from '@/api/api'
import { callbackify } from 'util'
  import RoleDataruleModal from './RoleDataruleModal.vue'

  export default {
    name: "RoleModal",
    components:{
      RoleDataruleModal
    },
    data(){
      return {
        roleId:"",
        treeData: [],
        defaultCheckedKeys:[],
        checkedKeys:[],
        expandedKeysss:[],
        allTreeKeys:[],
        autoExpandParent: true,
        checkStrictly: true,
        title:"角色权限配置",
        visible: false,
        loading: false,
        selectedKeys:[]
      }
    },
    methods: {
      onTreeNodeSelect(id){
        if(id && id.length>0){
          this.selectedKeys = id
        }
        this.$refs.datarule.show(this.selectedKeys[0],this.roleId)
      },
      onCheck (o) {
        if(this.checkStrictly){
          this.checkedKeys = o.checked;
        }else{
          this.checkedKeys = o
        }
      },
      show(roleId){
        this.roleId=roleId
        this.visible = true;
      },
      refresh () {
          this.selectedDepartKeys = []
          this.checkedDepartKeys = []
          this.checkedDepartNames = []
          this.checkedDepartNameString = ''
          this.userId = ''
          this.resultDepartOptions = []
          this.departId = []
          this.departIdShow = false
          this.currentTenant = []
      },
      close () {
        this.reset()
        this.$emit('close');
        this.visible = false;
        this.loadData(1);
        // this.$emit('close')
        // this.visible = false
        this.disableSubmit = false
        // this.selectedRole = []
        // this.userDepartModel = { userId: '', departIdList: [] }
        // this.checkedDepartNames = []
        // this.checkedDepartNameString = ''
        // this.checkedDepartKeys = []
        // this.selectedDepartKeys = []
        // this.resultDepartOptions = []
        // this.departIds = []
        // this.departIdShow = false
        // this.identity = '1'
        // this.fileList = []
      },
      onExpand(expandedKeys){
        this.expandedKeysss = expandedKeys;
        this.autoExpandParent = false
      },
      reset () {
        this.expandedKeysss = []
        this.checkedKeys = []
        this.defaultCheckedKeys = []
        this.loading = false
      },
      expandAll () {
        this.expandedKeysss = this.allTreeKeys
      },
      closeAll () {
        this.expandedKeysss = []
      },
      checkALL () {
        this.checkedKeys = this.allTreeKeys
      },
      cancelCheckALL () {
        //this.checkedKeys = this.defaultCheckedKeys
        this.checkedKeys = []
      },
      switchCheckStrictly (v) {
        if(v==1){
          this.checkStrictly = false
        }else if(v==2){
          this.checkStrictly = true
        }
      },
      handleCancel () {
        this.close()
      },
      handleSubmit(exit) {
        let that = this;
        let params =  {
          roleId:that.roleId,
          permissionIds:that.checkedKeys.join(","),
          lastpermissionIds:that.defaultCheckedKeys.join(","),
        };
        that.loading = true;
        console.log("请求参数：",params);
        saveUserPermission(params).then((res)=>{
          if(res.success){
            that.$message.success(res.message);
            // that.loading = false;
            if (exit) {
              that.close();
              // that.ResetUserList();
              
            }
            // this.confirmLoading = false
            //   this.checkedDepartNames = []
            //   this.userDepartModel.departIdList = { userId: '', departIdList: [] }
            //   this.close()
          }else {
            that.$message.error(res.message);
            that.loading = false;
            if (exit) {
              that.close()
            }
          }
        })
      },
      loadData(){
        queryTreeListForUser().then((res) => {
          this.treeData = res.result.treeList
          this.allTreeKeys = res.result.ids
          queryUserPermission({roleId:this.roleId}).then((res)=>{
              this.checkedKeys = [...res.result];
              this.defaultCheckedKeys = [...res.result];
              this.expandedKeysss = this.allTreeKeys;
              console.log(this.defaultCheckedKeys)
          })
        })
      }
    },
  watch: {
    visible () {
      if (this.visible) {
        this.loadData();
      }
    },
  },
//    watch: {
// 　　'$route': function (to, from) {
//       // console.log(to,from);
//      if(to.fullPath && !to.query.refresh){
// 	/*
// 	refresh此参数为个人添加的路由后缀参数，具体实现看个人需求自行处理逻辑
// 	*/
//       // 执行-重置业务方法
//       this.ResetUserList();
//      }
// 　　}
//   }
  }

</script>
<style lang="less" scoped>
  .drawer-bootom-button {
    position: absolute;
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }

</style>