<style lang="less">
</style>
<template>
  <div class="search">
    <a-card style="margin-bottom:10px;">
      <p slot="title">
        <span>流程审批进度历史</span>
      </p>
      <a-row style="position:relative">
        <a-table :loading="loading" rowKey="id" :dataSource="data" :pagination="ipagination" ref="table" @change="handleTableChange">
          <a-table-column title="#" width="50">
            <template slot-scope="t,r,i">
              <span> {{i+1}} </span>
            </template>
          </a-table-column>
          <a-table-column title="任务名称" dataIndex="name" width="150" align="center">
            <template slot-scope="t">
              <span> {{t}} </span>
            </template>
          </a-table-column>
          <a-table-column title="处理人" dataIndex="assignees" width="150" align="center">
            <template slot-scope="t">
              <div v-if="t">
                <span v-for="item in t">
                  <span v-if="item.isExecutor" style="color: #00DB00;">{{item.username}} </span>
                  <span v-else style="color: #999;">{{item.username}} </span>
                </span>
              </div>
            </template>
          </a-table-column>
          <a-table-column title="审批操作" dataIndex="deleteReason" width="150" align="center">
            <template slot-scope="t">
              <span v-if="t.toString().indexOf('通过')>-1" style="color: #00DB00">{{t}}</span>
              <span v-else-if="t.toString().indexOf('驳回')>-1" style="color: red;">{{t}}</span>
              <span v-else-if="t.toString().indexOf('催办')>-1" style="color: red;">{{t}}</span>
              <span v-else-if="t.toString().indexOf('督办')>-1" style="color: red;">{{t}}</span>
              <span v-else-if="t.toString().indexOf('延期')>-1" style="color: orange;">{{t}}</span>
              <span v-else-if="t.toString().indexOf('联合')>-1" style="color: orange;">{{t}}</span>
              <span v-else>{{t}}</span>
            </template>
          </a-table-column>
          <a-table-column title="审批意见" dataIndex="comment" width="150" align="center">
            <template slot-scope="t">
              <span>{{t}}</span>
            </template>
          </a-table-column>
          <a-table-column title="耗时" dataIndex="duration" width="150" align="center">
            <template slot-scope="t">
              <span>{{millsToTime(t)}}</span>
            </template>
          </a-table-column>
          <a-table-column title="创建时间" dataIndex="createTime" width="150" align="center">
            <template slot-scope="t">
              <span>{{t}}</span>
            </template>
          </a-table-column>
          <a-table-column title="完成时间" dataIndex="endTime" width="150" align="center">
            <template slot-scope="t">
              <span>{{t}}</span>
            </template>
          </a-table-column>
          <a-table-column title="状态" dataIndex="endTime" key="aaa" width="150" align="center">
            <template slot-scope="t,r,i">
              <template v-if="r.deleteReason == '催办'">
                <span style="color: red;">催办</span>
              </template>
              <template v-else-if="r.deleteReason == '督办'">
                <span style="color: red;">督办</span>
              </template>
              <template v-else-if="r.deleteReason == '联合办理'">
                <span style="color: orange;">联合办理</span>
              </template>
              <template v-else-if="r.deleteReason == '延期办理'">
                <span style="color: orange;">延期办理</span>
              </template>
              <template v-else-if="r.deleteReason == '联合办理-通过'">
                <span style="color: orange;">联合办理-通过</span>
              </template>
              <template v-else>
                <span v-if="t" style="color: blue;">已办理</span>
                <span v-else style="color: #999999">待处理</span>
              </template>

            </template>
          </a-table-column>
        </a-table>
      </a-row>
    </a-card>
    <a-card style="margin-bottom:10px;">
      <p slot="title">
        <span>流程审批进度图</span>
      </p>
      <a-row style="position:relative">
        <a-steps :current="currentNode" :status='currentStatus'>

          <a-popover slot="progressDot" slot-scope="{ index, status, prefixCls }">
            <template slot="content" v-if="nodeList[index].type==1">
              <span>处理人
                <template v-if="nodeList[index].assignees != null && nodeList[index].assignees.length>0">
                  <div v-for="(user, u_index) in nodeList[index].assignees">
                    <font v-if="user.isExecutor" color="deeppink">{{user.username}}</font>
                  </div>
                </template>
              </span>
            </template>
            <span :class="`${prefixCls}-icon-dot`" />
          </a-popover>

          <a-step v-for="(node, index) in nodeList" :key="index">
            <span style="font-size: 14px" slot="title">
              {{node.title}}

            </span>

            <span slot='description'>
              {{node.endTime}}
            </span>
          </a-step>

        </a-steps>
      </a-row>


    </a-card>
    <template v-if="visitShowFlag == true">
      <a-card style="margin-bottom:10px;">
        <p slot="title">
          <span>回访列表</span>
          <a-row style="position:relative">
            <a-table :loading="loading" rowKey="id" :dataSource="visitList">
              <a-table-column title="#" width="50">
                <template slot-scope="t,r,i">
                  <span> {{i+1}} </span>
                </template>
              </a-table-column>
              <a-table-column title="回访人" dataIndex="visitor" width="150" align="center">
                <template slot-scope="t">
                  <span> {{t}} </span>
                </template>
              </a-table-column>
              <a-table-column title="回访时间" dataIndex="visitTime" width="150" align="center">
                <template slot-scope="t">
                  <span> {{t}} </span>
                </template>
              </a-table-column>
              <a-table-column title="回访留言" dataIndex="visitRemark" width="150" align="center">
                <template slot-scope="t">
                  <span> {{t}} </span>
                </template>
              </a-table-column>
              <a-table-column title="回访满意度" dataIndex="visitRank" width="150" align="center">
                <template slot-scope="t,r,i">
                  <template v-if="t == 1">
                    <span> 很不满意 </span>
                  </template>
                  <template v-else-if="t == 2">
                    <span> 不满意 </span>
                  </template>
                  <template v-else-if="t == 3">
                    <span> 理解 </span>
                  </template>
                  <template v-else-if="t == 4">
                    <span> 满意 </span>
                  </template>
                  <template v-else>
                    <span> 非常满意 </span>
                  </template>
                </template>
              </a-table-column>
            </a-table>
          </a-row>
        </p>
      </a-card>
    </template>
    <a-card>
      <p slot="title">
        <span>实时流程图</span>
      </p>
      <a-row style="position:relative">
        <img :src="imgUrl" />
        <a-spin size="large" fix v-if="loadingImg"></a-spin>
      </a-row>
    </a-card>
  </div>
