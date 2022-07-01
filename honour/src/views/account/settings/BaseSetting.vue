<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">

        <a-form layout="vertical">
          <a-form-item label="真实姓名">
            <a-input placeholder="给自己起个名字" v-model="model.realname" />
          </a-form-item>

          <a-form-item label="性别">
            <j-dict-select-tag v-model="model.sex" type="radio" dictCode="sex" />
          </a-form-item>

          <a-form-item label="生日">
            <a-date-picker style="width: 100%" placeholder="请选择生日" v-model="model.birthday" />
          </a-form-item>

          <a-form-item label="电子邮件" :required="false">
            <a-input placeholder="请输入电子邮箱" v-model="model.email" />
          </a-form-item>
          <a-button type="primary" @click="reset()">重置</a-button>
          <a-button style="margin-left: 8px" @click="save()">保存</a-button>
          </a-form-item>
        </a-form>

      </a-col>
      <!-- <a-col :md="24" :lg="8" :style="{ minHeight: '180px' }">
        <div class="ant-upload-preview" @click="$refs.modal.edit(1)">
          <a-icon type="cloud-upload-o" class="upload-icon" />
          <div class="mask">
            <a-icon type="plus" />
          </div>
          <img :src="getAvatar()" />
        </div>
      </a-col> -->

    </a-row>

    <avatar-modal ref="modal">

    </avatar-modal>
  </div>
</template>

<script>
  import moment from 'moment'
  import AvatarModal from './AvatarModal'
  import {
    mapGetters
  } from 'vuex'
  import {
    getFileAccessHttpUrl,
    putAction
  } from '@/api/manage'
  export default {
    components: {
      AvatarModal
    },
    data() {
      return {
        // cropper
        model: {},
        dateFormat: 'YYYY-MM-DD',
        preview: {},
        option: {
          img: '/avatar2.jpg',
          info: true,
          size: 1,
          outputType: 'jpeg',
          canScale: false,
          autoCrop: true,
          // 只有自动截图开启 宽度高度才生效
          autoCropWidth: 180,
          autoCropHeight: 180,
          fixedBox: true,
          // 开启宽度和高度比例
          fixed: true,
          fixedNumber: [1, 1]
        },
        url: {
          updateMyInfo: '/sys/user/updateMyInfo'
        }
      }
    },
    created() {
      this.userInfo().birthday = moment(this.userInfo().birthday)
      this.model = Object.assign(this.userInfo(), {})
      console.log(this.userInfo())
    },
    methods: {

      ...mapGetters(['nickname', 'avatar', 'userInfo']),
      getAvatar() {
        return getFileAccessHttpUrl(this.avatar())
      },
      moment,
      save() {
        var that = this
        if (this.model.realname == '') {
          that.$message.error('请输入真实姓名')
          return
        }
        let formData = Object.assign(this.model, {})
        putAction(this.url.updateMyInfo, formData).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          that.confirmLoading = false
        })
      },
      reset() {
        this.userInfo().birthday = moment(this.userInfo().birthday)
        this.model = Object.assign(this.userInfo(), {})
      }
    }
  }
</script>

<style lang="less" scoped>
  .avatar-upload-wrapper {
    height: 200px;
    width: 100%;
  }

  .ant-upload-preview {
    position: relative;
    margin: 0 auto;
    width: 100%;
    max-width: 180px;
    border-radius: 50%;
    box-shadow: 0 0 4px #ccc;

    .upload-icon {
      position: absolute;
      top: 0;
      right: 10px;
      font-size: 1.4rem;
      padding: 0.5rem;
      background: rgba(222, 221, 221, 0.7);
      border-radius: 50%;
      border: 1px solid rgba(0, 0, 0, 0.2);
    }

    .mask {
      opacity: 0;
      position: absolute;
      background: rgba(0, 0, 0, 0.4);
      cursor: pointer;
      transition: opacity 0.4s;

      &:hover {
        opacity: 1;
      }

      i {
        font-size: 2rem;
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -1rem;
        margin-top: -1rem;
        color: #d6d6d6;
      }
    }

    img,
    .mask {
      width: 100%;
      max-width: 180px;
      height: 100%;
      border-radius: 50%;
      overflow: hidden;
    }
  }
</style>
