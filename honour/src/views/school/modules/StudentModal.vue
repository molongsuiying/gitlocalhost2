<template>
  <j-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :fullscreen.sync="fullscreen"
    :switchFullscreen="switchFullscreen"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="16">
          <a-col span="16">
            <a-col span="12">
              <a-form-item label="学院名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input
                  placeholder="请输入学院名称"
                  v-decorator.trim="[ 'collegeName', {}]"
                  :readOnly="true"
                  autocomplete="off" />
              </a-form-item>
            </a-col>
            <a-col span="12">
              <a-form-item label="院系名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input
                  placeholder="请输入院系名称"
                  v-decorator.trim="[ 'departmentName', {}]"
                  :readOnly="true"
                  autocomplete="off" />
              </a-form-item>
            </a-col>
            <a-col span="12">
              <a-form-item label="专业名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input
                  placeholder="请输入专业名称"
                  v-decorator.trim="[ 'majorName', {}]"
                  :readOnly="true"
                  autocomplete="off" />
              </a-form-item>
            </a-col>
            <a-col span="12">
              <a-form-item label="班级名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-input
                  placeholder="请输入专业名称"
                  v-decorator.trim="[ 'bunchName', {}]"
                  :readOnly="true"
                  autocomplete="off" />
              </a-form-item>
            </a-col>
          </a-col>
          <a-col span="8">
            <a-form-item label="头像" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload class="avatar-uploader" text="上传" v-model="fileList"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col span="8">
            <a-form-item label="学生姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                placeholder="请输入学生姓名"
                v-decorator.trim="[ 'studentName', validatorRules.studentName]"
                autocomplete="off" />
            </a-form-item>
          </a-col>
          <a-col span="8">
            <a-form-item label="学生学号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                placeholder="请输入学生学号"
                v-decorator.trim="[ 'studentCode', validatorRules.studentCode]"
                autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="入学日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-month-picker
                style="width: 100%"
                placeholder="请选择入学日期"
                v-decorator="['admissionTime', {
                  initialValue:!model.admissionTime?null:moment(model.admissionTime,dateFormat),
                  rules: [{required: true,message: '请输入入学日期!'}]}]"
                :getCalendarContainer="node => node.parentNode" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-decorator="['sex', validatorRules.sex]" placeholder="请选择性别">
                <a-select-option :value="1">男</a-select-option>
                <a-select-option :value="2">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="出生日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                style="width: 100%"
                placeholder="请选择出生日期"
                v-decorator="['birthday', {initialValue:!model.birthday?null:moment(model.birthday,'YYYY-MM-DD'),rules:[{required:true,message:'请输入出生日期!'}]}]"
                :getCalendarContainer="node => node.parentNode" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="身份证" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入身份证" v-decorator.trim="[ 'idCard', validatorRules.idCard]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="民族" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                placeholder="请输入民族"
                v-decorator.trim="[ 'nation', {rules: [{required: true,message: '请输入民族!'}]}]"
                autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="籍贯" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                placeholder="请输入籍贯"
                v-decorator.trim="[ 'nativePlace', {rules: [{required: true,message: '请输入籍贯!'}]}]"
                autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="户口" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="[ 'registeredResidence', {rules: [{required: true,message: '请选择户口!'}]}]"
                placeholder="请选择户口">
                <a-select-option :value="1">城市</a-select-option>
                <a-select-option :value="2">农村</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="联系方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入联系方式" v-decorator.trim="[ 'mobile', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="地区" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-area-linkage v-decorator="[ 'areaLink', {}]" type="cascader" :getCalendarContainer="node => node.parentNode" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="详细地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入详细地址" v-decorator.trim="[ 'address', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="邮箱地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入邮箱地址" v-decorator.trim="[ 'email', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="身高" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入身高" addon-after="cm" v-decorator.trim="[ 'height', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="8">
            <a-form-item label="体重" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入体重" addon-after="kg" v-decorator.trim="[ 'weight', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>

          <a-col span="24">
            <a-form-item label="备注">
              <a-textarea
                v-decorator="[
                  'remarks',
                  {},
                ]"
                :rows="6"
                placeholder="请输入备注" />
            </a-form-item>
          </a-col>
        </a-row>

      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
  import JImageUpload from '../../../components/jeecg/JImageUpload'
  import JAreaLinkage from '@comp/jeecg/JAreaLinkage'
  import pick from 'lodash.pick'
  import {
    postAction,
    putAction
  } from '@/api/manage'
  import moment from 'moment'
  export default {
    name: 'MajorModal',
    components: {
      JImageUpload,
      JAreaLinkage
    },
    data() {
      return {
        dateFormat: 'YYYY-MM',
        title: '操作',
        visible: false,
        visibleCheck: true,
        fullscreen: false,
        switchFullscreen: true,
        model: {},
        departmentId: '',
        status: 1,
        labelCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 7
          }
        },
        wrapperCol: {
          xs: {
            span: 24
          },
          sm: {
            span: 15
          }
        },
        fileList: [],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          studentName: {
            rules: [{
              required: true,
              message: '请输入学生名称!'
            }]
          },
          studentCode: {
            rules: [{
              required: true,
              message: '请输入学生编号!'
            }]
          },
          sex: {
            rules: [{
              required: true,
              message: '请输入性别!'
            }]
          },
          idCard: {
            rules: [{
              required: true,
              message: '请输入身份证!'
            }]
          }
        },
        url: {
          fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
          addStudent: '/student/add',
          editStudent: '/student/edit'
        },
        currentBunch: {}
      }
    },
    created() {},
    methods: {
      add(currentBunch) {
        this.currentBunch = currentBunch
        this.bunchId = currentBunch.key
        var student = {
          bunchId: this.bunchId,
          bunchName: currentBunch.title
        }
        this.edit(student)
      },
      moment,
      edit(record) {
        this.form.resetFields()
        if (record.hasOwnProperty('id')) {
          setTimeout(() => {
            this.fileList = record.icon
          }, 5)
        }
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'bunchName', 'collegeName', 'departmentName', 'majorName', 'studentName', 'studentCode',
           'sex', 'idCard', 'nation', 'nativePlace', 'registeredResidence', 'areaLink', 'address', 'height', 'weight', 'mobile', 'email', 'remarks'))
        })
      },
      onChose(checked) {
        if (checked) {
          this.status = 1
          this.visibleCheck = true
        } else {
          this.status = 0
          this.visibleCheck = false
        }
      },
      // 确定
      handleOk() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true

            if (!values.admissionTime) {
              values.admissionTime = ''
            } else {
              values.admissionTime = values.admissionTime.format(this.dateFormat)
            }
            if (!values.birthday) {
              values.birthday = ''
            } else {
              values.birthday = values.birthday.format('YYYY-MM-DD')
            }

            let formData = Object.assign(this.model, values)
            if (that.fileList.length > 0) {
              formData.icon = that.fileList
            } else {
              formData.icon = null
            }
            if (!this.model.id) {
              postAction(that.url.addStudent, formData).then((res) => {
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
            } else {
              putAction(that.url.editStudent, formData).then((res) => {
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
          }
        })
      },
      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      normFile(e) {
        if (Array.isArray(e)) {
          return e
        }
        return e && e.fileList
      },
      beforeUpload: function(file) {
        var fileType = file.type
        if (fileType.indexOf('image') < 0) {
          this.$message.warning('请上传图片')
          return false
        }
        // TODO 验证文件大小
      },
      handleChange(info) {
        this.picUrl = ''
        if (info.file.status === 'uploading') {
          this.uploadLoading = true
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response
          this.uploadLoading = false
          console.log(response)
          if (response.success) {
            this.model.avatar = response.message
            this.picUrl = 'Has no pic url yet'
          } else {
            this.$message.warning(response.message)
          }
        }
      }

    }
  }
</script>
