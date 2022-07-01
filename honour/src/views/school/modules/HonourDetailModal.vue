<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="文件类型">
              <span>{{ modal.honourClass_dictText }}</span>
            </a-form-item>
          </a-col>
          <a-divider>详细信息</a-divider>
          <a-col :md="24" :sm="24">
            <a-form-item :label="compLabel[0]">
              <span>{{ modal.title }}</span>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :label="compLabel[1]">
              <span>{{ modal.leader }}</span>
            </a-form-item>
          </a-col>

        </a-row>
        <a-row :gutter="24">
          <template v-if="modal.honourClass == 1">
            <a-col :md="12" :sm="24">
              <a-form-item label="作品名称">
                <span>{{ modal.workName }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="荣誉等级">
                <span>{{ modal.honourLevel_dictText }}</span>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="12" :sm="24">
            <a-form-item :label="requireTimeLabel[modal.honourClass]">
              <span>{{ modal.acquireTime }}</span>
            </a-form-item>
          </a-col>
          <template v-if="modal.honourClass == 1">

            <a-col :md="12" :sm="24">
              <a-form-item label="发文字号">
                <span>{{ modal.articleNum }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="发文机关">
                <span>
                  {{ modal.authorityType_dictText }}
                  <font v-if="modal.authorityType == 99 ">({{ modal.authorityTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果类别">
                <span>
                  {{ modal.achievementType_dictText }}
                  <font v-if="modal.achievementType == 99 ">({{ modal.achievementTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <span>
                  {{ modal.achievementLevel_dictText }}
                  <font v-if="modal.achievementLevel == 99 ">({{ modal.achievementLevelTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 2">
            <a-col :md="12" :sm="12">
              <a-form-item label="证书类型">
                <template v-if="modal.studentName != ''">
                  <span>学生姓名:{{ modal.studentName }}</span><br />
                  <span>指导教师:{{ modal.teacherName }}</span>
                </template>
                <template v-else-if="modal.teacherName != '' ">
                  <span>教师:{{ modal.teacherName }}</span>
                </template>
                <template v-else-if="modal.schoolName != '' ">
                  <span>学校:{{ modal.schoolName }}</span>
                </template>
                <template v-else-if="modal.departmentName != '' ">
                  <span>部门:{{ modal.departmentName }}</span>
                </template>
                <template v-else>
                  <span>其他:{{ modal.otheName }}</span>
                </template>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="荣誉等级">
                <span>{{ modal.honourLevel_dictText }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="颁发机关">
                <span>
                  {{ modal.authorityType_dictText }}
                  <font v-if="modal.authorityType == 99 ">({{ modal.authorityTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果类别">
                <span>
                  {{ modal.achievementType_dictText }}
                  <font v-if="modal.achievementType == 99 ">({{ modal.achievementTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <span>
                  {{ modal.achievementLevel_dictText }}
                  <font v-if="modal.achievementLevel == 99 ">({{ modal.achievementLevelTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 3">
            <a-col :md="12" :sm="24">
              <a-form-item label="物图类别">
                <span>
                  {{ modal.articleType_dictText }}
                  <font v-if="modal.articleType == 99 ">({{ modal.articleTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="开展部门">
                <span>
                  {{ modal.authorityType_dictText }}
                  <font v-if="modal.authorityType == 99 ">({{ modal.authorityTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果类别">
                <span>
                  {{ modal.achievementType_dictText }}
                  <font v-if="modal.achievementType == 99 ">({{ modal.achievementTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <span>
                  {{ modal.achievementLevel_dictText }}
                  <font v-if="modal.achievementLevel == 99 ">({{ modal.achievementLevelTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 4">
            <a-col :md="12" :sm="24">
              <a-form-item label="媒体类别">
                <span>
                  {{ modal.mediumType_dictText }}
                  <font v-if="modal.mediumType == 99 ">({{ modal.mediumTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="媒体名称">
                <span>
                  {{ modal.mediumName }}
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="作者署名">
                <span>
                  {{ modal.author }}
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果类别">
                <span>
                  {{ modal.achievementType_dictText }}
                  <font v-if="modal.achievementType == 99 ">({{ modal.achievementTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <span>
                  {{ modal.achievementLevel_dictText }}
                  <font v-if="modal.achievementLevel == 99 ">({{ modal.achievementLevelTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 5">
            <a-col :md="12" :sm="24">
              <a-form-item label="合作类别">
                <span>
                  {{ modal.cooperationType_dictText }}
                  <font v-if="modal.cooperationType == 99 ">({{ modal.cooperationTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="合作单位">
                <span>{{ modal.cooperationUnit }}</span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 6">
            <a-col :md="12" :sm="24">
              <a-form-item label="人才证书">
                <span>
                  {{ modal.certificateType_dictText }}
                  <font v-if="modal.certificateType == 99 ">({{ modal.certificateTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="授发单位">
                <span>{{ modal.certificateTypeTxt }}</span>
              </a-form-item>
            </a-col>
          </template>
          <template v-else-if="modal.honourClass == 7">
            <a-col :md="12" :sm="12">
              <a-form-item label="课题类别">
                <template v-if="modal.studentName != ''">
                  <span>学生姓名:{{ modal.studentName }}</span><br />
                  <span>指导教师:{{ modal.teacherName }}</span>
                </template>
                <template v-else-if="modal.teacherName != '' ">
                  <span>教师:{{ modal.teacherName }}</span>
                </template>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="立项负责人">
                <span>{{ modal.buildProjectCharge }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="结项负责人">
                <span>{{ modal.overProjectCharge }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="荣誉等级">
                <span>{{ modal.honourLevel_dictText }}</span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="颁发机关">
                <span>
                  {{ modal.authorityType_dictText }}
                  <font v-if="modal.authorityType == 99 ">({{ modal.authorityTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果类别">
                <span>
                  {{ modal.achievementType_dictText }}
                  <font v-if="modal.achievementType == 99 ">({{ modal.achievementTypeTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item label="成果级别">
                <span>
                  {{ modal.achievementLevel_dictText }}
                  <font v-if="modal.achievementLevel == 99 ">({{ modal.achievementLevelTxt }})</font>
                </span>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="12" :sm="24">
            <a-form-item label="归属部门">
              <span>{{ modal.majorName }}</span>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="团队成员">
              <span>{{ modal.teamPersons }}</span>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <a-divider>附件</a-divider>
      <template v-if="dataSource.length > 0">
        <div style="margin-bottom: 10px;margin-top: 10px;" v-if="downloadAuth && appendixs.length>0">
          <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
            全选
          </a-checkbox>
        </div>
        <a-checkbox-group
          @change="onChange"
          v-model="appendixIds"
          style="width: 99%;max-height: 420px;overflow-y: auto;">
          <div v-for="(appendix,index) in dataSource[0].appendixs" :key="index">
            <div class="img-div1">
              <div class="img-div2" :title="appendix.fileName">
                <div style="text-align: center;">

                  <template v-if="appendix.fileType.toUpperCase().indexOf(&quot;JPG&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;JPEG&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;PNG&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;WEBP&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;BMP&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;GIF&quot;) !== -1 ">
                    <img class="show-img" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;TXT&quot;) !== -1 ">
                    <a-icon type="file-text" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;PDF&quot;) !== -1 ">
                    <a-icon type="file-pdf" style="fontSize:100px;width: 100%;margin-top: 30px;" @click="pdfPreview(appendix)"/></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;EXCEL&quot;) !== -1 ">
                    <a-icon type="file-excel" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;XLSX&quot;) !== -1 ">
                    <a-icon type="file-excel" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;PPT&quot;) !== -1 ">
                    <a-icon type="file-ppt" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;ZIP&quot;) !== -1 ">
                    <a-icon type="file-zip" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                  <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;DOC&quot;) !== -1 ">
                    <a-icon
                      type="file-word"
                      style="fontSize:100px;width: 100%;margin-top: 30px;"
                      @click="showDocx(appendix)" /></br>
                  </template>
                  <template v-else>
                    <a-icon type="file-unknown" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                  </template>
                </div>

                </br>
                <div style="position: absolute;bottom: 10px;">
                  <a-checkbox :value="appendix.id">
                    <font class="text">{{ appendix.fileName }}</font>
                  </a-checkbox>
                </div>

              </div>
            </div>
          </div>
        </a-checkbox-group>

      </template>
      <pdf-preview-modal ref="pdfmodal"></pdf-preview-modal>
      <div v-if="downloadAuth && appendixs.length>0">
        <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
          批量下载
        </a-button>
      </div>

    </a-spin>
  </a-modal>

</template>

<script>
  import Vue from 'vue'
  import {
    ACCESS_TOKEN
  } from '@/store/mutation-types'
  import JSZip from 'jszip'
  import FileSaver from 'file-saver'
  import {
    getAction,
    getFileAccessHttpUrl,
    downFile,
    postAction
  } from '@/api/manage'
  import PdfPreviewModal from '../pdf/PdfPreviewModal'
  export default {
    name: 'HonourDetailModal',
    components: {
      PdfPreviewModal
    },
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
        checkAll: false,
        appendixs: [],
        appendixIds: [],
        plainOptions: [],
        indeterminate: false,
        compLabel: ['标\xa0\xa0\xa0\xa0\xa0\xa0\xa0题', '负\xa0\xa0责\xa0\xa0人'],
        requireTimeLabel: ['', '发文日期', '颁发日期', '发表时间', '授发时间', '签约时间', '授发时间', '发表时间'],
        title: '操作',
        confirmLoading: false,
        description: '汇总查询',
        uploading: false,
        modal: {},
        visible: false,
        url: {
          getDetail: '/honour/findDetailById',
          auth: '/honour/auth/getMyAuth',
          getAppendix: '/honour/findAppendixByIds',
          saveHistory: '/honour/saveHistoriesByIds',
          word2pdf: '/sys/common/wordToPdf'
        },
        data: {
          beginTime: null
        },
        btndisabled: false,
        downloadAuth: false,
        dataSource: [{
          key: 0,
          appendixs: []
        }]

      }
    },
    created() {
    },
    computed: {

    },
    methods: {
      detail(r) {
        // this.btndisabled = true
        // var r = this.processData
        console.log(r)
        var that = this
        that.dataSource = [{
          key: 0,
          appendixs: []
        }]
        that.checkAll = false
        that.plainOptions = []
        that.appendixIds = []
        that.appendixs = []
        that.downloadAuth = false
        that.visible = true
        var params = {
          id: r.tableId,
          honourClass: r.honourClass
        }
        console.log(params.id)
        getAction(this.url.getDetail, params).then((res) => {
          if (res.success && res.result) {
            if (res.result.total > 0) {
              that.modal = res.result.records[0]
              that.modal.honourClass = r.honourClass
              that.modal.honourClass_dictText = r.honourClass_dictText
              that.modal.majorName = r.majorName
              this.getMyAuth(r.honourClass, r.appendixIds)
            }
          }
        })
      },
      showDocx(appendix) {
        var that = this
        var param = {}
        var key = 'wordToPdf'
        param.wordPath = appendix.fileUrl
        this.$message.loading({
          content: '转码中...请稍后',
          key
        })
        getAction(this.url.word2pdf, param).then((res) => {
          if (res.success) {
            console.log(res.result)
            that.$message.success({
              content: '转码成功!',
              key,
              duration: 2
            })
            var item = {}
            item.fileUrl = res.result
            that.pdfPreview(item)
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      pdfPreview: function(record) {
        const token = Vue.ls.get(ACCESS_TOKEN)
        this.headers = {
          'X-Access-Token': token
        }
        this.$refs.pdfmodal.previewFiles(record, token)
      },
      getMyAuth(honourClass, appendixIds) {
        let params = {}
        var that = this
        params.honourClass = parseInt(honourClass)

        getAction(this.url.auth, params).then((res) => {
          if (res.success && res.result) {
            var now = new Date().getTime()
            that.honourAuth = res.result
            if (that.honourAuth.downloadAuth == 1) {
              that.downloadAuth = true
            } else if (that.honourAuth.downloadAuth == 2) {
              var downTime = Date.parse(new Date(that.honourAuth.downloadAuth))
              if (now - downTime < 0) {
                that.downloadAuth = true
              }
            }

            if (that.downloadAuth && appendixIds) {
              that.getAppendix(appendixIds)
            }
          }
        })
      },
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      onChange(checkedValues) {
        this.indeterminate = !!this.appendixIds.length && this.appendixIds.length < this.plainOptions.length
        this.checkAll = this.appendixIds.length === this.plainOptions.length
      },
      onCheckAllChange(e) {
        Object.assign(this, {
          appendixIds: e.target.checked ? this.plainOptions : [],
          indeterminate: false,
          checkAll: e.target.checked
        })
      },
      getAvatarView(appendix) {
        return getFileAccessHttpUrl(appendix.fileUrl)
      },
      getAppendix(appendixIds) {
        var that = this
        var params = {
          appendixIds: appendixIds
        }
        var data = {
          key: 0,
          appendixs: []
        }

        that.dataSource = []
        that.dataSource.push(data)
        getAction(this.url.getAppendix, params).then((res) => {
          if (res.success) {
            this.appendixs = res.result
            for (var i = 0; i < this.appendixs.length; i++) {
              this.plainOptions.push(this.appendixs[i].id)
              this.appendixs[i].isImage = false
              if (this.appendixs[i].hasOwnProperty('fileType')) {
                if (this.appendixs[i].fileType.indexOf('png') > -1 ||
                  this.appendixs[i].fileType.indexOf('jpg') > -1 ||
                  this.appendixs[i].fileType.indexOf('JPG') > -1 ||
                  this.appendixs[i].fileType.indexOf('PNG') > -1 ||
                  this.appendixs[i].fileType.indexOf('bmp') > -1 ||
                  this.appendixs[i].fileType.indexOf('BMP') > -1 ||
                  this.appendixs[i].fileType.indexOf('JPEG') > -1 ||
                  this.appendixs[i].fileType.indexOf('jpeg') > -1 ||
                  this.appendixs[i].fileType.indexOf('webp') > -1 ||
                  this.appendixs[i].fileType.indexOf('gif') > -1) {
                  this.appendixs[i].isImage = true
                }
              }
            }
            console.log(this.appendixs)
            this.dataSource[0].appendixs = this.appendixs
          } else {
            that.$message.warning(res.message)
          }
        })
      },
      handleOk() {
        if (this.appendixs.length == 0) {

        }
        var data = []
        var ids = ''
        for (var i = 0; i < this.appendixs.length; i++) {
          if (this.appendixIds.indexOf(this.appendixs[i].id) > -1) {
            ids += this.appendixs[i].id + ','
            var url = getFileAccessHttpUrl(this.appendixs[i].fileUrl)
            data.push(url)
          }
        }

        const zip = new JSZip()
        const cache = {}
        const promises = []
        data.forEach(item => {
          const promise = downFile(item, {}).then(data => { // 下载文件, 并存成ArrayBuffer对象
            var arr_name = item.split('/')
            var file_name = arr_name[arr_name.length - 1] // 获取文件名
            zip.file(file_name, data, {
              binary: true
            }) // 逐个添加文件
            cache[file_name] = data
          })
          promises.push(promise)
        })

        Promise.all(promises).then(() => {
          zip.generateAsync({
            type: 'blob'
          }).then(content => { // 生成二进制流
            FileSaver.saveAs(content, '附件.zip') // 利用file-saver保存文件
          })
        })
        this.saveHistory(ids)
      },
      saveHistory(appendixIds) {
        var params = {}
        params.appendixIds = appendixIds
        params.title = this.modal.title
        params.honourClass = this.modal.honourClass
        var formData = []
        formData.push(params)
        postAction(this.url.saveHistory, formData).then((res) => {

        })
      }
    }
  }
</script>

<style scoped>
  /deep/.ant-form-item-label>label {
    color: rgb(0 0 0 / 45%) !important;
    margin-left: 80px;
  }

  span {
    color: #000000;
  }

  .img-div1 {
    float: left;
    width: 23%;
    height: 200px;
    margin-right: 10px;
    margin: 0 8px 8px 0;
  }

  .img-div2 {
    width: 100%;
    height: 100%;
    position: relative;
    padding: 8px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
  }

  .show-img {
    max-height: 140px;
    width: 100%;
  }

  .text {
    width: 120px;
    white-space: nowrap;
    word-break: break-all;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
    float: right
  }
</style>
