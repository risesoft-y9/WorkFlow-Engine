<template>
  <a-collapse v-model:activeKey="tabActive" 
    :accordion="element.options.accordion"
    :class="{
      [element.options.customClass]: element.options.customClass?true: false
    }"
    style="margin-bottom: 18px;"
    v-if="elementDisplay"
  >
    <a-collapse-panel
      :key="item.name" 
      :header="item.title"
      v-for="item in element.tabs"
    >
      <template v-for="tab in item.list">
        <generate-col-item
          v-if="tab.type == 'grid'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          v-else-if="tab.type == 'report'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          v-else-if="tab.type == 'inline'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          v-else-if="tab.type == 'tabs'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          v-else-if="tab.type == 'collapse'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          v-else-if="tab.type == 'card'"
          :key="tab.key"
          :model="dataModels"
          :rules="rules"
          :element="tab"
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
          :key="tab.key"
          :models="dataModels"
          :rules="rules"
          :widget="tab"
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
    </a-collapse-panel>
  </a-collapse>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import GenerateInline from './GenereteInline.vue'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'generate-collapse',
  components: {
    GenerateFormItem,
    GenerateInline,
    GenerateColItem : defineAsyncComponent(() => import('./GenerateColItem.vue')),
    GenerateReport: defineAsyncComponent(() => import('./GenerateReport.vue')),
    GenerateTabItem: defineAsyncComponent(() => import('./GenerateTabItem.vue')),
    GenerateCard: defineAsyncComponent(() => import('./GenerateCard.vue'))
  },
  props: ['config', 'element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'isGroup', 'group', 'fieldNode'],
  data () {
    return {
      dataModels: this.model,
      tabActive: this.element.tabs.length ? [this.element.tabs[0].name] : '',
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
    activateCollapse (title) {

      if (typeof title === 'string') {
        const currentCollapse = this.element.tabs.find(item => item.title == title)

        if (currentCollapse) {
          this.tabActive = currentCollapse.name
        }
      }

      if (typeof title === 'object' && Array.isArray(title)) {
        const collapses = title.map(item => {
          const current = this.element.tabs.find(iitem => iitem.title == item)

          if (current) {
            return current.name
          }
          return ''
        })

        this.tabActive = collapses
      }
    }
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
