<template>
    <a-form-item style="margin-top:24px">
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="loginBtn"
          @click.stop.prevent="handleSubmit($event,'1535167297677205505')"
          :disabled="loginBtn">成果管理系统
        </a-button>
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="loginBtn"
          @click.stop.prevent="handleSubmit($event,'1535097203408338946')"
          :disabled="loginBtn">信息管理系统
        </a-button>
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="loginBtn"
          @click.stop.prevent="handleSubmit($event,'1535167376039387137')"
          :disabled="loginBtn">应用管理系统
        </a-button>
    </a-form-item>
</template>

<script>
 
import { userInfo } from 'os';
import { select1 } from '@/api/api';
  import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
  import { mapActions } from 'vuex'
  import { timeFix } from '@/utils/util'
  import Vue from 'vue'
  import { ACCESS_TOKEN, ENCRYPTED_STRING, USER_INFO } from '@/store/mutation-types'
  import { putAction, postAction, getAction } from '@/api/manage'
  import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt'
  import store from '@/store/'

  import LoginSelectModal from './LoginSelectModal.vue'
  export default {
    data () {
      return {
        msg: ""
      }
    },
    methods: {
      handleSubmit (e,num) {
        let id = store.getters.userInfo.id
        let sysType = num
        console.log(id,num,e,this)
        select1({ id: id, sysType: sysType }).then((res) => {
                if (res.success) {
                  this.$message.success(res.message)
                  // this.loadData()
                  // this.onClearSelected()
                  this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
                console.log('登录跳转首页出错,这个错误从哪里来的')
            })
                } else {
                  this.$message.warning(res.message)
                }
              })
        
        // var url = "/app/select1"+id+sysType
        // getAction(url).then((res) => {
        //   if (res.success) {
        //     this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
        //         console.log('登录跳转首页出错,这个错误从哪里来的')
        //     })
        //     this.$notification.success({
        //         message: '欢迎',
        //         description: `${timeFix()}，进入本系统`
        // })
        //   }
        // })
        //  fetch("http://localhost:8084/jeecg-boot/app/select1"+id+"/"+sysType).then((res)=>{
        //       return res.json()
        //   }).then((res)=>{
        //       this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
        //         console.log('登录跳转首页出错,这个错误从哪里来的')
        //       })
        //       this.$notification.success({
        //         message: '欢迎',
        //         description: `${timeFix()}，进入本系统`
        //       })
        //   }).catch((err)=>{
        //       console.log(err)
        //       this.requestFailed(err)
        //   }),
      //     async function getJSON() {
      //       var id = "admin";
      //       console.log(id)
      //       var sysType = "0";
      //       let response = await fetch("http://localhost:8084/jeecg-boot/app/select1"+id+"/"+sysType);
      //       if (response.status >= 200 && response.status < 300) {
      //         //   this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
      //         //   console.log('登录跳转首页出错,这个错误从哪里来的')
      //         // })
      //         this.$notification.success({
      //           message: '欢迎',
      //           description: `${timeFix()}，欢迎进入本系统`
      //         })
      //           return await response.text();
      //       } else {
      //           throw new Error(this.requestFailed(err));
      //       }
      //     }
      },

      // ...mapGetters(['nickname', 'avatar', 'userInfo']),
    },
    // created() {
    //   this.hello();
    // }
  }
</script>

<style>

</style>