<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="ruleForm" :model="model" :rules="rules">

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户账号">
          <j-select-multi-user v-model="model.multiUser"></j-select-multi-user>
        </a-form-model-item>

        <!--        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="荣誉类型">
          <a-checkbox-group v-model="honourTypes">
            <a-checkbox value="1" name="文件类">
              文件类
            </a-checkbox>
            <a-checkbox value="2" name="证书类">
              证书类
            </a-checkbox>
            <a-checkbox value="3" name="物图类">
              物图类
            </a-checkbox>
            <a-checkbox value="4" name="报道类">
              报道类
            </a-checkbox>
            <a-checkbox value="5" name="协议类">
              协议类
            </a-checkbox>
            <a-checkbox value="6" name="人才类">
              人才类
            </a-checkbox>
          </a-checkbox-group>
        </a-form-model-item> -->

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账号权限">
          <a-radio-group name="radioGroup" v-model="model.authType" @change="changeAuth">
            <a-radio :value="1">
              高级权限
            </a-radio>
            <a-radio :value="2">
              临时高级权限
            </a-radio>
            <a-radio :value="3">
              普通权限
            </a-radio>
          </a-radio-group>
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
                <font style="margin-left: 25px;margin-right: 25px;">到期时间:</font>
                <a-date-picker
                  v-model="model.showDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;"
                />
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
                <font style="margin-left: 25px;margin-right: 25px;">到期时间:</font>
                <a-date-picker
                  v-model="model.editDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;"
                />
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
                <font style="margin-left: 25px;margin-right: 25px;">到期时间:</font>
                <a-date-picker
                  v-model="model.downloadDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;"
                />
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
                <font style="margin-left: 25px;margin-right: 25px;">到期时间:</font>
                <a-date-picker
                  v-model="model.exportDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;"
                />
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
                <font style="margin-left: 25px;margin-right: 25px;">到期时间:</font>
                <a-date-picker
                  v-model="model.examineDeadline"
                  show-time
                  type="date"
                  placeholder="选择到期时间"
                  style="width: 50%;"
                />
              </template>
            </a-radio>
          </a-radio-group>
        </a-form-model-item>

      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
  import JSelectMultiUser from '@/components/jeecgbiz/JSelectMultiUser'
  import pick from 'lodash.pick'
  import {
    httpAction
  } from '@/api/manage'
  import moment from 'moment'
  export default {
    name: 'HonourAuthModal',
    components: {
      JSelectMultiUser
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
        honourTypes: ['1', '2', '3', '4', '5', '6'],
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
            message: '请选择附件权限'
          }],
          exportAuth: [{
            required: true,
            message: '请选择导出权限'
          }],
          examineAuth: [{
            required: true,
            message: '请选择审核权限'
          }]
        },
        file_auth: [],
        url: {
          add: '/honour/auth/batchSave'
        },
        dateFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    },
    created() {},
    methods: {
      add() {
        this.model = {}
        this.edit()
      },
      moment,
      edit() {
        this.form.resetFields()
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
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
            }

            if (that.model.multiUser == null) {
              that.$message.error('请选择账号')
              return false
            }
            /* if (that.honourTypes.length == 0 || that.honourTypes == null) {
              that.$message.error("请选择荣誉类型")
              return false
            } else {
              that.model.honourTypes = that.honourTypes.join(",")
            } */

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
              this.model.exportDeadline = this.model.exportDeadline.format(this.dateFormat)
            } else {
              this.model.exportDeadline = ''
            }
            if (this.model.examineDeadline && this.model.examineAuth == 2) {
              this.model.examineDeadline = this.model.examineDeadline.format(this.dateFormat)
            } else {
              this.model.examineDeadline = ''
            }
            if (this.model.downloadDeadline && this.model.downloadAuth == 2) {
              this.model.downloadDeadline = this.model.downloadDeadline.format(this.dateFormat)
            } else {
              this.model.downloadDeadline = ''
            }
            that.confirmLoading = true
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
      },
      examineAuthChange() {
        console.log(this.model)
        console.log(this.form)
      },
      changeAuth(e) {
        var type = e.target.value
        this.model.editAuth = 1
        this.model.showAuth = 1
        this.model.examineAuth = 0
        this.model.downloadAuth = 0
        this.model.exportAuth = 0
        if (type == 1) {
          this.model.downloadAuth = 1
          this.model.exportAuth = 1
          this.model.examineAuth = 1
        } else if (type == 2) {
          this.model.downloadAuth = 2
          this.model.exportAuth = 2
        }
      },
      handleCancel() {
        this.close()
      },
      filterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        )
      }

    }
  }
</script>

<style lang="less" scoped>

</style>
