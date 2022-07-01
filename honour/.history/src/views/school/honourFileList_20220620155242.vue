<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper" style="margin-bottom: 20px;">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="24">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input>

            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24">
            <a-form-item label="审核状态">
              <!-- <a-checkbox-group v-model="statusType">
                <a-checkbox :value="0" name="未提交">
                  未提交
                </a-checkbox>
              </a-checkbox-group> -->
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="1" name="审核中">
                  审核中
                </a-checkbox>
              </a-checkbox-group>
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="2" name="已通过">
                  已通过
                </a-checkbox>
              </a-checkbox-group>
              <a-checkbox-group v-model="statusType">
                <a-checkbox :value="-1" name="已驳回">
                  已驳回
                </a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>

              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? "收起" : "展开" }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>

        </a-row>

        <a-row :gutter="24" style="margin-bottom: 20px;" v-show="toggleSearchStatus">
          <a-col :md="6" :sm="24">
            <a-form-item label=" 负   责  人 ">
              <a-input placeholder="请输入负责人" v-model="queryParam.leader">
              </a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="团队成员">
              <a-input placeholder="请输入团队成员" v-model="queryParam.teamPersons">
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24" style="margin-bottom: 10px;">
            <a-form-item label="时间范围" style="margin-bottom:0;">
              <a-form-item :style="{ display: &quot;inline-block&quot;, width: &quot;calc(50% - 12px)&quot; }">
                <a-date-picker style="width: 100%" v-model="queryParam.beginTime" />
              </a-form-item>
              <span
                :style="{ display: &quot;inline-block&quot;, width: &quot;24px&quot;, textAlign: &quot;center&quot; }">
                -
              </span>
              <a-form-item :style="{ display: &quot;inline-block&quot;, width: &quot;calc(50% - 12px)&quot; }">
                <a-date-picker style="width: 100%" v-model="queryParam.endTime" />
              </a-form-item>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="机关单位">
              <j-dict-select-tag v-model="queryParam.authorityType" type="radio" dictCode="certificate_authority" />
            </a-form-item>
          </a-col>

          <a-col :md="24" :sm="24">
            <a-form-item label="成果类别">
              <j-dict-select-tag v-model="queryParam.achievementType" type="radio" dictCode="achievement_type" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="成果级别">
              <j-dict-select-tag v-model="queryParam.achievementLevel" type="radio" dictCode="achievement_level" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="证书类型">
              <j-dict-select-tag v-model="queryParam.certificateType" type="radio" dictCode="certificate_type" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="荣誉级别">
              <j-dict-select-tag v-model="queryParam.honourLevels" type="radio" dictCode="honour_level" />
            </a-form-item>
          </a-col>

          <a-col :md="24" :sm="24">
            <a-form-item label="合作类别">
              <j-dict-select-tag v-model="queryParam.cooperationType" type="radio" dictCode="cooperation_type" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="物图类别">
              <j-dict-select-tag v-model="queryParam.articleType" type="radio" dictCode="article_type" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="媒体类别">
              <j-dict-select-tag v-model="queryParam.mediumType" type="radio" dictCode="medium_type" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md="24" :sm="24">
            <a-button type="primary" @click="exportAll" icon="export" style="margin-left: 8px">导出总表</a-button>
            <a-button type="primary" @click="uploadBatchFile" icon="upload" style="margin-left: 8px" v-has="'user:honourImportFiles'">
              批量导入附件
            </a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <a-collapse v-model="activeKey" :expand-icon-position="expandIconPosition">
      <template #expandIcon="props">
        <a-icon type="caret-right" :rotate="props.isActive ? 90 : 0" />
      </template>
      <a-collapse-panel key="1" header="文件类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(1)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <file-excel-modal ref="fileExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(1)">
          </file-excel-modal>
          <template v-if="exportAuthList[1] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(1)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group @change="onColSettingsChange" v-model="settingColumns" :defaultValue="settingColumns">
                <a-row>
                  <template v-for="(item,index) in defColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>

        <!-- table区域-begin -->
        <div>

          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            filterMultiple="filterMultiple"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            @change="handleTableChange">

            <span slot="status" slot-scope="text,record">
              <template v-if="text == 0">
                <font color="blue">未提交</font>
              </template>
              <template v-else-if="text == 1">
                <font color="orange">待审核</font>
              </template>
              <template v-else-if="text == 2">
                <font color="green">已通过</font>
              </template>
              <template v-else>
                <a-tooltip>
                  <template slot="title">
                    驳回理由:{{ record.remarks }}
                  </template>
                  <font color="red">已驳回</font>
                </a-tooltip>
              </template>
            </span>

            <span slot="action" slot-scope="text, record">

              <a @click="handleDetail(record)">
                <a-icon type="file-search" />详细信息
              </a>
              <a-divider type="vertical" />

              <!-- <a v-has="'user:honourEdit'" @click="handleEdit(record)" v-show="record.status==1">编辑</a>
              <a-divider type="vertical" v-has="'user:honourEdit'" v-show="record.status==1"/> -->

              <!-- <a v-has="'user:honourSubmit'" @click="submit(record)" v-show="record.status==1">提交</a>
              <a-divider type="vertical" v-has="'user:honourSubmit'" v-show="record.status==1"/> -->

              <a @click="handlePass(record)" v-has="'user:examine'" v-show="record.status==1">审核</a>
              <a-divider type="vertical" v-has="'user:examine'" v-show="record.status==1"/>

              <!-- <a v-has="'user:adminEdit'" @click="handleEdit(record)" v-show="record.status==2">编辑</a>
              <a-divider type="vertical" v-has="'user:adminEdit'" v-show="record.status==2"/> -->

              <template v-if="honourAuth.downloadAuth == 1">

                <a @click="downloadAppendix(record)">下载附件</a>
              </template>
              <template v-else-if="honourAuth.downloadAuth == 2">

                <template v-if="moment(honourAuth.currentTime) - moment(honourAuth.downloadDeadline) < 0">
                  <a-tooltip>
                    <template slot="title">
                      到期时间:{{ honourAuth.downloadDeadline }}
                    </template>
                    <a @click="downloadAppendix(record)">下载附件</a>
                  </a-tooltip>
                </template>
                <template v-else>
                  <a-tooltip>
                    <template slot="title">
                      <font color="white">已过期:{{ honourAuth.downloadDeadline }}</font>
                    </template>
                    <font style="cursor: not-allowed;">下载附件</font>
                  </a-tooltip>
                </template>
              </template>
              <template v-else>
                <a-tooltip>
                  <template slot="title">
                    <font color="white">无权下载</font>
                  </template>
                  <font style="cursor: not-allowed;">下载附件</font>
                </a-tooltip>
              </template>

              <a-divider type="vertical" v-has="'user:deleteHonour'" v-show="record.status < 1"/>

              <a-popconfirm v-has="'user:deleteHonour'" v-show="record.status< 1" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
              </a-popconfirm>

            </span>

            <span slot="authority" slot-scope="text,record">
              <template v-if="record.authorityType == 99">
                <font>{{ text }}({{ record.authorityTxt }})</font>
              </template>
              <template v-else>
                <font>{{ text }}</font>
              </template>
            </span>

          </a-table>
        </div>
        <!-- table区域-end -->
      </a-collapse-panel>

      <a-collapse-panel key="2" header="证书类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(2)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <cer-excel-modal ref="cerExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(2)">
          </cer-excel-modal>
          <template v-if="exportAuthList[2] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(2)">导出EXCEL</a-button>
          </template>

          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group @change="onColSettingsChangeByKey(2)" v-model="cerColumns" :defaultValue="cerColumns">
                <a-row>
                  <template v-for="(item,index) in defCerColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-certificate ref="honourCertificateInfo" @certificateFn="certificateFn"></honour-certificate>
      </a-collapse-panel>
      <a-collapse-panel key="3" header="物图类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(3)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <article-excel-modal ref="articleExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(3)">
          </article-excel-modal>
          <template v-if="exportAuthList[3] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(3)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group
                @change="onColSettingsChangeByKey(3)"
                v-model="articleColumns"
                :defaultValue="articleColumns">
                <a-row>
                  <template v-for="(item,index) in defArticleColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-article ref="honourArticleInfo" @articleFn="articleFn"></honour-article>
      </a-collapse-panel>
      <a-collapse-panel key="4" header="报道类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(4)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <report-excel-modal ref="reportExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(4)">
          </report-excel-modal>
          <template v-if="exportAuthList[4] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(4)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group
                @change="onColSettingsChangeByKey(4)"
                v-model="reportColumns"
                :defaultValue="reportColumns">
                <a-row>
                  <template v-for="(item,index) in defReportColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-report ref="honourReportInfo" @reportFn="reportFn"></honour-report>
      </a-collapse-panel>
      <a-collapse-panel key="5" header="协议类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(5)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <agree-excel-modal ref="agreeExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(5)">
          </agree-excel-modal>
          <template v-if="exportAuthList[5] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(5)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group
                @change="onColSettingsChangeByKey(5)"
                v-model="agreeColumns"
                :defaultValue="agreeColumns">
                <a-row>
                  <template v-for="(item,index) in defAgreeColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-agree ref="honourAgreeInfo" @agreeFn="agreeFn"></honour-agree>
      </a-collapse-panel>
      <a-collapse-panel key="6" header="人才类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(6)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <person-excel-modal ref="personExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(6)">
          </person-excel-modal>
          <template v-if="exportAuthList[6] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(6)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group
                @change="onColSettingsChangeByKey(6)"
                v-model="personColumns"
                :defaultValue="personColumns">
                <a-row>
                  <template v-for="(item,index) in defPersonColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-person ref="honourPersonInfo" @personFn="personFn"></honour-person>
      </a-collapse-panel>
      <a-collapse-panel key="7" header="课题类">
        <span slot="extra" style="position: relative;top: -5px;">
          <a-button
            v-has="'user:honourImport'"
            type="dashed"
            @click="importXls(7)"
            icon="import"
            style="margin-right: 8px">导入EXCEL</a-button>
          <project-excel-modal ref="projectExcelModal" @ok="modalFormOk" @exportDownSearch="exportDownSearch(7)">
          </project-excel-modal>
          <template v-if="exportAuthList[7] == 1">
            <a-button type="dashed" @click.stop="exportXlsByKey(7)">导出EXCEL</a-button>
          </template>
          <a-popover title="自定义列" trigger="hover" placement="leftBottom">
            <template slot="content">
              <a-checkbox-group
                @change="onColSettingsChangeByKey(7)"
                v-model="projectColumns"
                :defaultValue="projectColumns">
                <a-row>
                  <template v-for="(item,index) in defProjectColumns" :index="index">
                    <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12">
                        <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a>
              <!-- <a-icon type="setting" />设置 -->
              <a-button type="dashed" icon="setting" style="margin-left: 16px;">设置</a-button>
            </a>
          </a-popover>
        </span>
        <honour-project ref="honourProjectInfo" @projectFn="projectFn"></honour-project>
      </a-collapse-panel>
    </a-collapse>

    <honour-file-modal ref="modalForm" @ok="modalFormOk"></honour-file-modal>
    <imag-preview ref="imgPerview" @ok="modalFormOk"></imag-preview>
    <honour-detail-modal ref="detail" @ok="modalFormOk"></honour-detail-modal>
  </a-card>
