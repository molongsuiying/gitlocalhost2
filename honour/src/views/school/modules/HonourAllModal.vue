<template>
  <j-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :fullscreen="fullscreen"
    :switchFullscreen="switchFullscreen"
    cancelText="关闭"
    :footer="null">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="采集类型">
          <a-radio-group v-model="honourType">
            <a-radio :value="1">
              文件类
            </a-radio>
            <a-radio :value="2">
              证书类
            </a-radio>
            <a-radio :value="3">
              物图类
            </a-radio>
            <a-radio :value="4">
              报道类
            </a-radio>
            <a-radio :value="5">
              协议类
            </a-radio>
            <a-radio :value="6">
              人才类
            </a-radio>
            <a-radio :value="7">
              课题类
            </a-radio>
          </a-radio-group>
        </a-form-item>
        <honour-file-form v-if="honourType==1"></honour-file-form>
        <honour-certificate-form v-if="honourType==2"></honour-certificate-form>
        <honour-article-form v-if="honourType==3"></honour-article-form>
        <honour-report-form v-if="honourType==4"></honour-report-form>
        <honour-agree-form v-if="honourType==5"></honour-agree-form>
        <honour-person-form v-if="honourType==6"></honour-person-form>
        <honour-project-form v-if="honourType==7"></honour-project-form>
      </a-form>

    </a-spin>

  </j-modal>
</template>

<script>
  import moment from "moment"
  import JtreeSelectMajor from "./JtreeSelectMajor"
  import JImageUpload from "./JImageUpload"
  import HonourCertificateForm from "./HonourCertificateForm"
  import HonourPersonForm from "./HonourPersonForm"
  import HonourFileForm from "./HonourFileForm"
  import HonourReportForm from "./HonourReportForm"
  import HonourAgreeForm from "./HonourAgreeForm"
  import HonourArticleForm from "./HonourArticleForm"
  import HonourProjectForm from "./HonourProjectForm"
  export default {
    name: "HonourAllModal",
    components: {
      JtreeSelectMajor,
      JImageUpload,
      HonourPersonForm,
      HonourCertificateForm,
      HonourFileForm,
      HonourReportForm,
      HonourAgreeForm,
      HonourArticleForm,
      HonourProjectForm
    },
    data() {
      return {
        honourType: 1,
        thisPage: true,
        fullscreen: true,
        confirmLoading: false,
        switchFullscreen: true,
        title: "操作",
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

        form: this.$form.createForm(this)
      }
    },
    created() {
    },
    methods: {
      add() {
        this.edit({})
      },
      moment,
      edit() {
        this.visible = true
      },
      handleCancel() {
        this.visible = false
      }
    }
  }
</script>

<style lang="less" scoped>

</style>
