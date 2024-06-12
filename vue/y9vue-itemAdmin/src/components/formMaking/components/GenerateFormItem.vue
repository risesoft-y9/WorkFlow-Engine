<template>
  <el-form-item :label="widget.name" :prop="widget.model">
    <template v-if="widget.type == 'input'" >
      <el-input 
        v-if="widget.options.dataType == 'number' || widget.options.dataType == 'integer' || widget.options.dataType == 'float'"
        :type="widget.options.dataType"
        v-model.number="dataModel"
        :placeholder="widget.options.placeholder"
        :style="{width: widget.options.width}"
        :disabled="widget.options.disabled"
        :readonly="widget.options.readonly"
      ></el-input>
      <el-input 
        v-else
        :type="widget.options.dataType"
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :readonly="widget.options.readonly"
        :placeholder="widget.options.placeholder"
        :style="{width: widget.options.width}"
      ></el-input>
    </template>

    <template v-if="widget.type == 'textarea'">
      <el-input type="textarea" :rows="5"
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :readonly="widget.options.readonly"
        :placeholder="widget.options.placeholder"
        :style="{width: widget.options.width}"
      ></el-input>
    </template>

    <template v-if="widget.type == 'number'">
      <el-input-number 
        v-model="dataModel" 
        :style="{width: widget.options.width}"
        :step="widget.options.step"
        controls-position="right"
        :disabled="widget.options.disabled"
        :readonly="widget.options.readonly"
      ></el-input-number>
    </template>

    <template v-if="widget.type == 'radio'">
      <el-radio-group v-model="dataModel"
        :style="{width: widget.options.width}"
        :disabled="widget.options.disabled"
      >
        <el-radio
          :style="{display: widget.options.inline ? 'inline-block' : 'block'}"
          :label="item.value" v-for="(item, index) in (widget.options.remote ? widget.options.remoteOptions : widget.options.options)" :key="index"
        >
          <template v-if="widget.options.remote">{{item.label}}</template>
          <template v-else>{{widget.options.showLabel ? item.label : item.value}}</template>
        </el-radio>
      </el-radio-group>
    </template>

    <template v-if="widget.type == 'checkbox'">
      <el-checkbox-group v-model="dataModel"
        :style="{width: widget.options.width}"
        :disabled="widget.options.disabled"
      >
        <el-checkbox
          
          :style="{display: widget.options.inline ? 'inline-block' : 'block'}"
          :label="item.value" v-for="(item, index) in (widget.options.remote ? widget.options.remoteOptions : widget.options.options)" :key="index"
        >
          <template v-if="widget.options.remote">{{item.label}}</template>
          <template v-else>{{widget.options.showLabel ? item.label : item.value}}</template>
        </el-checkbox>
      </el-checkbox-group>
    </template>

    <template v-if="widget.type == 'time'">
      <el-time-picker 
        v-model="dataModel"
        :is-range="widget.options.isRange"
        :placeholder="widget.options.placeholder"
        :start-placeholder="widget.options.startPlaceholder"
        :end-placeholder="widget.options.endPlaceholder"
        :readonly="widget.options.readonly"
        :disabled="widget.options.disabled"
        :editable="widget.options.editable"
        :clearable="widget.options.clearable"
        :arrowControl="widget.options.arrowControl"
        :value-format="widget.options.format"
        :style="{width: widget.options.width}"
      >
      </el-time-picker>
    </template>

    <template v-if="widget.type=='date'">
      <el-date-picker
        v-model="dataModel"
        :type="widget.options.type"
        :placeholder="widget.options.placeholder"
        :start-placeholder="widget.options.startPlaceholder"
        :end-placeholder="widget.options.endPlaceholder"
        :readonly="widget.options.readonly"
        :disabled="widget.options.disabled"
        :editable="widget.options.editable"
        :clearable="widget.options.clearable"
        :value-format="widget.options.timestamp ? 'timestamp' : widget.options.format"
        :format="widget.options.format"
        :style="{width: widget.options.width}"
      >
      </el-date-picker>
    </template>

    <template v-if="widget.type =='rate'">
      <el-rate v-model="dataModel"
        :max="widget.options.max"
        :disabled="widget.options.disabled"
        :allow-half="widget.options.allowHalf"
      ></el-rate>
    </template>

    <template v-if="widget.type == 'color'">
      <el-color-picker 
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :show-alpha="widget.options.showAlpha"
      ></el-color-picker>
    </template>

    <template v-if="widget.type == 'select'">
      <el-select
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :multiple="widget.options.multiple"
        :clearable="widget.options.clearable"
        :placeholder="widget.options.placeholder"
        :style="{width: widget.options.width}"
        :filterable="widget.options.filterable"
      >
        <el-option v-for="item in (widget.options.remote ? widget.options.remoteOptions : widget.options.options)" :key="item.value" :value="item.value" :label="widget.options.showLabel || widget.options.remote?item.label:item.value"></el-option>
      </el-select>
    </template>

    <template v-if="widget.type=='switch'">
      <el-switch
        v-model="dataModel"
        :disabled="widget.options.disabled"
      >
      </el-switch>
    </template>

    <template v-if="widget.type=='slider'">
      <el-slider 
        v-model="dataModel"
        :min="widget.options.min"
        :max="widget.options.max"
        :disabled="widget.options.disabled"
        :step="widget.options.step"
        :show-input="widget.options.showInput"
        :range="widget.options.range"
        :style="{width: widget.options.width}"
      ></el-slider>
    </template>

    <template v-if="widget.type=='imgupload'">
      <fm-upload
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :style="{'width': widget.options.width}"
        :width="widget.options.size.width"
        :height="widget.options.size.height"
        :token="widget.options.token"
        :domain="widget.options.domain"
        :multiple="widget.options.multiple"
        :length="widget.options.length"
        :is-qiniu="widget.options.isQiniu"
        :is-delete="widget.options.isDelete"
        :min="widget.options.min"
        :is-edit="widget.options.isEdit"
        :action="widget.options.action"
      >
      </fm-upload>
    </template>

    <template v-if="widget.type == 'editor'">
      <template v-if="printRead">
        <div v-html="dataModel" class="ql-editor"></div>
      </template>
      <template v-else>
        <Editor
          v-model="dataModel"
          :custom-style="{width: isTable ? '100%' : widget.options.width, cursor: (widget.options.disabled) ? 'no-drop' : '', backgroundColor: (widget.options.disabled) ? '#F5F7FA' : ''}"
          :toolbar="widget.options.customToolbar"
          :disabled="widget.options.disabled"
          :ref="'fm-'+widget.model"
        ></Editor>
      </template>
    </template>

    <template v-if="widget.type == 'cascader'">
      <el-cascader
        v-model="dataModel"
        :disabled="widget.options.disabled"
        :clearable="widget.options.clearable"
        :placeholder="widget.options.placeholder"
        :style="{width: widget.options.width}"
        :options="widget.options.remoteOptions"
      >

      </el-cascader>
    </template>
  </el-form-item>
