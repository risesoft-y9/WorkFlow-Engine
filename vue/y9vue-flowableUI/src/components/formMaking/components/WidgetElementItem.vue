<template>
    <template v-if="element.type == 'input'">
      <el-input 
        v-model="element.options.defaultValue"
        :style="{width: isTable ? '100%' : element.options.width}"
        :placeholder="element.options.placeholder"
        :disabled="element.options.disabled"
        :show-password="element.options.showPassword"
        :maxlength="element.options.maxlength"
        :show-word-limit="element.options.showWordLimit"
        v-bind="element.options.customProps"
      ></el-input>
    </template>

    <template v-if="element.type == 'textarea'">
      <el-input type="textarea" :rows="element.options.rows"
        v-model="element.options.defaultValue"
        :style="{width: isTable ? '100%' : element.options.width}"
        :disabled="element.options.disabled"
        :placeholder="element.options.placeholder"
        :maxlength="element.options.maxlength"
        :show-word-limit="element.options.showWordLimit"
        :autosize="element.options.autosize"
        v-bind="element.options.customProps"
      ></el-input>
    </template>

    <template v-if="element.type == 'number'">
      <el-input-number 
        v-model="element.options.defaultValue" 
        :disabled="element.options.disabled"
        :controls-position="element.options.controlsPosition"
        :precision="element.options.precision"
        :controls="element.options.controls"
        :style="{width: isTable ? '100%' : element.options.width}"
        v-bind="element.options.customProps"
      ></el-input-number>
    </template>

    <template v-if="element.type == 'radio'">
      <el-radio-group v-model="element.options.defaultValue"
        :style="{width: isTable ? '100%' : element.options.width, display: 'block'}"
        :disabled="element.options.disabled"
        v-bind="element.options.customProps"
      >
        <el-radio  
          :style="{display: element.options.inline ? 'inline-block' : 'block'}"
          :label="item.value" :value="item.value" v-for="(item, index) in element.options.options" :key="item.value + index"
        >
          {{element.options.showLabel ? item.label : item.value}}
        </el-radio>
      </el-radio-group>
    </template>

    <template v-if="element.type == 'checkbox'">
      <el-checkbox-group v-model="element.options.defaultValue"
        :style="{width: isTable ? '100%' : element.options.width}"
        :disabled="element.options.disabled"
        v-bind="element.options.customProps"
      >
        <el-checkbox
          :style="{display: element.options.inline ? 'inline-block' : 'block'}"
          :label="item.value" v-for="(item, index) in element.options.options" :key="item.value + index"
        >
          {{element.options.showLabel ? item.label : item.value}}
        </el-checkbox>
      </el-checkbox-group>
    </template>

    <template v-if="element.type == 'time'">
      <el-time-picker 
        :key="elementKey"
        v-model="element.options.defaultValue"
        :is-range="element.options.isRange"
        :placeholder="element.options.placeholder"
        :start-placeholder="element.options.startPlaceholder"
        :end-placeholder="element.options.endPlaceholder"
        :readonly="element.options.readonly"
        :disabled="element.options.disabled"
        :editable="element.options.editable"
        :clearable="element.options.clearable"
        :style="{width: isTable ? '100%' : element.options.width}"
        :value-format="element.options.format"
        v-bind="element.options.customProps"
      >
      </el-time-picker>
    </template>

    <template v-if="element.type == 'date'">
      <el-date-picker
        :key="elementKey"
        v-model="element.options.defaultValue"
        :type="element.options.type"
        :is-range="element.options.isRange"
        :placeholder="element.options.placeholder"
        :start-placeholder="element.options.startPlaceholder"
        :end-placeholder="element.options.endPlaceholder"
        :readonly="element.options.readonly"
        :disabled="element.options.disabled"
        :editable="element.options.editable"
        :clearable="element.options.clearable"
        :value-format="element.options.timestamp ? 'timestamp' : element.options.format"
        :format="element.options.format"
        :style="{width: isTable ? '100%' : element.options.width}"  
        v-bind="element.options.customProps"
      >
      </el-date-picker>
    </template>

    <template v-if="element.type == 'rate'">
      <el-rate v-model="element.options.defaultValue"
        :max="element.options.max"
        :disabled="element.options.disabled"
        :allow-half="element.options.allowHalf"
        :show-score="element.options.showScore"
        :style="{width: isTable ? '100%' : element.options.width, display: 'inline-block'}"
        v-bind="element.options.customProps"
      ></el-rate>
    </template>

    <template v-if="element.type == 'color'">
      <el-color-picker 
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :show-alpha="element.options.showAlpha"
        v-bind="element.options.customProps"
      ></el-color-picker>
    </template>

    <template v-if="element.type == 'select'">
      <el-select
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :multiple="element.options.multiple"
        :clearable="element.options.clearable"
        :placeholder="element.options.placeholder"
        :style="{width: isTable ? '100%' : element.options.width}"
        v-bind="element.options.customProps"
      >
        <el-option v-for="item in element.options.options" :key="item.value" :value="item.value" :label="element.options.showLabel?item.label:item.value"></el-option>
      </el-select>
    </template>

    <template v-if="element.type=='switch'">
      <el-switch
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :style="{width: isTable ? '100%' : element.options.width}"
        v-bind="element.options.customProps"
      >
      </el-switch>
    </template>

    <template v-if="element.type=='slider'">
      <el-slider 
        v-model="element.options.defaultValue"
        :min="element.options.min"
        :max="element.options.max"
        :disabled="element.options.disabled"
        :step="element.options.step"
        :show-input="element.options.showInput"
        :range="element.options.range"
        :style="{width: isTable ? '100%' : element.options.width}"
        v-bind="element.options.customProps"
      ></el-slider>
    </template>

    <template v-if="element.type=='imgupload'">
      <fm-upload
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :style="{'width': isTable ? '100%' : element.options.width}"
        :width="element.options.size.width"
        :height="element.options.size.height"
        token="xxx"
        domain="xxx"
      >
        
      </fm-upload>
    </template>

    <template v-if="element.type == 'cascader'">
      <el-cascader
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :clearable="element.options.clearable"
        :placeholder="element.options.placeholder"
        :style="{width: isTable ? '100%' : element.options.width}"
        :options="element.options.remote ? [] : element.options.options"
        :props="{multiple: element.options.multiple, checkStrictly: element.options.checkStrictly}"
        v-bind="element.options.customProps"
      >

      </el-cascader>
    </template>

    <template v-if="element.type == 'treeselect'">
      <el-tree-select
        :key="element.options.defaultValue"
        v-model="element.options.defaultValue"
        :disabled="element.options.disabled"
        :clearable="element.options.clearable"
        :placeholder="element.options.placeholder"
        :style="{width: isTable ? '100%' : element.options.width}"
        :data="element.options.remote ? [] : element.options.options"
        :multiple="element.options.multiple"
        v-bind="element.options.customProps"
      ></el-tree-select>
    </template>

    <template v-if="element.type == 'editor'">
      <Editor
        :modelValue="element.options.defaultValue"
        :custom-style="{width: isTable ? '100%' : element.options.width, cursor: element.options.disabled ? 'no-drop' : '', backgroundColor: element.options.disabled ? '#F5F7FA' : ''}"
        :toolbar="element.options.customToolbar"
        class="fm-editor"
        :disabled="element.options.disabled"
        :key="elementKey"
      ></Editor>
    </template>

    <template v-if="element.type=='blank'">
      <div style="height: 50px;color: #999;line-height:50px;text-align:center;width: 100%;">{{element.model}}</div>
      
    </template>

    <template v-if="element.type == 'component'">
      <component :style="{width: isTable ? '100%' : element.options.width}" :is="`component-${element.key}`" :key="key" v-model="element.options.defaultValue"></component>
    </template>

    <template v-if="element.type == 'custom'">
      <component  
        :is="element.el" 
        v-model="element.options.defaultValue"
        :width="element.options.width"
        :height="element.options.height"
        :placeholder="element.options.placeholder"
        :readonly="element.options.readonly"
        :disabled="element.options.disabled"
        :editable="element.options.editable"
        :clearable="element.options.clearable"
        v-bind="{...element.options.customProps, ...element.options.extendProps }"
      ></component>
    </template>

    <template v-if="element.type == 'text'">
      <span :style="{'width': isTable ? '100%' : (element.options.width || '100%'), display: 'inline-block'}">{{element.options.defaultValue}}</span>
    </template>

    <template v-if="element.type == 'html'">
      <span :style="{'width': isTable ? '100%' : (element.options.width || '100%'), display: 'inline-block'}" v-html="element.options.defaultValue"></span>
    </template>

    <template v-if="element.type == 'fileupload'">
      <el-upload
        action="https://jsonplaceholder.typicode.com/posts/"
        :style="{width: isTable ? '100%' : element.options.width}"
        :disabled="element.options.disabled"
        :limit="element.options.limit"
      >
        <el-button size="default" type="primary">{{$t('fm.actions.upload')}}</el-button>
      </el-upload>
    </template>

    <template v-if="element.type == 'button'">
      <el-button
        :disabled="element.options.disabled"
        :size="element.options.buttonSize"
        :type="element.options.buttonType == 'text' || element.options.buttonType == 'link' ? 'primary' : element.options.buttonType"
        :text="element.options.buttonType == 'text'"
        :link="element.options.buttonType == 'link'"
        :plain="element.options.buttonPlain"
        :round="element.options.buttonRound"
        :circle="element.options.buttonCircle"
        :style="{width: element.options.width}"
        v-bind="element.options.customProps"
      >
        {{element.options.buttonName}}
      </el-button>
    </template>

    <template v-if="element.type == 'link'">
      <el-link
        :disabled="element.options.disabled"
        :type="element.options.linkType"
        :underline="element.options.underline"
        v-bind="element.options.customProps"
      >
        {{element.options.linkName}}
      </el-link>
    </template>

    <template v-if="element.type == 'steps'">
      <el-steps 
        :active="element.options.defaultValue"
        :space="element.options.space"
        :direction="element.options.direction"
        :process-status="element.options.processStatus"
        :finish-status="element.options.finishStatus"
        :align-center="element.options.alignCenter"
        :simple="element.options.simple"
        :style="{'line-height': 'normal'}"
        v-bind="element.options.customProps"
      >
        <el-step :title="item.title" :description="item.description" v-for="(item, index) in element.options.steps" :key="index"></el-step>
      </el-steps>
    </template>

    <template v-if="element.type == 'transfer'">
      <el-transfer
        :model-value="element.options.defaultValue"
        :data="element.options.data"
        :filterable="element.options.filterable"
        :titles="element.options.titles"
        :style="{'width': isTable ? '100%' : element.options.width}"
        v-bind="element.options.customProps"
      ></el-transfer>
    </template>

    <template v-if="element.type == 'pagination'">
      <el-pagination
        v-model:currentPage="element.options.defaultValue"
        :page-size="element.options.pageSize"
        :pager-count="element.options.pagerCount"
        :disabled="element.options.disabled"
        :background="element.options.background"
        layout="total, prev, pager, next"
        :total="element.options.total"
        v-bind="element.options.customProps"
      />
    </template>
</template>

<script>
import FmUpload from './Upload/index.vue'
import Editor from './Editor/index.vue'

export default {
  components: {
    FmUpload,
    Editor
  },
  props: ['element', 'isTable'],
  data () {
    return {
      key: Math.random().toString(36).slice(-8),
      elementKey: Math.random().toString(36).slice(-8)
    }
  },
  created () {
    if (this.element.type == 'component') {
      this.$options.components[`component-${this.element.key}`] = {
        template: `${this.element.options.template}`,
        props: ['modelValue'],
        data: () => ({
          dataModel: this.modelValue
        })
      }
    }
  },
  watch: {
    'element.options.template': function (val) {

      this.$options.components[`component-${this.element.key}`] = {
        template: `${val}`,
        props: ['modelValue'],
        data: () => ({
          dataModel: this.modelValue
        })
      }

      this.key = Math.random().toString(36).slice(-8)
    },
    'element.options.defaultValue': function (val) {
      this.elementKey = Math.random().toString(36).slice(-8)
      
    }
  }
}
</script>
