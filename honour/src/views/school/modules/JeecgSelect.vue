<template>
  <a-select
    allowClear
    show-search
    :fields="fields"
    :tableId="tableId"
    :value="keyWord"
    :placeholder="placeholder"
    :default-active-first-option="true"
    :show-arrow="false"
    :filter-option="false"
    :not-found-content="null"
    @search="handleSearch"
    @change="handleChange">
    <a-select-option v-for="d in data" :key="d">
      {{ d }}
    </a-select-option>
  </a-select>
</template>

<script>
  import {
    getAction
  } from '@/api/manage'
  // option {label:,value:}
  export default {
    name: 'JeecgSelect',
    props: {
      placeholder: {
        type: String,
        default: '',
        required: false
      },
      value: {
        type: String,
        required: false
      },
      tableId: {
        type: String,
        required: true
      },
      fields: {
        type: String,
        required: true
      },
      triggerChange: {
        type: Boolean,
        required: false,
        default: false
      }
    },
    data() {
      return {
        data: [],
        keyWord: this.value,
        tempWord: this.value
      }
    },
    watch: {
      value(val) {
        if (!val) {
          this.keyWord = ''
        } else {
          this.keyWord = this.value
        }
      }
    },
    methods: {
      handleSearch(text) {
        console.log(text)
        if (text !== '') {
          var that = this
          var params = {
            fields: that.fields,
            tableId: that.tableId,
            value: text
          }
          that.tempWord = text
          getAction('honour/queryValueByTableAndField', params).then((res) => {
            if (res.success) {
              that.data = res.result
              if (that.data.length > 0) {
                if (that.data.indexOf(text) !== -1) {
                  for (var i = 0; i < that.data.length; i++) {
                    if (that.data[i] === text) {
                      that.data.splice(i, 1) // 如果数据组存在该元素，则把该元素删除
                      break
                    }
                  }
                  that.data.unshift(text) // 再添加到第一个位置
                } else {
                  that.data.splice(0, 0, text)
                }
              }
            }
          })
        }
      },
      handleChange(value) {
        this.keyWord = value
        this.$emit('change', value)
      }
    },
    model: {
      prop: 'value',
      event: 'change'
    }

  }
</script>