</template>

<script>
import { EventBus } from '../util/event-bus.js'
import Editor from './Editor/index.vue'
export default {
  components: {
    Editor
  },
  props: ['widget', 'models', 'rules', 'remote', 'blanks', 'display', 'edit', 
    'remoteOption', 'platform', 'preview', 'containerKey', 'dataSourceValue', 'eventFunction', 
    'printRead', 'isSubform', 'rowIndex', 'subName', 'subHideFields', 'subDisabledFields', 
    'isDialog', 'dialogName', 'group', 'fieldNode', 'isGroup', 'isTable', 'isMobile', 'tableName'],
  data () {
    return {
      
      dataModel: this.models[this.widget.model],
      dataModels: this.models
    }
  },
  computed: {
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
      handler (val) {
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
        if (this.widget.events && this.widget.events.onChange && this.$refs['generateElementItem']) {
          let funcKey = this.widget.events.onChange
          
          if (this.isTable) {
            this.eventFunction[funcKey]({
              value: val,
              field: this.widget.model,
              rowIndex: this.rowIndex,
              table: this.tableName,
              dialog: this.isDialog ? this.dialogName : null,
              currentRef: this.$refs['generateElementItem']?.$refs[`fm-${this.widget.model}`],
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
              currentRef: this.$refs['generateElementItem']?.$refs[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          } else if (this.isDialog) {
            this.eventFunction[funcKey]({
              value: val,
              field: this.widget.model,
              dialog: this.dialogName,
              currentRef: this.$refs['generateElementItem']?.$refs[`fm-${this.widget.model}`],
              group: this.group,
              fieldNode: this.fieldNode ? this.fieldNode + '.' + this.widget.model : this.widget.model
            })
          } else {
            this.eventFunction[funcKey]({
              value: val, 
              field: this.widget.model,
              currentRef: this.$refs['generateElementItem']?.$refs[`fm-${this.widget.model}`],
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
