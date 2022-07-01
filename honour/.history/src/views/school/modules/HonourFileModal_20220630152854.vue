<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标题">
          <template v-if="examineFlag">
            <a-input
              placeholder="请输入标题"
              v-decorator="['title', validatorRules.title]"
              :disabled="examineFlag"
              style="width: 73%;" />
            <a-button
              type="primary"
              icon="search"
              style="margin-left: 10px;"
              @click="findLikeTitleList"
              :disabled="infoFlag">
              搜索相同项
            </a-button>
            <title-modal ref="titleModal" @ok="titleModalOk"></title-modal>
          </template>
          <template v-else>
            <a-input placeholder="请输入标题" v-decorator="['title', validatorRules.title]" />
          </template>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="负责人">
          <a-input placeholder="请输入负责人" v-decorator="['leader', {}]" :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作品名称">
          <a-input placeholder="请输入作品名称" :disabled="examineFlag" v-decorator="['workName', validatorRules.workName]" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="发文字号">
          <a-input
            placeholder="请输入发文字号"
            v-decorator="['articleNum', validatorRules.articleNum]"
            :disabled="examineFlag" />
        </a-form-item>

        <a-form-item label="发文日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择发文日期"
            v-decorator="['acquireTime', {initialValue:!model.acquireTime?null:moment(model.acquireTime,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode"
            :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="发文机关">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['authorityType', validatorRules.authorityType]"
            @change="onAuthorityChange">
            <a-radio v-for="(item, key) in authorityDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="发文机关(其他)" v-show="authorityFlag">
          <a-input placeholder="请输入发文机关(其他)" v-decorator="['authorityTxt',{}]" :disabled="examineFlag" />

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="荣誉等级">
          <a-radio-group :disabled="examineFlag" v-decorator="['honourLevel', {}]">
            <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成果类别">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['achievementType', validatorRules.achievementType]"
            @change="onAchievementChange">
            <a-radio v-for="(item, key) in achievementDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成果类别(其他)" v-show="achievementFlag">
          <a-input placeholder="请输入成果类别(其他)" v-decorator="['achievementTypeTxt',{}]" :disabled="examineFlag" />

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成果级别">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['achievementLevel', validatorRules.achievementLevel]"
            @change="onLevelChange">
            <a-radio v-for="(item, key) in levelDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成果级别(其他)" v-show="levelFlag">
          <a-input placeholder="请输入成果级别(其他)" v-decorator="['achievementLevelTxt',{}]" :disabled="examineFlag" />

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="团队成员">
          <a-select
            mode="tags"
            placeholder="请输入团队成员姓名"
            v-model="teamPersons"
            :disabled="examineFlag"
            @change="saveTeamsPerson">
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="归属专业">
          <!-- <jtree-select-major
            v-model="model.majorId"
            placeholder="请选择归属专业"
            dictCode="zd_major,major_name,id,1=1 order by major_code"
            :disabled="examineFlag"></jtree-select-major> -->
          <j-select-depart v-model="model.majorId" :trigger-change="true" customReturnField="id" :disabled="examineFlag"></j-select-depart>
        </a-form-item>

        <a-form-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-image-upload class="avatar-uploader" text="上传" v-model="fileList" :disabled="infoFlag"></j-image-upload>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="理由" v-if="examineFlag && !infoFlag">
          <a-input placeholder="请输入理由" v-model="model.remarks" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="驳回理由" v-show="model.status == -1">
          <a-input placeholder="请输入理由" v-model="model.remarks" disabled />
        </a-form-item>

      </a-form>
    </a-spin>

    <template slot="footer">
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
      <template v-if="examineFlag && !infoFlag">
        <a-button  type="danger"  @click="backTask">
          审核驳回
        </a-button>
        <a-button  type="primary" @click="passTask">
          审核通过
        </a-button>
      </template>
      <template v-else-if="!infoFlag">
        <a-button key="save" type="primary" :loading="confirmLoading" @click="handleSave">
          保存草稿
        </a-button>
        <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
          上传提交
        </a-button>
      </template>
    </template>
  </a-modal>
</template>

