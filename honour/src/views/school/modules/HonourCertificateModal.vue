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
            <a-input placeholder="请输入标题" v-decorator="['title', validatorRules.title]" :disabled="examineFlag" style="width: 73%;"/>
            <a-button type="primary" icon="search" style="margin-left: 10px;" @click="findLikeTitleList" :diabled="infoFlag">
              搜索相同项
            </a-button>
            <title-modal ref="titleModal" @ok="titleModalOk"></title-modal>
          </template>
          <template v-else>
            <a-input placeholder="请输入标题" v-decorator="['title', validatorRules.title]" />
          </template>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="负责人">
          <a-input placeholder="请输入负责人" v-decorator="['leader', {}]" :disabled="examineFlag"/>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="证书类别">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['prizeType', validatorRules.prizeType]"
            @change="onChange">
            <a-radio :value="'1'">
              奖励
            </a-radio>
            <a-radio :value="'99'">
              其他
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="证书类别" v-show="prizeType == 1">
          <a-input-group compact>
            <a-select default-value="studentName" @change="handleChange" :disabled="examineFlag">
              <a-select-option value="studentName">
                学生
              </a-select-option>
              <a-select-option value="teacherName">
                教师
              </a-select-option>
              <a-select-option value="departmentName">
                部门
              </a-select-option>
              <a-select-option value="schoolName">
                学校
              </a-select-option>
            </a-select>

            <a-input
              style="width: 87%"
              v-if="selectName=='departmentName'"
              placeholder="请输入部门名称"
              v-decorator="['departmentName',{}]"
              :disabled="examineFlag" />
            <a-input
              style="width: 87%"
              v-if="selectName=='schoolName'"
              placeholder="请输入学校名称"
              v-decorator="['schoolName',{}]"
              :disabled="examineFlag" />
            <a-input
              style="width: 87%"
              v-if="selectName=='teacherName'"
              placeholder="请输入教师姓名"
              v-decorator="['teacherName',{}]"
              :disabled="examineFlag" />
            <a-input
              style="width: 87%"
              v-if="selectName=='studentName'"
              placeholder="请输入学生姓名"
              v-decorator="['studentName',{}]"
              :disabled="examineFlag" />
          </a-input-group>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="指导教师"
          v-if="selectName == 'studentName' && prizeType == 1">
          <a-input placeholder="请输入指导教师" v-decorator="['teacherName',{}]" :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="证书类别(其他)" v-show="prizeType == 99">
          <a-input placeholder="请输入证书类别(其他)" v-decorator="['otherName',{}]" :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="荣誉等级">
          <a-radio-group :disabled="examineFlag" v-decorator="['honourLevel', {}]">
            <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item label="颁发日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择颁发日期"
            v-decorator="['acquireTime', {initialValue:!model.acquireTime?null:moment(model.acquireTime,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode"
            :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="颁发机关">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['authorityType', validatorRules.authorityType]"
            @change="onAuthorityChange">
            <a-radio v-for="(item, key) in authorityDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="颁发机关(其他)" v-show="authorityFlag">
          <a-input placeholder="请输入颁发机关(其他)" v-decorator="['authorityTxt',{}]" :disabled="examineFlag" />

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
          <a-select mode="tags" placeholder="请输入团队成员姓名" v-model="teamPersons" :disabled="examineFlag" @change="saveTeamsPerson">
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
          <j-image-upload class="avatar-uploader" text="上传" v-model="fileList" :diabled="infoFlag"></j-image-upload>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="理由" v-if="examineFlag && !infoFlag">
          <a-input placeholder="请输入理由" v-model="model.remarks" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="驳回理由" v-show="model.status == -1">
          <a-input placeholder="请输入理由" v-model="model.remarks" disabled/>
        </a-form-item>

      </a-form>
    </a-spin>

    <template slot="footer" >
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
      <template v-if="examineFlag && !infoFlag">
        <a-button key="refuse" type="danger" :loading="confirmLoading" @click="handleRefuse">
          审核驳回
        </a-button>
        <a-button key="pass" type="primary" :loading="confirmLoading" @click="handlePass">
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
  export default {
    name: 'HonourPersonModal',
    components: {
      JSelectDepart,
      JImageUpload,
      TitleModal
    },
    data() {
      return {
        selectName: 'studentName',
        examineFlag: false,
        title: '操作',
        visible: false,
        infoFlag: false,
        authorityFlag: false,
        achievementFlag: false,
        levelFlag: false,
        prizeType: false,
        model: {},
        fileList: [],
        teamPersons: [],
        fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
        dictOptions: [],
        authorityDictOptions: [],
        achievementDictOptions: [],
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
          honourLevel: {
            rules: [{
              required: true,
              message: '请输入荣誉等级'
            }]
          },
          authorityType: {
            rules: [{
              required: true,
              message: '请选择颁发机关'
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
          }
        },
        url: {
          add: '/honour/certificate/saveCertificate',
          edit: '/honour/certificate/editCertificate',
          submit: '/honour/certificate/submitCertificate',
          getAppendix: '/honour/findAppendixByIds',
          pass: '/honour/certificate/pass',
          refuse: '/honour/certificate/refuse'
        }
      }
    },
    created() {
      this.initDictConfig()
    },
    methods: {
      add() {
        this.edit({})
      },
      showInfo(record) {
          this.pass(record)
          this.infoFlag = true
      },
      handleChange(value) {
        this.selectName = value
      },
      moment,
      edit(record) {
        this.teamPersons = []
        this.fileList = []
        this.examineFlag = false
        this.infoFlag = false
        this.form.resetFields()
        this.prizeType = 0
        this.selectName = 'studentName'
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

          this.model.prizeType += ''
          this.model.honourLevel += ''
          this.prizeType = this.model.prizeType

          if (this.model.studentName !== '') {
            this.selectName = 'studentName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'studentName',
                'teacherName'
              ))
            })
          } else if (this.model.teacherName !== '') {
            this.selectName = 'teacherName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'teacherName'
              ))
            })
          } else if (this.model.departmentName !== '') {
            this.selectName = 'departmentName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'departmentName'
              ))
            })
          } else if (this.model.schoolName !== '') {
            this.selectName = 'schoolName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'schoolName'
              ))
            })
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

          var persons = this.model.teamPersons
          if (persons !== null && persons.length !== 0) {
            this.teamPersons = persons.split(',')
          }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'leader',
            'prizeType',
            'otherName',
            'honourLevel',
            'authorityType',
            'authorityTxt',
            'achievementType',
            'achievementTypeTxt',
            'achievementLevel',
            'achievementLevelTxt'
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
        // 初始化字典 - 性别
        initDictOptions('honour_level').then((res) => {
          if (res.success) {
            this.dictOptions = res.result
          }
        })
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
        this.teamPersons = []
      },
      saveTeamsPerson(teamPersons) {
        console.log(teamPersons)
        if (teamPersons.length !== 0) {
          this.model.teamPersons = teamPersons.join(',')
        }
      },
      onLoadMajor(txt, label) {
        console.log(txt)
        console.log(label)
        this.model.majorId = txt
      },
      onChange(e) {
        if (e.target.value == '99') {
          this.prizeType = e.target.value
        } else {
          this.prizeType = e.target.value
        }
      },
      onAuthorityChange(e) {
        if (e.target.value == '99') {
          this.authorityFlag = true
        } else {
          this.authorityFlag = false
        }
      },
      onAchievementChange(e) {
        if (e.target.value == '99') {
          this.achievementFlag = true
        } else {
          this.achievementFlag = false
        }
      },
      onLevelChange(e) {
        if (e.target.value == '99') {
          this.levelFlag = true
        } else {
          this.levelFlag = false
        }
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      findLikeTitleList() {
        this.$refs.titleModal.add(this.model, 2)
        this.$refs.titleModal.visible = true
      },
      titleModalOk() {

      },
      pass(record) {
        this.teamPersons = []
        this.visible = true
        this.infoFlag = false
        record.remarks = ''
        this.form.resetFields()
        this.prizeType = 0
        this.selectName = 'studentName'
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

          this.model.prizeType += ''
          this.model.honourLevel += ''
          this.prizeType = this.model.prizeType

          if (this.model.studentName !== '') {
            this.selectName = 'studentName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'studentName',
                'teacherName'
              ))
            })
          } else if (this.model.teacherName !== '') {
            this.selectName = 'teacherName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'teacherName'
              ))
            })
          } else if (this.model.departmentName !== '') {
            this.selectName = 'departmentName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'departmentName'
              ))
            })
          } else if (this.model.schoolName !== '') {
            this.selectName = 'schoolName'
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model,
                'schoolName'
              ))
            })
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

          var persons = this.model.teamPersons
          if (persons !== null && persons.length !== 0) {
            this.teamPersons = persons.split(',')
          }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'leader',
            'prizeType',
            'buildProjectCharge',
            'overProjectCharge',
            'otherName',
            'honourLevel',
            'authorityType',
            'authorityTxt',
            'achievementType',
            'achievementTypeTxt',
            'achievementLevel',
            'achievementLevelTxt'
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

            if (values.prizeType == 1) {
              if (this.selectName == 'studentName') {
                values.departmentName = ''
                values.schoolName = ''
              } else if (this.selectName == 'teacherName') {
                values.studentName = ''
                values.departmentName = ''
                values.schoolName = ''
              } else if (this.selectName == 'departmentName') {
                values.studentName = ''
                values.teacherName = ''
                values.schoolName = ''
              } else if (this.selectName == 'departmentName') {
                values.studentName = ''
                values.teacherName = ''
                values.departmentName = ''
              }
            }

            var query = {
              fields: 'title',
              title: values.title,
              tableId: 2
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
          console.log(values)
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

          if (values.prizeType == 1) {
            if (this.selectName == 'studentName') {
              values.departmentName = ''
              values.schoolName = ''
            } else if (this.selectName == 'teacherName') {
              values.studentName = ''
              values.departmentName = ''
              values.schoolName = ''
            } else if (this.selectName == 'departmentName') {
              values.studentName = ''
              values.teacherName = ''
              values.schoolName = ''
            } else if (this.selectName == 'departmentName') {
              values.studentName = ''
              values.teacherName = ''
              values.departmentName = ''
            }
          }

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
/deep/.ant-select-selection--multiple .ant-select-selection__choice{
    background-color: #ffc53d;
  }
</style>
