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
          label="专业名称">
          <a-select
            :disabled="!!model.id"
            show-search
            v-decorator="['majorId', {}]"
            placeholder="请输入专业名称"
            option-filter-prop="children"
            :filter-option="filterOption">
            <a-select-option v-for="m in majorList" :key="m.majorId">
              {{ m.majorName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="班级名称">
          <a-input placeholder="请输入班级名称" v-decorator="['bunchName', validatorRules.bunchName]" autocomplete="off"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="班级介绍">
          <a-textarea
            v-decorator="[
              'describe',
              {},
            ]"
            :rows="4"
            placeholder="请输入班级介绍" />
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
    name: "BunchModal",
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
        majorList: [],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          bunchName: {
            rules: [
              { required: true, message: "请输入班级名称" }
            ]
          }
        },
        url: {
          add: "/bunch/add",
          edit: "/bunch/edit"
        }
      }
    },
    created() {
    },
    methods: {
      add(majorList, dataRef) {
        var bunch = {
          majorId: dataRef.key,
          sort: 1
        }
        this.edit(majorList, bunch)
      },
      moment,
      edit(majorList, record) {
        this.majorList = majorList
        this.form.resetFields()

        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            "majorId",
            "sort",
            "describe",
            "bunchName"
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

            let formData = Object.assign(this.model, values)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                var type = "edit"
                if (res.result) {
                  that.model.id = res.result.id
                  type = "add"
                }
                that.$message.success(res.message)
                that.$emit("ok", that.model, type)
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
