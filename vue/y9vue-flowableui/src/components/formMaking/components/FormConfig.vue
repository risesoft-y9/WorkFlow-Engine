<template>
<el-scrollbar>
  <div class="form-config-container">
    <el-form label-position="top" size="default">
      <el-form-item :label="$t('fm.config.form.labelPosition.title')">
        <el-radio-group v-model="dataModel.labelPosition" :key="data.labelPosition">
          <el-radio-button value="left" label="left">{{$t('fm.config.form.labelPosition.left')}}</el-radio-button>
          <el-radio-button value="right" label="right">{{$t('fm.config.form.labelPosition.right')}}</el-radio-button>
          <el-radio-button value="top" label="top">{{$t('fm.config.form.labelPosition.top')}}</el-radio-button>
        </el-radio-group>
      </el-form-item>
        
      <el-form-item :label="$t('fm.config.form.labelWidth')">
        <el-input-number v-model="dataModel.labelWidth" :min="0" :max="200" :step="10"></el-input-number>
      </el-form-item>
      <!-- <el-form-item :label="$t('fm.config.widget.labelWidth')" v-if="Object.keys(data.options).indexOf('labelWidth')>=0 && !data.options.tableColumn">
        <el-checkbox v-model="data.options.isLabelWidth" style="margin-right: 5px;">{{$t('fm.config.widget.custom')}}</el-checkbox>
        <el-input-number v-model="data.options.labelWidth" :disabled="!data.options.isLabelWidth" :min="0" :max="99999" :step="10"></el-input-number>
      </el-form-item> -->

      <!-- Y9权限表单 -->
      <el-form-item :label="$t('fm.config.form.permissionForm')" title="开启后需要每个输入框配置字段写权限，否则不可填写，不开启则起草节点可填写。">
        <el-switch v-model="data.permissionForm"></el-switch>
      </el-form-item>

      <el-form-item :label="$t('fm.config.form.size')">
        <el-radio-group v-model="dataModel.size">
          <el-radio-button value="large" label="large">Large</el-radio-button>
          <el-radio-button value="default" label="default">Default</el-radio-button>
          <el-radio-button value="small" label="small">Small</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</el-scrollbar>
</template>

<script>
import { splitStyleSheets } from '../util'
import { EventBus } from '../util/event-bus.js'
import { ElMessage } from 'element-plus'

export default {
  components: {
  },
  props: ['data', 'sheets', 'formKey'],
  emits: ['on-style-update', 'on-datasource-update', 'on-eventscript-update', 'on-eventscript-confirm', 'update:data'],
  data () {
    return {
      customClassArray: this.data && this.data.customClass ? this.data.customClass.split(' ').filter(item => item) : [],
      dataModel: this.data
    }
  },
  inject: ['isMobile', 'useAntdForm'],
  methods: {
    

    handlestyleSheetsConfirm (value) {

      this.dataModel.styleSheets = value

      this.$refs.styleDialog.close()

      var arr = splitStyleSheets(value)

      this.$emit('on-style-update', arr)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },
    editScript (eventName, eventKey) {
      this.dataModel.eventScript = this.data.eventScript || []

      this.$refs.eventScriptDialog.open(this.data.eventScript, eventName, eventKey)
    },
  },
  mounted () {
    if (this.useAntdForm) {
      this.dataModel.ui = 'antd'
    } else {
      this.dataModel.ui = 'element'
    }
  },
  watch: {
    'data.customClass': function(val) {
      this.customClassArray = this.data && this.data.customClass ? this.data.customClass.split(' ').filter(item => item) : []
    },
    customClassArray (val) {
      this.dataModel.customClass = val.join(' ')
    },
    data (val) {
      this.dataModel = val
    },
    dataModel: {
      deep: true,
      handler (val) {
        this.$emit('update:data', val)
      }
    }
  }
}
</script>
