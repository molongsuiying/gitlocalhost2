<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading">
      <a-row :gutter="[16,16]">
        <a-col :span="24">

          <template v-if="dataSource.length > 0">
            <div style="margin-bottom: 10px;margin-top: 10px;">
              <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
                全选
              </a-checkbox>
            </div>
            <a-checkbox-group @change="onChange" v-model="appendixIds" style="width: 100%;">
              <div v-for="(appendix,index) in dataSource[0].appendixs" :key="index">
                <div style="float: left;width:24%;height:265px;margin-right: 10px;margin: 0 8px 8px 0;">
                  <div
                    style="width: 100%;height: 100%;position: relative;padding: 8px;border: 1px solid #d9d9d9;border-radius: 4px;">
                    <div style="text-align: center;">

                      <!-- <template v-if="appendix.fileType.toUpperCase().indexOf("JPG")">
                        <img style="max-height: 166px;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf("TXT")">
                          <a-icon type="file-text" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                      </template> -->

                      <template v-if="appendix.fileType.toUpperCase().indexOf(&quot;JPG&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;JPEG&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;PNG&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;WEBP&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;BMP&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;GIF&quot;) !== -1 ">
                        <img style="max-height: 166px;width: 100%;" :src="getAvatarView(appendix)" :preview="dataSource[0].key">
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
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;PPT&quot;) !== -1 ">
                        <a-icon type="file-ppt" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;ZIP&quot;) !== -1 ">
                        <a-icon type="file-zip" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                      </template>
                      <template v-else-if="appendix.fileType.toUpperCase().indexOf(&quot;DOC&quot;) !== -1 ">
                        <a-icon type="file-word" style="fontSize:100px;width: 100%;margin-top: 30px;" @click="showDocx(appendix)"/></br>
                      </template>
                      <template v-else>
                        <a-icon type="file-unknown" style="fontSize:100px;width: 100%;margin-top: 30px;" /></br>
                      </template>

                    </div>

                    </br>
                    <div style="position: absolute;bottom: 10px;">
                      <a-checkbox :value="appendix.id">
                        <font class="img_name" :title="appendix.fileName">{{ appendix.fileName }}</font>
                      </a-checkbox>
                    </div>

                  </div>
                </div>
              </div>
            </a-checkbox-group>

          </template>
        </a-col>
      </a-row>
    </a-spin>
    <template slot="footer">
      <a-button key="back" @click="handleCancel">
        返回
      </a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleOk">
        批量下载
      </a-button>
    </template>
    <pdf-preview-modal ref="pdfmodal"></pdf-preview-modal>
  </a-modal>
</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from '@/store/mutation-types'
  import JSZip from 'jszip'
  import FileSaver from 'file-saver'
  import PdfPreviewModal from '../pdf/PdfPreviewModal'
  import {
    getAction,
    getFileAccessHttpUrl,
    downFile,
    postAction
  } from '@/api/manage'

  export default {
    name: 'ImagPreview',
    components: {
        PdfPreviewModal
    },
    data() {
      return {
        reName: '',
        indeterminate: false,
        checkAll: false,
        appendixs: [],
        records: {},
        appendixIds: [],
        plainOptions: [],
        title: '下载附件',
        visible: false,
        confirmLoading: false,
        description: '图片预览页面',
        spinning: false,
        // 数据集
        dataSource: [{
          key: 0,
          appendixs: []
        }],
        url: {
          getAppendix: '/honour/findAppendixByIds',
          saveHistory: '/honour/saveHistoriesByIds',
          word2pdf: '/sys/common/wordToPdf'
        }
      }
    },
    created() {},
    methods: {
      add(records) {
        this.records = records
        this.checkAll = false
        this.plainOptions = []
        this.appendixIds = []
        this.dataSource = [{
          key: 0,
          appendixs: []
        }]
        console.log(records)
        if (records.appendixIds && records.appendixIds != null) {
            this.getAppendix(records.appendixIds)
        }
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
        var data = { key: 0, appendixs: [] }

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
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleCancel() {
        this.close()
      },
      saveHistory(appendixIds) {
        var params = {}
        params.appendixIds = appendixIds
        params.title = this.records.title
        params.honourClass = this.records.honourClass
        var formData = []
        formData.push(params)
        postAction(this.url.saveHistory, formData).then((res) => {

        })
      },
      showDocx(appendix) {
        var that = this
        var param = {}
        var key = 'wordToPdf'
        param.wordPath = appendix.fileUrl
        this.$message.loading({ content: '转码中...请稍后', key })
        getAction(this.url.word2pdf, param).then((res) => {
          if (res.success) {
            that.$message.success({ content: '转码成功!', key, duration: 2 })
            console.log(res.result)
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
      handleOk() {
        if (this.appendixs.length == 0) {
          this.close()
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
      }

    }
  }
</script>
<style scoped>
  /deep/.ant-checkbox-wrapper {
    width: 100%;
  }
  .img_name{
    width: 175px;
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
