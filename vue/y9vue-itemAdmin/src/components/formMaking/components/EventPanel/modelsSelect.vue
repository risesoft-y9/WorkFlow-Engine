<template>
  <el-tree-select
    v-model="value"
    :data="formModels"
    check-strictly
    :render-after-expand="false"
    default-expand-all
    :expand-on-click-node="false"
    node-key="id"
    :props="{label: 'name'}"
  >
    <template #default="{ node, data }">
      <span class="models-tree-node">
        <span>{{ node.label }} </span> {<span style="opacity: 0.6;">{{node.key}}</span>}
      </span>
    </template>
  </el-tree-select>
</template>

<script>
export default {
  props: ['modelValue'],
  emits: ['update:modelValue'],
  data () {
    return {
      value: this.modelValue,
      formModels: []
    }
  },
  inject: ['getFormModels'],
  mounted () {
    this.formModels = this.getFormModels()
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