</template>

<script>
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import {
    activitiMixin
  } from '@/views/activiti/mixins/activitiMixin'
  export default {
    name: "historic_detail",
    mixins: [activitiMixin, JeecgListMixin],
    props: {
      /**/
      procInstId: {
        type: String,
        default: '',
        required: true
      },
      procDefId: {
        type: String,
        default: '',
        required: true
      },
    },
    data() {
      return {
        url: {
          historicFlow: '/actTask/historicFlow/',
          getProcessNode: "/actTask/historicProcessFlow",
          getVisitList: '/actBusiness/getVisitList',
          getHighlightImg: `${window._CONFIG['domianURL']}/activiti/models/getHighlightImg/`
        },
        type: 0,
        loading: false, // 表单加载状态
        loadingImg: false,
        data: [],
        visitList: [],
        visitShowFlag: false,
        id: "",
        imgUrl: "",
        backRoute: "",
        nodeList: [],
        currentNode: 0,
        currentStatus: 'wait',
      };
    },
    created() {
      this.init();
    },
    watch: {},
    methods: {
      loadData() {

      },
      init() {
        this.id = this.procInstId;
        console.log(this.id);
        this.imgUrl = this.url.getHighlightImg + this.id + "?time=" + new Date();
        this.getDataList();
        var defId = this.procDefId;
        console.log('xxx:' + defId);
        this.getNodeData(this.procDefId, this.procInstId);
        this.getVisitList(this.procInstId);
      },
      getVisitList(procInstId) {
        this.getAction(this.url.getVisitList, {
          procInstId: procInstId
        }).then(res => {
          this.loading = false;
          if (res.success) {
            this.visitList = res.result;
            if (res.result.length == 0) {
              this.visitShowFlag = false;
            } else {
              this.visitShowFlag = true;
            }
          } else {
            this.$message.error(res.message);
          }
        });
        console.log(this.visitShowFlag);
      },
      getDataList() {
        this.loading = true;
        this.getAction(this.url.historicFlow + this.id).then(res => {
          this.loading = false;
          if (res.success) {
            this.data = res.result;
            if (!res.result || res.result.length == 0) {
              this.$message.info("未找到该记录审批历史数据,历史数据可能已被删除");
            }
          } else {
            this.$message.error(res.message);
          }
        });
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        // this.loadData();
      },
      /*节点设置*/
      getNodeData(procDefId, procInstId) {
        console.log(procDefId);
        console.log(procInstId);
        let _this = this;
        _this.getAction(_this.url.getProcessNode, {
          //procDefId: '请假申请流程名称:1:17',
          //procInstId: "10029"
          procDefId: procDefId.toString(),
          procInstId: procInstId.toString()
        }).then(res => {
          if (res.success) {
            // 转换null为""
            _this.nodeList = res.result || [];
            console.log("_this.nodeList", _this.nodeList);
            for (var i = 0; i < _this.nodeList.length; i++) {
              var node = _this.nodeList[i];
              if (node.currentNode == true) {
                _this.currentNode = i;
                var j = i + 1;
                if (j <= _this.nodeList.length) {
                  var nextNode = _this.nodeList[j];
                  if (nextNode.deleteReason == "审批驳回") {
                    _this.currentStatus = 'error';
                    _this.currentNode = j;
                  } else if (j == _this.nodeList.length - 1 && node.deleteReason == "审批通过") {
                    _this.currentStatus = 'finish';
                    _this.currentNode = j + 1;
                  }
                }

                break;
              }
            }
          } else {
            _this.$message.error(res.message);
          }
        });
      },
    },

  };
</script>
