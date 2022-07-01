<template>
  <j-modal
    :title="title"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :fullscreen="fullscreen"
    :switchFullscreen="switchFullscreen"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="标题">
          <a-input placeholder="请输入标题" v-decorator="['title', validatorRules.title]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="负责人">
          <a-input placeholder="请输入负责人" v-decorator="['leader', {}]"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物图类别"
        >
          <a-radio-group v-decorator="['articleType', validatorRules.articleType]" @change="onArticleChange">
            <a-radio v-for="(item, key) in articleDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物图类别(其他)"
          v-show="articleFlag"
        >
          <a-input placeholder="请输入物图类别(其他)" v-decorator="['articleTypeTxt',{}]"/>

        </a-form-item>

        <a-form-item label="拍取日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-date-picker
            style="width: 100%"
            placeholder="请选择拍取日期"
            v-decorator="['acquireTime', {initialValue:!model.acquireTime?null:moment(model.acquireTime,dateFormat)}]"
            :getCalendarContainer="node => node.parentNode" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开展部门"
        >
          <a-radio-group v-decorator="['authorityType', validatorRules.authorityType]" @change="onAuthorityChange">
            <a-radio v-for="(item, key) in authorityDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开展部门(其他)"
          v-show="authorityFlag"
        >
          <a-input placeholder="请输入开展部门(其他)" v-decorator="['authorityTxt',{}]"/>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="成果类别"
        >
          <a-radio-group v-decorator="['achievementType', validatorRules.achievementType]" @change="onAchievementChange">
            <a-radio v-for="(item, key) in achievementDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="成果类别(其他)"
          v-show="achievementFlag"
        >
          <a-input placeholder="请输入成果类别(其他)" v-decorator="['achievementTypeTxt',{}]"/>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="成果级别"
        >
          <a-radio-group v-decorator="['achievementLevel', validatorRules.achievementLevel]" @change="onLevelChange">
            <a-radio v-for="(item, key) in levelDictOptions" :key="key" :value="item.value">
              {{ item.text }}
            </a-radio>
          </a-radio-group>

        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="成果级别(其他)"
          v-show="levelFlag"
        >
          <a-input placeholder="请输入成果级别(其他)" v-decorator="['achievementLevelTxt',{}]"/>

        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="团队成员">
          <a-select mode="tags" placeholder="请输入团队成员姓名" v-model="teamPersons" @change="saveTeamsPerson">
          </a-select>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="归属专业">
          <!-- <jtree-select-major v-model="model.majorId" placeholder="请选择归属专业" dictCode="zd_major,major_name,id,1=1 order by major_code"></jtree-select-major> -->
          <j-select-depart v-model="model.majorId" :trigger-change="true" customReturnField="id"></j-select-depart>
        </a-form-item>

        <a-form-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-image-upload class="avatar-uploader" text="上传" v-model="fileList"></j-image-upload>
        </a-form-item>

        <a-form-item label="操作" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-checkbox v-model="thisPage">
            保留当前页面
          </a-checkbox>
        </a-form-item>

      </a-form>
    </a-spin>

    <template slot="footer">
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
      <a-button key="save" type="primary" :loading="confirmLoading" @click="handleSave">
        保存草稿
      </a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
        上传提交
      </a-button>
    </template>
  </j-modal>
</template>

