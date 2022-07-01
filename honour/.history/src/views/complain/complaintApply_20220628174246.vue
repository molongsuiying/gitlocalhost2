<template>
  <a-card :bordered="false">
    <!--流程申请选择-->
    <component :is="LcDict" :trigger-change="true" v-model="queryParam.categoryId" style="margin-bottom: 10px;margin-right:10px;width: 200px"
      placeholder="请选择投诉分类" dictCode="complain_type" @change="handleInput"></component>
    <a-select @change="change_routeName" placeholder="关联表单" :trigger-change="true" v-model='routeForm.routeName' style="margin-bottom: 10px;margin-right:10px;width: 200px">
      <a-select-option v-for="(item, i) in allFormComponent" :key="item.routeName" :value="item.routeName">
        <span style="display: inline-block;width: 100%" :title=" item.text">
          {{ item.text}}
        </span>
      </a-select-option>
    </a-select>
    <component :is="LcDict" :trigger-change="true" v-model="queryParam.status" style="margin-bottom: 10px;margin-right:10px;width: 200px"
      placeholder="请选择投诉状态" dictCode="complain_status" @change="handleStatus"></component>
      <a-input-search style="margin-bottom: 10px;margin-right:10px;width: 200px" v-model="queryParam.name"
                      placeholder="输入投诉人姓名" @search="onSearchProcess('',false)" />
      <a-input-search style="margin-bottom: 10px;margin-right:10px;width: 200px" v-model="queryParam.name"
                      placeholder="输入投诉人手机号" @search="onSearchProcess('',false)" />
    <span @click="viewForm()" style="cursor:pointer;color: blue;">预览表单</span>
    <a-button @click="onSearchProcess('',false)" type="primary" style='margin-left: 10px;'>查询</a-button>
    <a-button @click="onSearchProcess('',true)" style='margin-left: 10px;'>重置</a-button>
    <!-- <a-button @click="handleToApplyList" type="primary" style="float: right;">前往我的审批列表</a-button> -->
    <a-empty description="无申诉表单" v-if="activeKeyAll.length==0" />
    <div v-else>
      <a-collapse v-model="activeKey">
        <a-collapse-panel v-for="(value, index)  in activeKeyAll" :header="filterDictText(dictOptions,value)||'未分类'"
          :key="value">
          <a-list :grid="{ gutter: 10,column:4}" :dataSource="processDataMap[value]">
            <a-list-item slot="renderItem" slot-scope="item">
              <a-card>
                <div slot="title">
                  <a-row>
                    <a-col span="12" :title="item.name">投诉人:<span style="margin-left: 10px;"></span>{{item.name}}
                    </a-col>
                    <a-col span="12" style="text-align: right;">
                      <template v-if="item.status == 0">
                        <a href="javascript:void (0)" style="color: red;" @click="getProcessModelList(item)">前往处理</a>
                      </template>
                      <template v-else>
                        <a href="javascript:void (0)" style="color: green;" @click="handleToApplyList(item)">请往查看</a>
                      </template>
                    </a-col>
                  </a-row>
                </div>
                <b>是否受理：</b>

                <template v-if="item.status == 0">
                  <font style="color: red;">未受理</font>
                </template>
                <template v-else-if="item.status == 1">
                  <font style="color: blue;">已受理</font>
                </template>
                <template v-else-if="item.status == 2">
                  <font style="color: green;">待回访</font>
                </template>
                <template v-else-if="item.status == 3">
                  <font style="color: green;">已回访</font>
                </template>
                 <!-- item.title -->
                <br />
                <b>标题：</b>{{item.title}}
                <template v-if ="routeForm.businessTable=='act_z_complain'">
                  <br />
                  <b>联系手机：</b> {{item.mobile}}
                  <br />
                  <b>发生时间：</b>{{item.beginTime}}
                  <br />
                  <div style="height: 150px;display: -webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp: 7;overflow: hidden;">
                  <b>具体描述：</b>{{item.content}}
                  </div>
                </template>
                
                
               

                <template slot="actions" class="ant-card-actions">
                  <a-icon key="solution" type="solution" title='查看详情' @click='detail(item)' />

                  <template v-if="item.status == 0">
                    <a-icon key="edit" type="edit" title='正常处理' @click="getProcessModelList(item)" />
                  </template>

                  <template v-if="item.status == 0">
                    <a-icon key="rocket" type="rocket" title='直接处理' @click='quickHandle(item)' />
                  </template>
                  <template v-if="item.status == 2">
                    <a-icon key="highlight" type="highlight"  v-has="'operator:addRemark'" @click='reRemark(item)' title="回访说明"/>
                  </template>

                  <template v-if="item.status == 3">
                    <a-icon key="highlight" type="highlight" v-has="'operator:addRemark'"  @click='reRemark(item)' title="重新回访" />
                  </template>
                </template>
              </a-card>

            </a-list-item>

          </a-list>
          <div style="text-align: center;">
            <a-pagination v-model="queryParam.pageNo" :total="count" :pageSize='queryParam.pageSize' show-less-items
              @change='onSearchProcess("",false)' />
          </div>

        </a-collapse-panel>
      </a-collapse>

    </div>
    <!--流程表单-->
    <a-modal :destroyOnClose="true" :title="lcModa.title" v-model="lcModa.visible" :footer="null" :maskClosable="false"
      width="80%">
      <component :disabled="lcModa.disabled" v-if="lcModa.visible" :is="lcModa.formComponent" :processData="lcModa.processData"
        :isNew="lcModa.isNew" @afterSubmit="afterSub" @close="lcModa.visible=false,lcModa.disabled = false"></component>
    </a-modal>
    <j-select-process-by-complain-modal ref="selectModal" :customReturnField="customReturnField" :modal-width="modalWidth"
      :multi="multi" @ok="selectOK" />


    <a-modal :title="modalTaskTitle" v-model="modalTaskVisible" :mask-closable="false" :width="500">

      <div v-if="modalTaskVisible">
        <a-form ref="form" :model="form" :label-width="85">
          <a-form-item label="审批意见" prop="reason">
            <a-input type="textarea" v-model="form.comment" :rows="4" />
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

    <a-modal :title="modalRemarkTitle" v-model="modalRemarkVisible" :mask-closable="false" :width="500">

      <div v-if="modalRemarkVisible">
        <a-form ref="form" :model="reForm" :label-width="85">
          <a-form-item label="回访说明" prop="reason">
            <a-input type="textarea" v-model="reForm.reason" :rows="4" />
          </a-form-item>

          <span style="margin-right: 20px;">回访满意度:</span>
          <a-rate :tooltips="desc" :v-model="reForm.degree" @change="getDegree" />
          <span class="ant-rate-text" style="margin-left: 10px;">{{ desc[reForm.degree - 1] }}</span>
        </a-form>
      </div>
      <div slot="footer">
        <a-button type="text" @click="modalRemarkVisible=false">取消</a-button>
        <a-button type="primary" :loading="submitLoading" @click="handelReSubmit">提交</a-button>
      </div>
    </a-modal>





  </a-card>

