<template>
  <el-select v-model="value" style="width: 100%;">
    <el-option v-for="item in dataSource" :key="item.key" :value="item" :label="item.label"></el-option>
  </el-select>
</template>

<script>
export default {
  props: ['modelValue'],
  emits: ['update:modelValue'],
  data () {
    return {
      value: this.modelValue,
      dataSource: []
    }
  },
  inject: ['getDataSourceArray'],
  mounted () {
    this.dataSource = this.getDataSourceArray()
  },
  watch: {
    modelValue (val) {
      this.value = val
    },
    value (val) {
      this.$emit('update:modelValue', val)
    }
  }
}
</script>