<template>
  <div style="padding: 20px;">
    <div style="padding-top: 5%;padding-left: 10%;padding-right: 10%;">

      <a-row :gutter="[48,60]">
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddFile()">
            <p><img src="~@/assets/honour/图标-文件夹.png" /></p>
            <p class="honour_typeText">文件类</p>
            <p>在此新增文件类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddCertificate()">
            <p><img src="~@/assets/honour/图标-证书.png" /></p>
            <p class="honour_typeText">证书类</p>
            <p>在此新增证书类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddArticle()">
            <p><img src="~@/assets/honour/图标-物图类.png" /></p>
            <p class="honour_typeText">物图类</p>
            <p>在此新增物图类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddReport()">
            <p><img src="~@/assets/honour/图标-报道类.png" /></p>
            <p class="honour_typeText">报道类</p>
            <p>在此新增报道类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddAgree()">
            <p><img src="~@/assets/honour/图标-协议类.png" /></p>
            <p class="honour_typeText">协议类</p>
            <p>在此新增协议类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddPerson()">
            <p><img src="~@/assets/honour/图标-人才类.png" /></p>
            <p class="honour_typeText">人才类</p>
            <p>在此新增人才类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAddProject()">
            <p><img src="~@/assets/honour/图标-课题类.png" /></p>
            <p class="honour_typeText">课题类</p>
            <p>在此新增课题类证书</p>
          </a-card>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-card hoverable class="honour_box" @click="handleAdd()">
            <p><img src="~@/assets/honour/图标-总类.png" /></p>
            <p class="honour_typeText">总类</p>
            <p>在此新增总类证书</p>
          </a-card>
        </a-col>
        <!--      <a-col :md="16" :sm="24">

          <div style="margin-top: 30px;">
            <a-tooltip placement="top">
              <template slot="title">
                <span>总类</span>
              </template>
              <a class="btn btn1" @click="handleAdd()">
                <img src="~@/assets/honour/图标-总类@2x.png" />
                <font class="btn-font">总类</font>
              </a>
            </a-tooltip>
          </div>

        </a-col> -->
      </a-row>
    </div>
    <!-- <div style="display: flex;padding-top: 30px;">
      <div class="btn-div">
        <a-tooltip placement="top">
          <template slot="title">
            <span>文件类</span>
          </template>
          <a class="btn" @click="handleAddFile()">
            <img src="./icon/file.png" style="padding-bottom: 8px;" /><br />
            <font>文件类</font>
          </a>
        </a-tooltip>
      </div>
      <div class="btn-div">
        <a-tooltip placement="top">
          <template slot="title">
            <span>证书类</span>
          </template>
          <a class="btn" @click="handleAddCertificate()">
            <img src="./icon/cer.png" style="padding-bottom: 12px;padding-top: 8px;" /><br />
            <font>证书类</font>
          </a>
        </a-tooltip>

      </div>
      <div class="btn-div">

        <a-tooltip placement="top">
          <template slot="title">
            <span>物图类</span>
          </template>
          <a class="btn" @click="handleAddArticle()">
            <img src="./icon/article.png" style="padding-bottom: 8px;" /><br />
            <font>物图类</font>
          </a>
        </a-tooltip>
      </div>
      <div class="btn-div">

        <a-tooltip placement="top">
          <template slot="title">
            <span>报道类</span>
          </template>
          <a class="btn" @click="handleAddReport()">
            <img src="./icon/report.png" style="padding-bottom: 8px;" /><br />
            <font>报道类</font>
          </a>
        </a-tooltip>
      </div>
      <div class="btn-div">

        <a-tooltip placement="top">
          <template slot="title">
            <span>协议类</span>
          </template>
          <a class="btn" @click="handleAddAgree()">
            <img src="./icon/agree.png" style="padding-bottom: 8px;" /><br />
            <font>协议类</font>
          </a>
        </a-tooltip>
      </div>
      <div class="btn-div">
        <a-tooltip placement="top">
          <template slot="title">
            <span>人才类</span>
          </template>
          <a class="btn" @click="handleAddPerson()">
            <img src="./icon/person.png" style="padding-bottom: 8px;" /><br />
            <font>人才类</font>
          </a>
        </a-tooltip>
      </div>
      <div class="btn-div">

        <a-tooltip placement="top">
          <template slot="title">
            <span>课题类</span>
          </template>
          <a class="btn" @click="handleAddProject()">
            <img src="./icon/project.png" style="padding-bottom: 8px;" /><br />
            <font>课题类</font>
          </a>
        </a-tooltip>
      </div>
    </div> -->

    <honour-file-all-modal ref="modalForm" @ok="modalFormOk"></honour-file-all-modal>
    <honour-person-all-modal ref="personModalForm" @ok="modalFormOk"></honour-person-all-modal>
    <honour-certificate-all-modal ref="cerModalForm" @ok="modalFormOk"></honour-certificate-all-modal>
    <honour-agree-all-modal ref="agreeModalForm" @ok="modalFormOk"></honour-agree-all-modal>
    <honour-article-all-modal ref="articleModalForm" @ok="modalFormOk"></honour-article-all-modal>
    <honour-report-all-modal ref="reportModalForm" @ok="modalFormOk"></honour-report-all-modal>
    <honour-all-modal ref="allModalForm" @ok="modalFormOk"></honour-all-modal>
    <honour-project-all-modal ref="projectModal" @ok="modalFormOk"></honour-project-all-modal>
  </div>

