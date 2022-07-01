<template>
  <!--流程业务表单-->
  <div class="form-main">
    <a-card :body-style="{padding: '0px 0px 24px 32px'}" :bordered="false">
      
      
      <a-form @submit="handleSubmit" :form="form">
        <a-form-item label="姓名" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">

          <a-input :disabled="disabled" v-decorator="[
              'name',
              {rules: [{ required: true, message: '请输入姓名' }]}
            ]"
            name="name" placeholder="请输入名字" />
        </a-form-item>
        <a-form-item label="标题" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">

          <a-input :disabled="disabled" v-decorator="[
              'wordTitle',
              {rules: [{ required: true, message: '请输入标题' }]}
            ]"
            name="wordTitle" placeholder="请输入标题" />
        </a-form-item>
        <!-- <a-form-item label="身份证" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-input :disabled="disabled" v-decorator="[
              'idCard',
              {rules: [{ required: true,max:20,message:'请输入正确格式的身份证号码!',validator:IDValidator}]}
            ]"
            name="idCard" placeholder="请输入18位身份证" />
        </a-form-item> -->


        <a-form-item :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }" label="发生时间"
          hasFeedback>
          <a-date-picker showTime :disabled="disabled" format="YYYY-MM-DD HH:mm:ss" v-decorator="[ 'beginTime', {initialValue:processData.beginTime,required: true}]" />
        </a-form-item>
        <a-form-item label="问题/建议描述" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-textarea :disabled="disabled" placeholder="问题/建议描述" v-decorator="[ 'content', {initialValue:processData.content, required: true}]"
            name='content' :rows="8" />
        </a-form-item>
        <a-form-item label="联系手机" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-input :disabled="disabled" placeholder="请输入手机号码" v-decorator="['mobile',{initialValue:processData.mobile, rules: [{ required: true,max:11,message:'请输入正确格式的手机号码!',validator:MobileNumberValidator}]}]" />
        </a-form-item>
        <a-form-item :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }" label="联系邮箱">
          <a-input :disabled="disabled" placeholder="请输入邮箱" v-decorator="['email',{initialValue:processData.email, rules: [{ required: false,max:20,message:'请输入正确格式的邮箱!',validator:EmailValidator}]}]" />
        </a-form-item>
        <a-form-item label="详细地址" :labelCol="{lg: {span: 7}, sm: {span: 7}}" :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">

          <a-input :disabled="disabled" v-decorator="[
              'address',
              {rules: [{ required: true, message: '请输入详细地址' }]}
            ]"
            name="address" placeholder="请输入详细地址" />
        </a-form-item>
		
        <a-form-item>
          <a-input v-decorator="[ 'categoryId', {initialValue:processData.categoryId, rules: []}]" type='hidden' />
        </a-form-item>
        <a-form-item v-if="!disabled" :wrapperCol="{ span: 24 }" style="text-align: center">
          <a-button type="primary" :disabled="disabled||btndisabled" @click="handleSubmit">保存</a-button>
          <a-button style="margin-left: 8px" :disabled="disabled" @click="close">取消</a-button>
        </a-form-item>
        <a-form-item>
          <a-input v-decorator="[ 'proComplainId', {initialValue:processData.id, rules: []}]" type='hidden' />
        </a-form-item>


        <a-form-item v-if="task" :wrapperCol="{ span: 24 }" style="text-align: center">
          <a-button type="primary" @click="passTask">通过</a-button>
          <a-button style="margin-left: 8px" @click="backTask">驳回</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  export default {
    name: 'DemoForm',
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
        url: {
          getForm: '/pro_complain/getForm',
          addApply: '/pro_complain/addByOut',
          editForm: '/pro_complain/editForm'
        },
        description: '流程表单demo，按例开发表单。需在 activitiMixin.js 中加入写好的表单',
        // form
        form: this.$form.createForm(this),
        /* 表单回显数据 */
        data: {
          beginTime: null
        },
        btndisabled: false,
      }
    },
    created() {
      console.log('流程数据', this.processData)
      if (!this.isNew) {
        this.init()
      }
    },
    methods: {
      /* 回显数据 */
      init() {
        this.btndisabled = true
        var r = this.processData;
        var _this = this;
        if (r.tableId == null || r.tableId == '') {

        } else {
          this.getAction(this.url.getForm, {
            tableId: r.tableId,
            tableName: r.tableName
          }).then((res) => {
            if (res.success) {
              let formData = res.result
              formData.tableName = r.tableName
              this.data = formData
              this.data = Object.assign({}, formData);
              console.log('表单回显数据', this.data)
              this.$nextTick(() => {
                this.form.setFieldsValue(pick(this.data, 'name', 'beginTime', 'content', 'email', 'mobile','adress'))
                this.form.setFieldsValue({
                  beginTime: moment(formData.beginTime, "YYYY-MM-DD HH:mm:ss")
                })
              })
              this.btndisabled = false
            } else {
              this.$message.error(res.message)
            }
          })
        }

      },
      // handler
      handleSubmit(e) {
        e.preventDefault()

        this.form.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.data || {}, values)

            if (!formData.tableName) formData.tableName = this.processData.businessTable
            console.log(values);
            formData.filedNames = _.keys(values).join(",");
            console.log('formData', values)

            var url = this.url.addApply;
            if (!this.isNew) {
              url = this.url.editForm
            }
            this.btndisabled = true
            this.postFormAction(url, formData).then((res) => {
              if (res.success) {
                this.$message.success('发起成功！')

                // todo 将表单的数据传给父组件
                this.$emit('afterSubmit', formData);
              } else {
                this.$message.error(res.message)
              }
            }).finally(() => {
              this.btndisabled = false
            })
          }
        })
      },
      close() {
        // todo 关闭后的回调
        this.$emit('close')
      },
      /* 通过审批 */
      passTask() {
        this.$emit('passTask')
      },
      /* 驳回审批 */
      backTask() {
        this.$emit('backTask')
      },
      /* onChange(checked) {
        console.log(`a-switch to ${checked}`);
      }, */
      // 手机号验证
      MobileNumberValidator(rule, value, callback) {
        const idcardReg = /^1(3|4|5|6|7|8|9)\d{9}$/
        if (!idcardReg.test(value)) {
          // eslint-disable-next-line standard/no-callback-literal
          callback('非法格式')
        }
        // Note: 必须总是返回一个 callback，否则 validateFieldsAndScroll 无法响应
        callback()
      },
      // 邮箱验证
      EmailValidator(rule, value, callback) {
        const idcardReg = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/
        if (!idcardReg.test(value)) {
          // eslint-disable-next-line standard/no-callback-literal
          callback('非法格式')
        }
        // Note: 必须总是返回一个 callback，否则 validateFieldsAndScroll 无法响应
        callback()
      },
      // 身份证验证 
      IDValidator(rule, value, callback) {
        const idcardReg =
          /^[1-9][0-9]{5}(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|31)|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}([0-9]|x|X)$/
        if (!idcardReg.test(value)) {
          // eslint-disable-next-line standard/no-callback-literal
          callback('非法格式')
        }
        // Note: 必须总是返回一个 callback，否则 validateFieldsAndScroll 无法响应
        callback()

      },
    }
  }
</script>
<style lang="less" scoped>
  .form-main {}
</style>
