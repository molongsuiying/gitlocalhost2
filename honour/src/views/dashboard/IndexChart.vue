<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="总访问量" :total="loginfo.totalVisitCount">
          <a-tooltip title="总的访问量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area :dataSource="miniAreaData" :y="'访问量'"/>
          </div>
          <template slot="footer">今日访问量:<span> {{ loginfo.todayVisitCount | NumberFormat }}</span>次</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="总查询量" :total="totalData.queryCount | NumberFormat">
          <a-tooltip title="荣誉总查询量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area :dataSource="queryDataArea" :y="'查询量'"/>
          </div>
          <template slot="footer">今日查询量:<span> {{ todayQueryNum | NumberFormat }}</span>次</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="8" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="总下载量" :total="totalData.downCount | NumberFormat">
          <a-tooltip title="附件总下载量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <mini-area :dataSource="downDataArea" :y="'下载量'"/>
          </div>
          <template slot="footer">今日下载量<span> {{ todayDownNum | NumberFormat }}</span>次</template>
        </chart-card>
      </a-col>

    </a-row>

    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
          <div class="extra-wrapper" slot="tabBarExtraContent">
            <div class="extra-item">
              <a @click="getCurrentType(1,$event)" :class="{ red:activeType==1}">今日</a>
              <a @click="getCurrentType(2,$event)" :class="{ red:activeType==2}">本月</a>
              <a @click="getCurrentType(0,$event)" :class="{ red:activeType==0}">本年</a>
            </div>

          </div>
          <a-tab-pane loading="true" tab="荣誉新增量" key="1">
            <a-row>
              <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
                <bar title="荣誉新增量" :dataSource="barData" :y="'荣誉数量'"/>
              </a-col>
              <!-- <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="荣誉类型总量排行榜" :list="rankList" />
              </a-col> -->
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-card>

    <a-row>
      <a-col :span="24">
        <a-card :loading="loading" :bordered="false" title="荣誉类型总量排行榜" :style="{ marginTop: '24px' }">
          <a-row>
            <rank-list :list="rankList" />
            <!-- <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
              <rank-list title="荣誉类型总量排行榜" :list="rankList" />
            </a-col> -->
            <!-- <a-col :span="6">
              <head-info title="今日IP" :content="loginfo.todayIp"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class="circle-cust">
                <a-icon slot="indicator" type="environment" style="font-size: 24px" />
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="今日访问" :content="loginfo.todayVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class="circle-cust">
                <a-icon slot="indicator" type="team" style="font-size: 24px" />
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="总访问量" :content="loginfo.totalVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class="circle-cust">
                <a-icon slot="indicator" type="rise" style="font-size: 24px" />
              </a-spin>
            </a-col> -->
          </a-row>
          <!-- <line-chart-multid :fields="visitFields" :dataSource="visitInfo"></line-chart-multid> -->
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import ChartCard from '@/components/ChartCard'
  import ACol from 'ant-design-vue/es/grid/Col'
  import ATooltip from 'ant-design-vue/es/tooltip/Tooltip'
  import MiniArea from '@/components/chart/MiniArea'
  import MiniBar from '@/components/chart/MiniBar'
  import MiniProgress from '@/components/chart/MiniProgress'
  import RankList from '@/components/chart/RankList'
  import Bar from '@/components/chart/Bar'
  import LineChartMultid from '@/components/chart/LineChartMultid'
  import HeadInfo from '@/components/tools/HeadInfo.vue'

  import Trend from '@/components/Trend'
  import {
    getLoginfo,
    getVisitInfo
  } from '@/api/api'

  import {
    getAction
  } from '@/api/manage'

  export default {
    name: 'IndexChart',
    components: {
      ATooltip,
      ACol,
      ChartCard,
      MiniArea,
      MiniBar,
      MiniProgress,
      RankList,
      Bar,
      Trend,
      LineChartMultid,
      HeadInfo
    },
    data() {
      return {
        loading: true,
        center: null,
        totalData: {},
        rankList: [],
        barData: [],
        todayQueryNum: 0,
        todayDownNum: 0,
        queryDataArea: [],
        downDataArea: [],
        activeType: 0,
        loginfo: {},
        visitFields: ['ip', 'visit'],
        visitInfo: [],
        miniAreaData: [],
        url: {
          queryDataList: '/honour/data/queryDataList',
          queryRankList: '/honour/data/queryRankList',
          queryDataVoList: '/honour/data/queryDataVoList'
        },
        indicator: <a-icon type="loading" style="font-size: 24px" spin />
      }
    },
    created() {
      setTimeout(() => {
        this.loading = !this.loading
      }, 1000)
      this.initLogInfo()
      this.init()
      this.initRankList()
      this.initDataVoList()
    },
    methods: {
      initLogInfo() {
        getLoginfo(null).then((res) => {
          if (res.success) {
            Object.keys(res.result).forEach(key => {
              res.result[key] = res.result[key] + ''
            })
            this.loginfo = res.result
          }
        })
        getVisitInfo().then(res => {
          if (res.success) {
            this.visitInfo = res.result
            this.initMiniArea()
          }
        })
      },
      initMiniArea() {
        var that = this
        var data = this.visitInfo
        var area = []
        for (let i = 0; i < data.length; i++) {
          var x = data[i].type
          var y = data[i].visit

          area.push({
            x: x,
            y: y
          })
        }
        that.miniAreaData = area
      },
      init() {
        var param = {}
        getAction(this.url.queryDataList, param).then(res => {
          if (res.success) {
              this.totalData = res.result

              var queryDataArea = res.result.queryCountList
              var downDataArea = res.result.downDataVoList

              var items = []
              var downItems = []
              var now = new Date()
              var today = (now.getDate() < 10 ? '0' + now.getDate() : now.getDate()) + '日'
              console.log(today)
              var d = new Date(now.getFullYear(), now.getMonth() + 1, 0)
              var days = d.getDate()

              var todayQueryNum = 0
              var downQueryNum = 0
              for (let i = 1; i <= days; i += 1) {
                var day = (i < 10 ? '0' + i : i)
                var x = `${day}日`
                var y = 0
                
                if (queryDataArea.hasOwnProperty(x)) {
                  y = queryDataArea[x]
                  if (x == today) {
                    todayQueryNum = y
                  }
                }
                items.push({
                  x: x,
                  y: y
                })
                var dy = 0
                if (downDataArea.hasOwnProperty(x)) {
                  dy = downDataArea[x]
                  if (x == today) {
                    downQueryNum = dy
                  }
                }
                downItems.push({
                  x: x,
                  y: dy
                })
              }
              this.queryDataArea = items
              this.downDataArea = downItems
              console.log(this.downDataArea)
              this.todayQueryNum = todayQueryNum
              this.todayDownNum = downQueryNum
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      initRankList() {
        var that = this
        var param = {}
        getAction(this.url.queryRankList, param).then(res => {
          if (res.success) {
            that.rankList = res.result
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      initDataVoList() {
        var that = this
        var param = {
          type: that.activeType
        }
        getAction(this.url.queryDataVoList, param).then(res => {
          if (res.success) {
            that.packBar(res.result)
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      getCurrentType(type, event) {
        var that = this
        that.activeType = type
        that.initDataVoList()
      },
      packBar(record) {
        var that = this
        var activeType = that.activeType
        var barData = []
        var x = ''
        var y = 0
        if (activeType === 0) {
          var date = new Date()
          var year = date.getFullYear()

          for (let i = 1; i <= 12; i += 1) {
            var month = (i < 10 ? '0' + i : i)
            x = year + `年${month}月`
            y = 0
            if (record.hasOwnProperty(x)) {
              y = record[x]
            }
            barData.push({
              x: x,
              y: y
            })
          }
          that.barData = barData
        } else if (activeType === 2) {
          var now = new Date()
          var d = new Date(now.getFullYear(), now.getMonth() + 1, 0)
          var days = d.getDate()

          for (let i = 1; i <= days; i += 1) {
            var day = (i < 10 ? '0' + i : i)
            x = `${day}日`
            y = 0
            if (record.hasOwnProperty(x)) {
              y = record[x]
            }
            barData.push({
              x: x,
              y: y
            })
          }
          that.barData = barData
        } else if (activeType === 1) {
            for (let i = 1; i <= 24; i += 1) {
              var time = (i < 10 ? '0' + i : i)
              x = `${time}时`
              y = 0
              if (record.hasOwnProperty(x)) {
                y = record[x]
              }
              barData.push({
                x: x,
                y: y
              })
            }
            that.barData = barData
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .red {color: #ff0000;}
  .circle-cust {
    position: relative;
    top: 28px;
    left: -100%;
  }

  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  /* 首页访问量统计 */
  .head-info {
    position: relative;
    text-align: left;
    padding: 0 32px 0 0;
    min-width: 125px;

    &.center {
      text-align: center;
      padding: 0 32px;
    }

    span {
      color: rgba(0, 0, 0, .45);
      display: inline-block;
      font-size: .95rem;
      line-height: 42px;
      margin-bottom: 4px;
    }

    p {
      line-height: 42px;
      margin: 0;

      a {
        font-weight: 600;
        font-size: 1rem;
      }
    }

  }
</style>
