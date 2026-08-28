<template>
  <el-row
    :class="{
      [element.options && element.options.customClass]: element.options && element.options.customClass?true: false
    }"
    :type="element.options.flex ? 'flex' : ''"
    :gutter="element.options.gutter || 0"
    :justify="element.options.justify"
    :align="element.options.align"
    v-if="elementDisplay"
  >

    <template v-for="(item, index) in element.columns" :key="index">
      <el-col
        v-if="!hideCols.includes(index)"
        :span="item.options ? getColMD(item.options) : item.span"
        :xs="item.options ? getColXS(item.options) : item.span"
        :sm="item.options ? getColSM(item.options) : item.span"
        :md="item.options ? getColMD(item.options) : item.span"
        :lg="item.options ? getColMD(item.options) : item.span"
        :xl="item.options ? getColMD(item.options) : item.span"
        :offset="item.options ? item.options.offset : 0"
        :push="item.options ? item.options.push : 0"
        :pull="item.options ? item.options.pull : 0"
        :class="{
          [item.options && item.options.customClass]: item.options && item.options.customClass?true: false
        }"
      >
        <template v-for="col in item.list">
          <generate-col-item
            v-if="col.type == 'grid'"
            :key="col.key"
            :model="dataModels"
            :rules="rules"
            :element="col"
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
            :is-subform="isSubform"
            :row-index="rowIndex"
            :sub-name="subName"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="isGroup"
            :group="group"
            :field-node="fieldNode"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-col-item>

          <generate-form-item
            v-else
            :key="col.key"
            :models="dataModels"
            :rules="rules"
            :widget="col"
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
            :is-subform="isSubform"
            :row-index="rowIndex"
            :sub-name="subName"
            :is-dialog="isDialog"
            :dialog-name="dialogName"
            :is-group="isGroup"
            :group="group"
            :field-node="fieldNode"
          >
            <template v-slot:[blank.name]="scope" v-for="blank in blanks">
              <slot :name="blank.name" :model="scope.model"></slot>
            </template>
          </generate-form-item>
        </template>
      </el-col>
    </template>
  </el-row>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'generate-col-item',
  components: {
    GenerateFormItem:defineAsyncComponent(() => import('./GenerateFormItem.vue')),
  },
  props: ['element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup'],
  data () {
    return {
      dataModels: this.model,
      hideCols: []
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
    getColXS (options) {
      if (this.preview) {
        if (this.platform == 'pc') {
          return options.md
        }
        if (this.platform == 'pad') {
          return options.sm
        }
        if (this.platform == 'mobile') {
          return options.xs
        }
      } else {
        return options.xs
      }
    },
    getColSM (options) {
      if (this.preview) {
        if (this.platform == 'pc') {
          return options.md
        }
        if (this.platform == 'pad') {
          return options.sm
        }
        if (this.platform == 'mobile') {
          return options.xs
        }
      } else {
        return options.sm
      }
    },
    getColMD (options) {
      if (this.preview) {
        if (this.platform == 'pc') {
          return options.md
        }
        if (this.platform == 'pad') {
          return options.sm
        }
        if (this.platform == 'mobile') {
          return options.xs
        }
      } else {
        return options.md
      }
    },
    hideCol (index) {
      !this.hideCols.includes(index) && this.hideCols.push(index)
    },
    displayCol (index) {
      if (this.hideCols.includes(index)) {
        this.hideCols.splice(this.hideCols.indexOf(index), 1)
      }
    }
  },
  watch: {
    model: {
      deep: true,
      handler (val) {
        this.dataModels = val
      }
    }
  }
}
</script>