<script>
 import { JeecgListMixin } from '../../../mixins/JeecgListMixin'
 
  import pick from 'lodash.pick'
  import moment from 'moment'
  import {
    httpAction,
    getAction
  } from '@/api/manage'
  import {
    initDictOptions
  } from '@/components/dict/JDictSelectUtil'
  // import JtreeSelectMajor from './JtreeSelectMajor'
  import JSelectDepart from './JSelectDepart'
  import JImageUpload from './JImageUpload'
  import TitleModal from '../examine/TitleModal'
  import { checkDuplicateHonourByTitle } from '@/api/api'
  import kuayemiandiaoyong from '../../../utils/kuayemiandiaoyong'
  export default {
    name: 'HonourFileModal',
    components: {
      JSelectDepart,
      JImageUpload,
      TitleModal,
      kuayemiandiaoyong
    },
    mixins:[JeecgListMixin],
    props: {
      /* 全局禁用，可表示查看 */
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      /* 流程数据 */
      processData: {
        type: Object,
        default: () => {
          return {
            beginTime: null,
          }
        },
        required: false
      },
      /* 是否新增 */
      isNew: {
        type: Boolean,
        default: false,
        required: false
      },
      /* 是否处理流程 */
      task: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data() {
      return {
        title: '操作',
        examineFlag: false,
        infoFlag: false,
        visible: false,
        authorityFlag: false,
        achievementFlag: false,
        levelFlag: false,
        model: {},
        fileList: [],
        teamPersons: [],
        fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
        authorityDictOptions: [],
        achievementDictOptions: [],
        dictOptions: [],
        levelDictOptions: [],
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
        confirmLoading: false,
        dateFormat: 'YYYY-MM-DD',
        form: this.$form.createForm(this),
        validatorRules: {
          title: {
            rules: [{
                required: true,
                message: '请输入标题'
              }

            ]
          },
          articleNum: {
            rules: [{
              required: true,
              message: '请输入发文字号'
            }]
          },
          authorityType: {
            rules: [{
              required: true,
              message: '请选择发文机关'
            }]
          },
          achievementType: {
            rules: [{
              required: true,
              message: '请选择成果类别'
            }]
          },
          achievementLevel: {
            rules: [{
              required: true,
              message: '请选择成果级别'
            }]
          },
          workName: {
            rules: [{
              required: true,
              message: '请选择作品名称'
            }]
          }
        },
        url: {
          add: '/honour/file/saveFile',
          edit: '/honour/file/editFile',
          submit: '/honour/file/submitFile',
          getAppendix: '/honour/findAppendixByIds',
          pass: '/honour/file/pass',
          refuse: '/honour/file/refuse',
        }
      }
    },
    created() {
      this.initDictConfig()
      // this.pass()
    },
    methods: {
      add() {
        this.edit({})
      },
      showInfo(record) {
        this.pass()
        this.infoFlag = true
      },
      moment,
      edit(record) {
        this.teamPersons = []
        this.fileList = []
        this.examineFlag = false
        this.infoFlag = false
        this.form.resetFields()
        this.authorityFlag = false
        this.achievementFlag = false
        this.levelFlag = false
        this.model = Object.assign({}, record)
        this.visible = true
        console.log(this.model)
        if (this.model.id) {
          if (this.model.appendixIds != null) {
            this.getAppendix(this.model.appendixIds)
          }

          this.model.authorityType += ''
          if (this.model.authorityType === '99') {
            this.authorityFlag = true
          }
          this.model.achievementType += ''
          if (this.model.achievementType === '99') {
            this.achievementFlag = true
          }

          this.model.achievementLevel += ''
          if (this.model.achievementLevel === '99') {
            this.levelFlag = true
          }

          var persons = this.model.teamPersons
          if (persons !== null && persons.length !== 0) {
            this.teamPersons = persons.split(',')
          }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'leader',
            'articleNum',
            'authorityType',
            'authorityTxt',
            'achievementType',
            'achievementTypeTxt',
            'achievementLevel',
            'achievementLevelTxt',
            'workName',
            'honourLevel'
          ))
        })
      },
      getAppendix(appendixIds) {
        var that = this
        var params = {
          appendixIds: appendixIds
        }
        this.fileList = []
        getAction(this.url.getAppendix, params).then((res) => {
          if (res.success) {
            var appendixs = res.result

            for (var i = 0; i < appendixs.length; i++) {
              var temp = {
                path: appendixs[i].fileUrl,
                appendixId: appendixs[i].id,
                type: appendixs[i].fileType
              }
              this.fileList.push(temp)
            }
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      initDictConfig() {
        initDictOptions('certificate_authority').then((res) => {
          if (res.success) {
            this.authorityDictOptions = res.result
          }
        })
        initDictOptions('achievement_type').then((res) => {
          if (res.success) {
            this.achievementDictOptions = res.result
          }
        })
        initDictOptions('achievement_level').then((res) => {
          if (res.success) {
            this.levelDictOptions = res.result
          }
        })
        initDictOptions('honour_level').then((res) => {
          if (res.success) {
            this.dictOptions = res.result
          }
        })
        this.teamPersons = []
      },
      onLoadMajor(txt, label) {
        console.log(txt)
        console.log(label)
        this.model.majorId = txt
      },
      onAuthorityChange(e) {
        if (e.target.value === '99') {
          this.authorityFlag = true
        } else {
          this.authorityFlag = false
        }
      },
      onAchievementChange(e) {
        if (e.target.value === '99') {
          this.achievementFlag = true
        } else {
          this.achievementFlag = false
        }
      },
      onLevelChange(e) {
        if (e.target.value === '99') {
          this.levelFlag = true
        } else {
          this.levelFlag = false
        }
      },
      close() {
        kuayemiandiaoyong.$emit('close',this.model)
        this.visible = false
      },
      findLikeTitleList() {
        this.$refs.titleModal.add(this.model, 1)
        this.$refs.titleModal.visible = true
      },
      titleModalOk() {

      },
      saveTeamsPerson(teamPersons) {
        console.log(teamPersons)
        if (teamPersons.length !== 0) {
          this.model.teamPersons = teamPersons.join(',')
        }
      },
      pass(record) {
        this.btndisabled = true
        this.form.resetFields()
        this.infoFlag = false
        record.remarks = ''
        this.authorityFlag = false
        this.achievementFlag = false
        this.levelFlag = false
        this.model = Object.assign({}, record)
        this.visible = true
        console.log(this.model)
        if (this.model.id) {
          if (this.model.appendixIds != null) {
            this.getAppendix(this.model.appendixIds)
          }
          this.model.authorityType += ''
          if (this.model.authorityType == '99') {
            this.authorityFlag = true
          }
          this.model.achievementType += ''
          if (this.model.achievementType == '99') {
            this.achievementFlag = true
          }

          this.model.achievementLevel += ''
          if (this.model.achievementLevel == '99') {
            this.levelFlag = true
          }
          // var persons = this.model.teamPersons
          // if (persons !== null && persons.length !== 0) {
          //   this.teamPersons = persons.split(',')
          // }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'leader',
            'articleNum',
            'authorityType',
            'authorityTxt',
            'achievementType',
            'achievementTypeTxt',
            'achievementLevel',
            'achievementLevelTxt',
            'workName',
            'honourLevel',
            'leader',
            'teamPersons'
          ))
        })
        this.examineFlag = true
      },
      handlePass() {
        var that = this
        that.confirmLoading = true
        let httpurl = this.url.pass
        let method = 'post'

        let formData = Object.assign(this.model, {})
        httpAction(httpurl, formData, method).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.$emit('ok')
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          that.confirmLoading = false
          that.close()
        })
      },
      handleRefuse() {
        var that = this
        that.confirmLoading = true
        let httpurl = this.url.refuse
        let method = 'post'

        let formData = Object.assign(this.model, {})
        httpAction(httpurl, formData, method).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.$emit('ok')
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          that.confirmLoading = false
          that.close()
        })
      },
       /* 通过审批 */
      passTask() {
        kuayemiandiaoyong.$emit('passTask',this.model)
      },
      /* 驳回审批 */
      backTask() {
        // console.log(111)
        kuayemiandiaoyong.$emit('backTask',this.model)
      //  this.$refs.todoManage.backTask(v)
        console.log(111)
      },
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            if (this.model.majorId == '' || this.model.majorId == null) {
              this.$message.warning('请选择归属专业')
              return false
            }

            if (!this.fileList == null || this.fileList.length == 0) {
              this.$message.warning('请上传附件')
              return false
            } else {
              var appendixIds = this.fileList.map((obj) => {
                return obj.appendixId
              }).join(',')
              this.model.appendixIds = appendixIds
            }

            var query = {
              fields: 'title',
              title: values.title,
              tableId: 1
            }

            let httpurl = ''
            let method = ''

            if (!this.model.id) {
              httpurl += this.url.add
              this.model.status = 1
              method = 'post'
            } else {
              httpurl += this.url.edit
              this.model.status = 1
              method = 'put'
              query.id = this.model.id
            }

            checkDuplicateHonourByTitle(query).then((duplicateRes) => {
              if (duplicateRes.success) {
                var title = '确认提交'
                var content = '是否提交选中数据?'
                if (duplicateRes.result) {
                  title = '当前系已提交过 [' + values.title + ']'
                  content = '是否确认提交?'
                }
                that.$confirm({
                  title: title,
                  content: content,
                  onOk: function() {
                    that.confirmLoading = true
                    let formData = Object.assign(that.model, values)
                    httpAction(httpurl, formData, method).then((res) => {
                      if (res.success) {
                        that.$message.success(res.message)
                        that.$emit('ok')
                      } else {
                        that.$message.warning(res.message)
                      }
                    }).finally(() => {
                      that.confirmLoading = false
                      that.close()
                    })
                  }
                })
              } else {
                that.$message.warning(duplicateRes.message)
              }
            })
          }
        })
      },
      handleCancel() {
        this.close()
      },
      handleSave() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (that.model.majorId == '' || that.model.majorId == null) {
            that.$message.warning('请选择归属专业')
            return false
          }

          if (!that.fileList == null || that.fileList.length == 0) {
            that.$message.warning('请上传附件')
            return false
          } else {
            var appendixIds = that.fileList.map((obj) => {
              return obj.appendixId
            }).join(',')
            that.model.appendixIds = appendixIds
          }

          if (!values.acquireTime) {
            values.acquireTime = ''
          } else {
            values.acquireTime = values.acquireTime.format(this.dateFormat)
          }

          // 团队成员

          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            that.model.status = 0
            if (!that.model.id) {
              httpurl += that.url.add
              method = 'post'
            } else {
              httpurl += that.url.edit
              method = 'put'
            }

            console.log(that.model)

            let formData = Object.assign(that.model, values)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              that.close()
            })
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  /deep/.ant-select-selection--multiple .ant-select-selection__choice {
    background-color: #ffc53d;
  }
</style>
