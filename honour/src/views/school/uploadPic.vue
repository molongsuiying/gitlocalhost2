<template>
  <a-card :bordered="false">
    
    <div class="clearfix">

      <a-upload
        :multiple="isMultiple"
        :file-list="fileList"
        :remove="handleRemove"
        :before-upload="beforeUpload"
        :accept="accept"
        list-type="picture-card"
        @preview="handlePreview"
        class="upload-list-inline"
        @change="handleChange">
        <div v-if="fileList.length < maxPicNum">
          <a-icon type="plus" />
          <div class="ant-upload-text">
            上传图片
          </div>
        </div>
      </a-upload>
      <a-divider orientation="right" dashed>
            操作
          </a-divider>
          <div style="float:right">
            <a-button  @click="cleanAllFile" style="margin-right: 16px;" :disabled="cleanDisabled">清空重置</a-button>
            <a-button  @click="balanceStudent" style="margin-right: 16px;" :disabled="cleanDisabled">学生对比</a-button>

            <a-button type="primary" :disabled="fileList.length === 0" :loading="uploading" style="margin-top: 16px" @click="handleUpload">
              {{ uploading ? "上传中..." : "开始上传" }}
            </a-button>
          </div>
     
      <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
        <img alt="example" style="width: 100%" :src="previewImage" />
      </a-modal>
    </div>

  </a-card>
</template>

<script>

  import { postAction } from "@/api/manage"
  function getBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => resolve(reader.result)
      reader.onerror = error => reject(error)
    })
  }

  export default {
    name: "uploadPic",
    components: {
    },
    data() {
      return {
        description: "图片批量上传",
        fileList: [],
        accept:".jpg,.png,.gif,.jpeg,.webp,.bmp",
        maxPicNum: 10,
        isMultiple: true,
        uploading: false,
        previewVisible: false,
        cleanDisabled:false,
        previewImage: "",
        url: {
          upload: "/sys/common/uploadMultipartFile",
        },
        
      }
    },
    computed: {

    },
    methods: {
      handleRemove(file) {
        const index = this.fileList.indexOf(file)
        const newFileList = this.fileList.slice()
        newFileList.splice(index, 1)
        this.fileList = newFileList
      },
      beforeUpload(file) {
        this.fileList = [...this.fileList, file]
        if (this.fileList.length > this.maxPicNum) {
          this.fileList = this.fileList.slice(0, 10)
        }
        return false
      },
      handleUpload() {
        this.cleanDisabled = true;
        const {
          fileList
        } = this
        const formData = new FormData()
        fileList.forEach(file => {
          console.log(file);
          formData.append("files[]", file.originFileObj)
        })
        formData.append("biz", "temp");
        this.uploading = true;
        postAction(this.url.upload,formData).then(res=>{
          if(res.success){
            console.log(res.result);
            this.fileList = [];
            this.cleanDisabled = false;
            this.$message.success("上传成功!");
          }
        }).finally(() => {
          this.uploading = false;
          this.cleanDisabled = false;
        });
      },
      handleCancel() {
        this.previewVisible = false
      },
      async handlePreview(file) {
        console.log(file)
        if (!file.url && !file.preview) {
          file.preview = await getBase64(file.originFileObj)
        }
        this.previewImage = file.url || file.preview
        this.previewVisible = true
      },
      handleChange({ fileList }) {
          this.fileList = fileList
          if (this.fileList.length > this.maxPicNum) {
            this.fileList = this.fileList.slice(0, 10)
          }
      },
      cleanAllFile(){
        this.fileList = [];
      },
      balanceStudent(){
        console.log(this.fileList);
      }
    }
  }
</script>
<style>
.ant-upload-list-picture-card .ant-upload-list-item-info {
    position: relative;
    height: 100%;
    overflow: inherit;
}
.ant-upload-list-picture-card .ant-upload-list-item-name {
    display: inline-block;
    margin: 8px 0 0;
    padding: 0;
    line-height: 1.5;
    text-align: center;
}
</style>
<style scoped>
  @import "~@assets/less/common.less"
</style>
