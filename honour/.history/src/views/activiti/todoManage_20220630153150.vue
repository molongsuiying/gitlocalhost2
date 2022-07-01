<style lang="less">
  @import '~@assets/less/common.less';
</style>
<template>
  <div class="search">

    <a-card>
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="handleSearch">
          <a-row :gutter="24">
            <a-col :md="6" :sm="8">
              <a-form-item label="任务名称" prop="name">
                <a-input type="text" allowClear v-model="searchForm.name" placeholder="请输入" />
              </a-form-item>
            </a-col>
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-col :md="6" :sm="12">
                <a-button type="primary" style="left: 10px" @click="handleSearch" icon="search">查询</a-button>
                <a-button type="primary" @click="handleReset" icon="reload" style="margin-left: 8px;left: 10px">重置</a-button>
              </a-col>
            </span>
          </a-row>
        </a-form>
      </div>
      <!--<a-row class="operation">
        <a-button @click="passAll" icon="md-checkmark-circle-outline">批量通过</a-button>
        <a-button @click="backAll" icon="md-close">批量驳回</a-button>
        <a-button @click="getDataList" icon="md-refresh">刷新</a-button>
      </a-row>-->
      <a-row>
        <a-table :scroll="scroll" bordered :loading="loading" rowKey="id" :dataSource="data" :pagination="ipagination"
          @change="handleTableChange" ref="table">
          <a-table-column title="#" :width="50">
            <template slot-scope="t,r,i">
              <span> {{i+1}} </span>
            </template>
          </a-table-column>
          <a-table-column title="任务名称" dataIndex="name" :width="150" align="center">
            <template slot-scope="t">
              <span> {{t}} </span>
            </template>
          </a-table-column>
          <a-table-column title="所属流程" dataIndex="processName" :width="150" align="center">
            <template slot-scope="t">
              <span> {{t}} </span>
            </template>
          </a-table-column>
          <a-table-column title="委托人" dataIndex="owner" align="center" :width="150">
            <template slot-scope="t">
              <span> {{t}} </span>
            </template>
          </a-table-column>
          <a-table-column title="流程发起人" dataIndex="applyer" :width="150" align="center">
            <template slot-scope="t">
              <span> {{t}} </span>
            </template>
          </a-table-column>
          <a-table-column title="优先级" dataIndex="priority" :width="100" align="center" key="so" :sorter="(a,b)=>a.priority - b.priority">
            <template slot-scope="t">
              <span v-if="t==0" style="color: green;"> 普通 </span>
              <span v-else-if="t==1" style="color: orange;"> 重要 </span>
              <span v-else-if="t==2" style="color: red;font-weight: bold;"> 紧急 </span>
              <span v-else style="color: #999;"> 无 </span>
            </template>
          </a-table-column>
          <a-table-column title="状态" dataIndex="isSuspended" :width="100" align="center" key="z" :sorter="(a,b)=>Boolean(a.isSuspended)?0:1 - Boolean(b.isSuspended)?0:1">
            <template slot-scope="t">
              <span v-if="!Boolean(t)" style="color: green;"> 已激活 </span>
              <span v-if="Boolean(t)" style="color: orange;"> 已挂起 </span>
            </template>
          </a-table-column>
          <a-table-column title="逾期时间" dataIndex="dueTime" :width="170" align="center">
            <template slot-scope="t,r,i">
              <template v-if="Date.parse(t) < new Date()">
                <span style="color: red;font-weight: bold;position: relative;">{{t}} <img src="../../../public/due.png" style="position: absolute;top: -16px;right: -18px;"/> </span>
              </template>
              <template v-else-if="t == null">
                 <span></span>
              </template>
              <template v-else>
                 <span>{{t}}</span>
              </template>
            </template>
          </a-table-column>
          <a-table-column title="联合办理" dataIndex="startUnite" :width="100" align="center">
            <template slot-scope="t">
              <span v-if="t==1" style="color: green;"> 已启动 </span>
              <span v-else style="color: #999;"> 未启动 </span>
            </template>
          </a-table-column>
          <a-table-column title="创建时间" dataIndex="createTime" :width="170" align="center">
            <template slot-scope="t">
              <span>{{t}}</span>
            </template>
          </a-table-column>
          <a-table-column title="操作" dataIndex="" align="center">
            <template slot-scope="t,r,i">
              <template v-if="r.startUnite != 1">
                
                <span v-if="Boolean(r.isSuspended)" style="cursor: no-drop;color: #999999;" title="流程已被挂起，无法操作！">
                  查看并处理
                  <a-divider type="vertical" />
                  委托他人代办
                  <a-divider type="vertical" />
                </span>
                <span v-else>
                  <a @click="detail(r)" style="color: blue">查看并处理</a>
                  <a-divider type="vertical" />
                  <a href="javascript:void(0);" @click="delegateTask(r)" style="color: #00A0E9">委托他人代办</a>
                  <a-divider type="vertical" />
                </span>
                <template v-if="r.unite != 0">
                  <a href="javascript:void(0);" style="color: orange" @click="uniteTask(r)">请求联合办理</a>
                  <a-divider type="vertical" />
                </template>
              </template>
              <template v-else>
                <span v-if="Boolean(r.isSuspended)" style="cursor: no-drop;color: #999999;" title="流程已被挂起，无法操作！">
                  查看并处理
                  <a-divider type="vertical" />
                </span>
                <span v-else>
                  <a href="javascript:void(0);" @click="detail(r)" style="color: blue">查看并处理</a>
                  <a-divider type="vertical" />
                </span>
              </template>
              <a href="javascript:void(0);"  @click="postpone(r)" style="color: #F1C40F">申请延期</a>
              <a-divider type="vertical" />
              <a href="javascript:void(0);" @click="history(r)" style="color: #217dbb">流程历史</a>
            </template>
          </a-table-column>
        </a-table>
      </a-row>
      <honour-file-modal ref="modalForm" v-on="backTask"></honour-file-modal>
    </a-card>
    <!---->
    <a-modal title="审批历史" v-model="modalLsVisible" :mask-closable="false" :width="'80%'" :footer="null">
      <div v-if="modalLsVisible">
        <component :is="historicDetail" :procInstId="procInstId" :procDefId="procDefId"></component>
      </div>
    </a-modal>
    <!--流程表单-->
    <a-modal :title="lcModa.title" v-model="lcModa.visible" :footer="null" :maskClosable="false" width="80%">
      <component :disabled="lcModa.disabled" v-if="lcModa.visible" :is="lcModa.formComponent" :processData="lcModa.processData"
        :isNew="lcModa.isNew" :task="true" @passTask="()=>passTask(lcModa.processData)" @backTask="()=>backTask(lcModa.processData)"
        @close="lcModa.visible=false,lcModa.disabled = false,showCustomizedDepart=false,showAssign=false"></component>
    </a-modal>
    <!-- 审批操作 -->
    <a-modal :title="modalTaskTitle" v-model="modalTaskVisible" :mask-closable="false" :width="500">

      <div v-if="modalTaskVisible">
        <a-form ref="form" :model="form" :label-width="85" :rules="formValidate">
          <a-form-item label="审批意见" prop="reason">
            <a-input type="textarea" v-model="form.comment" :rows="4" />
          </a-form-item>
          <a-form-item label="下一审批人" prop="assignees" v-show="showAssign" :error="error">
            <a-select v-model="form.assignees" placeholder="请选择" allowClear mode="multiple" :loading="userLoading">
              <a-select-option v-for="(item, i) in assigneeList" :key="i" :value="item.username">{{item.realname}}</a-select-option>
            </a-select>

          </a-form-item>
          <a-form-item label="下一审批人" v-show="showCustomizedDepart && form.type != 1">
            <j-select-user-by-dep v-model="userIds" :multi="true" :customizedRole='customizedRole' :subDepartId="subDepartId"></j-select-user-by-dep>
          </a-form-item>

          <a-form-item label="下一审批人" v-show="isGateway">
            <span>分支网关处暂不支持自定义选择下一审批人，将发送给下一节点所有人</span>
          </a-form-item>
          <div v-show="form.type==1">
            <a-form-item label="驳回至">
              <a-select v-model="form.backTaskKey" :loading="backLoading" @change="changeBackTask">
                <a-select-option v-for="(item, i) in backList" :key="i" :value="item.key">{{item.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="指定原节点审批人" prop="assignees" v-show="form.backTaskKey!=-1" :error="error">
              <a-select v-model="form.assignees" placeholder="请选择" allowClear mode="multiple" :loading="userLoading">
                <a-select-option v-for="(item, i) in assigneeList" :key="i" :value="item.username">{{item.realname}}</a-select-option>
              </a-select>
            </a-form-item>
          </div>
          <a-form-item label="选择委托人" prop="userId" :error="error" v-show="form.type==2">
            <JSelectUserByDep v-model="form.userId" :multi="false"></JSelectUserByDep>
          </a-form-item>
          <a-form-item label="消息通知">
            <a-checkbox v-model="form.sendMessage">站内消息通知</a-checkbox>
            <a-checkbox v-model="form.sendSms" disabled>短信通知</a-checkbox>
            <a-checkbox v-model="form.sendEmail" disabled>邮件通知</a-checkbox>
          </a-form-item>
        </a-form>
      </div>
      <div slot="footer">
        <a-button type="text" @click="modalTaskVisible=false">取消</a-button>
        <a-button type="primary" :loading="submitLoading" @click="handelSubmit">提交</a-button>
      </div>
    </a-modal>



    <a-modal :title="modalUniteTitle" v-model="modalUniteVisible" :mask-closable="false" :width="500">

      <div v-if="modalUniteVisible">
        <a-form ref="form" :model="uniteForm" :label-width="85">
          <a-form-item label="审批意见" prop="reason">
            <a-input type="textarea" v-model="uniteForm.comment" :rows="4" />
          </a-form-item>

          <a-form-item label="联合办理" prop="userId" :error="error">
            <JSelectUserByDep v-model="uniteForm.userId" :multi="true" :subDepartId="uniteForm.uniteDepartId"
              :customizedRole='uniteForm.uniteRoleIds' :uniteFlag='uniteForm.uniteFlag'></JSelectUserByDep>
          </a-form-item>
          <a-form-item label="消息通知">
            <a-checkbox v-model="uniteForm.sendMessage">站内消息通知</a-checkbox>
            <a-checkbox v-model="uniteForm.sendSms" disabled>短信通知</a-checkbox>
            <a-checkbox v-model="uniteForm.sendEmail" disabled>邮件通知</a-checkbox>
          </a-form-item>
        </a-form>
      </div>
      <div slot="footer">
        <a-button type="text" @click="modalUniteVisible=false">取消</a-button>
        <a-button type="primary" :loading="submitLoading" @click="handelUniteSubmit">提交</a-button>
      </div>
    </a-modal>

    <a-modal title="联合办理" v-model="modalUnite" :mask-closable="false" :width="500">

      <div v-if="modalUnite">
        <a-form ref="form" :model="uniteData" :label-width="85">
          <a-form-item label="审批意见" prop="reason">
            <a-input type="textarea" v-model="uniteData.comment" :rows="4" />
          </a-form-item>

          <a-form-item label="审批操作" prop="status">
            <a-radio-group v-model="uniteData.status" button-style="solid">
              <a-radio-button value="agree">
                同意
              </a-radio-button>
              <a-radio-button value="refuse">
                驳回
              </a-radio-button>
            </a-radio-group>
          </a-form-item>
          
          <template v-if="uniteData.status == 'refuse'">
            <a-form-item label="驳回至">
              <a-select v-model="uniteData.backTaskKey" :loading="backLoading" @change="changeBackTask">
                <a-select-option v-for="(item, i) in backList" :key="i" :value="item.key">{{item.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="指定原节点审批人" prop="assignees" v-show="uniteData.backTaskKey!=-1" :error="error">
              <a-select v-model="uniteData.assignees" placeholder="请选择" allowClear mode="multiple" :loading="userLoading">
                <a-select-option v-for="(item, i) in assigneeList" :key="i" :value="item.username">{{item.realname}}</a-select-option>
              </a-select>
            </a-form-item>
          </template>

          <a-form-item label="消息通知">
            <a-checkbox v-model="uniteData.sendMessage">站内消息通知</a-checkbox>
            <a-checkbox v-model="uniteData.sendSms" disabled>短信通知</a-checkbox>
            <a-checkbox v-model="uniteData.sendEmail" disabled>邮件通知</a-checkbox>
          </a-form-item>
        </a-form>
      </div>
      <div slot="footer">
        <a-button type="text" @click="modalUnite=false">取消</a-button>
        <a-button type="primary" :loading="submitLoading" @click="passUnite()">提交</a-button>
      </div>
    </a-modal>
    
    <a-modal title="申请延期" v-model="modalPostpone" :mask-closable="false" :width="500">
    
      <div v-if="modalPostpone">
        <a-form ref="form" :model="postponeData" :label-width="85">
          <a-form-item label="延期原因" prop="reason">
            <a-input type="textarea" v-model="postponeData.comment" :rows="4" />
          </a-form-item>
          <a-form-item label="延期日期" prop="dueTime">
            <a-date-picker showTime :disabledDate="disabledDate" format="YYYY-MM-DD HH:mm:ss" v-model="postponeData.dueTime" />
          </a-form-item>
          <!-- <a-form-item label="消息通知">
            <a-checkbox v-model="uniteData.sendMessage">站内消息通知</a-checkbox>
            <a-checkbox v-model="uniteData.sendSms" disabled>短信通知</a-checkbox>
            <a-checkbox v-model="uniteData.sendEmail" disabled>邮件通知</a-checkbox>
          </a-form-item> -->
        </a-form>
      </div>
      <div slot="footer">
        <a-button type="text" @click="modalPostpone=false">取消</a-button>
        <a-button type="primary" :loading="submitLoading" @click="postponeSubmit()">提交</a-button>
      </div>
    </a-modal>

  </div>
</template>

<script>
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import moment from 'moment'
  import {
    activitiMixin
  } from '@/views/activiti/mixins/activitiMixin'
  import JSelectUserByDep from '@/views/activiti/modal/JSelectUserByDep'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import HonourFileModal from '../school/modules/HonourFileModal'
  import kuayemiandiaoyong from '../../utils/kuayemiandiaoyong'
  export default {
    name: "todo-manage",
    mixins: [activitiMixin, JeecgListMixin],
    components: {
      JSelectUserByDep,
      JEllipsis,
      HonourFileModal,
      kuayemiandiaoyong
    },
    
    data() {
      return {
        openSearch: true,
        openTip: true,
        loading: true, // 表单加载状态
        modalTaskVisible: false,
        userLoading: false,
        backLoading: false,
        selectCount: 0, // 多选计数
        selectList: [], // 多选数据
        assigneeList: [],
        backList: [{
          key: "-1",
          name: "发起人"
        }],
        error: "",
        showAssign: false,
        searchForm: {
          // 搜索框对应data对象
          name: "",
        },
        modalTaskTitle: "",
        modalTitle: "", // 添加或编辑标题
        form: {
          id: "",
          userId: "",
          procInstId: "",
          comment: "",
          type: 0,
          assignees: [],
          backTaskKey: "-1",
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        },
        uniteForm: {
          id: "",
          userId: "",
          procInstId: "",
          comment: "",
          assignees: [],
          uniteDepartId: '',
          uniteRoleIds: '',
          uniteFlag:'1',
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        },
        uniteData: {
          id: "",
          procInstId: "",
          comment: "",
          backTaskKey: "-1",
          assignees: [],
          status: 'agree',
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        },
        postponeData: {
          id: "",
          procInstId: "",
          comment: "",
          dueTime:null,
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        },
        formValidate: {
          // 表单验证规则
        },
        submitLoading: false, // 添加或编辑提交状态
        data: [], // 表单数据
        total: 0, // 表单数据总数
        dictPriority: [],
        isGateway: false,
        lcModa: {
          title: '',
          disabled: false,
          visible: false,
          formComponent: null,
          isNew: false
        },
        url: {
          todoList: '/actTask/todoList',
          pass: '/actTask/pass',
          back: '/actTask/back',
          backToTask: '/actTask/backToTask',
          delegate: '/actTask/delegate',
          getNextNode: '/activiti_process/getNextNode',
          getNode: '/activiti_process/getNode/',
          getBackList: '/actTask/getBackList/',
          passAll: '/actTask/passAll/',
          backAll: '/actTask/backAll/',
          buildUnite: '/actTask/buildUnite',
          getNodeUnite: '/actTask/getNodeUnite',
          isLastPassUnite: '/actTask/isLastPassUnite',
          passUnite:'/actTask/passUnite',
          postpone:'/actTask/postpone',
          awayTask:'/actTask/awayTask',
        },
        /*历史*/
        modalLsVisible: false,
        showCustomizedDepart: false,
        subDepartId: '',
        customizedRole: '',
        userIds: '',
        procInstId: '',
        procDefId: '',
        multiFlag: false,
        modalUniteTitle: false,
        modalUniteVisible: false,
        modalUnite: false,
        modalPostpone:false,
      };
    },
    mounted() {
      this.init();
      var that = this;
      kuayemiandiaoyong.$on('backTask',function(v){
        console.log(v);
        // console.log(this.lcModa.processData)
        // var v= this.lcModa.processData
        that.backTask(v);
      });
      kuayemiandiaoyong.$on('passTask',function(v){
        console.log(v);
        // console.log(this.lcModa.processData)
        // var v= this.lcModa.processData
        that.passTask(v);
      })
      kuayemiandiaoyong.$on('close',function(v){
        console.log(v);
        // console.log(this.lcModa.processData)
        // var v= this.lcModa.processData
        that.close();
      })
    },
    methods: {
      init() {
        this.getDataList();
      },
      loadData() {},
      getDataList() {
        this.loading = true;
        this.getAction(this.url.todoList, this.searchForm).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result || [];
            this.total = this.data.leading;
          }
        });
        this.initForm();
      },
      initForm(){
        this.form = {
          id: "",
          userId: "",
          procInstId: "",
          comment: "",
          type: 0,
          assignees: [],
          backTaskKey: "-1",
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        };
        this.uniteForm = {
          id: "",
          userId: "",
          procInstId: "",
          comment: "",
          assignees: [],
          uniteDepartId: '',
          uniteRoleIds: '',
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        };
        this.uniteData = {
          id: "",
          procInstId: "",
          comment: "",
          backTaskKey: "-1",
          assignees: [],
          status: 'agree',
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        };
        this.postponeData = {
          id: "",
          procInstId: "",
          comment: "",
          dueTime:null,
          sendMessage: true,
          sendSms: false,
          sendEmail: false
        };
        this.userIds = "";
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        // this.loadData();
      },
      handleSearch() {
        this.getDataList();
      },
      handleReset() {
        this.searchForm = {};
        // 重新加载数据
        this.getDataList();
      },
      showSelect(e) {
        this.selectList = e;
        this.selectCount = e.length;
      },
      clearSelectAll() {
        this.$refs.table.selectAll(false);
      },
      /*审批提交的方法*/
      handelSubmit() {
        console.log("提交")
        this.submitLoading = true;
        var formData = Object.assign({}, this.form);
        formData.assignees = formData.assignees.join(",");
        if (formData.type == 0) {
          // 通过
          if (this.showAssign && formData.assignees.length < 1) {
            this.$message.error("请至少选择一个审批人")
            this.submitLoading = false;
            return;
          } else {
            this.error = "";
          }

          if (this.showCustomizedDepart && this.userIds.length < 1) {
            this.error = "请至少选择一个审批人";
            this.$message.error(this.error)
            this.submitLoading = false;
            return;
          } else {
            this.error = "";
          }

          if (this.userIds) {
            formData.assignees = this.userIds;
          }

          this.postFormAction(this.url.pass, formData).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.lcModa.visible = false
              this.$message.success("操作成功");
              this.modalTaskVisible = false;
              this.getDataList();
            }
          });
        } else if (formData.type == 1) {
          // 驳回
          if (formData.backTaskKey == "-1") {
            // 驳回至发起人
            this.postFormAction(this.url.back, formData).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.lcModa.visible = false
                this.$message.success("操作成功");
                this.modalTaskVisible = false;
                this.getDataList();
              }
            });
          } else {
            // 自定义驳回
            if (formData.backTaskKey != "-1" && formData.assignees.length < 1) {
              this.$message.error("请至少选择一个审批人")
              this.submitLoading = false;
              return;
            } else {
              this.error = "";
            }
            this.postFormAction(this.url.backToTask, formData).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.lcModa.visible = false
                this.$message.success("操作成功");
                this.modalTaskVisible = false;
                this.getDataList();
              }
            });
          }
        } else if (formData.type == 2) {
          // 委托
          if (!formData.userId) {
            this.$message.error("请选择一委托人")
            this.submitLoading = false;
            return;
          } else {
            this.error = "";
          }
          this.postFormAction(this.url.delegate, formData).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.$message.success("操作成功");
              this.modalTaskVisible = false;
              this.getDataList();
            }
          });
        }
      },
      handlePass(r) {
        var record = r;
        this.$refs.modalForm.pass(record)
        console.log(record)
        this.$refs.modalForm.title = '审核文件类荣誉'
        this.$refs.modalForm.disableSubmit = false
      },
      detail(r) {
        if (!r.routeName) {
          this.$message.warning(
            "该流程信息未配置表单，请联系开发人员！"
          );
          return;
        }
        if(r.routeName=="@/views/school/modules/HonourFileForm"){
          this.handlePass(r);
        }else{
        this.lcModa.disabled = true;
        this.lcModa.title = '查看流程业务信息：' + r.processName;
        this.lcModa.formComponent = this.getFormComponent(r.routeName).component;
        this.lcModa.processData = r;
        // console.log(r);
        this.lcModa.isNew = false;
        this.lcModa.visible = true;
        
        var param = {
          procInstId:r.procInstId,
          id:r.id
        }
        this.postFormAction(this.url.awayTask, param).then(res => {
          if (res.success) {
            this.$message.success(res.message);
          }
        });
        }
      },
      passTask(v) {
        this.modalTaskTitle = "审批通过";
        this.form.id = v.id;
        this.form.procInstId = v.procInstId;
        this.form.priority = v.priority;
        this.form.type = 0;
        this.form.assignees = [];
        this.modalTaskVisible = true;
        this.userLoading = true;
        this.getAction(this.url.getNextNode, {
          procDefId: v.procDefId,
          currActId: v.key
        }).then(res => {
          this.userLoading = false;
          if (res.success) {

            if (res.result.chooseCustomized) {
              this.modalVisible = true;
              var departments = res.result.departments;
              var roles = res.result.roles;
              this.showCustomizedDepart = true;
              if (departments != null && departments.length > 0) {
                this.subDepartId = departments[0].id;
              }
              var roleIds = [];
              if (roles != null && roles.length > 0) {
                for (var i = 0; i < roles.length; i++) {
                  roleIds.push(roles[i].id);
                }
                this.customizedRole = roleIds.join(",");
              }
            } else {
              if (res.result.type == 4) {
                this.isGateway = true;
                this.showAssign = false;
                this.error = "";
                return;
              }
              this.isGateway = false;
              if (res.result.users && res.result.users.length > 0) {
                this.error = "";
                this.assigneeList = res.result.users;
                console.log(this.assigneeList)
                // 默认勾选
                let ids = [];
                res.result.users.forEach(e => {
                  ids.push(e.username);
                });
                this.form.assignees = ids;
                this.showAssign = true;
              } else {
                this.form.assignees = [];
                this.showAssign = false;
              }
            }
          }
        });
      },
      changeBackTask(v) {
        if (v == "-1") {
          return;
        }
        this.userLoading = true;
        this.getAction(this.url.getNode + v).then(res => {
          this.userLoading = false;
          if (res.success) {
            if (res.result.users && res.result.users.length > 0) {
              this.assigneeList = res.result.users;
              // 默认勾选
              let ids = [];
              res.result.users.forEach(e => {
                ids.push(e.username);
              });
              this.form.assignees = ids;
            }
          }
        });
      },
      backTask(v) {
        this.modalTaskTitle = "审批驳回";
        this.form.id = v.id;
        this.form.procInstId = v.procInstId;
        this.form.procDefId = v.procDefId;
        this.form.priority = v.priority;
        this.form.type = 1;
        this.showAssign = false;
        this.modalTaskVisible = true;
        // 获取可驳回节点
        this.backList = [{
          key: "-1",
          name: "发起人"
        }];
        console.log(v)
        this.form.backTaskKey = "-1";
        this.backLoading = true;
        this.getAction(this.url.getBackList,{procInstId:v.procInstId}).then(res => {
          this.backLoading = false;
          if (res.success) {
            res.result.forEach(e => {
              this.backList.push(e);
            });
          }
        });
      },
      delegateTask(v) {
        this.modalTaskTitle = "委托他人代办";
        this.form.id = v.id;
        this.form.procInstId = v.procInstId;
        this.form.type = 2;
        this.showAssign = false;
        this.modalTaskVisible = true;
        this.showCustomizedDepart = false;
      },
      history(v) {
        if (!v.procInstId) {
          this.$message.error("流程实例ID不存在");
          return;
        }
        this.procInstId = v.procInstId;
        this.procDefId = v.procDefId;
        this.modalLsVisible = true;
      },
      passAll() {
        if (this.selectCount <= 0) {
          this.$message.warning("您还未选择要通过的数据");
          return;
        }
        // 批量通过
        this.modalVisible = true;
        this.$confirm({
          title: "确认通过",
          content: "您确认要通过所选的 " +
            this.selectCount +
            " 条数据? 注意：将默认分配给节点设定的所有可审批用户",
          loading: true,
          onOk: () => {
            let ids = "";
            this.selectList.forEach(function(e) {
              ids += e.id + ",";
            });
            ids = ids.substring(0, ids.length - 1);
            this.postFormAction(this.url.passAll, {
              ids: ids
            }).then(res => {
              if (res.success) {
                this.$message.success("操作成功");
                this.modalVisible = false;
                this.clearSelectAll();
                this.getDataList();
              }
            });
          }
        });
      },
      backAll() {
        if (this.selectCount <= 0) {
          this.$message.warning("您还未选择要驳回的数据");
          return;
        }
        // 批量驳回
        this.modalVisible = true;
        this.$confirm({
          title: "确认驳回",
          content: "您确认要驳回所选的 " +
            this.selectCount +
            " 条数据? 注意：所有流程将驳回至发起人",
          loading: true,
          onOk: () => {
            let procInstIds = "";
            this.selectList.forEach(function(e) {
              procInstIds += e.procInstId + ",";
            });
            procInstIds = procInstIds.substring(0, procInstIds.length - 1);
            this.postFormAction(this.url.backAll, {
              procInstIds: procInstIds
            }).then(res => {
              if (res.success) {
                this.$message.success("操作成功");
                this.modalVisible = false;
                this.clearSelectAll();
                this.getDataList();
              }
            });
          }
        });
      },
      uniteTask(v) {
        this.modalUniteTitle = "发起联合办理";
        this.uniteForm.id = v.id;
        this.uniteForm.procInstId = v.procInstId;

        var param = {
          id: v.key,
          procDefId: v.procDefId
        }
        this.getAction(this.url.getNodeUnite, param).then(res => {
          if (res.success) {
            this.showAssign = false;
            this.modalUniteVisible = true;
            this.showCustomizedDepart = false;
            this.uniteForm.uniteDepartId = res.result.uniteDepartId;
            this.uniteForm.uniteRoleIds = res.result.uniteRoleIds;
            this.uniteForm.uniteFlag = '1';
          }
        });
      },
      handelUniteSubmit() {

        var param = Object.assign(this.uniteForm, {})
        console.log(param);
        if (param.userId == '' || param.userId == null) {
          this.$message.error("请至少选择一名人员")
          this.submitLoading = false;
          return;
        } else {
          this.error = "";
        }
        this.postFormAction(this.url.buildUnite, param).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.modalUniteVisible = false
            this.$message.success("操作成功");
            this.getDataList();
            this.uniteForm = {
              id: "",
              userId: "",
              procInstId: "",
              comment: "",
              assignees: [],
              uniteDepartId: '',
              uniteRoleIds: '',
              uniteFlag:'1',
              sendMessage: true,
              sendSms: false,
              sendEmail: false
            }
          }
        });
      },
      uniteHandle(r) {
        var param = {
          id: r.id
        }
        this.getAction(this.url.isLastPassUnite, param).then(res => {

          if (res.success) {
            if (res.result == 0) {
              this.openUnite(r);
            } else if (res.result == 1) {
              this.detail(r);
            }
          } else {
            this.$message.error(res.message)
          }
        });
        param.procInstId = r.procInstId;
        this.postFormAction(this.url.awayTask, param).then(res => {
          if (res.success) {
            this.$message.success(res.message);
          }
        });
      },
      openUnite(v) {
        this.modalUnite = true;
        this.uniteData.backTaskKey = '-1';
        this.uniteData.id = v.id;
        this.uniteData.procInstId = v.procInstId;
      },
      passUnite(){
        
        var param = Object.assign(this.uniteData, {})
        //同意
        if(param.status == 'agree'){
          this.postFormAction(this.url.passUnite, param).then(res => {
            this.submitLoading = false;
            if (res.success) {
              this.modalUnite = false
              this.$message.success("操作成功");
              this.getDataList();
            }
          });
        }else{
          // 驳回
          if (param.backTaskKey == "-1") {
            // 驳回至发起人
            this.postFormAction(this.url.back, param).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.modalUnite = false
                this.$message.success("操作成功");
                this.getDataList();
              }
            });
          } else {
            // 自定义驳回
            if (param.backTaskKey != "-1" && param.assignees.length < 1) {
              this.$message.error("请至少选择一个审批人")
              this.submitLoading = false;
              return;
            } else {
              this.error = "";
            }
            this.postFormAction(this.url.backToTask, param).then(res => {
              this.submitLoading = false;
              if (res.success) {
                this.$message.success("操作成功");
                this.modalUnite = false;
                this.getDataList();
              }
            });
          }
          this.uniteData = {
            id: "",
            procInstId: "",
            comment: "",
            backTaskKey: "-1",
            assignees: [],
            status: 'agree',
            sendMessage: true,
            sendSms: false,
            sendEmail: false
          }
        }
      },
      postpone(v){
        this.modalPostpone = true;
        this.postponeData.dueTime=  v.dueTime;
        this.postponeData.id = v.id;
        this.postponeData.procInstId = v.procInstId;
      },
      disabledDate(current) {
      
        var time = this.postponeData.dueTime;
        // Can not select days before today and today
        return current.valueOf() < moment(time)
      },
      postponeSubmit(){
        var param = Object.assign(this.postponeData, {})
        param.dueTime = moment(param.dueTime).format('YYYY-MM-DD HH:mm:ss');
        this.postFormAction(this.url.postpone, param).then(res => {
          this.submitLoading = false;
          if (res.success) {
            this.modalPostpone = false
            this.$message.success("操作成功");
            this.getDataList();
            this.postponeData = {
              id: "",
              procInstId: "",
              comment: "",
              sendMessage: true,
              sendSms: false,
              sendEmail: false
            }
          }
        });
      },
    },

  };
</script>
s
