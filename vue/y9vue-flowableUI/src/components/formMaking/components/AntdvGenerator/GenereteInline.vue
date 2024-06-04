<template>
  <div class="fm-inline-container"
    v-if="elementDisplay"
    :class="{
      [element.options && element.options.customClass]: element.options && element.options.customClass ? true : false
    }"
  >
    <template v-for="item in element.list" :key="item.key">

      <generate-form-item
        :models="dataModels"
        :rules="rules"
        :widget="item"
        :remote="remote"
        :blanks="blanks"
        :display="display"
        :sub-hide-fields="subHideFields"
        :sub-disabled-fields="subDisabledFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :config="config"
        :is-subform="isSubform"
        :row-index="rowIndex"
        :sub-name="subName"
        :is-dialog="isDialog"
        :dialog-name="dialogName"
        :style="{'margin-right': element.options.spaceSize+'px'}"
        :is-group="isGroup"
        :group="group"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-form-item>
    </template>
  </div>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'

export default {
  name: 'generate-inline',
  components: {
    GenerateFormItem
  },
  props: ['config', 'element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup'],
  data () {
    return {
      dataModels: this.model,
    }
  },
  computed: {
    elementDisplay () {
      if (this.formHideFields.includes(this.fieldNode ? this.fieldNode + '.' + this.element.model : this.element.model)
        || this.formHideFields.includes(this.group ? this.group + '.' + this.element.model : this.element.model)
      ) {
        return false
      } else {
        return true
      }
    }
  },
  inject: ['generateComponentInstance', 'deleteComponentInstance', 'formHideFields'],
  mounted () {
    this.generateComponentInstance && this.generateComponentInstance(this.fieldNode ? `${this.fieldNode}.${this.element.model}` : this.element.model, this)
  },
  beforeUnmount () {
    this.deleteComponentInstance && this.deleteComponentInstance(this.fieldNode ? `${this.fieldNode}.${this.element.model}` : this.element.model)
  },
  methods: {
  },
  watch: {
    model: {
      deep: true,
      handler (val) {
        this.dataModels = this.model
      }
    }
  }
}
</script>

<style lang="scss">
.fm-inline-container{
  > *{
    display: inline-block;
    vertical-align: top;
  }
}
</style>