</template>

<script>
  import {
    JeecgListMixin
  } from '@/mixins/JeecgListMixin'
  import HonourFileAllModal from './modules/HonourFileAllModal'
  import HonourPersonAllModal from './modules/HonourPersonAllModal'
  import HonourCertificateAllModal from './modules/HonourCertificateAllModal'
  import HonourAgreeAllModal from './modules/HonourAgreeAllModal'
  import HonourArticleAllModal from './modules/HonourArticleAllModal'
  import HonourReportAllModal from './modules/HonourReportAllModal'
  import HonourAllModal from './modules/HonourAllModal'
  import HonourProjectAllModal from './modules/HonourProjectAllModal'
  import {
    getAction,
    getFileAccessHttpUrl
  } from '@/api/manage'

  export default {
    name: 'AddHonour',
    mixins: [JeecgListMixin],
    components: {
      HonourFileAllModal,
      HonourPersonAllModal,
      HonourCertificateAllModal,
      HonourAgreeAllModal,
      HonourArticleAllModal,
      HonourReportAllModal,
      HonourAllModal,
      HonourProjectAllModal
    },
    data() {
      return {
        dataSource: [],
        queryParam: {
          keyWord: ''
        },
        url: {
          list: '/college/list'
        },
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        }
      }
    },
    mounted() {
      this.loadData()
    },
    methods: {
      getQueryParams() {

      },
      loadData() {
        var param = this.queryParam
        getAction(this.url.list, param).then(res => {
          if (res.success) {
            console.log(res.result)
            this.dataSource = res.result.records
            this.ipagination.total = res.result.total
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      getAvatarView: function(avatar) {
        return getFileAccessHttpUrl(avatar)
      },
      handleAdd: function() {
        this.$refs.allModalForm.add()
        this.$refs.allModalForm.title = '新增荣誉'
        this.$refs.allModalForm.disableSubmit = false
      },
      handleAddFile: function() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增文件类荣誉'
        this.$refs.modalForm.disableSubmit = false
      },
      handleAddCertificate: function() {
        this.$refs.cerModalForm.add()
        this.$refs.cerModalForm.title = '新增证书类荣誉'
        this.$refs.cerModalForm.disableSubmit = false
      },
      handleAddProject: function() {
        this.$refs.projectModal.add()
        this.$refs.projectModal.title = '新增课题类荣誉'
        this.$refs.projectModal.disableSubmit = false
      },
      handleAddArticle: function() {
        this.$refs.articleModalForm.add()
        this.$refs.articleModalForm.title = '新增物图类荣誉'
        this.$refs.articleModalForm.disableSubmit = false
      },
      handleAddReport: function() {
        this.$refs.reportModalForm.add()
        this.$refs.reportModalForm.title = '新增报道类荣誉'
        this.$refs.reportModalForm.disableSubmit = false
      },
      handleAddAgree: function() {
        this.$refs.agreeModalForm.add()
        this.$refs.agreeModalForm.title = '新增协议类荣誉'
        this.$refs.agreeModalForm.disableSubmit = false
      },
      handleAddPerson: function() {
        this.$refs.personModalForm.add()
        this.$refs.personModalForm.title = '新增人才类荣誉'
        this.$refs.personModalForm.disableSubmit = false
      },
      handleEdit: function(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
        this.$refs.modalForm.disableSubmit = false
      },
      onSearch() {
        this.loadData()
      },
      modalFormOk() {
        this.loadData()
      },
      gotoMajor(collegeId) {
        this.$router.push({
          path: '/school/departmentList',
          query: {
            collegeId: collegeId
          }
        })
      }
    }
  }
</script>

<style scoped>
  .btn {
    float: left;
    height: 120px;
    width: 120px;
    padding: 15px;
    margin-right: 12px;
    position: relative;
    text-decoration: none;
    border: 1px solid #CCC;

    background-color: #FFFFFF;
    margin-bottom: 20px;
    margin-left: 0px;
    text-align: center;
    color: #666;
    font-size: 12px;
    font-weight: bold;
    margin-top: 5px;
  }

  .btn:hover {
    transition: transform 0.5s;
    transform: scale(1.1);
    background-color: #f0f2f5;

  }

  .btn-div {
    width: 14.2%;
    text-align: center;
    padding-top: 100px;
  }

  .btn1 {
    width: 300px;
    padding: 27px;
  }

  .btn-font {
    margin-left: 35px;
    font-size: 24px;
    vertical-align: middle;
  }

  .honour_box {
    text-align: center;
    height: 250px;
    padding-top: 20px;
  }

  .honour_typeText {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 12px;
  }
</style>
