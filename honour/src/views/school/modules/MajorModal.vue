<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="专业名称">
          <a-input placeholder="请输入专业名称" v-decorator.trim="['majorName', validatorRules.majorName]" autocomplete="off"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="专业编号">
          <a-input placeholder="请输入专业编号" v-decorator.trim="['majorCode', validatorRules.majorCode]" autocomplete="off"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="描述">
          <a-textarea
            v-decorator="[
              'describe',
              {},
            ]"
            :rows="4"
            placeholder="请输入描述" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="排序值">
          <a-input-number :min="1" v-decorator="['sort',{initialValue:1}]"/>
          值越小越靠前，支持小数
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="是否启用"
          hasFeedback>
          <a-switch checkedChildren="启用" unCheckedChildren="禁用" @change="onChose" v-model="visibleCheck"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import {
    postAction,
    putAction
  } from '@/api/manage'
  export default {
    name: 'MajorModal',
    data() {
      return {
        title: '操作',
        visible: false,
        visibleCheck: true,
        model: {},
        departmentId: '',
        status: 1,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          majorName: { rules: [{ required: true, message: '请输入专业名称!' }] },
          majorCode: { rules: [{ required: true, message: '请输入专业编号!' }] }
        },
        url: {
          fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
          addMajor: '/major/add',
          editMajor: '/major/edit'
        }
      }
    },
    created() {
    },
    methods: {
      add(departmentId) {
        this.departmentId = departmentId
        this.edit({})
      },
      edit(record) {
        if (record.id) {
          this.departmentId = record.departmentId
          this.visibleCheck = (record.status == 1)
        }
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.model.departmentId = this.departmentId
        this.model.status = this.status
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'majorName', 'majorCode', 'describe', 'sort'))
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
            values.majorName = (values.majorName || '').trim()
            values.majorCode = (values.majorCode || '').trim()
            values.describe = (values.describe || '').trim()
            let formData = Object.assign(this.model, values)
            formData.status = this.status
            if (!this.model.id) {
              postAction(that.url.addMajor, formData).then((res) => {
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
              putAction(that.url.editMajor, formData).then((res) => {
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
      }
    }
  }
</script>
