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
            上传附件
          </div>
        </div>
      </a-upload>
      <div style="float:right">
        <a-button @click="cleanAllFile" style="margin-right: 16px;" :disabled="cleanDisabled">清空重置</a-button>
        <a-button
          type="primary"
          :disabled="fileList.length === 0"
          :loading="uploading"
          style="margin-top: 16px"
          @click="handleUpload">
          {{ uploading ? "上传中..." : "开始上传" }}
        </a-button>
      </div>

      <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
        <img alt="example" style="width: 100%" :src="previewImage" />
      </a-modal>
    </div>
    <a-divider dashed>
      操作
    </a-divider>
    <a-row :gutter="[16,16]">
      <a-col :span="8">
        <div class="operator operate-pic">
          <draggable
            v-model="list"
            group="itxst"
            animation="300"
            style="min-height: 500px;width: 100%;display: inline-block;">
            <div v-for="(item,index) in list" :key="index" style="width: 32%;display: inline-block;margin: 2px;">
              <div class="pic-container">
                <a-tooltip placement="top">
                  <template slot="title">
                    <span>文件名:{{ item.fileName }}</span><br>
                    <span>文件类型:{{ item.fileType }}</span>
                  </template>
                  <div class="pic-container2">
                    <span style="width: 86px;height: 86px;display: block;">
                      <template v-if="item.fileType.indexOf('image') != -1 ">
                        <img
                          style="width: 100%;height: 100%;"
                          :src="getAvatarView(item)"
                          :preview="getAvatarView(item)">
                      </template>
                      <template v-else-if="item.fileType.indexOf('zip') != -1 ">
                        <a-icon type="file-zip" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                      <template v-else-if="item.fileType.indexOf('text') != -1 ">
                        <a-icon type="file-text" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                      <template v-else-if="item.fileType.indexOf('stream') != -1 ">
                        <a-icon type="file-word" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                      <template v-else-if="item.fileType.indexOf('pdf') != -1 ">
                        <a-icon type="file-pdf" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                      <template
                        v-else-if="item.fileType.indexOf('excel')!=-1||item.fileType.indexOf('sheet')!=-1">
                        <a-icon type="file-excel" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                      <template v-else>
                        <a-icon type="file-unknown" style="width: 100%;height: 100%;margin-top: 27px;fontSize:30px;" />
                      </template>
                    </span>
                  </div>
                </a-tooltip>

              </div>
            </div>
          </draggable>
        </div>
      </a-col>

      <a-col :span="16">
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="[16,0]">
              <a-col :md="6" :sm="24">
                <a-form-item label="荣誉类型">
                  <a-select v-model="honourType" placeholder="请选择荣誉类型" @change="handleTypeChange">
                    <a-select-option :value="1">
                      文件类
                    </a-select-option>
                    <a-select-option :value="2">
                      证书类
                    </a-select-option>
                    <a-select-option :value="3">
                      物图类
                    </a-select-option>
                    <a-select-option :value="4">
                      报道类
                    </a-select-option>
                    <a-select-option :value="5">
                      协议类
                    </a-select-option>
                    <a-select-option :value="6">
                      人才类
                    </a-select-option>
                    <a-select-option :value="7">
                      课题类
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="标题">
                  <a-input placeholder="请输入标题" v-model="queryParam.title"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="附件">
                  <a-checkbox :checked="checked" @change="onCheckChange">
                    无附件
                  </a-checkbox>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <span style="float: right;margin-right: 35px;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                </span>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div class="operator">

          <div
            v-infinite-scroll="handleInfiniteOnLoad"
            class="demo-infinite-container"
            :infinite-scroll-disabled="busy"
            :infinite-scroll-distance="10">
            <a-list :data-source="list2">
              <a-list-item slot="renderItem" slot-scope="item, index">

                <div style="width: 175px;display:inline-block;">
                  <a-list-item-meta>
                    <a slot="title" :id="item.id">
                      标题:{{ item.title }}
                    </a>
                  </a-list-item-meta>
                  <template v-if="item.appendixIds != null">
                    <a-tag color="#87d068">已有附件</a-tag>
                  </template>
                  <template v-else>
                    <a-tag>暂无附件</a-tag>
                  </template>
                </div>

                <div style="display: inline-block;width: 100%;">
                  <draggable group="itxst" animation="300" v-model="item.appendixs" class="draggable">
                    <template v-if="item.appendixs.length == 0">
                      <div class="non-pic-tip">
                        拖拽到此处
                      </div>
                    </template>
                    <template v-else v-for="(appendix,index2) in item.appendixs" :index="index2">

                      <a-tooltip placement="top" :key="index2">
                        <template slot="title">
                          <span>文件名:{{ appendix.fileName }}</span>
                        </template>
                        <div style="float: left;width:64px;height:64px;margin: 8px;">
                          <div
                            style="width: 100%;height: 100%;position: relative;padding: 8px;border: 1px solid #d9d9d9;border-radius: 4px;">
                            <span style="width: 48px;height: 48px;display: block;">
                              <template v-if="appendix.fileType.indexOf('image') != -1 ">
                                <img
                                  style="width: 100%;height: 100%;"
                                  :src="getAvatarView(appendix)"
                                  :preview="getAvatarView(appendix)">
                              </template>
                              <template v-else-if="appendix.fileType.indexOf('zip') != -1 ">
                                <a-icon type="file-zip" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>
                              <template v-else-if="appendix.fileType.indexOf('text') != -1 ">
                                <a-icon type="file-text" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>
                              <template v-else-if="appendix.fileType.indexOf('stream') != -1 ">
                                <a-icon type="file-word" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>
                              <template v-else-if="appendix.fileType.indexOf('pdf') != -1 ">
                                <a-icon type="file-pdf" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>
                              <template
                                v-else-if="appendix.fileType.indexOf('excel')!=-1||appendix.fileType.indexOf('sheet')!=-1">
                                <a-icon type="file-excel" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>
                              <template v-else>
                                <a-icon type="file-unknown" style="width: 100%;height: 100%;margin-top: 8px;fontSize:30px;" />
                              </template>

                            </span>
                          </div>
                        </div>
                      </a-tooltip>
                    </template>
                  </draggable>
                </div>
              </a-list-item>
              <div v-if="loading && !busy" class="demo-loading-container">
                <a-spin />
              </div>
            </a-list>
          </div>

        </div>
        <div style="float: right;padding-right: 35px;">
          <a-button type="primary" @click="batchSave">保存</a-button>
        </div>
      </a-col>
    </a-row>
  </a-card>
