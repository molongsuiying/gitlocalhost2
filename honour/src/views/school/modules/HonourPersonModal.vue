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
            <a-button type="primary" icon="search" style="margin-left: 10px;" @click="findLikeTitleList" :disabled="infoFlag">
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

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="人才证书">
          <a-radio-group
            :disabled="examineFlag"
            v-decorator="['certificateType', validatorRules.certificateType]"
            @change="onChange">
            <a-radio v-for="(item, key) in cerDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="人才证书(其他)" v-show="txtFlag">
          <a-input placeholder="请输入人才证书(其他)" v-decorator="['certificateTypeTxt',{}]" :disabled="examineFlag" />

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="授发单位">
          <a-input
            placeholder="请输入授发单位"
            v-decorator="['acquireUnit', validatorRules.acquireUnit]"
            :disabled="examineFlag" />
        </a-form-item>

        <a-form-item label="授发日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择授发日期"
            v-decorator="['acquireTime', {initialValue:!model.acquireTime?null:moment(model.acquireTime,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode"
            :disabled="examineFlag" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="团队成员">
          <a-select mode="tags" placeholder="请输入团队成员姓名" v-model="teamPersons" :disabled="examineFlag" @change="saveTeamsPerson">
          </a-select>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="归属专业">
          <!-- <jtree-select-major v-model="model.majorId" placeholder="请选择归属专业" :disabled="examineFlag"></jtree-select-major> -->
          <j-select-depart v-model="model.majorId" :trigger-change="true" customReturnField="id" :disabled="examineFlag"></j-select-depart>
        </a-form-item>

        <a-form-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-image-upload class="avatar-uploader" text="上传" v-model="fileList" :disabled="infoFlag"></j-image-upload>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="理由" v-show="examineFlag && !infoFlag">
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
  import { checkDuplicateHonourByTitle } from '@/api/api'
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
  export default {
    name: 'HonourPersonModal',
    components: {
      JSelectDepart,
      JImageUpload,
      TitleModal
    },
    data() {
      return {
        title: '操作',
        visible: false,
        txtFlag: false,
        examineFlag: false,
        infoFlag: false,
        model: {},
        fileList: [],
        teamPersons: [],
        fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
        cerDictOptions: [],
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
          acquireUnit: {
            rules: [{
              required: true,
              message: '请输入授发单位'
            }]
          },
          certificateType: {
            rules: [{
              required: true,
              message: '请选择人才证书'
            }]
          }
        },
        url: {
          add: '/honour/person/savePerson',
          edit: '/honour/person/editPerson',
          submit: '/honour/person/submitPerson',
          getAppendix: '/honour/findAppendixByIds',
          pass: '/honour/person/passPerson',
          refuse: '/honour/person/refusePerson'
        }
      }
    },
    created() {
      this.initDictConfig()
    },
    methods: {
      add() {
        this.fileList = []
        this.edit({})
      },
      findLikeTitleList() {
        this.$refs.titleModal.add(this.model, 6)
        this.$refs.titleModal.visible = true
      },
      titleModalOk() {

      },
      showInfo(record) {
          this.pass(record)
          this.infoFlag = true
      },
      moment,
      edit(record) {
        this.fileList = []
        this.teamPersons = []
        this.examineFlag = false
        this.infoFlag = false
        this.form.resetFields()
        this.txtFlag = false
        this.model = Object.assign({}, record)
        this.visible = true
        console.log(this.model)
        if (this.model.id) {
          if (this.model.appendixIds != null) {
            this.getAppendix(this.model.appendixIds)
          }
          this.model.certificateType += ''
          if (this.model.certificateType == '99') {
            this.txtFlag = true
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
            'acquireUnit',
            'certificateType',
            'certificateTypeTxt'
          ))
        })
      },
      saveTeamsPerson(teamPersons) {
        console.log(teamPersons)
        if (teamPersons.length !== 0) {
          this.model.teamPersons = teamPersons.join(',')
        }
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
        initDictOptions('certificate_type').then((res) => {
          if (res.success) {
            this.cerDictOptions = res.result
          }
        })
      },
/*      onLoadMajor(txt, label) {
        console.log(txt)
        console.log(label)
        this.model.majorId = txt
      }, */
      onChange(e) {
        if (e.target.value == '99') {
          this.txtFlag = true
        } else {
          this.txtFlag = false
        }
      },
      pass(record) {
        this.teamPersons = []
        this.visible = true
        this.infoFlag = false
        record.remarks = ''
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.txtFlag = false
        if (this.model.id) {
          if (this.model.appendixIds != null) {
            this.getAppendix(this.model.appendixIds)
          }
          this.model.certificateType += ''
          if (this.model.certificateType == '99') {
            this.txtFlag = true
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
            'acquireUnit',
            'certificateType',
            'certificateTypeTxt'
          ))
        })
        this.examineFlag = true
      },

      close() {
        this.$emit('close')
        this.visible = false
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

            var query = {
              fields: 'title',
              title: values.title,
              tableId: 6
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

          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
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
