<template>
  <a-drawer
    :title="title"
    :maskClosable="true"
    :width="drawerWidth"
    placement="right"
    :closable="true"
    @close="handleCancel"
    :visible="visible"
    :fullScreen="true"
    style="height: 100%;overflow: auto;padding-bottom: 53px;">

    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
        <span style="display:inline-block;width:calc(100% - 51px);padding-right:10px;text-align: right">
          <a-button @click="toggleScreen" icon="appstore" style="height:20px;width:20px;border:0px"></a-button>
        </span>
      </div>

    </template>

    <a-spin :spinning="confirmLoading">
      <a-form :form="form" layout="vertical" hide-required-mark>
        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="学院名称">
              <a-input
                placeholder="请输入学院名称"
                v-decorator.trim="[ 'collegeName', validatorRules.collegeName]"
                :readOnly="!!model.id"
                autocomplete="off" />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="英文名称">
              <a-input
                placeholder="请输入英文名称"
                v-decorator.trim="[ 'englishName', validatorRules.englishName]"
                autocomplete="off" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="简称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入简称" v-decorator.trim="[ 'abbreviation', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="校训" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入校训" v-decorator.trim="['motto', {}]" autocomplete="off" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="办学性质" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['natureOfRunning', validatorRules.natureOfRunning]"
                placeholder="请选择办学性质"
                :getPopupContainer="(target) => target.parentNode">
                <a-select-option value="公立大学">公立大学</a-select-option>
                <a-select-option value="私立大学">私立大学</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="学院类型">
              <j-dict-select-tag placeholder="请选择学院类型" :triggerChange="true" dictCode="college_type" v-decorator="['collegeType', validatorRules.collegeType]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="院校代码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                placeholder="请输入院校代码"
                v-decorator.trim="[ 'collegeCode', validatorRules.collegeCode]"
                autocomplete="off" />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="创办日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                style="width: 100%"
                placeholder="请选择创办日期"
                v-decorator="['buildTime', {initialValue:!model.buildTime?null:moment(model.buildTime,dateFormat)}]"
                :getCalendarContainer="node => node.parentNode" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="24">
            <a-form-item label="校徽" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload class="avatar-uploader" text="上传" v-model="fileList"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-area-linkage v-decorator="[ 'areaLink', {}]" type="cascader" :getCalendarContainer="node => node.parentNode" />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="详细地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入详细地址" v-decorator="[ 'address', {}]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col span="12">
            <a-form-item label="联系方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入联系方式" v-decorator="[ 'mobile', {}]" />
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number :min="1" style="width: 100%;" placeholder="请输入排序" v-decorator="[ 'sort', {initialValue:1}]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="描述">
              <a-textarea
                v-decorator="[
                  'describe',
                  {},
                ]"
                :rows="6"
                placeholder="请输入描述" />
            </a-form-item>
          </a-col>
        </a-row>

      </a-form>
    </a-spin>

    <div class="drawer-bootom-button" v-show="!disableSubmit">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
  import pick from "lodash.pick"
  import moment from "moment"
  import JAreaLinkage from "@comp/jeecg/JAreaLinkage"
  import {
    postAction,
    putAction
  } from "@/api/manage"
  import {
    duplicateCheck
  } from "@/api/api"
  import {
    disabledAuthFilter
  } from "@/utils/authFilter"

  import JImageUpload from "../../../components/jeecg/JImageUpload"

  export default {
    name: "CollegeModal",
    components: {
      JImageUpload,
      JAreaLinkage
    },
    data() {
      return {
        modalWidth: 800,
        drawerWidth: 720,
        modaltoggleFlag: true,
        confirmDirty: false,
        areaLink: "",
        disableSubmit: false,
        dateFormat: "YYYY-MM-DD",
        validatorRules: {
          collegeName: {
            rules: [{
              required: true,
              message: "请输入学院名称!"
            }, {
              validator: this.validateCollegeName
            }]
          },
          mobile: {
            rules: [{
              validator: this.validateMobile
            }]
          },
          collegeType: {
            rules: [{
              required: true,
              message: "请选择学院类型!"
            }]
          },
          natureOfRunning: {
            initialValue: "公立大学"
          },
          collegeCode: {
            rules: [{
                required: true,
                message: "请院校代码"
              },
              {
                validator: this.validateCollegeCode
              }
            ]
          }
        },
        title: "操作",
        visible: false,
        model: {},
        collegeId: "",
        labelCol: {

        },
        wrapperCol: {},
        uploadLoading: false,
        confirmLoading: false,
        headers: {},
        form: this.$form.createForm(this),
        picUrl: "",
        url: {
          fileUpload: window._CONFIG["domianURL"] + "/sys/common/upload",
          addCollege: "/college/add",
          editCollege: "/college/edit"
        },
        fileList: []
      }
    },
    created() {},
    computed: {
      uploadAction: function() {
        return this.url.fileUpload
      }
    },
    methods: {
      isDisabledAuth(code) {
        return disabledAuthFilter(code)
      },
      // 窗口最大化切换
      toggleScreen() {
        if (this.modaltoggleFlag) {
          this.modalWidth = window.innerWidth
        } else {
          this.modalWidth = 800
        }
        this.modaltoggleFlag = !this.modaltoggleFlag
      },
      refresh() {

      },
      add() {
        this.picUrl = ""
        this.refresh()
        this.edit({})
      },
      edit(record) {
        this.resetScreenSize() // 调用此方法,根据屏幕宽度自适应调整抽屉的宽度
        let that = this
        that.form.resetFields()
        if (record.hasOwnProperty("id")) {
          setTimeout(() => {
            this.fileList = record.icon
          }, 5)
        }
        that.visible = true
        that.collegeId = record.id
        that.model = Object.assign({}, record)
        that.$nextTick(() => {
          that.form.setFieldsValue(pick(this.model, "collegeName", "englishName", "abbreviation", "collegeCode",
            "motto", "natureOfRunning", "collegeType", "areaLink", "address", "mobile", "describe", "sort"))
        })
      },

      close() {
        this.$emit("close")
        this.visible = false
        this.disableSubmit = false
        this.fileList = []
      },
      moment,
      handleSubmit() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            if (!values.buildTime) {
              values.buildTime = ""
            } else {
              values.buildTime = values.buildTime.format(this.dateFormat)
            }
            let formData = Object.assign(this.model, values)
            if (that.fileList.length > 0) {
              formData.icon = that.fileList
            } else {
              formData.icon = null
            }
            if (!this.model.id) {
              postAction(that.url.addCollege, formData).then((res) => {
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
            } else {
              putAction(that.url.editCollege, formData).then((res) => {
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
          }
        })
      },
      handleCancel() {
        this.close()
      },
      validateMobile(rule, value, callback) {
        if (!value) {
          callback()
        } else {
          // update-begin--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
          if (new RegExp(/^1[3|4|5|7|8|9][0-9]\d{8}$/).test(value)) {
            // update-end--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
          } else {
            callback("请输入正确格式的手机号码!")
          }
        }
      },
      validateCollegeName(rule, value, callback) {
        var params = {
          tableName: "zd_college",
          fieldName: "college_name",
          fieldVal: value,
          dataId: this.collegeId
        }
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback("学院名称已存在!")
          }
        })
      },
      validateCollegeCode(rule, value, callback) {
        var params = {
          tableName: "zd_college",
          fieldName: "college_code",
          fieldVal: value,
          dataId: this.collegeId
        }
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback("院校代码已存在!")
          }
        })
      },
      handleConfirmBlur(e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
      },

      normFile(e) {
        if (Array.isArray(e)) {
          return e
        }
        return e && e.fileList
      },
      beforeUpload: function(file) {
        var fileType = file.type
        if (fileType.indexOf("image") < 0) {
          this.$message.warning("请上传图片")
          return false
        }
        // TODO 验证文件大小
      },
      handleChange(info) {
        this.picUrl = ""
        if (info.file.status === "uploading") {
          this.uploadLoading = true
          return
        }
        if (info.file.status === "done") {
          var response = info.file.response
          this.uploadLoading = false
          console.log(response)
          if (response.success) {
            this.model.avatar = response.message
            this.picUrl = "Has no pic url yet"
          } else {
            this.$message.warning(response.message)
          }
        }
      },

      // 根据屏幕变化,设置抽屉尺寸
      resetScreenSize() {
        let screenWidth = document.body.clientWidth
        if (screenWidth < 500) {
          this.drawerWidth = screenWidth
        } else {
          this.drawerWidth = 700
        }
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader>.ant-upload {
    width: 104px;
    height: 104px;
  }

  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: -8px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>