<script>

  import pick from 'lodash.pick'
  import moment from 'moment'
  import { httpAction, getAction } from '@/api/manage'
  import { initDictOptions } from '@/components/dict/JDictSelectUtil'
  //import JtreeSelectMajor from './JtreeSelectMajor'
  import JSelectDepart from './JSelectDepart'
  import JImageUpload from './JImageUpload'
  import { checkDuplicateHonourByTitle } from '@/api/api'
  export default {
    name: 'HonourArticleModal',
    components: {
      JSelectDepart,
      JImageUpload
    },
    data() {
      return {
        thisPage: true,
        title: '操作',
        fullscreen: true,
        switchFullscreen: true,
        visible: false,
        articleFlag: false,
        authorityFlag: false,
        achievementFlag: false,
        levelFlag: false,
        model: {},
        fileList: [],
        teamPersons: [],
        fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
        authorityDictOptions: [],
        articleDictOptions: [],
        achievementDictOptions: [],
        levelDictOptions: [],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        confirmLoading: false,
        dateFormat: 'YYYY-MM-DD',
        form: this.$form.createForm(this),
        validatorRules: {
          title: {
            rules: [
              { required: true, message: '请输入标题' }

            ]
          },
          articleType: { rules: [{ required: true, message: '请输入物图类别' }] },
          authorityType: { rules: [{ required: true, message: '请选择开展部门' }] },
          achievementType: { rules: [{ required: true, message: '请选择成果类别' }] },
          achievementLevel: { rules: [{ required: true, message: '请选择成果级别' }] }
        },
        url: {
          add: '/honour/article/saveArticle',
          edit: '/honour/article/editArticle',
          submit: '/honour/article/submitArticle',
          getAppendix: '/honour/findAppendixByIds'
        }
      }
    },
    created() {
      this.initDictConfig()
    },
    methods: {
      add() {
        this.teamPersons = []
        this.edit({})
      },
      saveTeamsPerson(teamPersons) {
        console.log(teamPersons)
        if (teamPersons.length !== 0) {
          this.model.teamPersons = teamPersons.join(',')
        }
      },
      moment,
      edit(record) {
        this.form.resetFields()
        this.articleFlag = false
        this.authorityFlag = false
        this.achievementFlag = false
        this.levelFlag = false
        this.model = Object.assign({}, record)
        this.visible = true
        console.log(this.model)
        if (this.model.id) {
          if (this.model.appendixIds != null) {
            this.getAppendix(this.model.appendixIds)
          }
          this.model.articleType += ''
          if (this.model.articleType == '99') {
            this.articleFlag = true
          }

          this.model.authorityType += ''
          if (this.model.authorityType == '99') {
            this.authorityFlag = true
          }
          this.model.achievementType += ''
          if (this.model.achievementType == '99') {
            this.achievementFlag = true
          }

          this.model.achievementLevel += ''
          if (this.model.achievementLevel == '99') {
            this.levelFlag = true
          }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,
            'title',
            'articleType',
            'articleTypeTxt',
            'authorityType',
            'authorityTxt',
            'achievementType',
            'achievementTypeTxt',
            'achievementLevel',
            'achievementLevelTxt'
          ))
        })
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
        initDictOptions('article_type').then((res) => {
          if (res.success) {
            this.articleDictOptions = res.result
          }
        })
        initDictOptions('certificate_authority').then((res) => {
          if (res.success) {
            this.authorityDictOptions = res.result
          }
        })
        initDictOptions('achievement_type').then((res) => {
          if (res.success) {
            this.achievementDictOptions = res.result
          }
        })
        initDictOptions('achievement_level').then((res) => {
          if (res.success) {
            this.levelDictOptions = res.result
          }
        })
      },
      onLoadMajor(txt, label) {
          console.log(txt)
          console.log(label)
          this.model.majorId = txt
      },
      onArticleChange(e) {
        if (e.target.value == '99') {
          this.articleFlag = true
        } else {
          this.articleFlag = false
        }
      },
      onAuthorityChange(e) {
        if (e.target.value == '99') {
          this.authorityFlag = true
        } else {
          this.authorityFlag = false
        }
      },
      onAchievementChange(e) {
        if (e.target.value == '99') {
          this.achievementFlag = true
        } else {
          this.achievementFlag = false
        }
      },
      onLevelChange(e) {
        if (e.target.value == '99') {
          this.levelFlag = true
        } else {
          this.levelFlag = false
        }
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      initArticle() {
        this.model = {}
        this.form.resetFields()
        this.fileList = []
        this.teamPersons = []
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
              var appendixIds = this.fileList.map((obj) => { return obj.appendixId }).join(',')
              this.model.appendixIds = appendixIds
            }

            var query = {
              fields: 'title',
              title: values.title,
              tableId: 3
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
                      if (that.thisPage) {
                        that.initArticle()
                      } else {
                        that.close()
                      }
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
              var appendixIds = that.fileList.map((obj) => { return obj.appendixId }).join(',')
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
            that.model.status = 0
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
              if (that.thisPage) {
                that.initArticle()
              } else {
                that.close()
              }
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
