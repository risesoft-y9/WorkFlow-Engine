<template>
  <a-row
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
      <a-col
        v-if="!hideCols.includes(index)"
        :xs="item.options ? getColXS(item.options) : item.span"
        :sm="item.options ? getColXS(item.options) : item.span"
        :md="item.options ? getColSM(item.options) : item.span"
        :lg="item.options ? getColMD(item.options) : item.span"
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
            :config="config"
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

          <generate-tab-item
            v-else-if="col.type == 'tabs'"
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
            :config="config"
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
          </generate-tab-item>

          <generate-collapse
            v-else-if="col.type == 'collapse'"
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
            :config="config"
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
          </generate-collapse>

          <generate-report
            v-else-if="col.type == 'report'"
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
            :config="config"
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
          </generate-report>

          <generate-card
            v-else-if="col.type == 'card'"
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
            :config="config"
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
          </generate-card>

          <generate-inline
            v-else-if="col.type == 'inline'"
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
            :config="config"
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
          </generate-inline>

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
            :config="config"
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
      </a-col>
    </template>
  </a-row>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import GenerateInline from './GenereteInline.vue'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'generate-col-item',
  components: {
    GenerateFormItem,
    GenerateInline,
    GenerateReport: defineAsyncComponent(() => import('./GenerateReport.vue')),
    GenerateCollapse: defineAsyncComponent(() => import('./GenerateCollapse.vue')),
    GenerateTabItem: defineAsyncComponent(() => import('./GenerateTabItem.vue')),
    GenerateCard: defineAsyncComponent(() => import('./GenerateCard.vue')),
  },
  props: ['config', 'element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup'],
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