</template>

<script>
  import {
    activitiMixin
  } from '@/views/activiti/mixins/activitiMixin'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import JTreeSelect from '@/components/jeecg/JTreeSelect'
  import {
    initDictOptions,
    filterDictText
  } from '@/components/dict/JDictSelectUtil'
  import historicDetail from '@/views/activiti/historicDetail'
  import JSelectProcessByComplainModal from './modal/JSelectProcessByComplainModal'
  export default {
    name: "applyHome",
    mixins: [activitiMixin],
    components: {
      JEllipsis,
      JTreeSelect,
      historicDetail,
      JSelectProcessByComplainModal
    },
    props: {
      modalWidth: {
        type: Number,
        default: 1250,
        required: false
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false
      },
      multi: {
        type: Boolean,
        default: false,
        required: false
      },
      // 自定义返回字段，默认返回 username
      customReturnField: {
        type: String,
        default: 'id'
      }
    },
    model: {
      prop: 'value',
      event: 'change'
    },
    data() {
      return {

        processIds: '',
        description: '所有',
        dictOptions: [],
        url: {
          getProcessDataList: "/pro_complain/getFormList",
          listData: '/activiti_process/listData',
          addAndApplyBusiness: '/actBusiness/addAndApply',
          countData: "/pro_complain/countFormList",
          applyFinish: '/actBusiness/applyFinish',
          addRemark: '/actBusiness/addRemark',
        },
        // 查询条件
        queryParam: {
          categoryId: "",
          tableName: '',
          pageSize: 8,
          pageNo: 1,
          status: '',
        },
        // 表头
        labelCol: {
          xs: {
            span: 4
          },
          sm: {
            span: 4
          },
        },
        wrapperCol: {
          xs: {
            span: 20
          },
          sm: {
            span: 20
          },
        },
        processModalVisible: null,
        activeKeyAll: [],
        activeKey: [],
        processDataMap: {},
        searchProcessKey: null,
        addApplyLoading: false,
        currentForm: {},
        lcModa: {
          title: '',
          disabled: false,
          visible: false,
          formComponent: null,
          isNew: false,
        },
        count: 10,
        routeForm: {
          text: '申诉表单',
          routeName: '@/views/activiti/form/complain',
          businessTable: 'act_z_complain'
        },
        selectedDepUsers: '',
        modalTaskTitle: "",
        modalTaskVisible: false,
        form: {
          procInstId: "",
          comment: "",
          sendMessage: true,
          sendSms: false,
          sendEmail: false,
          tableId: '',
          tableName: '',
        },
        submitLoading: false, // 添加或编辑提交状态
        modalRemarkTitle: "",
        modalRemarkVisible: false,
        reForm: {
          reason: '',
          tableId: '',
          tableName: '',
          degree: 4
        },
        desc: ['很不满意', '不满意', '理解', '满意', '非常满意'],
      }
    },
    computed: {
      nameList: function() {

        var names = []
        for (var a = 0; a < this.selectList.length; a++) {
          names.push(this.selectList[a].name)
        }
        return names
      },
      LcDict: function() {
        var myComponent = () => import(`@/components/dict/JDictSelectTag`);
        return myComponent;
      },
    },
    mounted() {
      this.initDictConfig()
      this.initcountData();
      this.getProcessList()

    },
    methods: {

      initDictConfig() {
        //初始化字典 - 流程分类
        initDictOptions('complain_type').then((res) => {
          if (res.success) {
            this.dictOptions = res.result;
          }
        });

      },
      filterDictText(dictOptions, text) {
        if (dictOptions instanceof Array) {
          for (let dictItem of dictOptions) {
            if (text === dictItem.value) {
              return dictItem.text
            }
          }
        }
        return text || text == 'null' ? '' : text
      },
      initcountData() {
        this.getAction(this.url.countData, this.queryParam).then(res => {
          this.count = res.result;
          console.log("count:" + this.count);
        })
      },
      /*加载流程列表*/
      getProcessList() {
        this.addApplyLoading = true;
        this.queryParam.tableName = this.routeForm.businessTable;
        this.getAction(this.url.getProcessDataList, this.queryParam).then(res => {
          this.activeKeyAll = [];
          if (res.success) {
            var result = res.result || [];
            if (result.length > 0) {
              let searchProcessKey = this.searchProcessKey;
              if (searchProcessKey) { //过滤条件
                result = _.filter(result, function(o) {
                  return o.name.indexOf(searchProcessKey) > -1;
                });
              }

              this.processDataMap = _.groupBy(result, 'categoryId');
              for (const categoryId in this.processDataMap) {
                this.activeKeyAll.push(categoryId)
              }
              this.activeKey = this.activeKeyAll;
            }
            this.processModalVisible = true;
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => this.addApplyLoading = false);
      },
      onSearchProcess(value, flag) {
        if (flag == true) {
          this.queryParam = {
            categoryId: "",
            tableName: '',
            pageSize: 8,
            pageNo: 1,
          }
          this.routeForm = {
            text: '申诉表单',
            routeName: '@/views/activiti/form/complain',
            businessTable: 'act_z_complain'
          }
        }
        this.initcountData();
        this.getProcessList();
      },
      chooseProcess(v) {
        if (!v.routeName) {
          this.$message.warning(
            "该流程信息未配置表单，请联系开发人员！"
          );
          return;
        }
        this.lcModa.formComponent = this.getFormComponent(v.routeName).component;
        this.lcModa.title = '发起流程：' + v.name;
        this.lcModa.isNew = true;
        this.lcModa.processData = v;
        this.lcModa.visible = true;
        console.log("发起", v)
      },
      /*提交成功申请后*/
      afterSub(formData) {
        this.lcModa.visible = false;
        this.$message("请前往我的申请列表提交审批！")
      },
      change_routeName() {
        let routeName = this.routeForm.routeName;
        console.log("routeName", routeName);
        var route = this.getFormComponent(routeName);
        this.routeForm.businessTable = route.businessTable;
        console.log(this.queryParam.tableName);
        this.onSearchProcess('', false);
      },
      viewForm(routeName) {
        if (!routeName) routeName = this.routeForm.routeName;
        if (!routeName) {
          this.$message.warning(
            "请先选择表单！"
          );
          return;
        }
        let formComponent = this.getFormComponent(routeName);
        console.log(formComponent)
        this.lcModa.formComponent = formComponent.component;
        this.lcModa.title = '表单预览：' + formComponent.text;
        this.lcModa.visible = true;
      },
      getProcessModelList(r) {
        var param = {
          name: "",
          pageNo: 0,
          pageSize: 8,
          tableName: this.routeForm.businessTable
        };
        this.currentForm = r;
        this.$refs.selectModal.showModal(param);
      },

      selectOK(rows, idstr) {
        console.log("当前选中流程", rows);
        console.log("当前选中流程ID", idstr);
        console.log(this.currentForm);
        console.log(rows[0]);
        if (idstr != '' && idstr != null) {
          var params = {
            procDefId: idstr,
            procDeTitle: rows[0].name,
            tableId: this.currentForm.id,
            tableName: rows[0].businessTable
          }
          this.postFormAction(this.url.addAndApplyBusiness, params).then(res => {
            if (res.success) {
              this.$message.success("操作成功");
              this.onSearchProcess('', false);
              //this.handleToApplyList();
            } else {
              this.$message.error(res.message)
            }
          });
        }
      },

      detail(r) {
        r.routeName = this.routeForm.routeName;
        if (!r.routeName) {
          this.$message.warning(
            "该流程信息未配置表单，请联系开发人员！"
          );
          return;
        }
        r.tableId = r.id;
        r.tableName = this.routeForm.businessTable;
        this.lcModa.disabled = true;
        this.lcModa.title = '查看流程业务信息：' + r.name;
       
        this.lcModa.formComponent = this.getFormComponent(r.routeName).component;
        this.lcModa.processData = r;
        this.lcModa.isNew = false;
        this.lcModa.visible = true; 
        console.log( this.lcModa.processData);
      },
      /*前往我的申请页面*/
      handleToApplyList(item) {
        var param = {
          path: '/activiti/applyList',
          query: {
            tableId: item.id,
            tableName: this.queryParam.tableName
          }
        }
        console.log(param);
        this.$router.push(param);
      },
      handleInput(e) {
        console.log("选择:" + e);
        this.queryParam.categoryId = e;
        this.onSearchProcess('', false);

      },
      handleStatus(e) {
        this.queryParam.status = e;
        this.onSearchProcess('', false);
      },
      onSearchName(value) {
        this.onSearchProcess('', false);
      },
      quickHandle(e) {
        console.log(e);
        if (e.status != 0) {
          this.$message.warning(
            "该信息已被受理,无法进行直接处理"
          );
        } else {
          this.modalTaskVisible = true;
          this.modalTaskTitle = "直接处理";
          this.currentForm = e;
        }
      },
      handelSubmit() {
        console.log(this.form);
        var params = Object.assign({}, this.form);
        params.tableId = this.currentForm.id,
          console.log(params);
        this.postFormAction(this.url.applyFinish, params).then(res => {
          if (res.success) {
            this.$message.success("操作成功");
            this.onSearchProcess('', false);
            //this.handleToApplyList();
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => this.modalTaskVisible = false, this.submitLoading = false);
      },
      reRemark(item) {
        this.modalRemarkVisible = true;
        this.modalRemarkTitle = "回访留言";
        console.log(item);

        this.reForm.tableId = item.id,
          this.reForm.tableName = this.routeForm.businessTable;
      },
      handelReSubmit() {
        console.log(this.reForm);
        var params = Object.assign({}, this.reForm);
        console.log(params);
        this.postFormAction(this.url.addRemark, params).then(res => {
          if (res.success) {
            this.$message.success("操作成功");
            this.onSearchProcess('', false);
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => this.modalRemarkVisible = false, this.submitLoading = false);
      },
      getDegree(value) {
        this.reForm.degree = value;
      },
      phoneNumFilter (phone) {
            // 1字符串转化成数组
            let phoneArr = [...phone];
            phone = '';
            // 2.将数组中的4-7位变成*
            phoneArr.map((res, index) => {
              if (index > 2 && index < 7) {
                phone += '*';
              } else {
                phone += res;
              }
            });
            return phone;
          }
    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
</style>