</template>

<script>
  import {
    postAction,
    getAction,
    getFileAccessHttpUrl
  } from '@/api/manage'
  import draggable from 'vuedraggable'
  import infiniteScroll from 'vue-infinite-scroll'

  function getBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => resolve(reader.result)
      reader.onerror = error => reject(error)
    })
  }

  export default {
    name: 'UploadPic',
    components: {
      draggable
    },
    directives: {
      infiniteScroll
    },
    data() {
      return {
        queryParam: {
          pageNo: 1,
          pageSize: 7,
          nonSubmit: 1,
          hasAppendix: 0
        },
        description: '附件批量上传',
        fileList: [],
        cacheData: [],
        list: [],
        list2: [],
        endFlag: false,
        noneFlag: false,
        honourType: '',
        current_type: '',
        moveId: '',
        accept: '*',
        maxPicNum: 20,
        checked: true,
        isMultiple: true,
        uploading: false,
        previewVisible: false,
        cleanDisabled: false,
        previewImage: '',
        loading: false,
        busy: false,
        path: 'https://static.jeecg.com/upload/test/3a4490d5d1cd495b826e528537a47cc1.jpg',
        url: {
          upload: '/sys/common/uploadMultipartFileByHonour',
          list: ['', 'file', 'certificate', 'article', 'report', 'agree', 'person', 'project']
        }

      }
    },
    computed: {

    },
    created() {},
    methods: {
      fetchData(callback) {
        if (this.honourType == '') {
          return false
        }
        var value = this.url.list[this.honourType]
        var mid = value.substring(0, 1).toUpperCase() + value.substring(1)
        var url = '/honour/' + value + '/queryHonour' + mid + 'List'

        this.queryParam.pageNo += 1
        this.queryParam.pageSize = 7
        this.queryParam.nonSubmit = 1
        var params = this.queryParam
        params.create = 'upload'
        getAction(url, params).then((res) => {
          if (res.success && res.result) {
            for (var i = 0; i < res.result.records.length; i++) {
              res.result.records[i].appendixs = []
            }
            if (res.result.total <= 7) {
              this.endFlag = true
            }
            this.busy = true
            this.loading = false
            callback(res.result.records)
          }
        })
      },
      handleInfiniteOnLoad() {
        const list2 = this.list2
        if (this.endFlag) {
          if (!this.noneFlag) {
            this.$message.warning('没有更多了')
          }
          this.busy = true
          this.loading = false
          this.noneFlag = true
          return
        }
        this.fetchData(res => {
          this.loading = true
          this.list2 = list2.concat(res)
          this.loading = false
        })
      },
      searchQuery() {
        if (this.honourType == '') {
          this.$message.warning('请选择荣誉类型')
          return false
        }
        var value = this.url.list[this.honourType]
        var mid = value.substring(0, 1).toUpperCase() + value.substring(1)
        var url = '/honour/' + value + '/queryHonour' + mid + 'List'
        this.endFlag = false
        this.noneFlag = false
        this.queryParam.pageNo = 1
        this.queryParam.pageSize = 7
        this.queryParam.nonSubmit = 1
        var params = this.queryParam
        params.create = 'upload'
        getAction(url, params).then((res) => {
          if (res.success && res.result) {
            for (var i = 0; i < res.result.records.length; i++) {
              res.result.records[i].appendixs = []
            }
            this.list2 = res.result.records
            if (res.result.total <= 7) {
              this.endFlag = false
            }
            this.busy = true
            this.loading = false
            this.current_type = this.honourType
          }
        })
      },
      searchReset() {
        this.honourType = ''
        this.endFlag = false
        this.noneFlag = false
        this.queryParam = {}
        this.queryParam.pageNo = 1
        this.queryParam.pageSize = 7
        this.queryParam.nonSubmit = 1
        this.queryParam.hasAppendix = 0
        this.checked = true
        this.list2 = []
        this.list = this.cacheData
      },
      initList() {
        var list = []
        for (var i = 0; i < 10; i++) {
          list.push(this.path)
        }
        this.list = list
      },
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
        this.cleanDisabled = true
        const {
          fileList
        } = this
        const formData = new FormData()
        fileList.forEach(file => {
          console.log(file)
          formData.append('files[]', file.originFileObj)
        })
        formData.append('biz', 'temp')
        this.uploading = true
        postAction(this.url.upload, formData).then(res => {
          if (res.success) {
            console.log(res.result)
            this.fileList = []
            this.list = this.list.concat(res.result)
            this.cacheData = this.list
            this.cleanDisabled = false
            this.$message.success('上传成功!')
          }
        }).finally(() => {
          this.uploading = false
          this.cleanDisabled = false
        })
      },
      getAvatarView(record) {
        return getFileAccessHttpUrl(record.path)
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
      handleChange({
        fileList
      }) {
        this.fileList = fileList
        if (this.fileList.length > this.maxPicNum) {
          this.fileList = this.fileList.slice(0, 10)
        }
      },
      cleanAllFile() {
        this.fileList = []
      },
      balanceStudent() {
        console.log(this.fileList)
      },
      handleTypeChange() {
          this.searchQuery()
      },
      onCheckChange(e) {
        var checked = e.target.checked
        this.checked = e.target.checked
        if (checked) {
          this.queryParam.hasAppendix = 0
        } else {
          this.queryParam.hasAppendix = 1
        }
      },
      onStart(e) {
        console.log(e)
      },

      batchSave() {
        var list = this.list2
        var change_list = this.list2.filter(honour => honour.appendixs.length > 0)
        if (list.length == 0 || change_list.length == 0) {
          this.$message.error('荣誉为空,无需保存')
          return false
        }
        var formData = []
        for (var i = 0; i < change_list.length; i++) {
          var appendixIds = change_list[i].appendixs.map(function(item) {
            return item.appendixId
          })
          var entity = {
            id: change_list[i].id,
            appendixIds: appendixIds
          }
          formData.push(entity)
        }
        console.log(formData)
        var value = this.url.list[this.current_type]
        var url = '/honour/' + value + '/saveAppendixList'
        postAction(url, formData).then(res => {
          if (res.success) {
            console.log(res.result)
            this.list2 = this.list2.filter(honour => honour.appendixs.length == 0)
            this.cacheData = this.list
            this.$message.success(res.message)
          }
        }).finally(() => {
          this.uploading = false
          this.cleanDisabled = false
        })
      }
    }
  }
