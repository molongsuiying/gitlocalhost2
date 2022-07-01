<template>

  <a-upload
    name="file"
    listType="picture-card"
    :multiple="isMultiple"
    :action="uploadAction"
    :headers="headers"
    :data="{biz:bizPath}"
    :fileList="fileList"
    :beforeUpload="beforeUpload"
    :disabled="disabled"
    :isMultiple="isMultiple"
    :showUploadList="isMultiple"
    @change="handleChange"
    @preview="handlePreview">

    <div v-if="!isMultiple && picUrl ">
      <template v-if="isImage">
        <img :src="getAvatarView()" style="height:104px;max-width:300px"/></br>
      </template>
      <template v-else>
        <a-icon type="paper-clip" /></br>
      </template>
      <span>{{ fileList[0].name }}</span>
    </div>

    <div v-else >
      <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
      <div class="ant-upload-text">{{ text }}</div>
    </div>
    <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel()">
      <img alt="example" style="width: 100%" :src="previewImage"/>
    </a-modal>
    <pdf-preview-modal ref="pdfmodal"></pdf-preview-modal>
  </a-upload>

</template>

<script>
  import Vue from 'vue'
  import { ACCESS_TOKEN } from '@/store/mutation-types'
  import { getAction,getFileAccessHttpUrl } from '@/api/manage'
  import PdfPreviewModal from '../pdf/PdfPreviewModal'
  const uidGenerator = () => {
    return '-' + parseInt(Math.random() * 10000 + 1, 10)
  }
  const getFileName = (path) => {
    if (path.lastIndexOf('\\') >= 0) {
      let reg = new RegExp('\\\\', 'g')
      path = path.replace(reg, '/')
    }
    return path.substring(path.lastIndexOf('/') + 1)
  }
  export default {
    name: 'JImageUpload',
    components: {
        PdfPreviewModal
    },
    data() {
      return {
        uploadAction: window._CONFIG['domianURL'] + '/sys/common/uploadFile',
        uploadLoading: false,
        picUrl: false,
        headers: {},
        fileList: [],
        appendixs: [],
        previewImage: '',
        isImage: '',
        previewVisible: false,
        url:{
          word2pdf: "/sys/common/wordToPdf",
        }
      }
    },
    props: {
      text: {
        type: String,
        required: false,
        default: '上传'
      },
      /* 这个属性用于控制文件上传的业务路径 */
      bizPath: {
        type: String,
        required: false,
        default: 'temp'
      },
      value: {
        type: [String, Array],
        required: false
      },
      disabled: {
        type: Boolean,
        required: false,
        default: false
      },
      isMultiple: {
        type: Boolean,
        required: false,
        default: true
      }
    },
    watch: {
      value(val) {
        if (val instanceof Array) {
          this.initFileList(val)
        } else {
          this.initFileList(val)
        }
        if (!val || val.length == 0) {
          this.picUrl = false
        }
      }
    },
    created() {
      const token = Vue.ls.get(ACCESS_TOKEN)
      this.headers = { 'X-Access-Token': token }
    },
    methods: {
      initFileList(appendixs) {
        if (!appendixs || appendixs.length == 0) {
          this.fileList = []
          return
        }
        this.picUrl = true
        let fileList = []
        for (var a = 0; a < appendixs.length; a++) {
          let url = getFileAccessHttpUrl(appendixs[a].path)
          let name = getFileName(appendixs[a].path)

          var t = {
            uid: uidGenerator(),
            name: name,
            status: 'done',
            appendixId: appendixs[a].appendixId,
            url: url,
            response: {
              status: 'history',
              message: appendixs[a].path,
              result: {
                appendixId: appendixs[a].appendixId,
                path: appendixs[a].path
              }
            }
          }
          fileList.push(t)

          if (!this.isMultiple) {
            if (appendixs[a].hasOwnProperty('type')) {
              if (appendixs[a].type.indexOf('png') > -1 ||
                  appendixs[a].type.indexOf('jpg') > -1 ||
                  appendixs[a].type.indexOf('JPG') > -1 ||
                  appendixs[a].type.indexOf('PNG') > -1 ||
                  appendixs[a].type.indexOf('bmp') > -1 ||
                  appendixs[a].type.indexOf('BMP') > -1 ||
                  appendixs[a].type.indexOf('JPEG') > -1 ||
                  appendixs[a].type.indexOf('jpeg') > -1 ||
                  appendixs[a].type.indexOf('gif') > -1) {
                  this.isImage = true
              }
            }
          }
        }
        this.fileList = fileList
      },
      beforeUpload: function(file) {
        var fileType = file.type
        if (!this.isMultiple) {
          if (fileType.indexOf('image') < 0) {
            this.isImage = false
          } else {
            this.isImage = true
          }
        }
      },
      handleChange(info) {
        this.picUrl = false
        let fileList = info.fileList
        if (info.file.status === 'done') {
          if (info.file.response.success) {
            this.picUrl = true
            fileList = fileList.map((file) => {
              if (file.response) {
                var temp = file.response
                file.url = temp.result.path
                file.appendixId = temp.result.appendixId
              }
              return file
            })
          }
        } else if (info.file.status === 'error') {
          this.$message.error(`${info.file.name} 上传失败.`)
        } else if (info.file.status === 'removed') {
          this.handleDelete(info.file)
        }
        this.fileList = fileList
        if (info.file.status === 'done' || info.file.status === 'removed') {
          this.handlePathChange()
        }
      },
      // 预览
      handlePreview (file) {
        console.log(file)
        if (file.name.indexOf('png') > -1 ||
            file.name.indexOf('jpg') > -1 ||
            file.name.indexOf('JPG') > -1 ||
            file.name.indexOf('PNG') > -1 ||
            file.name.indexOf('bmp') > -1 ||
            file.name.indexOf('BMP') > -1 ||
            file.name.indexOf('JPEG') > -1 ||
            file.name.indexOf('jpeg') > -1 ||
            file.name.indexOf('gif') > -1) {
            this.previewImage = file.url || file.thumbUrl
            this.previewVisible = true
        } else if(file.name.indexOf('.pdf') > -1 || file.name.indexOf('.PDF') > -1){
          var record = {
            fileUrl:file.response.message
          }
          const hide = this.$message.loading('转码中...请稍后', 0);
          setTimeout(hide, 2500);
          this.pdfPreview(record)
        }else if(file.name.indexOf('.doc') > -1 || file.name.indexOf('.DOC') > -1){
          var record = {
            fileUrl:file.response.message
          }
          this.showDocx(record)
        }else{
          this.$message.warning(`该类型无法预览`)
          this.previewVisible = false
        }
      },
      showDocx(appendix) {
        var that = this;
        var param = {};
        var key = 'wordToPdf';
        param.wordPath = appendix.fileUrl;
        this.$message.loading({ content: '转码中...请稍后', key });
        getAction(this.url.word2pdf, param).then((res) => {
          if (res.success) {
            console.log(res.result);
            that.$message.success({ content: '转码成功!', key, duration: 2 });
            var item = {};
            item.fileUrl = res.result;
            that.pdfPreview(item);
            
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
      getAvatarView() {
        console.log(this.fileList)
        if (this.fileList.length > 0) {
          let url = this.fileList[0].url
          return getFileAccessHttpUrl(url)
        }
      },
      handlePathChange() {
        let uploadFiles = this.fileList
        let temp = {}
        let appendixs = []
        if (!uploadFiles || uploadFiles.length == 0) {
          appendixs = []
        }
        console.log('fileList------2')
        console.log(this.fileList)
        if (!this.isMultiple) {
          temp.appendixId = uploadFiles[uploadFiles.length - 1].appendixId
          temp.path = uploadFiles[uploadFiles.length - 1].url
          appendixs.push(temp)
        } else {
          for (var i = 0; i < uploadFiles.length; i++) {
            var r = {
              'path': uploadFiles[i].url,
              'appendixId': uploadFiles[i].appendixId
            }
            appendixs.push(r)
          }
        }
        console.log(appendixs)
        this.$emit('change', appendixs)
      },
      handleDelete(file) {
        // 如有需要新增 删除逻辑
        console.log(file)
      },
      handleCancel() {
        this.close()
        this.previewVisible = false
      },
      close () {

      }
    },
    model: {
      prop: 'value',
      event: 'change'
    }
  }
</script>

<style scoped>
.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
/deep/.ant-upload-list-picture-card .ant-upload-list-item-info{
  overflow: inherit;
}
/deep/.ant-upload-list-picture-card .ant-upload-list-item-name{
  display: block;
}
</style>
