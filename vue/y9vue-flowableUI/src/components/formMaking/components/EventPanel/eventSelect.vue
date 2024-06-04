<template>
  <el-select v-model="value" style="width: 100%;">
    <el-option v-for="item in events" :key="item.key" :value="item.name" :label="item.name">
      <div style="display: flex;justify-content: space-between;align-items: center;">
        <span>{{item.name}}</span>
        <el-tag size="small" :type="item.type == 'rule' ? 'warning' : 'success'">{{item.type == 'rule' ? 'VIS' : 'JS'}}</el-tag>
      </div>
    </el-option>
  </el-select>
</template>

<script>
export default {
  props: ['modelValue'],
  emits: ['update:modelValue'],
  data () {
    return {
      value: this.modelValue,
      events: []
    }
  },
  inject: ['getEventsArray'],
  mounted () {
    this.events = this.getEventsArray()
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