</script>
<style scoped>
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

  .draggable {
    display: inline-block;
    width: 90%;
    float: right;
    min-height: 74px;
    border: 1px dashed #CCC;
  }

  .operator {
    margin: 8px;
    width: 95%;
    min-height: 440px;
    border: 1px dashed #CCC;
  }

  .operate-pic {
    height: 520px;
    overflow-y: auto;
    overflow-x: hidden;
  }

  .table-page-search-wrapper {
    padding-left: 6px;
    padding-top: 4px;
  }

  .ant-form-item {
    margin-bottom: 0px !important;
  }

  .table-page-search-submitButtons {
    margin-bottom: 0px !important;
  }

  .pic-container {
    width: 104px;
    height: 104px;
    margin-left: 8px;
    margin-top: 8px;
    cursor: move;
  }

  .pic-container2 {
    width: 100%;
    height: 100%;
    position: relative;
    padding: 8px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
  }

  .demo-infinite-container {
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    overflow: auto;
    padding: 8px 24px;
    height: 440px;
  }

  .demo-loading-container {
    position: absolute;
    bottom: 40px;
    width: 100%;
    text-align: center;
  }

  .non-pic-tip {
    text-align: center;
    font-size: 40px;
    padding-top: 3px;
    color: #cccccc80
  }

  .upload-list-inline {
    margin-left: 8px;
  }
</style>
<style scoped>
  @import "~@assets/less/common.less"
</style>
