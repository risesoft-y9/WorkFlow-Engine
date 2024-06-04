<template>
<el-scrollbar>
  <div class="form-config-container">
    <el-form label-position="top" size="default">
      <el-form-item label="UI">
        <el-radio-group v-model="dataModel.ui">
          <el-radio-button value="element" label="element">Element</el-radio-button>
          <el-radio-button value="antd" label="antd" :disabled="!useAntdForm">Ant Design</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$t('fm.config.form.width')" >
        <el-input v-model="dataModel.width" clearable :disabled="isMobile()"></el-input>
      </el-form-item>
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
      <el-form-item :label="$t('fm.config.form.labelSuffix')">
        <el-switch v-model="dataModel.labelSuffix"></el-switch>
      </el-form-item>

      <el-form-item :label="$t('fm.config.form.size')">
        <el-radio-group v-model="dataModel.size">
          <el-radio-button value="large" label="large">Large</el-radio-button>
          <el-radio-button value="default" label="default">Default</el-radio-button>
          <el-radio-button value="small" label="small">Small</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item :label="$t('fm.config.form.styleSheets')">
        <el-button style="width: 100%;" @click="handleSetStyleSheets">{{$t('fm.config.widget.setting')}}</el-button>
      </el-form-item>

      <el-form-item :label="$t('fm.config.form.customClass')" >
        <el-select
          style="width: 100%;"
          v-model="customClassArray"
          multiple
          filterable
          allow-create
          default-first-option>
          <el-option
            v-for="item in sheets"
            :key="item"
            :label="item"
            :value="item">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item :label="$t('fm.datasource.name')">
        <el-button style="width: 100%;" @click="handleSetDatasource">{{$t('fm.config.widget.setting')}}</el-button>
      </el-form-item>

      <el-form-item :label="$t('fm.eventscript.name')">
        <el-button style="width: 100%;" @click="handleSetScript">{{$t('fm.config.widget.setting')}}</el-button>
      </el-form-item>
    </el-form>

    <code-dialog ref="styleDialog" mode="css" :title="$t('fm.config.form.styleSheets')" help="https://form.making.link/docs/manual/custom-style.html#%E8%A1%A8%E5%8D%95%E6%A0%B7%E5%BC%8F%E8%A1%A8" @on-confirm="handlestyleSheetsConfirm"></code-dialog>

    <datasource-dialog ref="dataSourceDialog" @dialog-close="handleDataSourceClose"></datasource-dialog>

    <event-script-dialog ref="eventScriptDialog" @dialog-close="handleEventScriptClose" @dialog-confirm="handleEventConfirm"></event-script-dialog>
  </div>
</el-scrollbar>
</template>

<script>
import CodeDialog from './CodeDialog.vue'
import DatasourceDialog from './DataSource/dialog.vue'
import EventScriptDialog from './EventPanel/dialog.vue'
import { splitStyleSheets } from '../util'
import { EventBus } from '../util/event-bus.js'
import { ElMessage } from 'element-plus'

export default {
  components: {
    CodeDialog,
    DatasourceDialog,
    EventScriptDialog
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
    handleSetStyleSheets () {
      let sheets = document.styleSheets[0]

      if (sheets.insertRule) {
        this.$refs.styleDialog.open(this.data.styleSheets || '')
      } else {
        ElMessage({
          message: this.$t('fm.message.notSupport'),
          type: 'warning'
        })
      }
    },

    handlestyleSheetsConfirm (value) {

      this.dataModel.styleSheets = value

      this.$refs.styleDialog.close()

      var arr = splitStyleSheets(value)

      this.$emit('on-style-update', arr)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },

    handleSetDatasource () {
      this.dataModel.dataSource = this.data.dataSource || []

      this.$refs.dataSourceDialog.open(this.data.dataSource)
    },
    handleDataSourceClose (list) {
      this.dataModel.dataSource = list

      this.$emit('on-datasource-update', list)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },

    handleSetScript () {
      this.dataModel.eventScript = this.data.eventScript || []

      this.$refs.eventScriptDialog.open(this.data.eventScript)
    },

    editScript (eventName, eventKey) {
      this.dataModel.eventScript = this.data.eventScript || []

      this.$refs.eventScriptDialog.open(this.data.eventScript, eventName, eventKey)
    },

    handleEventScriptClose (list) {
      this.dataModel.eventScript = list

      this.$emit('on-eventscript-update', list)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },

    handleEventConfirm (eventObj) {
      this.$emit('on-eventscript-confirm', eventObj)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    }
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