</template>

<script>
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import { checkDuplicateHonourByTitle } from '@/api/api'
  import moment from 'moment'
  import Vue from 'vue'
  import HonourCertificate from './modules/HonourCertificate1'
  import HonourReport from './modules/HonourReport1'
  import HonourAgree from './modules/HonourAgree1'
  import HonourArticle from './modules/HonourArticle1'
  import HonourPerson from './modules/HonourPerson1'
  import HonourProject from './modules/HonourProject1'
  import HonourFileModal from './modules/HonourFileModal'
  import ImagPreview from './modules/ImagPreview'
  import ColumnJson from './json/columns'
  import fileExcelModal from './excel/fileExcelModal'
  import cerExcelModal from './excel/cerExcelModal'
  import articleExcelModal from './excel/articleExcelModal'
  import reportExcelModal from './excel/reportExcelModal'
  import agreeExcelModal from './excel/agreeExcelModal'
  import personExcelModal from './excel/personExcelModal'
  import projectExcelModal from './excel/projectExcelModal'
  import HonourDetailModal from './modules/HonourDetailModal'
  import {
    getAction,
    getFileAccessHttpUrl,
    postAction
  } from '@/api/manage'
  import store from '../../store'
  export default {
    name: 'HonourFileList',
    mixins: [JeecgListMixin],
    components: {
      HonourReport,
      HonourAgree,
      HonourPerson,
      HonourProject,
      HonourArticle,
      HonourCertificate,
      HonourFileModal,
      ImagPreview,
      fileExcelModal,
      cerExcelModal,
      articleExcelModal,
      reportExcelModal,
      agreeExcelModal,
      personExcelModal,
      projectExcelModal,
      HonourDetailModal
    },
    data() {
      return {
        statusType: [],
        toggleSearchStatus: false,
        settingColumns: [],
        expandIconPosition: 'left',
        activeKey: ['1', '2', '3', '4', '5', '6', '7'],
        description: '荣誉管理页面',
        cerColumns: [],
        defCerColumns: [],
        articleColumns: [],
        defArticleColumns: [],
        reportColumns: [],
        defReportColumns: [],
        agreeColumns: [],
        defAgreeColumns: [],
        personColumns: [],
        defPersonColumns: [],
        projectColumns: [],
        defProjectColumns: [],
        exportAuthList: [0, 0, 0, 0, 0, 0, 0, 0],
        /* 分页参数 */
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
        defColumns: [{
            title: '标题',
            align: 'center',
            dataIndex: 'title'
          },
          {
            title: '负责人',
            align: 'center',
            dataIndex: 'leader'
          },
          {
            title: '作品名称',
            align: 'center',
            dataIndex: 'workName'
          },
          {
            title: '荣誉等级',
            align: 'center',
            dataIndex: 'honourLevel_dictText'
          },
          {
            title: '发文字号',
            align: 'center',
            dataIndex: 'articleNum'
          },
          {
            title: '发文时间',
            align: 'center',
            dataIndex: 'acquireTime'
          },
          {
            title: '发文机关',
            align: 'center',
            dataIndex: 'authorityType_dictText',
            scopedSlots: {
              customRender: 'authority'
            }
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText',
            customRender: function(t, r, index) {
              if (r.achievementType === 99) {
                return r.achievementType_dictText + '(' + r.achievementTypeTxt + ')'
              } else {
                return r.achievementType_dictText
              }
            }
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText',
            customRender: function(t, r, index) {
              if (r.achievementLevelType === 99) {
                return t + '(' + r.achievementLevelTxt + ')'
              } else {
                return t
              }
            }
          },
          {
            title: '团队成员',
            width: 100,
            align: 'center',
            dataIndex: 'teamPersons'
          },
          {
            title: '归属专业',
            align: 'center',
            dataIndex: 'majorName'
          },
          {
            title: '审核状态',
            align: 'center',
            dataIndex: 'status',
            scopedSlots: {
              customRender: 'status'
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
        // 表头
        columns: [{
            title: '标题',
            align: 'center',
            dataIndex: 'title'
          },
          {
            title: '负责人',
            align: 'center',
            dataIndex: 'leader'
          },
          {
            title: '发文字号',
            align: 'center',
            dataIndex: 'articleNum'
          },
          {
            title: '发文时间',
            align: 'center',
            dataIndex: 'acquireTime'

          },
          {
            title: '发文机关',
            align: 'center',
            dataIndex: 'authorityType_dictText'
          },
          {
            title: '成果类别',
            align: 'center',
            dataIndex: 'achievementType_dictText'
          },
          {
            title: '成果级别',
            align: 'center',
            dataIndex: 'achievementLevel_dictText'
          },
          {
            title: '团队成员',
            width: 100,
            align: 'center',
            dataIndex: 'teamPersons'
          },
          {
            title: '归属专业',
            align: 'center',
            dataIndex: 'majorName'
          },
          {
            title: '审核状态',
            align: 'center',
            dataIndex: 'status',
            scopedSlots: {
              customRender: 'status'
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
        // 分页参数
        url: {
          list: '/honour/file/queryHonourFileList',
          submit: '/honour/file/submitFile',
          exportXls: '/honour/file/exportXls',
          exportAllXls: '/honour/exportAllXls',
          auth: '/honour/auth/getMyAuth',
          authList: '/honour/auth/getMyAuthList',
          delete: '/honour/file/delete'
        }
      }
    },
    created() {
      this.initColumns()
      this.getMyAuth(1)
      this.getMyAuthList()
      this.initAll(ColumnJson)
    },
    methods: {
      getMyAuthList() {
        var that = this
        var exportAuthList = [0, 0, 0, 0, 0, 0, 0, 0]
        getAction(this.url.authList, {}).then((res) => {
          if (res.success && res.result) {
            var data = res.result
            var now = new Date().getTime()
            for (var i = 0; i < data.length; i++) {
              data[i].currentTime = now
              var index = data[i].honourType
              if (data[i].exportAuth == 1) {
                exportAuthList[index] = 1
              } else if (data[i].exportAuth == 2) {
                var exportTime = Date.parse(new Date(data[i].exportDeadline))
                if (now - exportTime < 0) {
                  exportAuthList[index] = 1
                }
              }
            }
            that.exportAuthList = exportAuthList
            console.log(exportAuthList)
          }
        })
      },
      importXls(type) {
        if (type === 1) {
          this.$refs.fileExcelModal.add()
          this.$refs.fileExcelModal.title = '导入文件类荣誉'
          this.$refs.fileExcelModal.disableSubmit = false
        } else if (type === 2) {
          this.$refs.cerExcelModal.add()
          this.$refs.cerExcelModal.title = '导入证书类荣誉'
          this.$refs.cerExcelModal.disableSubmit = false
        } else if (type === 3) {
          this.$refs.articleExcelModal.add()
          this.$refs.articleExcelModal.title = '导入物图类荣誉'
          this.$refs.articleExcelModal.disableSubmit = false
        } else if (type === 4) {
          this.$refs.reportExcelModal.add()
          this.$refs.reportExcelModal.title = '导入报道类荣誉'
          this.$refs.reportExcelModal.disableSubmit = false
        } else if (type === 5) {
          this.$refs.agreeExcelModal.add()
          this.$refs.agreeExcelModal.title = '导入协议类荣誉'
          this.$refs.agreeExcelModal.disableSubmit = false
        } else if (type === 6) {
          this.$refs.personExcelModal.add()
          this.$refs.personExcelModal.title = '导入人才类荣誉'
          this.$refs.personExcelModal.disableSubmit = false
        } else if (type === 7) {
          this.$refs.projectExcelModal.add()
          this.$refs.projectExcelModal.title = '导入课题类荣誉'
          this.$refs.projectExcelModal.disableSubmit = false
        } else {
          this.$message.error('未知类型!')
        }
      },
      moment,
      initAll(ColumnJson) {
        this.cerColumns = ColumnJson.cerColumns
        this.defCerColumns = ColumnJson.defCerColumns
        this.articleColumns = ColumnJson.articleColumns
        this.defArticleColumns = ColumnJson.defArticleColumns
        this.reportColumns = ColumnJson.reportColumns
        this.defReportColumns = ColumnJson.defReportColumns
        this.agreeColumns = ColumnJson.agreeColumns
        this.defAgreeColumns = ColumnJson.defAgreeColumns
        this.personColumns = ColumnJson.personColumns
        this.defPersonColumns = ColumnJson.defPersonColumns
        this.projectColumns = ColumnJson.projectColumns
        this.defProjectColumns = ColumnJson.defProjectColumns
      },
      downloadAppendix(record) {
        record.honourClass = 1
        this.$refs.imgPerview.add(record)
        this.$refs.imgPerview.visible = true
      },
      searchQuery: function() {
        if (this.statusType != null && this.statusType.length > 0) {
          var status = this.statusType[0]
          this.queryParam.status = ''
          this.queryParam.status = status + ''
        }
        this.searchSelect()
      },
      searchSelect() {
        if (!this.queryParam.beginTime) {
          this.queryParam.beginTime = ''
        } else {
          this.queryParam.beginTime = this.queryParam.beginTime.format('YYYY-MM-DD')
        }
        if (!this.queryParam.endTime) {
          this.queryParam.endTime = ''
        } else {
          this.queryParam.endTime = this.queryParam.endTime.format('YYYY-MM-DD')
        }

        if (this.queryParam.hasOwnProperty('authorityType')) {
          this.loadData()
          this.$refs.honourCertificateInfo.open(this.queryParam)
          this.$refs.honourProjectInfo.open(this.queryParam)
          this.$refs.honourArticleInfo.open(this.queryParam)

          this.activeKey = ['1', '2', '3', '7']
        } else if (this.queryParam.hasOwnProperty('achievementType') || this.queryParam.hasOwnProperty(
            'achievementLevel')) {
          this.loadData()
          this.$refs.honourArticleInfo.open(this.queryParam)
          this.$refs.honourReportInfo.open(this.queryParam)
          this.$refs.honourCertificateInfo.open(this.queryParam)
          this.$refs.honourProjectInfo.open(this.queryParam)

          this.activeKey = ['1', '2', '3', '4', '7']
        } else if (this.queryParam.hasOwnProperty('certificateType') || this.queryParam.hasOwnProperty('honourLevel')) {
          this.$refs.honourCertificateInfo.open(this.queryParam)
          this.$refs.honourProjectInfo.open(this.queryParam)
          this.activeKey = ['2', '7']
        } else if (this.queryParam.hasOwnProperty('cooperationType')) {
          this.$refs.honourAgreeInfo.open(this.queryParam)
          this.activeKey = ['5']
        } else if (this.queryParam.hasOwnProperty('articleType')) {
          this.$refs.honourArticleInfo.open(this.queryParam)
          this.activeKey = ['3']
        } else if (this.queryParam.hasOwnProperty('mediumType')) {
          this.$refs.honourReportInfo.open(this.queryParam)
          this.activeKey = ['4']
        } else {
          this.loadData()

          this.$refs.honourReportInfo.open(this.queryParam)
          this.$refs.honourAgreeInfo.open(this.queryParam)
          this.$refs.honourPersonInfo.open(this.queryParam)
          this.$refs.honourArticleInfo.open(this.queryParam)
          this.$refs.honourCertificateInfo.open(this.queryParam)
          this.$refs.honourProjectInfo.open(this.queryParam)
        }
      },
      searchReset() {
        this.queryParam = {}
        this.statusType = []
        this.$refs.honourReportInfo.open(this.queryParam)
        this.$refs.honourAgreeInfo.open(this.queryParam)
        this.$refs.honourPersonInfo.open(this.queryParam)
        this.$refs.honourArticleInfo.open(this.queryParam)
        this.$refs.honourCertificateInfo.open(this.queryParam)
        this.$refs.honourProjectInfo.open(this.queryParam)
        this.loadData(1)
      },
      exportDownSearch(key) {
        console.log(key)
        switch (key) {
          case 2:
            this.$refs.honourCertificateInfo.open(this.queryParam)
            break
          case 3:
            this.$refs.honourArticleInfo.open(this.queryParam)
            break
          case 4:
            this.$refs.honourReportInfo.open(this.queryParam)
            break
          case 5:
            this.$refs.honourAgreeInfo.open(this.queryParam)
            break
          case 6:
            this.$refs.honourPersonInfo.open(this.queryParam)
            break
          case 7:
            this.$refs.honourProjectInfo.open(this.queryParam)
            break
          default:
            this.loadData(1)
            break
        }
      },
      exportXlsByKey(key) {
        switch (key) {
          case 2:
            this.$refs.honourCertificateInfo.exportXls(this.queryParam)
            break
          case 3:
            this.$refs.honourArticleInfo.exportXls(this.queryParam)
            break
          case 4:
            this.$refs.honourReportInfo.exportXls(this.queryParam)
            break
          case 5:
            this.$refs.honourAgreeInfo.exportXls(this.queryParam)
            break
          case 6:
            this.$refs.honourPersonInfo.exportXls(this.queryParam)
            break
          case 7:
            this.$refs.honourProjectInfo.exportXls(this.queryParam)
            break
          default:
            this.exportXls()
            break
        }
      },
      onColSettingsChangeByKey(key) {
        switch (key) {
          case 2:
            this.$refs.honourCertificateInfo.onColSettingsChange(this.cerColumns)
            break
          case 3:
            this.$refs.honourArticleInfo.onColSettingsChange(this.articleColumns)
            break
          case 4:
            this.$refs.honourReportInfo.onColSettingsChange(this.reportColumns)
            break
          case 5:
            this.$refs.honourAgreeInfo.onColSettingsChange(this.agreeColumns)
            break
          case 6:
            this.$refs.honourPersonInfo.onColSettingsChange(this.personColumns)
            break
          case 7:
            this.$refs.honourProjectInfo.onColSettingsChange(this.projectColumns)
            break
          default:
            this.onColSettingsChange(this.settingColumns)
            break
        }
      },
      handleToggleSearch() {
        this.toggleSearchStatus = !this.toggleSearchStatus
      },
      loadData(arg) {
        if (!this.url.list) {
          this.$message.error('请设置url.list属性!')
          return
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        let params = this.getQueryParams() // 查询条件
        if (!params.year) {
          params.year = ''
        } else {
          params.year = params.year.format('YYYY')
        }
        params.create = 'create'
        // params.status = '0';
        getAction(this.url.list, params).then((res) => {
          if (res.success && res.result) {
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
            if (this.ipagination.total == 0) {
              this.activeKey = []
            } else {
              this.activeKey = this.activeKey.filter(t => t != '1')
              this.activeKey.push('1')
            }
          }
        })
      },
      certificateFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '2')
        if (record > 0) {
          this.activeKey.push('2')
        }
      },
      articleFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '3')
        if (record > 0) {
          this.activeKey.push('3')
        }
      },
      reportFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '4')
        if (record > 0) {
          this.activeKey.push('4')
        }
      },
      agreeFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '5')
        if (record > 0) {
          this.activeKey.push('5')
        }
      },
      personFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '6')
        if (record > 0) {
          this.activeKey.push('6')
        }
      },
      projectFn(record) {
        this.activeKey = this.activeKey.filter(t => t != '7')
        if (record > 0) {
          this.activeKey.push('7')
        }
      },

      onColSettingsChange(checkedValues) {
        var key = this.$route.name + ':colsettings'
        Vue.ls.set(key, checkedValues, 7 * 24 * 60 * 60 * 1000)

        this.settingColumns = checkedValues

        const cols = this.defColumns.filter(item => {
          if (item.key == 'rowIndex' || item.dataIndex == 'action') {
            return true
          }
          if (this.settingColumns.includes(item.dataIndex)) {
            return true
          }
          return false
        })

        this.columns = cols
      },
      exportXls() {
        var cells = ''
        if (this.settingColumns.length > 0) {
          for (var i = 0; i < this.settingColumns.length; i++) {
            if (this.settingColumns[i].indexOf('_dictText') != -1) {
              var cell = this.settingColumns[i].replace('_dictText', '')
              cells += cell + ','
            } else if (this.settingColumns[i].indexOf('action') != -1) {

            } else {
              cells += this.settingColumns[i] + ','
            }
          }
        }

        var ids = ''
        for (var a = 0; a < this.selectionRows.length; a++) {
          ids += this.selectionRows[a].id + ','
        }
        console.log(cells)
        let params = this.getQueryParams() // 查询条件
        params.customizeCells = cells

        if (ids.length > 0) {
          params.ids = ids
        }
        params.create = 'create'
        delete params.pageSize
        delete params.pageNo
        getAction(this.url.exportXls, params).then((res) => {
          if (res.success && res.result) {
            console.log(res)
            var url = getFileAccessHttpUrl(res.result)
            var fileName = '文件类荣誉表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      initColumns() {
        // 权限过滤（列权限控制时打开，修改第二个参数为授权码前缀）
        // this.defColumns = colAuthFilter(this.defColumns,"testdemo:");

        var key = this.description + ':colsettings'
        let colSettings = Vue.ls.get(key)
        if (colSettings == null || colSettings == undefined) {
          let allSettingColumns = []
          this.defColumns.forEach(function(item, i, array) {
            allSettingColumns.push(item.dataIndex)
          })
          this.settingColumns = allSettingColumns
          this.columns = this.defColumns
        } else {
          this.settingColumns = colSettings
          const cols = this.defColumns.filter(item => {
            if (item.key == 'rowIndex' || item.dataIndex == 'action') {
              return true
            }
            if (colSettings.includes(item.dataIndex)) {
              return true
            }
            return false
          })
          this.columns = cols
        }
        console.log(this.settingColumns)
      },
      handlePass(record) {
        this.$refs.modalForm.pass(record)
        this.$refs.modalForm.title = '审核文件类荣誉'
        this.$refs.modalForm.disableSubmit = false
      },
      submit(record) {
        var that = this
        console.log(record)
        if (record.status > 0) {
          this.$message.warning('请选择未提交或未通过的荣誉！')
          return false
        }

        var query = {
          id: record.id,
          fields: 'title',
          title: record.title,
          tableId: 1
        }
        checkDuplicateHonourByTitle(query).then((res) => {
            if (res.success) {
              var title = '确认提交'
              var content = '是否提交选中数据?'
              if (res.result) {
                title = '当前系已提交过 [' + record.title + ']'
                content = '是否确认提交?'
              }
              that.$confirm({
                title: title,
                content: content,
                onOk: function() {
                  that.loading = true
                  postAction(that.url.submit, record).then((res) => {
                    if (res.success) {
                      that.$message.success(res.message)
                      that.loadData()
                    } else {
                      that.$message.warning(res.message)
                    }
                  }).finally(() => {
                    that.loading = false
                  })
                }
              })
            } else {
              that.$message.warning(res.message)
            }
        })
      },
      exportAll() {
        var fieldsNames = ['', '', '', '', '', '', '']
        var fileCells = this.getCell(this.settingColumns)
        fieldsNames[0] = fileCells
        var cerCells = this.getCell(this.cerColumns)
        fieldsNames[1] = cerCells
        var articleCells = this.getCell(this.articleColumns)
        fieldsNames[2] = articleCells
        var reportCells = this.getCell(this.reportColumns)
        fieldsNames[3] = reportCells
        var agreeCells = this.getCell(this.agreeColumns)
        fieldsNames[4] = agreeCells
        var personCells = this.getCell(this.personColumns)
        fieldsNames[5] = personCells
        var projectCells = this.getCell(this.projectColumns)
        fieldsNames[6] = projectCells

        let params = this.getQueryParams() // 查询条件
        params.create = 'create'
        var formData = {}
        formData.fieldsNames = fieldsNames
        formData.queryParam = params
        console.log(formData)
        delete params.pageSize
        delete params.pageNo
        postAction(this.url.exportAllXls, formData).then((res) => {
          if (res.success && res.result) {
            console.log(res)
            var url = getFileAccessHttpUrl(res.result)
            var fileName = '荣誉总表'
            let link = document.createElement('a')
            link.style.display = 'none'
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()
          }
        })
      },
      getCell(columns) {
        var cells = ''
        if (columns.length > 0) {
          for (var i = 0; i < columns.length; i++) {
            if (columns[i].indexOf('_dictText') != -1) {
              var cell = columns[i].replace('_dictText', '')
              cells += cell + ','
            } else if (columns[i].indexOf('action') != -1) {

            } else {
              cells += columns[i] + ','
            }
          }
        }
        return cells
      },
      handleDetail(record) {
        record.honourClass = 1
        record.honourClass_dictText = '文件类'
        this.$refs.detail.title = record.honourClass_dictText + '详情'
        this.$refs.detail.detail(record)
        this.$refs.detail.visible = true
      },
      uploadBatchFile() {
        this.$router.push({ path: '/school/batchUploadAppendix' })
      }
    }
  }
</script>
<style scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }

  /deep/.ant-collapse-header {
    background-color: #e6f7ff !important;
    border: 0px solid #91d5ff !important;
  }
</style>
