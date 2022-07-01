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
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="院校名称">
          <a-select
            :disabled="!!model.id"
            show-search
            v-decorator="['collegeId', validatorRules.collegeId]"
            placeholder="请输入院校名称"
            option-filter-prop="children"
            :filter-option="filterOption">
            <a-select-option v-for="c in collegeList" :key="c.id">
              {{ c.text }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="院系名称">
          <a-input placeholder="请输入院系名称" v-decorator="['departmentName', validatorRules.departmentName]" autocomplete="off"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="院系编号">
          <a-input placeholder="请输入院系编号" v-decorator="['departmentCode', {}]" autocomplete="off"/>
        </a-form-item>
        <a-form-item label="成立日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择成立日期"
            v-decorator="['buildTime', {initialValue:!model.buildTime?null:moment(model.buildTime,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode" />
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
          <a-input-number :min="1" v-decorator="['sort',{'initialValue':1}]"/>
          值越小越靠前，支持小数
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import pick from "lodash.pick"
  import { httpAction } from "@/api/manage"
  import moment from "moment"
  export default {
    name: "DepartmentModal",
    components: { },
    data() {
      return {
        title: "操作",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        collegeList: [],
        form: this.$form.createForm(this),
        validatorRules: {
          departmentName: {
            rules: [
              { required: true, message: "请输入院系名称" }
            ]
          },
          collegeId: { rules: [{ required: true, message: "请选择院校" }] }
        },
        url: {
          add: "/department/add",
          edit: "/department/edit"
        },
        dateFormat: "YYYY-MM-DD"
      }
    },
    created() {
    },
    methods: {
      add(collegeList, collegeId) {
        // this.collegeList = collegeList
        this.edit(collegeList, { collegeId: collegeId })
      },
      moment,
      edit(collegeList, record) {
        this.collegeList = collegeList
        this.form.resetFields()

        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            "departmentName",
            "departmentCode",
            "sort",
            "describe",
            "collegeId"
          ))
        })
      },
      close() {
        this.$emit("close")
        this.visible = false
      },
      handleOk() {
        const that = this
        // 触发表单验证
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
        })
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
