<template>
  <div class="custom-component" v-if="!printRead" :style="{
    width,
    height
  }">
    <span>
      宽：<el-input v-model="dataModel.width" :disabled="disabled" style="width: 200px;"></el-input>
    </span>
    <span>
      高：<el-input v-model="dataModel.height" :disabled="disabled" style="width: 200px;"></el-input>
    </span>
    <div>{{tip}}</div>
  </div>
  <div v-else>{{JSON.stringify(dataModel)}}</div>
</template>

<script>
export default {
  name: 'custom-width-height',
  props: {
    modelValue: {
      type: Object,
      default: () => ({})
    },
    width: {
      type: String,
      default: ''
    },
    height: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    tip: {
      type: String,
      default: ''
    },
    printRead: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dataModel: this.modelValue
    }
  },
  emits: ['on-test'],
  mounted () {
    this.$emit('on-test', 'abc', '111')
  },
  watch: {
    modelValue (val) {
      this.dataModel = val
    },
    dataModel (val) {
      this.$emit('update:modelValue', val)
    }
  }
}
</script>

<style lang="scss">
.custom-component{
  // background: #eee;
  padding: 10px;

  span{
    +span{
      margin-left: 10px;
    }
  }
}

html.dark{
  .custom-component{
    // background: #424243;
  }
}
</style>