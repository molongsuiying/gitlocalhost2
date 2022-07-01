<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="ruleForm" :model="model" :rules="rules">

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="文件类型">
          <a-select show-search v-model="model.honourType" placeholder="请选择荣誉类型" :disabled="!!model.id">
            <a-select-option :value="1" :disabled="types[1] == 1">
              文件类
            </a-select-option>
            <a-select-option :value="2" :disabled="types[2] == 1">
              证书类
            </a-select-option>
            <a-select-option :value="3" :disabled="types[3] == 1">
              物图类
            </a-select-option>
            <a-select-option :value="4" :disabled="types[4] == 1">
              报道类
            </a-select-option>
            <a-select-option :value="5" :disabled="types[5] == 1">
              协议类
            </a-select-option>
            <a-select-option :value="6" :disabled="types[6] == 1">
              人才类
            </a-select-option>
            <a-select-option :value="7" :disabled="types[7] == 1">
              课题类
            </a-select-option>
          </a-select>
        </a-form-model-item>

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="显示权限" prop="showAuth">
          <a-radio-group v-model="model.showAuth">
            <a-radio :value="0">
              禁止
            </a-radio>
            <a-radio :value="1">
              永久
            </a-radio>
            <a-radio :value="2">
              限时
              <template v-if="model.showAuth==2">
                <font style="margin-left: 10px;margin-right: 10px;">到期时间:</font>
                <a-date-picker
                  v-model="model.showDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;" />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编辑权限" prop="editAuth">
          <a-radio-group v-model="model.editAuth">
            <a-radio :value="0">
              禁止
            </a-radio>
            <a-radio :value="1">
              永久
            </a-radio>
            <a-radio :value="2">
              限时
              <template v-if="model.editAuth==2">
                <font style="margin-left: 10px;margin-right: 10px;">到期时间:</font>
                <a-date-picker
                  v-model="model.editDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;" />
              </template>

            </a-radio>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件权限" prop="downloadAuth">
          <a-radio-group v-model="model.downloadAuth">
            <a-radio :value="0">
              禁止
            </a-radio>
            <a-radio :value="1">
              永久
            </a-radio>
            <a-radio :value="2">
              限时
              <template v-if="model.downloadAuth==2">
                <font style="margin-left: 10px;margin-right: 10px;">到期时间:</font>
                <a-date-picker
                  v-model="model.downloadDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;" />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="导出权限" prop="exportAuth">
          <a-radio-group v-model="model.exportAuth">
            <a-radio :value="0">
              禁止
            </a-radio>
            <a-radio :value="1">
              永久
            </a-radio>
            <a-radio :value="2">
              限时
              <template v-if="model.exportAuth==2">
                <font style="margin-left: 10px;margin-right: 10px;">到期时间:</font>
                <a-date-picker
                  v-model="model.exportDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;" />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="审核权限" prop="examineAuth">
          <a-radio-group v-model="model.examineAuth">
            <a-radio :value="0">
              禁止
            </a-radio>
            <a-radio :value="1">
              永久
            </a-radio>
            <a-radio :value="2">
              限时
              <template v-if="model.examineAuth==2">
                <font style="margin-left: 10px;margin-right: 10px;">到期时间:</font>
                <a-date-picker
                  v-model="model.examineDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;" />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-model-item>

      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
  import {
    httpAction
  } from '@/api/manage'
  import moment from 'moment'
  export default {
    name: 'HonourAuthModal',
    components: {

    },
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
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
        multiUser: '',
        confirmLoading: false,
        honourTypes: ['1', '2', '3', '4', '5', '6', '7'],
        form: this.$form.createForm(this),
        rules: {
          showAuth: [{
            required: true,
            message: '请选择显示权限'
          }],
          editAuth: [{
            required: true,
            message: '请选择编辑权限'
          }],
          downloadAuth: [{
            required: true,
            message: '请选择下载权限'
          }],
          exportAuth: [{
            required: true,
            message: '请选择附加权限'
          }],
          examineAuth: [{
            required: true,
            message: '请选择审核权限'
          }]
        },
        types: [],
        url: {
          add: '/honour/auth/add',
          edit: '/honour/auth/edit'
        },
        dateFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    },
    created() {},
    methods: {
      add(authId, types) {
        this.types = types
        this.model = {}
        this.model.authId = authId
        this.edit(this.model, types)
      },
      moment,
      edit(record, types) {
        this.model = Object.assign({}, record)
        if (this.model.editDeadline) {
          this.model.editDeadline = moment(this.model.editDeadline, this.dateFormat)
        }
        if (this.model.showDeadline) {
          this.model.showDeadline = moment(this.model.showDeadline, this.dateFormat)
        }
        if (this.model.exportDeadline) {
          this.model.exportDeadline = moment(this.model.exportDeadline, this.dateFormat)
        }
        if (this.model.examineDeadline) {
          this.model.examineDeadline = moment(this.model.examineDeadline, this.dateFormat)
        }
        if (this.model.downloadDeadline) {
          this.model.downloadDeadline = moment(this.model.downloadDeadline, this.dateFormat)
        }

        this.visible = true
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleOk() {
        const that = this

        this.$refs.ruleForm.validate(valid => {
          if (valid) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
              if (that.model.honourType == null) {
                that.$message.error('请选择荣誉类型')
                return false
              }
            } else {
              httpurl += this.url.edit
              method = 'put'
            }

            if (that.model.editDeadline && that.model.editAuth == 2) {
              that.model.editDeadline = that.model.editDeadline.format(that.dateFormat)
            } else {
              that.model.editDeadline = ''
            }
            if (that.model.showDeadline && that.model.showAuth == 2) {
              that.model.showDeadline = that.model.showDeadline.format(that.dateFormat)
            } else {
              this.model.showDeadline = ''
            }
            if (this.model.exportDeadline && this.model.exportAuth == 2) {
              this.model.exportDeadline = this.model.exportDeadline.format(that.dateFormat)
            } else {
              this.model.exportDeadline = ''
            }
            if (this.model.examineDeadline && this.model.examineAuth == 2) {
              this.model.examineDeadline = this.model.examineDeadline.format(that.dateFormat)
            } else {
              this.model.examineDeadline = ''
            }
            if (this.model.downloadDeadline && this.model.downloadAuth == 2) {
              this.model.downloadDeadline = this.model.downloadDeadline.format(that.dateFormat)
            } else {
              this.model.downloadDeadline = ''
            }
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
          } else {
            console.log('error submit!!')
            return false
          }
        })
        /* // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let httpurl = ""
            let method = ""
            if (!this.model.id) {
              httpurl += this.url.add
              method = "post"
            } else {
              httpurl += this.url.edit
              method = "put"
            }
            if (!values.buildTime) {
              values.buildTime = ""
            } else {
              values.buildTime = values.buildTime.format(this.dateFormat)
            }
            let formData = Object.assign(this.model, values)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit("ok")
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              that.close()
            })
          }
        }) */
      },
      handleCancel() {
        this.close()
      }
    }
  }
</script>

<style lang="less" scoped>

</style>
