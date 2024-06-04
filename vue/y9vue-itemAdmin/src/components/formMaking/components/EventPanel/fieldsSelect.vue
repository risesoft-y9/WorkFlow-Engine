<template>
  <el-tree-select
    v-model="value"
    :data="formFields"
    check-strictly
    :render-after-expand="false"
    :default-expand-all="defaultExpand"
    :expand-on-click-node="false"
    node-key="id"
    :multiple="multiple"
    :clearable="multiple"
    style="width: 100%;"
    class="fm-custom-fields-select"
    :props="{disabled: disabledProp, label: 'label'}"
    @current-change="handleChange"
  >
    <template #default="{ node }">
      <div class="custom-tree-node" :class="{'is-bind': node.data?.dataBind}">
        <span class="custom-tree-node-type">{{node.data.type ? '<' + this.$t('fm.components.fields.' + node.data.type) + '>' : node.data.label}}</span>
        <span class="custom-tree-node-model" v-if="node.data.model"> {{node.data.model}}</span>
      </div>
    </template>
  </el-tree-select>
</template>

<script>
import _ from 'lodash'

export default {
  props: ['modelValue', 'multiple', 'action', 'defaultExpand'],
  emits: ['update:modelValue', 'on-data-change'],
  data () {
    return {
      value: this.modelValue,
      formFields: []
    }
  },
  computed: {
    disabledProp () {
      switch (this.action) {
        case 'openDialog':
        case 'closeDialog':
          return 'dialogDisabled'
        case 'setData':
        case 'validate':
          return 'setdataDisabled'
        case 'refreshFieldDataSource':
        case 'getFieldDataSource':
          return 'remoteOptionDisabled'
        case 'hide':
        case 'display':
          return 'hideDisabled'
        default:
          return 'disabled'
      }
    }
  },
  inject: ['getFormFields'],
  mounted () {
    this.formFields = this.getFormFields()
  },
  methods: {
    handleChange (data) {
      if (this.action == 'refreshFieldDataSource' && !data.remoteOptionDisabled) {
        this.$emit('on-data-change', _.cloneDeep(data))
      }
    }
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

<style lang="scss">
.fm-custom-fields-select{
  .el-tag{
    height: 100%;
  }
  .el-select__tags-text{
    white-space: normal;
  }
}
</style>