<template>
<a-card :bordered="false">
  <div>
    
      <!-- <a-list-item-meta>
        <a slot="title">{{ item.title }}</a>
        <span slot="description">
          <span class="security-list-description">{{ item.description }}</span>
          <span v-if="item.value"> : </span>
          <span class="security-list-value">{{ item.value }}</span>
        </span>
      </a-list-item-meta>
      <template v-if="item.actions">
        <a slot="actions" @click="handlePerssion(recard.username)">{{ item.actions.title }}</a>
      </template> -->
          <a-button @click="handlePerssion" type="primary">修改密码</a-button>
   
        <role-modal ref="modalForm" @ok="modalFormOk"></role-modal>
        <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>
        </div>
  </a-card>
</template>

<script>
  import PasswordModal from '../../system/modules/PasswordModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import store from '@/store/'
  export default {
    name: "RoleList",
    mixins:[JeecgListMixin],
    components: {
      PasswordModal
    },
    data () {
      return {
        data: [
          { title: '账户密码' , description: '当前密码强度', value: '强', actions: { title: '修改' } },
        
          // { title: '密保手机' , description: '已绑定手机', value: '138****8293', actions: { title: '修改', callback: () => { this.$message.success('This is a message of success'); } }  },
          // { title: '密保问题' , description: '未设置密保问题，密保问题可有效保护账户安全', value: '', actions: { title: '设置', callback: () => { this.$message.error('This is a message of error'); } }  },
          // { title: '备用邮箱' , description: '已绑定邮箱', value: 'ant***sign.com', actions: { title: '修改', callback: () => { this.$message.warning('This is message of warning'); } }  },
          // { title: 'MFA 设备' , description: '未绑定 MFA 设备，绑定后，可以进行二次确认', value: '', actions: { title: '绑定', callback: () => { this.$message.info('This is a normal message'); } }  },
        ],
        model: {},
      }
    },
    methods: {
      handlePerssion: function(){
        let username = store.getters.userInfo.username
        console.log(username)
        this.$refs.passwordmodal.show(username);
      },
    }
  }
</script>

<style scoped>

</style>