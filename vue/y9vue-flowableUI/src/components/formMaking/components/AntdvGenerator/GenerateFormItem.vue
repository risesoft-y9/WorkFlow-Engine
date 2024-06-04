<template>
  <div class="fm-form-item" :data-id="widget.model" v-if="widget.key">
    <a-form-item 
      v-if="widget.type != 'divider' && widget.type != 'alert' &&  elementDisplay" 
      :name="fieldNode ? [...fieldNode.split('.'), widget.model] : widget.model"
      :rules="rules[ruleProp]"
      :class="{
        [widget.options && widget.options.customClass]: widget.options && widget.options.customClass?true: false,
        'no-label-form-item': widget.options.isLabelWidth && widget.options.labelWidth == 0,
        'no-label-left': widget.name === '',
        'fm-label-wrap': widget.options.labelWrap
      }"
      :label="(widget.options.hideLabel || (isTable && !isMobile)) ? '' : widget.name"
      :key="widget.key"
      :required="widget.options.required"
      ref="generateFormItem"
      :colon="config?.labelSuffix ? true : false"
      validateFirst
      
    >
      <div v-if="widget.options.tip" class="fm-item-tooltip" v-html="widget.options.tip.replace(/\n/g, '<br/>')"></div>
      <generate-element-item 
        :blanks="blanks" 
        :is-table="isTable" 
        :table-name="tableName"
        :widget="widget" 
        :models="dataModels"
        :remote="remote"
        :edit="edit"
        :remote-option="remoteOption"
        :key="widget.key"
        :rules="rules"
        v-model="dataModel"
        :platform="platform"
        :preview="preview"
        :data-source-value="dataSourceValue"
        :event-function="eventFunction"
        :container-key="containerKey"
        :print-read="printRead"
        :config="config"
        :is-subform="isSubform"
        :row-index="rowIndex"
        :sub-name="subName"
        :is-dialog="isDialog"
        :dialog-name="dialogName"
        ref="generateElementItem"
        :is-group="isGroup"
        :group="group"
        :field-node="fieldNode ? fieldNode + '.' + widget.model : widget.model"
      >
        
        <template v-slot:[blank.name]="scope" v-for="blank in blanks">
          <slot :name="blank.name" :model="scope.model"></slot>
        </template>
      </generate-element-item>
    </a-form-item>

    <a-form-item v-if="widget.type == 'divider' && elementDisplay" :wrapperCol="{span: 0, offset: 0}">
      <a-divider 
        :orientation="widget.options.contentPosition"
        v-bind="widget.options.customProps"
      >
        {{widget.name}}
      </a-divider>
    </a-form-item>

    <a-form-item v-if="widget.type == 'alert' && elementDisplay"  :wrapperCol="{span: 0, offset: 0}">
      <a-alert 
        :message="widget.options.title"
        :type="widget.options.type"
        :description="widget.options.description"
        :closable="widget.options.closable"
        :center="widget.options.center"
        :show-icon="widget.options.showIcon"
        :effect="widget.options.effect"
        :style="{width: widget.options.width}"
        v-bind="widget.options.customProps"
        @close="display[widget.model] = false"
      ></a-alert>
    </a-form-item>
  </div>
  
</template>

<script>
import GenerateElementItem from './GenerateElementItem.vue'
import { EventBus } from '../../util/event-bus.js'

export default {
  components: {
    GenerateElementItem
  },
  props: ['config', 'widget', 'models', 'rules', 'remote', 'blanks', 'display', 'edit', 'remoteOption', 'platform', 
    'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 'printRead', 'isSubform', 'rowIndex', 'subName', 
    'subHideFields', 'subDisabledFields', 'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup',
    'isTable', 'isMobile', 'tableName'  
  ],
  data () {
    return {
      
      dataModel: this.models[this.widget.model],
      dataModels: this.models
    }
  },
  computed: {
    labelWidth () {
      return this.widget.options.hideLabel ? '0px' : (this.widget.options.isLabelWidth ? this.widget.options.labelWidth + 'px' : this.config.labelWidth + 'px')
    },
    ruleProp () {
      let currentProp = this.widget.model

      if (this.group) {
        currentProp = this.group + '.' + currentProp
      }

      return currentProp
    },
    elementDisplay () {
      if (this.formHideFields.includes(this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model)
        || this.formHideFields.includes(this.group ? this.group + '.' + this.widget.model : this.widget.model)
      ) {
        return false
      } else {
        return true
      }
    }
  },
  inject: {
    setSubformData: {
      default: () => {}
    },
    setDialogData: {
      default: () => {}
    },
    setGroupData: {
      default: () => {}
    },
    setTableData: {
      default: () => {}
    },
    getFormComponent: {
      default: () => { return null }
    },
    onChange: {
      default: () => {}
    },
    formHideFields: {
      default: []
    }
  },
  methods: {
  },
  watch: {
    dataModel: {
      deep: true,
      handler (val, oldValue) {
        if (this.isTable) {
          this.setTableData(val, this.rowIndex, this.widget.model)
        } else if (this.isSubform) {
          this.setSubformData(val, this.rowIndex, this.widget.model)
        } else if (this.isDialog) {
          this.setDialogData(val, this.widget.model)
        } else if (this.isGroup) {
          this.setGroupData(val, this.widget.model)
        } else {
          this.onChange(val, this.widget.model)
        }

        // 执行 onChange 方法
        if (this.widget.events && this.widget.events.onChange && this.$refs?.['generateElementItem']) {
          let funcKey = this.widget.events.onChange
          
          if (this.isTable) {
            this.eventFunction[funcKey]({
              value: val,
              field: this.widget.model,
              rowIndex: this.rowIndex,
              table: this.tableName,
              dialog: this.isDialog ? this.dialogName : null,
              currentRef: this.$refs?.['generateElementItem']?.$refs?.[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          } else if (this.isSubform) {
            this.eventFunction[funcKey]({
              value: val,
              field: this.widget.model,
              rowIndex: this.rowIndex,
              subform: this.subName,
              dialog: this.isDialog ? this.dialogName : null,
              currentRef: this.$refs?.['generateElementItem']?.$refs?.[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          } else if (this.isDialog) {
            this.eventFunction[funcKey]({
              value: val,
              field: this.widget.model,
              dialog: this.dialogName,
              currentRef: this.$refs?.['generateElementItem']?.$refs?.[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          } else {
            this.eventFunction[funcKey]({
              value: val, 
              field: this.widget.model,
              currentRef: this.$refs?.['generateElementItem']?.$refs?.[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          }
        }
      }
    },
    models: {
      deep: true,
      handler (val) {
        this.dataModels = val
        this.dataModel = val[this.widget.model]
      }
    }
  }
}
</script>

<style lang="scss" >
.fm-form, .fm-generate-ant-dialog{
  .fm-form-item{

    .ant-form-item{
      .ant-col{
        max-width: none;
      }

      .ant-form-item-label{
        flex: 0 0 auto;
      }

      .ant-form-item-control{
        flex: 1;
      }
    }

    .ant-form-item{
      .ant-form-item-label{
        width: v-bind(labelWidth);
      }
    }

    .no-label-left{
      .ant-form-item-control{
        margin-left: v-bind(labelWidth);
      }
    }
  }
}
</style>