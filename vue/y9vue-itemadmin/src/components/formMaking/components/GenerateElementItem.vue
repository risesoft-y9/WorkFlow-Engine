<template>
  <div :class="{'print-read-label': printRead}" style="width: 100%;">
    <template v-if="widget.type == 'blank'">
      <div :style="{width: isTable ? '100%' : widget.options.width}">
        <slot :name="widget.model" :model="dataModels"></slot>
      </div>
    </template>

    <template v-if="widget.type == 'custom'">  
      <!-- 自定义意见框组件 -->
      <div v-if="widget.el == 'custom-opinion'" :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :minHeight="widget.options.minHeight"
          :opinionName="widget.name"
          :opinionframemark="widget.model.split('@')[1]"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
           :width="widget.options.width"
           v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
           v-on="dynamicEvents"
           v-on:opinion_click="opinionClick($event)"
        ></component>
      </div>
      <!-- 自定义意见框组件 -->
      <!-- 自定义附件列表组件 -->
      <div v-else-if="widget.el == 'custom-file'" :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
          :width="widget.options.width"
          v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
          v-on="dynamicEvents"
        ></component>
      </div>
      <!-- 自定义附件列表组件 -->
      <!-- 自定义人员树组件 -->
      <div v-else-if="widget.el == 'custom-personTree'" :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
          :tableField="widget.options.tableField"
          :width="widget.options.width"
          v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
          v-on="dynamicEvents"
          v-on:update_personName="updatePersonName($event)"
        ></component>
      </div>
      <!-- 自定义人员树组件 -->
      <!-- 自定义编号按钮组件 -->
      <div v-else-if="widget.el == 'custom-numberButton'" :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :numberCustom="widget.model.split('@')[1]"
          :numberName="widget.name"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
          :tableField="widget.options.tableField"
          :width="widget.options.width"
          v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
          v-on="dynamicEvents"
          v-on:update_number="updateNumber($event)"
        ></component>
      </div>
      <!-- 自定义编号按钮组件 -->
      <!-- 图片显示组件 -->
      <div v-else-if="widget.el == 'custom-picture'" :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
          :tableField="widget.options.tableField"
          :width="widget.options.width"
          v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
          v-on="dynamicEvents"
        ></component>
      </div>
      <!-- 图片显示组件 -->
      <div v-else :style="{width: isTable ? '100%' : widget.options.width}">
        <component
          :is="widget.el"
          :ref="'fm-'+widget.model"
          v-model="dataModel"
          :clearable="widget.options.clearable"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :height="widget.options.height"
          :placeholder="widget.options.placeholder"
          :print-read="printRead"
          :readonly="widget.options.readonly"
          :width="widget.options.width"
          v-bind="{...widget.options.customProps, ...widget.options.extendProps}"
          v-on="dynamicEvents"
        ></component>
      </div>
    </template>

    <template v-if="widget.type == 'input'" >
      <template v-if="printRead">
        <span>{{dataModel}}</span>
      </template>
      <template v-else>
        <el-input 
          v-if="widget.options.dataTypeCheck && (widget.options.dataType == 'number' || widget.options.dataType == 'integer' || widget.options.dataType == 'float')"
          type="number"
          v-model.number="dataModel"
          :disabled="elementDisabled"
          :clearable="widget.options.clearable"
          :placeholder="widget.options.placeholder"
          :readonly="widget.options.readonly"
          :show-password="widget.options.showPassword"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        ></el-input>
        <el-input 
          v-else
          :type="widget.options.dataTypeCheck ? widget.options.dataType : 'text'"
          v-model="dataModel"
          :disabled="elementDisabled"
          :placeholder="widget.options.placeholder"
          :readonly="widget.options.readonly"
          :show-password="widget.options.showPassword"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :clearable="widget.options.clearable"
          :ref="'fm-'+widget.model"
          :maxlength="widget.options.maxlength"
          :show-word-limit="widget.options.showWordLimit"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        ></el-input>
      </template>
      
    </template>

    <template v-if="widget.type == 'textarea'">
      <template v-if="printRead">
        <pre>{{dataModel}}</pre>
      </template>
      <template v-else>
        <el-input type="textarea" :rows="widget.options.rows"
          v-model="dataModel"
          :disabled="elementDisabled"
          :placeholder="widget.options.placeholder"
          :readonly="widget.options.readonly"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :clearable="widget.options.clearable"
          :ref="'fm-'+widget.model"
          :maxlength="widget.options.maxlength"
          :show-word-limit="widget.options.showWordLimit"
          :autosize="widget.options.autosize"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        ></el-input>
      </template>
    </template>

    <template v-if="widget.type == 'number'">
      <template v-if="printRead">
        <span>{{typeof dataModel == 'number' ? dataModel.toFixed(widget.options.precision) : dataModel}}</span>
      </template>
      <template v-else>
        <el-input-number 
          v-model="dataModel" 
          :style="{width: isTable ? '100%' : widget.options.width}"
          :step="widget.options.step"
          :disabled="elementDisabled"
          :readonly="widget.options.readonly"
          :min="widget.options.min"
          :max="widget.options.max"
          :controls-position="widget.options.controlsPosition"
          :precision="widget.options.precision"
          :controls="widget.options.controls"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        ></el-input-number>
      </template>
    </template>

    <template v-if="widget.type == 'radio'">
      <template v-if="printRead">
        <template v-if="widget.options.remote">
          {{
            remoteOptions.find(item => item.value == dataModel) 
            && remoteOptions.find(item => item.value == dataModel).label
          }}
        </template>
        <template v-else>
          {{
            widget.options.showLabel ? 
            (widget.options.options.find(item => item.value == dataModel) && widget.options.options.find(item => item.value == dataModel).label) :
            dataModel
          }}
        </template>
      </template>
      <template v-else>
        <el-radio-group v-model="dataModel"
          :style="{width: isTable ? '100%' : widget.options.width, display: 'block'}"
          :disabled="elementDisabled"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
        >
          <el-radio
            :style="{display: widget.options.inline ? 'inline-block' : 'block'}"
            :value="item.value" :label="item.value" v-for="(item, index) in (widget.options.remote ? remoteOptions : widget.options.options)" :key="index"
          >
            <template v-if="widget.options.remote">{{item.label}}</template>
            <template v-else>{{widget.options.showLabel ? item.label : item.value}}</template>
          </el-radio>
        </el-radio-group>
      </template>
    </template>

    <template v-if="widget.type == 'checkbox'">
      <template v-if="printRead">
        <template v-if="widget.options.remote">
          {{
            dataModel.map(dm => 
              remoteOptions.find(item => item.value == dm) 
              && remoteOptions.find(item => item.value == dm).label
            ).join('、')
            
          }}
        </template>
        <template v-else>
          {{
            widget.options.showLabel ? 
            dataModel.map(dm => widget.options.options.find(item => item.value == dm) && widget.options.options.find(item => item.value == dm).label).join('、') :
            dataModel.join('、')
          }}
        </template>
      </template>
      <template v-else>
        <el-checkbox-group v-model="dataModel"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :disabled="elementDisabled"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
        >
          <el-checkbox
            
            :style="{display: widget.options.inline ? 'inline-block' : 'block'}"
            :label="item.value" v-for="(item, index) in (widget.options.remote ? remoteOptions : widget.options.options)" :key="index"
          >
            <template v-if="widget.options.remote">{{item.label}}</template>
            <template v-else>{{widget.options.showLabel ? item.label : item.value}}</template>
          </el-checkbox>
        </el-checkbox-group>
      </template>
    </template>

    <template v-if="widget.type == 'time'">
      <template v-if="printRead">
        {{dataModel}}
      </template>
      <template v-else>
        <el-time-picker 
          v-model="dataModel"
          :is-range="widget.options.isRange"
          :placeholder="widget.options.placeholder"
          :start-placeholder="widget.options.startPlaceholder"
          :end-placeholder="widget.options.endPlaceholder"
          :readonly="widget.options.readonly"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :clearable="widget.options.clearable"
          :arrowControl="widget.options.arrowControl"
          :value-format="widget.options.format"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        >
        </el-time-picker>
      </template>
    </template>

    <template v-if="widget.type=='date'">
      <template v-if="printRead">
        {{typeof dataModel == 'object' ? dataModel.join('、')  : dataModel}}
      </template>
      <template v-else>
        <el-date-picker
          v-model="dataModel"
          :type="widget.options.type"
          :placeholder="widget.options.placeholder"
          :start-placeholder="widget.options.startPlaceholder"
          :end-placeholder="widget.options.endPlaceholder"
          :readonly="widget.options.readonly"
          :disabled="elementDisabled"
          :editable="widget.options.editable"
          :clearable="widget.options.clearable"
          :value-format="widget.options.timestamp ? 'timestamp' : widget.options.format"
          :format="widget.options.format"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        >
        </el-date-picker>
      </template>
    </template>

    <template v-if="widget.type =='rate'">
      <template v-if="printRead">
        {{dataModel}}
      </template>
      <template v-else>
        <el-rate v-model="dataModel"
          :max="widget.options.max"
          :disabled="elementDisabled"
          :allow-half="widget.options.allowHalf"
          :show-score="widget.options.showScore"
          :ref="'fm-'+widget.model"
          :style="{width: isTable ? '100%' : widget.options.width, display: 'inline-block'}"
          v-bind="widget.options.customProps"
        ></el-rate>
      </template>
    </template>

    <template v-if="widget.type == 'color'">
      <template v-if="printRead">
        {{dataModel}}
      </template>
      <template v-else>
        <el-color-picker 
          v-model="dataModel"
          :disabled="elementDisabled"
          :show-alpha="widget.options.showAlpha"
          :ref="'fm-'+widget.model"
          :style="{width: isTable ? '100%' : widget.options.width}"
          v-bind="widget.options.customProps"
        ></el-color-picker>
      </template>
    </template>

    <template v-if="widget.type == 'select'">
      <template v-if="printRead">
        <template v-if="widget.options.remote">
          {{
            typeof dataModel == 'object' ?
            (dataModel.map(dm => remoteOptions.find(item => item.value == dm) 
            && remoteOptions.find(item => item.value == dm).label).join('、'))
            : (remoteOptions.find(item => item.value == dataModel) 
            && remoteOptions.find(item => item.value == dataModel).label)
          }}
        </template>
        <template v-else>
          {{
            widget.options.showLabel ? 
            (
              typeof dataModel == 'object' ? 
              dataModel.map(dm => widget.options.options.find(item => item.value == dm) && widget.options.options.find(item => item.value == dm).label).join('、')
              : (widget.options.options.find(item => item.value == dataModel) && widget.options.options.find(item => item.value == dataModel).label)
            ) :
            typeof dataModel == 'object' ? dataModel.join('、') : dataModel
          }}
        </template>
      </template>
      <template v-else>
        <el-select
          v-model="dataModel"
          :disabled="elementDisabled"
          :multiple="widget.options.multiple"
          :clearable="widget.options.clearable"
          :placeholder="widget.options.placeholder"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :filterable="widget.options.filterable"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
        >
          <el-option v-for="item in (widget.options.remote ? remoteOptions : widget.options.options)" :key="item.value" :value="item.value" :label="widget.options.showLabel || widget.options.remote?item.label:item.value"></el-option>
        </el-select>
      </template>
    </template>

    <template v-if="widget.type=='switch'">
      <template v-if="printRead">
        {{dataModel}}
      </template>
      <template v-else>
        <el-switch
          v-model="dataModel"
          :disabled="elementDisabled"
          :ref="'fm-'+widget.model"
          :style="{width: isTable ? '100%' : widget.options.width}"
          v-bind="widget.options.customProps"
        >
        </el-switch>
      </template>
    </template>

    <template v-if="widget.type=='slider'">
      <template v-if="printRead">
        {{dataModel}}
      </template>
      <template v-else>
        <el-slider 
          v-model="dataModel"
          :min="widget.options.min"
          :max="widget.options.max"
          :disabled="elementDisabled"
          :step="widget.options.step"
          :show-input="widget.options.showInput"
          :range="widget.options.range"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :ref="'fm-'+widget.model"
          v-bind="widget.options.customProps"
        ></el-slider>
      </template>
    </template>

    <template v-if="widget.type=='imgupload'">
      <fm-upload
        v-model="dataModel"
        :disabled="elementDisabled"
        :readonly="widget.options.readonly || printRead"
        :style="{'width': isTable ? '100%' : widget.options.width}"
        :width="widget.options.size.width"
        :height="widget.options.size.height"
        :token="widget.options.token"
        :domain="widget.options.domain"
        :multiple="widget.options.multiple"
        :limit="widget.options.limit"
        :is-qiniu="widget.options.isQiniu"
        :is-delete="widget.options.isDelete"
        :min="widget.options.min"
        :is-edit="widget.options.isEdit"
        :action="widget.options.action"
        :headers="widget.options.headers || []"
        :ref="'fm-'+widget.model"
        :withCredentials="widget.options.withCredentials"
        :print-read="printRead"
        @on-upload-success="handleOnUploadSuccess"
        @on-upload-error="handleOnUploadError"
        @on-upload-remove="handleOnUploadRemove"
        @on-upload-progress="handleOnUploadProgress"
        :on-select="handleOnUploadSelect"
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
          :custom-style="{width: isTable ? '100%' : widget.options.width, cursor: (elementDisabled) ? 'no-drop' : '', backgroundColor: (elementDisabled) ? '#F5F7FA' : ''}"
          :toolbar="widget.options.customToolbar"
          :disabled="elementDisabled"
          :ref="'fm-'+widget.model"
        ></Editor>
      </template>
    </template>

    <template v-if="widget.type == 'cascader'">
      <template v-if="printRead">
        <template v-if="widget.options.remote">
          {{
            widget.options.multiple ?
            dataModel.map(dm => getCascaderText([...dm], remoteOptions).join(' / ')).join('、')
            : getCascaderText([...dataModel], remoteOptions).join(' / ')
          }}
        </template>
        <template v-else>
          {{
            widget.options.multiple ?
            dataModel.map(dm => getCascaderText([...dm], widget.options.options).join(' / ')).join('、')
            : getCascaderText([...dataModel], widget.options.options).join(' / ')
          }}
        </template>
      </template>
      <template v-else>
        <el-cascader
          v-model="dataModel"
          :disabled="elementDisabled"
          :clearable="widget.options.clearable"
          :placeholder="widget.options.placeholder"
          :style="{width: isTable ? '100%' : widget.options.width}"
          :options="widget.options.remote ? remoteOptions : widget.options.options"
          @change="onCascaderChange"
          :ref="'fm-'+widget.model"
          @focus="handleOnFocus"
          @blur="handleOnBlur"
          :props="propsModel"
          collapse-tags
          :filterable="widget.options.filterable"
          v-bind="widget.options.customProps"
        >
        </el-cascader>
      </template>
    </template>
    <template v-if="widget.type == 'text'">
      <span :ref="'fm-'+widget.model" :style="{width: isTable ? '100%' : (widget.options.width || '100%'), display: 'inline-block'}">{{dataModel}}</span>
    </template>
  </div>
