<template>
<div class="fm-report-table__wrapper">
  <table class="fm-report-table__table" :style="{
    'border-top-width': element.options.borderWidth + 'px',
    'border-top-color': element.options.borderColor,
    'border-left-width': element.options.borderWidth + 'px',
    'border-left-color': element.options.borderColor,
    'width': element.options.width || 'auto'
  }"
    v-if="elementDisplay"
    :class="{
      [element.options && element.options.customClass]: element.options && element.options.customClass ? true : false
    }"
  >
    <tr>
      <template v-for="(th, i) in element.headerRow" :key="i">
        <th :style="{
            'width': th.options.width,
            'height': 0,
            'padding': 0,
          }"
        ></th>
      </template>
    </tr>
    <tr :key="rIndex" v-for="(row, rIndex) in element.rows">
      <template v-for="(column, i) in row.columns" :key="rIndex + '-' + i">
        <td :style="{
              'border-right-width': element.options.borderWidth + 'px',
              'border-right-color': element.options.borderColor,
              'border-bottom-width': element.options.borderWidth + 'px',
              'border-bottom-color': element.options.borderColor,
              'width': column.options.width,
              'height': column.options.height || '40px',
              'min-height': '40px'
            }"
            
            v-if="!column.options.invisible"
            :colspan="column.options.colspan"
            :rowspan="column.options.rowspan"
            class="fm-report-table__td"
            valign="top"
            :class="{
              [column.options.customClass]: column.options.customClass?true: false
            }"
          >

          <template v-for="columnElement in column.list">

            <generate-col-item
              v-if="columnElement.type == 'grid'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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

            <generate-report
              v-else-if="columnElement.type == 'report'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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

            <generate-inline
              v-else-if="columnElement.type == 'inline'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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

            <generate-tab-item
              v-else-if="columnElement.type == 'tabs'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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
              v-else-if="columnElement.type == 'collapse'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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

            <generate-card
              v-else-if="columnElement.type == 'card'"
              :key="columnElement.key"
              :model="dataModels"
              :rules="rules"
              :element="columnElement"
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

            <generate-form-item
              v-else
              :key="columnElement.key"
              :models="dataModels"
              :rules="rules"
              :widget="columnElement"
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
          
        </td>
      </template>
      
    </tr>
  </table>
</div>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import GenerateInline from './GenereteInline.vue'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'generate-report',
  components: {
    GenerateFormItem,
    GenerateInline,
    GenerateColItem : defineAsyncComponent(() => import('./GenerateColItem.vue')),
    GenerateTabItem: defineAsyncComponent(() => import('./GenerateTabItem.vue')),
    GenerateCollapse: defineAsyncComponent(() => import('./GenerateCollapse.vue')),
    GenerateCard: defineAsyncComponent(() => import('./GenerateCard.vue'))
  },
  props: ['config', 'element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'isGroup', 'group', 'fieldNode'],
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