<template>
  <div
    :style="{
      width: element.options.width,
    }"
    :ref="`generate-group-${element.model}`"
  >
    <template v-for="item in element.list">
      <generate-col-item
        v-if="item.type == 'grid'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-col-item>

      <generate-tab-item
        v-else-if="item.type == 'tabs'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-tab-item>

      <generate-collapse
        v-else-if="item.type == 'collapse'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-collapse>

      <generate-report
        v-else-if="item.type == 'report'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-report>

      <generate-card
        v-else-if="item.type == 'card'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-card>

      <generate-inline
        v-else-if="item.type == 'inline'"
        :key="item.key"
        :model="groupModel"
        :rules="rules"
        :element="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
        :field-node="fieldNode"
      >
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-inline>

      <generate-form-item
        v-else
        :key="item.key"
        :models="groupModel"
        :rules="rules"
        :widget="item"
        :remote="remote"
        :blanks="blanks"
        :display="displayFields"
        :edit="edit"
        :remote-option="remoteOption"
        :platform="platform"
        :preview="preview"
        :container-key="containerKey"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :print-read="printRead"
        :is-group="true"
        :is-subform="false"
        :group="group ? group + '.' + element.model : element.model"
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
import { defineAsyncComponent } from 'vue'
import { updateClassName } from '../util/reuse-methods'
import _ from 'lodash'

export default {
  name: 'fm-group',
  components: {
    GenerateColItem: defineAsyncComponent(() => import('./GenerateColItem.vue')),
    GenerateFormItem: defineAsyncComponent(() => import('./GenerateFormItem.vue')),
    GenerateInline: defineAsyncComponent(() => import('./GenerateInline.vue')),
    GenerateReport: defineAsyncComponent(() => import('./GenerateReport.vue')),
    GenerateTabItem: defineAsyncComponent(() => import('./GenerateTabItem.vue')),
    GenerateCollapse: defineAsyncComponent(() => import('./GenerateCollapse.vue')),
    GenerateCard: defineAsyncComponent(() => import('./GenerateCard.vue'))
  },
  props: ['value', 'models', 'rules', 'element', 'remote', 'blanks', 'edit', 'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'group', 'fieldNode'],
  data () {
    return {
      displayFields: {},
      groupModel: this.value ?? {},
      groupDisabled: {}
    }
  },
  emits: ['update:value'],
  inject: ['generateComponentInstance', 'deleteComponentInstance', 'getFormComponent', 'onFormHide', 'onFormDisplay'],
  created () {
    this._generateModel(this.element.list)
  },
  mounted () {
    this.generateComponentInstance && this.generateComponentInstance(this.fieldNode, this.$refs[`generate-group-${this.element.model}`].$parent)
  },
  beforeUnmount () {
    this.deleteComponentInstance && this.deleteComponentInstance(this.fieldNode)
  },
  provide () {
    return {
      'setGroupData': this.setGroupData
    }
  },
  methods: {
    setGroupData (value, field) {
      this.groupModel[field] = value
    },
    _generateModel (genList) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type == 'grid') {
          this.displayFields[genList[i].model] = !genList[i].options.hidden

          genList[i].columns.forEach(item => {
            this._generateModel(item.list)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._generateModel(item.list)
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._generateModel(item.list)
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._generateModel(column.list)
            })
          })

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'inline') {
          this._generateModel(genList[i].list)

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else if (genList[i].type === 'card') {
          this._generateModel(genList[i].list)

          this.displayFields[genList[i].model] = !genList[i].options.hidden
        } else {
          if (genList[i].type == 'blank') {
            this.groupModel[genList[i].model] = genList[i].options.defaultType === 'String' ? '' : (genList[i].options.defaultType === 'Object' ? {} : [])
          } else {
            this.groupModel[genList[i].model] = _.cloneDeep(genList[i].options.defaultValue)
          }

          this.displayFields[genList[i].model] = !genList[i].options.hidden
          this.groupDisabled[genList[i].model] = !genList[i].options.disabled
        }
      }
    },
    hide (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormHide(`${this.fieldNode}.${field}`)
      })
    },
    display (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        this.onFormDisplay(`${this.fieldNode}.${field}`)
      })
    },
    disabled (fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      this._setDisabled(this.element.list, fields, disabled)
    },
    validate () {
      return new Promise((resolve, reject) => {
        let realFields = this._getAllRuleFields()
        this.getFormComponent().validateField(realFields, (valid, error) => {
          if (valid) {
            resolve()
          } else {
            reject(error)
          }
        })
      })
    },
    setOptions (fields, options) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      this._setOptions(this.element.list, fields, options)
    },
    setData (value) {
      return new Promise((resolve) => {
        this.$nextTick(() => {
          Object.keys(value).forEach(item => {
            this.groupModel[item] = value[item]
          })
          resolve()
        })
      })
    },
    getValues () {
      return this.groupModel
    },
    getValue (fieldName) {
      return this.groupModel[fieldName]
    },
    addClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.element.list, item.split('.'), className, 'add')
      })
    },
    removeClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.element.list, item.split('.'), className, 'remove')
      })
    },
    _setDisabled (genList, fields, disabled) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._setDisabled(item.list, fields, disabled)
          })
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._setDisabled(column.list, fields, disabled)
            })
          })
        } else if (genList[i].type === 'inline') {
          this._setDisabled(genList[i].list, fields, disabled)
        } else if (genList[i].type === 'card') {
          this._setDisabled(genList[i].list, fields, disabled)
        } else {
          if (fields.indexOf(genList[i].model) >= 0) {

            genList[i].options.disabled = disabled
          }
        }
      }
    },
    _setOptions (genList, fields, opts) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'tabs') {
          genList[i].tabs.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'collapse') {
          genList[i].tabs.forEach(item => {
            this._setOptions(item.list, fields, opts)
          })
        } else if (genList[i].type === 'report') {
          genList[i].rows.forEach(row => {
            row.columns.forEach(column => {
              this._setOptions(column.list, fields, opts)
            })
          })
        } else if (genList[i].type === 'inline') {
          this._setOptions(genList[i].list, fields, opts)
        } else if (genList[i].type === 'card') {
          this._setOptions(genList[i].list, fields, opts)
        }
        if (fields.indexOf(genList[i].model) >= 0) {

          Object.keys(opts).forEach(key => {
            genList[i].options[key] = opts[key]
          })
        }
      }
    },
    _getAllRuleFields () {
      let realFields = []

      Object.keys(this.groupModel).forEach((v) => {
        if (Array.isArray(this.groupModel[v])) {
          const currentArray = this.groupModel[v]

          currentArray.forEach((o, i) => {
            Object.keys(o).forEach((key) => {
              realFields.push(`${this.fieldNode}.${v}.${i}.${key}`)
            })
          })
        } else {
          realFields.push(`${this.fieldNode}.${v}`)
        }
      })

      return realFields
    }
  },
  watch: {
    groupModel: {
      deep: true,
      handler (val) {
        this.$emit('update:value', val)
      }
    },
    value: {
      deep: true,
      handler (val) {
        this.groupModel = val
      }
    }
  }
}
</script>