</template>

<script>
import FmUpload from './Upload/index.vue'
import { EventBus } from '../util/event-bus'
import Editor from './Editor/index.vue'

export default {
  name: 'generate-element-item',
  components: {
    FmUpload,
    Editor
  },
  props: ['widget', 'modelValue', 'models', 'remote', 'isTable', 'blanks', 'disabled', 'edit', 
    'remoteOption', 'rules', 'platform', 'preview', 'dataSourceValue', 'eventFunction', 
    'rowIndex', 'tableName', 'printRead', 'isMobile', 'isSubform', 'subName', 
    'isDialog', 'dialogName', 'group', 'fieldNode', 'containerKey', 'isGroup'],
  emits: ['update:modelValue', 'on-table-change'],
  data () {
    return {
      dataModel: this.modelValue,
      dataModels: this.models,
      key: new Date().getTime(),
      modelName: this.widget.model,
      propsModel: {multiple: this.widget.options.multiple || false, checkStrictly: this.widget.options.checkStrictly || false},
      propsTransfer: {key: 'key', label: 'label'},
      remoteOptions: [],
      dynamicEvents: this.handleOnDynamicEvent(),
      generateDisabled: undefined,
    }
  },
  computed: {
    elementDisabled () {
      if (typeof this.generateDisabled === 'boolean') {
        return !this.edit || this.generateDisabled
      } else {
        return !this.edit || this.widget.options.disabled
      }
    }
  },
  inject: ['generateComponentInstance', 'deleteComponentInstance', 'eventScriptConfig'],
  created () {

    if (this.widget.options.remote 
      && (Object.keys(this.widget.options).indexOf('remoteType') >= 0 ? this.widget.options.remoteType == 'func' : true)
      && this.remote[this.widget.options.remoteFunc]) {
        if(this.widget.options.optionData != undefined){//绑定了数据字典，this.widget.options.optionData为数据字典绑定信息
          this.remote[this.widget.options.remoteFunc]((data) => {
            this.loadOptions(data.option);
            setTimeout(()=>{
                if(this.dataModel == ""){//值为空则设置默认值
                  this.dataModel = data.defaultValue;
                }
              },500)
          },this.widget.options.optionData)
        }else{
          this.remote[this.widget.options.remoteFunc]((data) => {
            this.loadOptions(data)
          })
        }
    }

    if (this.widget.options.remote 
      && this.widget.options.remoteType == 'option' 
      && this.remoteOption[this.widget.options.remoteOption]) {
      
      this.loadOptions(this.remoteOption[this.widget.options.remoteOption])
    }

    if (this.widget.options.remote 
      && this.widget.options.remoteType == 'datasource' 
      && this.dataSourceValue) {

      let options = this.getDataSourceOptions()
      
      options && options.value && this.loadOptions(options.value)
    }

    if ((this.widget.type === 'imgupload' || this.widget.type === 'fileupload') && this.widget.options.isQiniu) {
      
      this.loadUploadConfig()
    }

    if (this.widget.type == 'component') {

      const _pthis = this

      this.$options.components[`component-${this.widget.key}-${this.key}`] = {
        template: `${this.widget.options.template}`,
        props: ['modelValue'],
        emits: ['update:modelValue'],
        data: () => ({
          dataModel: this.modelValue
        }),
        watch: {
          dataModel (val) {

            if (this.ui == 'antd') {
              EventBus.$emit('on-field-change', this.$attrs.id, val)
            } else {
              this.$emit('update:modelValue', val)
            }
          },
          modelValue (val) {
            this.dataModel = val
          }
        },
        methods: {
          triggerEvent (eventName, arg) {
            if (_pthis.eventScriptConfig()) {
              let currentEventScript = _pthis.eventScriptConfig().find(item => item.name == eventName)

              if (currentEventScript) {
                if (_pthis.isTable && _pthis.tableName) {
                  _pthis.eventFunction[currentEventScript.key]({
                    field: _pthis.widget.model,
                    table: _pthis.tableName, 
                    rowIndex: _pthis.rowIndex,
                    dialog: _pthis.isDialog ? _pthis.dialogName : null,
                    currentRef: this,
                    group: _pthis.group,
                    fieldNode: _pthis.fieldNode,
                    $eventArgs: arg})
                } else if (_pthis.isSubform && _pthis.subName) {
                  _pthis.eventFunction[currentEventScript.key]({
                    field: _pthis.widget.model,
                    subform: _pthis.subName, 
                    rowIndex: _pthis.rowIndex,
                    dialog: _pthis.isDialog ? _pthis.dialogName : null,
                    currentRef: this,
                    group: _pthis.group,
                    fieldNode: _pthis.fieldNode,
                    $eventArgs: arg})
                } else if (_pthis.isDialog && _pthis.dialogName) {
                  _pthis.eventFunction[currentEventScript.key]({
                    field: _pthis.widget.model,
                    dialog: _pthis.isDialog ? _pthis.dialogName : null,
                    currentRef: this,
                    group: _pthis.group,
                    fieldNode: _pthis.fieldNode,
                    $eventArgs: arg})
                } else {
                  _pthis.eventFunction[currentEventScript.key]({
                    field: _pthis.widget.model, 
                    currentRef: this,
                    group: _pthis.group,
                    fieldNode: _pthis.fieldNode,
                    $eventArgs: arg})
                }
              }
            }
          }
        }
      }
    }
  },
  mounted () {

    this.generateComponentInstance && this.generateComponentInstance(
      this.fieldNode, 
      this.$refs['fm-'+this.widget.model]
    )
  },
  beforeUnmount () {
    
    this.deleteComponentInstance && this.deleteComponentInstance(this.fieldNode)
  },
  methods: {
    handleOnDynamicEvent () {
      let currentEvents = {}

      for (let i in this.widget.events) {

        let funcKey = this.widget.events[i]

        funcKey && (
          currentEvents[i] = this.callbackDynamicFunc(funcKey)
        )
      }

      return currentEvents
    },
    callbackDynamicFunc (funcKey) {
      let callback = (...arg) => {
        if (this.isTable && this.tableName) {
          this.eventFunction[funcKey]({
            field: this.widget.model,
            table: this.tableName, 
            rowIndex: this.rowIndex,
            dialog: this.isDialog ? this.dialogName : null,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            $eventArgs: arg})
        } else if (this.isSubform && this.subName) {
          this.eventFunction[funcKey]({
            field: this.widget.model,
            subform: this.subName, 
            rowIndex: this.rowIndex,
            dialog: this.isDialog ? this.dialogName : null,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            $eventArgs: arg})
        } else if (this.isDialog && this.dialogName) {
          this.eventFunction[funcKey]({
            field: this.widget.model,
            dialog: this.dialogName,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            $eventArgs: arg
          })
        } else {
          this.eventFunction[funcKey]({
            field: this.widget.model,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            $eventArgs: arg
          })
        }
      }
      return callback
    },
    loadOptions (data) {
      if (!Array.isArray(data)) return
      this.remoteOptions = data.map(item => {
        if (this.widget.options.props.children 
              && this.widget.options.props.children.length 
              && Object.keys(item).includes(this.widget.options.props.children)) {
          return {
            value: item[this.widget.options.props.value],
            label: item[this.widget.options.props.label],
            children: this.processRemoteProps(item[this.widget.options.props.children], this.widget.options.props)
          }
        } else {
          if (this.widget.type == 'steps') {
            return {
              value: item[this.widget.options.props.title] + '', 
              label: item[this.widget.options.props.description]
            }
          } else if (this.widget.type == 'transfer') {
            return item
          } else {
            return {
              value: item[this.widget.options.props.value],
              label: item[this.widget.options.props.label]
            }
          }
        }
        
      })
    },
    processRemoteProps (children, props) {
      if (children && children.length) {
        return children.map(item => {
          if (this.processRemoteProps(item[props.children], props).length) {
            return {
              value: item[props.value],
              label: item[props.label],
              children: this.processRemoteProps(item[props.children], props)
            }
          } else{
            return {
              value: item[props.value],
              label: item[props.label],
            }
          }
        })
      } else {
        return []
      }
    },
    onCascaderChange (value) {
      if (value) {
        this.$nextTick(() => {
          setTimeout(() => {
            this.$parent && this.$parent.clearValidate()
            this.$parent && this.$parent.$parent && this.$parent.$parent.$refs.generateFormItem && this.$parent.$parent.$refs.generateFormItem.clearValidate()
          })
        })
      }
    },
    handleTransferChange (value) {
      this.dataModel = value
    },
    getCascaderText (value, options, texts = []) {
      if (value.length >= 1) {
        let currentOpt = options?.find(opt => opt.value == value[0])
        if (currentOpt) {
          texts.push(currentOpt.label)
        }
        value.splice(0, 1)
        return this.getCascaderText(value, currentOpt?.children, texts)
      } else if (value.length == 0) {
        return texts
      }
    },
    getTreeText (value, options) {
      for (let i = 0; i < options.length; i++) {
        let currentOpt = options[i]

        if (currentOpt.value == value) {
          return currentOpt.label
        }

        if (currentOpt.children && currentOpt.children.length > 0) {
          let res = this.getTreeText(value, currentOpt.children)

          if (res == '-') {
            continue
          } else {
            return res
          }
        } 
      }

      return '-'
    },
    loadUploadConfig () {
      if (this.widget.options.tokenType === 'func') {
        !this.widget.options.token && this.remote[this.widget.options.tokenFunc]((data) => {
          this.widget.options.token = data
        })
      } else {
        if (this.dataSourceValue) {
          let token = this.getDataSourceOptions('tokenDataSource')

          token && token.value && (this.widget.options.token = token.value)
        }
      }
    },
    opinionClick(data){//Y9
      console.log("意见框点击事件："+data.opinionFrameMark);
      EventBus.$emit('opinion_Click', data);
    },
    updateNumber(data){//Y9
      console.log("编号更新事件");
      EventBus.$emit('update_number', data);
    },
    updatePersonName(data){//Y9
      console.log("人员选择更新事件");
      EventBus.$emit('update_personName', data);
    },
    handleOnClick () {
      this.execFunction('onClick', {})
    },
    handleOnFocus () {
      this.execFunction('onFocus', {})
    },
    handleOnBlur () {
      this.execFunction('onBlur', {})
    },
    handleOnUploadSelect (file) {
      return this.execFunction('onSelect', {file: file})
    },
    handleOnUploadSuccess (file) {
      this.execFunction('onUploadSuccess', {file: file})
    },
    handleOnUploadError (file) {
      this.execFunction('onUploadError', {file: file})
    },
    handleOnUploadProgress (file) {
      this.execFunction('onUploadProgress', {file: file})
    },
    handleOnUploadRemove (file) {
      this.execFunction('onRemove', {file: file})
    },
    execFunction (method, arg) {
      if (this.widget.events && this.widget.events[method]) {
        let funcKey = this.widget.events[method]

        if (this.isTable && this.tableName) {
          return this.eventFunction[funcKey]({
            field: this.widget.model,
            table: this.tableName, 
            rowIndex: this.rowIndex,
            dialog: this.isDialog ? this.dialogName : null,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            ...arg,
          })
        } else if (this.isSubform && this.subName) {
          return this.eventFunction[funcKey]({
            field: this.widget.model,
            subform: this.subName, 
            rowIndex: this.rowIndex,
            dialog: this.isDialog ? this.dialogName : null,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            ...arg
          })
        } else if (this.isDialog && this.dialogName) {
          return this.eventFunction[funcKey]({
            field: this.widget.model,
            dialog: this.dialogName,
            currentRef: this.$refs['fm-'+this.widget.model],
            group: this.group,
            fieldNode: this.fieldNode,
            ...arg
          })
        } else {
          return this.eventFunction[funcKey]({
            field: this.widget.model, 
            currentRef: this.$refs['fm-'+this.widget.model], 
            group: this.group,
            fieldNode: this.fieldNode,
            ...arg
          })
        }
      }
    },
    getDataSourceOptions (remoteName = 'remoteDataSource') {

      let key = this.group ? this.group + '.' + this.widget.model + '.' + this.widget.options[remoteName]
        : this.widget.model + '.' + this.widget.options[remoteName]

      return this.dataSourceValue.find(item => item.key === key)
    },
    disabledElement (disabled) {
      this.generateDisabled = disabled
    }
  },
  watch: {
    modelValue (val) {
      this.dataModel = val
    },

    dataModel (val, oldValue) {
      this.$emit('update:modelValue', val)
    },
    'remoteOption': {
      deep: true,
      handler: function (val) {
        if (Object.keys(this.remoteOption).indexOf(this.widget.options.remoteOption) >= 0
          && this.widget.options.remote 
          && this.widget.options.remoteType == 'option' 
        ) {
          this.loadOptions(this.remoteOption[this.widget.options.remoteOption])
        }
      }
    },
    'dataSourceValue': {
      deep: true,
      handler: function(val) {
        if (this.dataSourceValue) {
          let options = this.getDataSourceOptions()
      
          options && options.value && this.loadOptions(options.value)
        }

        if ((this.widget.type === 'imgupload' || this.widget.type === 'fileupload') && this.widget.options.isQiniu) {
      
          this.loadUploadConfig()
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>

</style>