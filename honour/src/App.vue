<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view/>
    </div>
  </a-config-provider>
</template>
<script>
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
  import enquireScreen from '@/utils/device'

  export default {
    data () {
      return {
        locale: zhCN,
      }
    },
    created () {
      let that = this
      enquireScreen(deviceType => {
        // tablet
        if (deviceType === 0) {
          that.$store.commit('TOGGLE_DEVICE', 'mobile')
          that.$store.dispatch('setSidebar', false)
        }
        // mobile
        else if (deviceType === 1) {
          that.$store.commit('TOGGLE_DEVICE', 'mobile')
          that.$store.dispatch('setSidebar', false)
        }
        else {
          that.$store.commit('TOGGLE_DEVICE', 'desktop')
          that.$store.dispatch('setSidebar', true)
        }

      })
    }
  }
</script>
<style>
  #app {
    height: 100%;
  }
  .ant-table-middle > .ant-table-content > .ant-table-header > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-body > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-scroll > .ant-table-header > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-scroll > .ant-table-body > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-fixed-left > .ant-table-header > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-fixed-right > .ant-table-header > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-fixed-left > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-fixed-right > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-thead > tr > th, .ant-table-middle > .ant-table-content > .ant-table-header > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-body > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-scroll > .ant-table-header > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-scroll > .ant-table-body > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-fixed-left > .ant-table-header > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-fixed-right > .ant-table-header > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-fixed-left > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-tbody > tr > td, .ant-table-middle > .ant-table-content > .ant-table-fixed-right > .ant-table-body-outer > .ant-table-body-inner > table > .ant-table-tbody > tr > td{
    border: 1px solid #d9d9d9;
  }
  .sider ul.ant-menu {

      background: url("./assets/bg.jpg") center top / cover no-repeat fixed;
      color: #FFF;
    }
   .sider ul.ant-menu a{
     color: #FFF;
   }
   .ant-menu:not(.ant-menu-horizontal) .ant-menu-item-selected{
      background-color: rgb(0 0 0 / 25%)!important;
   }
   .toopTitle .ant-tooltip-inner{
     padding:0rem !important;
     background-color: rgba(0, 0, 0, 0)!important;
   }
</style>