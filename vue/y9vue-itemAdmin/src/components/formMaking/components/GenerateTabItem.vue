<template>
  <el-tabs v-model="tabActive" 
    :type="element.options.type"
    :tab-position="element.options.tabPosition"
    :class="{
      [element.options.customClass]: element.options.customClass?true: false
    }"
    style="margin-bottom: 18px;"
    v-if="elementDisplay"
  >
    <el-tab-pane 
      :key="item.name" 
      :label="item.label" 
      :name="item.name" 
      v-for="item in element.tabs"
    >
      <template v-for="tab in item.list" :key="tab.key">
        <generate-col-item
          v-if="tab.type == 'grid'"
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
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import GenerateInline from './GenerateInline.vue'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'generate-tab-item',
  components: {
    GenerateFormItem:defineAsyncComponent(() => import('./GenerateFormItem.vue')),
    GenerateInline,
    GenerateColItem : defineAsyncComponent(() => import('./GenerateColItem.vue')),
    GenerateReport: defineAsyncComponent(() => import('./GenerateReport.vue')),
    GenerateCollapse: defineAsyncComponent(() => import('./GenerateCollapse.vue')),
    GenerateCard: defineAsyncComponent(() => import('./GenerateCard.vue'))
  },
  props: ['element', 'model', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup'],
  data () {
    return {
      dataModels: this.model,
      tabActive: this.element.tabs.length ? this.element.tabs[0].name : '',
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
    activateTab (label) {
      const currentTab = this.element.tabs.find(item => item.label == label)

      if (currentTab) {
        this.tabActive = currentTab.name
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
