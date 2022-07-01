<template>
  <div>
    <div id="wordView" v-html="vHtml" />
  </div>
</template>

<script>
import mammoth from 'mammoth'
  import {
    getFileAccessHttpUrl
  } from '@/api/manage'
export default {
  name: 'Word',
  data() {
    return {
      vHtml: '',
      wordURL: ''// 文件地址
    }
  },
  created() {
    let vm = this
    vm.wordURL = this.$route.query.wordURL
    vm.wordURL = getFileAccessHttpUrl(vm.wordURL)
    console.log(vm.wordURL)
    const xhr = new XMLHttpRequest()
    xhr.open('get', this.wordURL, true)
    xhr.responseType = 'arraybuffer'
    xhr.onload = function () {
      if (xhr.status == 200) {
        mammoth
          .convertToHtml({ arrayBuffer: new Uint8Array(xhr.response) })
          .then(function (resultObject) {
            vm.$nextTick(() => {
              // document.querySelector("#wordView").innerHTML =
              //   resultObject.value;
              vm.vHtml = resultObject.value
            })
          })
      }
    }
    xhr.send()
  },
  methods: {
  }
}
</script>

<style>
</style>
