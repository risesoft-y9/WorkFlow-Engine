<template>
  <el-container class="fm2-container fm-form" :class="{
    ['fm-'+formKey]: true,
    'is-fullscreen': fullscreen
  }" :style="{'z-index': zIndex}">
    <el-main class="fm2-main">
      <el-container>
        <el-aside width="250px" class="widget-left-panel" ref="widgetLeftPanel">

          <el-tabs v-model="activeLeft"  class="left-tabs" v-show="!leftHide">
            <el-tab-pane name="field" >
              <template #label>
                <el-tooltip :content="$t('fm.actions.components')" placement="bottom-start">
                  <span>&nbsp;&nbsp;<i class="fm-iconfont icon-yuanshuju-zujianku"></i>&nbsp;&nbsp;</span>
                </el-tooltip>
              </template>
              <el-scrollbar>
                <div class="components-list">

                  <el-collapse v-model="activeFields">
                    <el-collapse-item name="layout" v-if="layoutFields.length" :title="$t('fm.components.layout.title')">
                      <draggable tag="ul" :list="layoutComponents" 
                        :group="{ name:'people', pull:'clone',put:false}"
                        :sort="false"
                        ghost-class="ghost"
                        @end="handleMoveEnd"
                        @start="handleMoveStart"
                        :move="handleMove"
                        item-key="name"
                      >
                        <template #item="{element}">
                          <li @click="handleField(element)" v-if="layoutFields.indexOf(element.type) >=0" class="form-edit-widget-label no-put">
                            <a>
                              <i class="icon iconfont fm-iconfont" :class="element.icon"></i>
                              <span>{{element.name}}</span>
                            </a>
                          </li>
                        </template>
                      </draggable>
                    </el-collapse-item>

                    <el-collapse-item name="collection" v-if="collectionFields.length" :title="$t('fm.components.collection.title')">
                      <draggable tag="ul" :list="collectionComponents" 
                        :group="{ name:'people', pull:'clone',put:false}"
                        :sort="false"
                        ghost-class="ghost"
                        @end="handleMoveEnd"
                        @start="handleMoveStart"
                        :move="handleMove"
                        item-key="name"
                      >
                        <template #item="{element}">
                          <li @click="handleField(element)" v-if="collectionFields.indexOf(element.type) >= 0" class="form-edit-widget-label" 
                            :class="{
                              'no-put': true,
                              'subform-put': element.type == 'table' || element.type == 'subform',
                              'dialog-put': element.type == 'dialog'
                            }">
                            <a>
                              <i class="icon iconfont fm-iconfont" :class="element.icon"></i>
                              <span>{{element.name}}</span>
                            </a>
                          </li>
                        </template>
                      </draggable>
                    </el-collapse-item>
                    
                    <el-collapse-item name="basic" v-if="basicFields.length" :title="$t('fm.components.basic.title')">
                      <draggable tag="ul" 
                        :list="basicComponents" 
                        :group="{ name:'people', pull:'clone',put:false}"
                        :sort="false"
                        ghost-class="ghost"
                        @end="handleMoveEnd"
                        @start="handleMoveStart"
                        :move="handleMove"
                        item-key="name"
                      >
                        <template #item="{element}">
                          <li @click="handleField(element)" v-if="basicFields.indexOf(element.type)>=0" class="form-edit-widget-label" :class="{'no-put': element.type == 'divider'}">
                            <a>
                              <i class="icon iconfont fm-iconfont" :class="element.icon"></i>
                              <span>{{element.name}}</span>
                            </a>
                          </li>
                        </template>
                        
                      </draggable>
                    </el-collapse-item>

                    <el-collapse-item name="advance" v-if="advanceFields.length" :title="$t('fm.components.advance.title')">
                      <draggable tag="ul" :list="advanceComponents" 
                        :group="{ name:'people', pull:'clone',put:false}"
                        :sort="false"
                        ghost-class="ghost"
                        @end="handleMoveEnd"
                        @start="handleMoveStart"
                        :move="handleMove"
                        item-key="name"
                      >
                        <template #item="{element}">
                          <li @click="handleField(element)" v-if="advanceFields.indexOf(element.type) >= 0" class="form-edit-widget-label" 
                            :class="{
                              'no-put': element.type == 'table' || element.type == 'subform' || element.type == 'dialog' || element.type == 'group',
                              'subform-put': element.type == 'table' || element.type == 'subform',
                              'dialog-put': element.type == 'dialog'
                            }">
                            <a>
                              <i class="icon iconfont fm-iconfont" :class="element.icon"></i>
                              <span>{{element.name}}</span>
                            </a>
                          </li>
                        </template>
                      </draggable>
                    </el-collapse-item>

                    <el-collapse-item name="custom" v-if="customFields.length" :title="$t('fm.components.custom.title')">
                      <draggable tag="ul" :list="customComponents"
                        :group="{ name:'people', pull:'clone',put:false}"
                        :sort="false"
                        ghost-class="ghost"
                        @end="handleMoveEnd"
                        @start="handleMoveStart"
                        :move="handleMove"
                        item-key="name"
                      >
                        <template #item="{element}">
                          <li @click="handleField(element)" class="form-edit-widget-label" >
                            <a>
                              <i class="icon iconfont fm-iconfont custom" :class="element.icon ? '' : 'icon-extend'">
                                <span v-html="element.icon" v-if="element.icon"></span>
                              </i>
                              <span>{{element.name}}</span>
                            </a>
                          </li>
                        </template>
                        
                      </draggable>
                    </el-collapse-item>
                  </el-collapse>
                  
                </div>
              </el-scrollbar>
            </el-tab-pane>

            <el-tab-pane name="outline">
              <template #label>
                <el-tooltip :content="$t('fm.actions.outline')" placement="bottom">
                  <span>&nbsp;&nbsp;<i class="fm-iconfont icon-fuhao-dagangshu"></i>&nbsp;&nbsp;</span>
                </el-tooltip>
              </template>
              <outline ref="outlineRef" :show="activeLeft == 'outline'" :data="widgetForm.list" @select="onSelectWidget"></outline>
            </el-tab-pane>
          </el-tabs>

          <div class="container-left-arrow" @click="handleLeftToggle"></div>
        </el-aside>
        <el-container class="center-container" direction="vertical">
          <el-header class="btn-bar" style="height: 45px;">
            <div class="btn-bar-plat">
              <a :class="{'active': platform == 'pc'}" @click="handlePlatform('pc')"><i class="fm-iconfont icon-pc"></i></a>
              <a :class="{'active': platform == 'pad'}" @click="handlePlatform('pad')"><i class="fm-iconfont icon-pad"></i></a>
              <a :class="{'active': platform == 'mobile'}" @click="handlePlatform('mobile')"><i class="fm-iconfont icon-mobile"></i></a>
            </div>
            <div class="btn-diviler"></div>
            <div class="btn-bar-action">
              <el-tooltip :content="$t('fm.actions.undo')" placement="bottom">
                <a @click="handleUndo" :class="{'disabled': !undo}"><i class="fm-iconfont icon-007caozuo_chexiao"></i></a>
              </el-tooltip>
              <el-tooltip :content="$t('fm.actions.redo')" placement="bottom">
                <a @click="handleRedo" :class="{'disabled': !redo}"><i class="fm-iconfont icon-8zhongzuo"></i></a>
              </el-tooltip>
              
            </div>
            <div class="btn-diviler"></div>
            <div class="btn-bar-action">
              <el-tooltip :content="$t('fm.actions.fullScreen')" placement="bottom" v-if="!fullscreen">
                <a @click="handleFullScreen"><i class="fm-iconfont icon-quanping_o"></i></a>
              </el-tooltip>
              <el-tooltip :content="$t('fm.actions.exitFullScreen')" placement="bottom" v-else>
                <a @click="handleExitFullScreen"><i class="fm-iconfont icon-quxiaoquanping_o"></i></a>
              </el-tooltip>
            </div>
            <slot name="action">
            </slot>

            <el-button v-if="upload" link type="primary" size="default" @click="handleUpload"><i class="fm-iconfont icon-daoru" style="font-size: 16px; font-weight: 600; margin: 5px;" />{{$t('fm.actions.import')}}</el-button>
            <el-button v-if="preview" link type="primary" size="default" @click="handlePreview"><i class="fm-iconfont icon-icon_yulan" style="font-size: 16px; font-weight: 600; margin: 5px;" />{{$t('fm.actions.preview')}}</el-button>
            <el-button v-if="clearable" link type="primary" size="default" @click="handleClear"><i class="fm-iconfont icon-qingkong" style="font-size: 16px; font-weight: 600; margin: 5px;" />{{$t('fm.actions.clear')}}</el-button>
            <el-button v-if="generateJson" link type="primary" size="default"  @click="handleGenerateJson"><i class="fm-iconfont icon-json1" style="font-size: 16px; font-weight: 600; margin: 5px;" />{{$t('fm.actions.json')}}</el-button>
            <el-button v-if="generateCode" link type="primary" size="default"  @click="handleGenerateCode"><i class="fm-iconfont icon-daimakuai" style="font-size: 16px; font-weight: 600; margin: 5px;" />{{$t('fm.actions.code')}}</el-button>
          </el-header>
          <el-main :class="{'widget-empty': widgetForm.list.length == 0}">
            
            <widget-form v-if="!resetJson"  ref="widgetForm" :data="widgetForm" v-model:select="widgetFormSelect" :platform="platform" :form-key="formKey"></widget-form>
          </el-main>
        </el-container>
        
        <el-aside class="widget-config-container" ref="widgetConfigContainer">
          <el-container v-show="!rightHide">
            <el-header height="45px">
              <div class="config-tab" :class="{active: configTab=='widget'}" @click="handleConfigSelect('widget')">{{$t('fm.config.widget.title')}}</div>
              <div class="config-tab" :class="{active: configTab=='form'}" @click="handleConfigSelect('form')">{{$t('fm.config.form.title')}}</div>
            </el-header>
            <el-main class="config-content">
              <!-- <el-scrollbar ref="configScroll"> -->
                <widget-config v-show="configTab=='widget'" ref="widgetConfig"
                  :platform="platform" 
                  :sheets="styleSheetsArray" 
                  :datasources="dataSourceArray" 
                  :eventscripts="eventScriptArray" 
                  :data="widgetFormSelect" 
                  :key="widgetFormSelect ? widgetFormSelect.key : 0"
                  @on-event-add="handleEventAdd"
                  @on-event-edit="handleEventEdit"
                  @on-event-remove="handleEventRemove"
                  :form-key="formKey"
                  :field-models="fieldModels"
                >
                  <template #widgetconfig="{type, customProps, data}">
                    <slot name="widgetconfig" :type="type" :customProps="customProps" :data="data"></slot>
                  </template>
                </widget-config>
                <form-config v-show="configTab=='form'" ref="formConfig"
                  :sheets="styleSheetsArray" 
                  :data="widgetForm.config" 
                  @on-style-update="onStyleUpdate" 
                  @on-datasource-update="onDataSourceUpdate" 
                  @on-eventscript-update="onEventScriptUpdate"
                  @on-eventscript-confirm="onEventScriptConfirm"
                  :form-key="formKey"
                ></form-config>
              <!-- </el-scrollbar> -->
            </el-main>
          </el-container>
          <div class="container-right-arrow" @click="handleRightToggle"></div>
        </el-aside>

        <preview-dialog ref="previewDialog" @get-data-success="preivewGetData"></preview-dialog>

        <import-json-dialog ref="importJsonDialog" @load-json="handleLoadJson"></import-json-dialog>

        <cus-dialog
          :visible="jsonVisible"
          @on-close="jsonVisible = false"
          ref="jsonPreview"
          width="800px"
          form
          :title="jsonTitle"
        >

          <code-editor height="400px" mode="json" v-model="jsonTemplate" wrap></code-editor>
          
          <template #action>
            <el-button type="primary" class="json-btn" :data-clipboard-text="jsonCopyValue">{{$t('fm.actions.copyData')}}</el-button>
            <el-button type="primary" @click="handleExportJSON">{{$t('fm.actions.export')}}</el-button>
          </template>
        </cus-dialog>

        <cus-dialog
          :visible="codeVisible"
          @on-close="codeVisible = false"
          ref="codePreview"
          width="800px"
          form
          :title="$t('fm.actions.code')"
        >
          <el-tabs type="border-card" style="box-shadow: none;" v-model="codeActiveName">
            <el-tab-pane label="Vue Component" name="vue">
              <code-editor height="450px" mode="html" v-model="vueTemplate"></code-editor>
            </el-tab-pane>
            <el-tab-pane label="HTML" name="html">
              <code-editor height="450px" mode="html" v-model="htmlTemplate"></code-editor>
            </el-tab-pane>
          </el-tabs>

          <template #action>
            <el-button type="primary" class="code-btn" :data-clipboard-text="codeCopyValue">{{$t('fm.actions.copyData')}}</el-button>
            <el-button type="primary" @click="handleExport">{{$t('fm.actions.export')}}</el-button>
          </template>
        </cus-dialog>
      </el-container>
    </el-main>
    <el-footer height="30px" style="font-weight: 600;">Powered by <a target="_blank" href="https://form.making.link">FormMaking</a> {{version ? ` - V${version}` : ''}}</el-footer>
  </el-container>
  
</template>

<script>
import Draggable from 'vuedraggable/src/vuedraggable'
import WidgetConfig from './WidgetConfig.vue'
import FormConfig from './FormConfig.vue'
import WidgetForm from './WidgetForm.vue'
import CusDialog from './CusDialog.vue'
import ClipboardJS from 'clipboard'
import CodeEditor from './CodeEditor/index.vue'
import {basicComponents, layoutComponents, advanceComponents, collectionComponents} from './componentsConfig.js'
import {updateStyleSheets, splitStyleSheets, splitSheetName, addClass, removeClass} from '../util/index.js'
import { EventBus } from '../util/event-bus.js'
import generateCode from './generateCode.js'
import historyManager from '../util/history-manager.js'
import _ from 'lodash'
import { UpgradeData } from '../util/version-upgrade'
import PreviewDialog from './PreviewDialog.vue'
import ImportJsonDialog from './ImportJson/dialog.vue'
import Outline from './Outline.vue'
import { findModelNodeString } from '../util/find-node.js'
import { getModels } from '../util/model-outline.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'fm-making-form',
  components: {
    Draggable,
    WidgetConfig,
    FormConfig,
    WidgetForm,
    CusDialog,
    CodeEditor,
    PreviewDialog,
    ImportJsonDialog,
    Outline
  },
  props: {
    preview: {
      type: Boolean,
      default: false
    },
    generateCode: {
      type: Boolean,
      default: false
    },
    generateJson: {
      type: Boolean,
      default: false
    },
    upload: {
      type: Boolean, 
      default: false
    },
    clearable: {
      type: Boolean,
      default: false
    },
    basicFields: {
      type: Array,
      default: () => ['input', 'textarea', 'number', 'radio', 'checkbox', 'time', 'date', 'rate', 'color', 'select', 'switch', 'slider', 'text', 'html', 'button', 'link', 'cascader', 'treeselect', 'steps', 'transfer', 'pagination']
    },
    advanceFields: {
      type: Array,
      default: () => ['blank', 'component', 'fileupload', 'imgupload', 'editor']
    },
    layoutFields: {
      type: Array,
      default: () => ['grid', 'report', 'tabs', 'collapse', 'inline', 'card', 'divider', 'alert']
    },
    collectionFields: {
      type: Array,
      default: () => ['table', 'subform', 'dialog', 'group']
    },
    customFields: {
      type: Array,
      default: () => []
    },
    globalConfig: {
      type: Object,
      default: () => ({})
    },
    fieldConfig: {
      type: Array,
      default: () => []
    },
    name: {
      type: String,
      default: ''
    },
    cache: {
      type: Boolean,
      default: false
    },
    jsonTemplates: {
      type: Array,
      default: () => []
    },
    initFromTemplate: {
      type :Boolean,
      default: false
    },
    fieldModels: {
      type: Array,
      default: () => []
    },
    panel: {
      type: String,
      default: 'field'
    },
    zIndex: {
      type: Number,
      default: 2000
    },
    useAntdForm: {
      type: Boolean,
      default: false
    }
  },
  emits: ['ready', 'preview'],
  data () {
    return {
      version: window.FormMaking_OPTIONS['version'],
      basicComponents,
      layoutComponents,
      advanceComponents,
      collectionComponents,
      customComponents: [],
      resetJson: false,
      widgetForm: {
        list: [],
        config: {
          labelWidth: 100,
          labelPosition: 'left',
          size: 'default',
          customClass: '',
          ui: 'element',
          layout: 'horizontal',
          width: '100%',
          hideLabel: false,
          hideErrorMessage: false
        },
      },
      configTab: 'form',
      widgetFormSelect: null,
      previewVisible: false,
      jsonVisible: false,
      codeVisible: false,
      uploadVisible: false,
      blank: '',
      htmlTemplate: '',
      jsonTemplate: '',
      vueTemplate: '',
      uploadEditor: null,
      jsonCopyValue: '',
      jsonClipboard: null,
      codeCopyValue: '',
      codeClipboard: null,
      codeActiveName: 'vue',
      undo: false,
      redo: false,
      formKey: Math.random().toString(36).slice(-8),
      styleSheetsArray: [],
      dataSourceArray: [],
      eventScriptArray: [],
      platform: 'pc',
      activeFields: ['basic', 'advance', 'layout', 'custom', 'collection'],
      activeLeft: this.panel,
      isScrollTo: true,
      jsonTitle: this.$t('fm.actions.json'),
      modelNode: '',
      rightHide: false,
      leftHide: false,
      fullscreen: false
    }
  },
  created () {
    this._loadComponents()
  },
  provide () {
    return {
      'changeConfigTab': this.changeConfigTab,
      'getModelNode': this.getModelNode,
      'isMobile': this.isMobile,
      'getFormModels': this.getFormModels,
      'getFormFields': this.getFormFields,
      'getDataSourceArray': this.getDataSourceArray,
      'useAntdForm': this.useAntdForm
    }
  },
  mounted () {
    const _this = this

    historyManager.clear().then(() => {
      // 添加表单默认事件
      this.widgetForm.config.eventScript = [
        {key: 'mounted', name: 'mounted', func: ''},
        {key: 'refresh', name: 'refresh', func: ''},
      ]

      // 加载全局配置项
      this.widgetForm.config = {
        ...this.widgetForm.config,
        ...this.globalConfig
      }

      this.platform = this.widgetForm.config.platform || 'pc'

      this.initConfig()

      this.cache && this.setJSON(localStorage.getItem('fmjson'+this.name) ?? this.widgetForm)

      this.$emit('ready')

      EventBus.$on('on-history-add-' + this.formKey, () => {
        
        historyManager.add(this.widgetForm, (this.widgetFormSelect && this.widgetFormSelect.key) ? this.widgetFormSelect.key : '').then(() => {
          _this.undo = true
          _this.redo = false
        })

        this.saveJsonCache()
      })

      this.$refs.widgetForm.$el.addEventListener('contextmenu', (e) => {
        // e.preventDefault()
        
      })

      window.onbeforeunload = (e) => {
        this.saveJsonCache()
      }

      // 从模板导入
      if (this.initFromTemplate) {
        this.handleUpload()
      }
    })
  },
  beforeUnmount () {
    EventBus.$off('on-history-add-' + this.formKey)

    this.saveJsonCache()
  },
  methods: {
    generatePreviewQrcode (url) {
      this.$refs.previewDialog.generateQrcode(url)
    },
    handleRightToggle () {
      if (this.rightHide) {
        removeClass(this.$refs['widgetConfigContainer'].$el, 'hide-status')

        this.rightHide = false
      } else {
        addClass(this.$refs['widgetConfigContainer'].$el, 'hide-status')

        this.rightHide = true
      }
    },
    handleLeftToggle () {
      if (this.leftHide) {
        removeClass(this.$refs['widgetLeftPanel'].$el, 'hide-status')

        this.leftHide = false
      } else {
        addClass(this.$refs['widgetLeftPanel'].$el, 'hide-status')

        this.leftHide = true
      }
    },
    saveJsonCache () {
      this.cache && localStorage.setItem('fmjson'+this.name, JSON.stringify(this.widgetForm))
    },
    removeJsonCache () {
      localStorage.setItem('fmjson'+this.name, JSON.stringify(this.widgetForm))
    },
    initConfig () {
      this.platform = this.widgetForm.config.platform || 'pc'

      this.onStyleUpdate(splitStyleSheets(this.widgetForm.config.styleSheets || ''))

      this.onDataSourceUpdate(this.widgetForm.config.dataSource || [])

      this.onEventScriptUpdate(this.widgetForm.config.eventScript || [])
    },
    handleGoGithub () {
      window.location.href = 'https://github.com/GavinZhuLei/vue-form-making'
    },
    handleConfigSelect (value) {
      this.configTab = value
    },
    handleMoveEnd (evt) {
    },
    handleMoveStart ({oldIndex}) {
    },
    handleMove () {
      return true
    },
    handlePreview () {

      this.$emit('preview', _.cloneDeep(this.widgetForm))

      this.$refs.previewDialog.preview(_.cloneDeep(this.widgetForm), this.platform)
    },
    preivewGetData (data) {
      this.jsonTitle = this.$t('fm.actions.getData')
      this.jsonVisible = true
      this.jsonTemplate = data
      this.$nextTick(() => {

        this.jsonClipboard = new ClipboardJS(document.getElementsByClassName('json-btn')[0])
        this.jsonClipboard.on('success', (e) => {
          ElMessage({
            message: this.$t('fm.message.copySuccess'),
            type: 'success'
          })
        })
        this.jsonCopyValue = JSON.stringify(data)
      })
    },
    handleGenerateJson () {
      this.jsonTitle = this.$t('fm.actions.json')
      this.jsonVisible = true
      this.jsonTemplate = this.widgetForm
      this.$nextTick(() => {
        
        this.jsonClipboard = new ClipboardJS(document.getElementsByClassName('json-btn')[0])
        this.jsonClipboard.on('success', (e) => {
          
          ElMessage({
            message: this.$t('fm.message.copySuccess'),
            type: 'success'
          })

          e.clearSelection()
        })
        this.jsonCopyValue = JSON.stringify(this.widgetForm)
      })
    },
    handleGenerateCode () {
      this.codeVisible = true
      this.htmlTemplate = generateCode(JSON.stringify(this.widgetForm), 'html', this.widgetForm.config.ui)
      this.vueTemplate = generateCode(JSON.stringify(this.widgetForm), 'vue', this.widgetForm.config.ui)
      this.$nextTick(() => {

        this.codeClipboard = new ClipboardJS(document.getElementsByClassName('code-btn')[0])
        this.codeClipboard.on('success', (e) => {
          ElMessage({
            message: this.$t('fm.message.copySuccess'),
            type: 'success'
          })
        })
        this.codeCopyValue = this.codeActiveName == 'vue' ? this.vueTemplate : this.htmlTemplate
      })
    },
    handleUpload () {
      this.$refs.importJsonDialog.open(this.jsonTemplates)
    },
    handleLoadJson (json) {
      try {
        this.setJSON(json)
      } catch (e) {

        ElMessage({
          message: e.message,
          type: 'error'
        })
      }
    },
    handleClear () {
      this.widgetForm = {
        ...this.widgetForm,
        list: [],
      }

      this.widgetFormSelect = {}

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },
    clear () {
      this.handleClear()
    },
    getJSON () {
      return this.widgetForm
    },
    getHtml (type = 'html', ui = 'element') {
      return generateCode(JSON.stringify(this.widgetForm), type, ui)
    },
    setJSON (json) {
      if (typeof json === 'string') {
        json = JSON.parse(json)
      }

      this.widgetForm = _.cloneDeep({
        ...json,
        list: json.list.map(item => UpgradeData(item))
      })

      if (json.list.length> 0) {
        this.widgetFormSelect = this.widgetForm.list[0]
      } else {
        this.widgetFormSelect = {}
      }

      this.initConfig()

      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleInput (val) {
      this.blank = val
    },
    handleField (item) {
      EventBus.$emit('on-field-add-' + this.formKey, item)
    },
    handleUndo () {
      if (this.undo) {
        historyManager.updateLatest(this.widgetForm, (this.widgetFormSelect && this.widgetFormSelect.key) ? this.widgetFormSelect.key : '').then(() => {
          historyManager.undo().then((data) => {
            this.widgetForm = {...data.data}
            this.widgetFormSelect = this._findWidgetItem(this.widgetForm.list, data.key)
            this.undo = data.undo
            this.redo = data.redo

            this.initConfig()
          })
        })
      }
    },
    handleRedo () {
      if (this.redo) {
        historyManager.redo().then((data) => {
          this.widgetForm = {...data.data}
          this.widgetFormSelect = this._findWidgetItem(this.widgetForm.list, data.key)

          this.undo = data.undo
          this.redo = data.redo

          this.initConfig()
        })
      }
    },
    handleFullScreen() {
      this.fullscreen = true
    },
    handleExitFullScreen () {
      this.fullscreen = false
    },
    _findWidgetItem (list, key, type = 'key') {
      const index = list.findIndex(item => item[type] == key)
      
      if (index >= 0) {
        return list[index]
      } else {
        for (let m = 0; m < list.length; m++) {
          const item = list[m]

          if (item.type === 'grid') {

            let findItem = this._findWidgetItem(item.columns, key, type)

            if (findItem.key) {
              return findItem
            }

            for (let i = 0; i < item.columns.length; i++) {

              let findItem = this._findWidgetItem(item.columns[i].list, key, type)
              if (findItem.key) {
                return findItem
              }
            }
          }
          if (item.type === 'table') {
            let findItem = this._findWidgetItem(item.tableColumns, key, type)
            if (findItem.key) {
              return findItem
            }
          }
          if (item.type === 'subform') {
            let findItem = this._findWidgetItem(item.list, key, type)
            if (findItem.key) {
              return findItem
            }
          }
          if (item.type === 'tabs') {

            for (let i = 0; i < item.tabs.length; i++) {
              let findItem = this._findWidgetItem(item.tabs[i].list, key, type)
              if (findItem.key) {
                return findItem
              }
            }
          }
          if (item.type === 'collapse') {
            for (let i = 0; i < item.tabs.length; i++) {
              let findItem = this._findWidgetItem(item.tabs[i].list, key, type)
              if (findItem.key) {
                return findItem
              }
            }
          }
          if (item.type === 'report') {
            for (let r = 0; r < item.rows.length; r++) {

              let findItem = this._findWidgetItem(item.rows[r].columns, key, type)

              if (findItem.key) {
                return findItem
              }

              for (let c = 0; c < item.rows[r].columns.length; c++) {
                let findItem = this._findWidgetItem(item.rows[r].columns[c].list, key, type)
                if (findItem.key) {
                  return findItem
                }
              }
            }
          }
          if (item.type === 'inline') {
            let findItem = this._findWidgetItem(item.list, key, type)
            if (findItem.key) {
              return findItem
            }
          }
          if (item.type === 'dialog') {
            let findItem = this._findWidgetItem(item.list, key, type)
            if (findItem.key) {
              return findItem
            }
          }
          if (item.type === 'card') {
            let findItem = this._findWidgetItem(item.list, key, type)
            if (findItem.key) {
              return findItem
            }
          }
          if (item.type === 'group') {
            let findItem = this._findWidgetItem(item.list, key, type)
            if (findItem.key) {
              return findItem
            }
          }

        }

        return {}
      }
    },
    _loadComponents () {
      this.basicComponents = this.basicComponents.map(item => {
        return {
          ...item,
          name: this.$t(`fm.components.fields.${item.type}`),
          options: (() => {
            let newField = this.fieldConfig.find(o => o.type == item.type)
             
            if (newField) {
              return {...item.options, ...newField.options}
            } else {
              return {...item.options}
            }
          })()
        }
      })
      this.advanceComponents = this.advanceComponents.map(item => {
        return {
          ...item,
          name: this.$t(`fm.components.fields.${item.type}`),
          options: (() => {
            let newField = this.fieldConfig.find(o => o.type == item.type)
             
            if (newField) {
              return {...item.options, ...newField.options}
            } else {
              return {...item.options}
            }
          })()
        }
      })
      this.layoutComponents = this.layoutComponents.map(item => {
        return {
          ...item,
          name: this.$t(`fm.components.fields.${item.type}`),
          options: (() => {
            let newField = this.fieldConfig.find(o => o.type == item.type)
             
            if (newField) {
              return {...item.options, ...newField.options}
            } else {
              return {...item.options}
            }
          })()
        }
      })

      this.collectionComponents = this.collectionComponents.map(item => {
        return {
          ...item,
          name: this.$t(`fm.components.fields.${item.type}`),
          options: (() => {
            let newField = this.fieldConfig.find(o => o.type == item.type)
             
            if (newField) {
              return {...item.options, ...newField.options}
            } else {
              return {...item.options}
            }
          })()
        }
      })

      this.customComponents = this.customFields.map(item => {
        return {
          ...item,
          type: 'custom',
          options: (() => {
            let newField = this.fieldConfig.find(o => o.type == item.type)
             
            if (newField) {
              return {...item.options, ...newField.options}
            } else {
              return {...item.options}
            }
          })()
        }
      })
    },
    onStyleUpdate (sheets) {
      let head = '.fm-' + this.formKey + ' '

      updateStyleSheets(sheets, head)

      this.styleSheetsArray = splitSheetName(sheets)
    },
    onDataSourceUpdate (dataSource) {
      this.dataSourceArray = dataSource.map(item => ({
        value: item.key,
        label: item.name,
        args: item.args ? Object.fromEntries(item.args.map(o => [o, ''])) : {}
      }))
    },
    onEventScriptUpdate (eventScript) {
      this.eventScriptArray = eventScript.map(item => ({
        value: item.key,
        label: item.name
      }))
    },
    onEventScriptConfirm (eventObj) {
      this.$refs.widgetConfig.setEvent(eventObj)

      this.$nextTick(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      })
    },
    handlePlatform (platform) {
      this.widgetForm.config.platform = this.platform = platform
    },
    handleExport () {

      const fileName = (new Date().getTime()) + '.' + this.codeActiveName

      const fileData = this.codeActiveName == 'vue' ? this.vueTemplate : this.htmlTemplate

      this._exportFile(fileData, fileName)
    },
    handleExportJSON () {
      this._exportFile(JSON.stringify(this.jsonTemplate), (new Date().getTime()) + '.json')
    },
    handleEventAdd (name) {
      this.$refs.formConfig.editScript(name)
    },
    handleEventEdit ({eventName, functionKey}) {
      this.$refs.formConfig.editScript(eventName, functionKey)
    },
    handleEventRemove (eventName) {
      this.widgetFormSelect.events[eventName] = ''
    },
    _exportFile (data, fileName) {
      let blob = new Blob([data], {
        type: 'application/octet-stream'
      })

      if(navigator.msSaveOrOpenBlob ){
        navigator.msSaveOrOpenBlob(blob, fileName);
      }else{
        // Create download link element
        let downloadLink = document.createElement("a");
        // Create a link to the file
        downloadLink.href = window.URL.createObjectURL(blob);
        // Setting the file name
        downloadLink.download = fileName;

        downloadLink.style.display = 'none'

        document.body.appendChild(downloadLink);
        
        //triggering the function
        downloadLink.click();

        document.body.removeChild(downloadLink);
      }
    },
    setSelect (field) {
      let selectWidget = this._findWidgetItem(this.widgetForm.list, field, 'model')

      if (selectWidget) {
        this.widgetFormSelect = selectWidget
      }
    },
    changeConfigTab (tab) {
      this.configTab = tab
    },
    onSelectWidget (key) {
      
      let selectWidget = this._findWidgetItem(this.widgetForm.list, key, 'key')

      if (selectWidget) {
        this.isScrollTo = false
        this.widgetFormSelect = selectWidget
      }

      setTimeout(() => {
        this.$refs.widgetForm.scrollTo()
      }, 200)
    },
    getModelNode () {
      return this.modelNode
    },
    isMobile () {
      return this.platform === 'mobile'
    },
    getFormModels () {
      return getModels(this.widgetForm.list)
    },
    getFormFields () {
      return this._getFormFields(this.widgetForm.list)
    },
    _getFormFields (list, group = '', parent = '') {
      let currentNode = []

      for (let i = 0; i < list.length; i++) {

        if (!list[i].type) continue

        const curLabel = parent 
          ? [parent, `<${this.$t('fm.components.fields.' + list[i].type)}>${list[i].model ?? ''}`].join(' / ')
          : `<${this.$t('fm.components.fields.' + list[i].type)}>${list[i].model ?? ''}`

        currentNode.push({
          id: list[i].model ? ( group ? group + '.' + list[i].model : list[i].model ) : list[i].key,
          label: curLabel,
          icon: list[i].icon,
          type: list[i].type,
          model: list[i].model,
          children: [],
          dataBind: list[i].options?.dataBind,
          options: list[i].options,
          disabled: (list[i].model ? false : true) || ['grid', 'report', 'tabs', 'collapse', 'inline', 'card', 'divider', 'alert'].includes(list[i].type),
          hideDisabled: list[i].model ? false : true,
          setdataDisabled: (list[i].model ? false : true) || ['grid', 'report', 'tabs', 'collapse', 'inline', 'card', 'divider', 'alert', 'button', 'link'].includes(list[i].type),
          remoteOptionDisabled: !(list[i].options.remote && list[i].options.remoteType == 'datasource'),
          dialogDisabled: list[i].type !== 'dialog'
        })

        if (list[i].type == 'grid') {
          currentNode[i].children = this._getFormFields(list[i].columns, group, curLabel)
        }
        if (list[i].type == 'col') {
          currentNode[i].children = this._getFormFields(list[i].list, group, curLabel)
        }
        if (list[i].type == 'report') {
          let reportList = []

          for (let r = 0; r < list[i].rows.length; r++) {
            for (let c = 0; c < list[i].rows[r].columns.length; c++) {
              let td = list[i].rows[r].columns[c]

              if (!td.options.invisible) {
                reportList.push(td)
              }
            }
          }

          currentNode[i].children = this._getFormFields(reportList, group, curLabel)
        }
        if (list[i].type == 'td') {
          currentNode[i].children = this._getFormFields(list[i].list, group, curLabel)
        }
        if (list[i].type == 'tabs') {
          for (let t = 0; t < list[i].tabs.length; t++) {
            currentNode[i].children.push({
              label: list[i].tabs[t].label,
              children: [],
              disabled: true,
              id: Math.random().toString(36).slice(-8),
              setdataDisabled: true,
              remoteOptionDisabled: true,
              dialogDisabled: true
            })

            currentNode[i].children[t].children = this._getFormFields(list[i].tabs[t].list, group, curLabel)
          }
        }
        if (list[i].type == 'collapse') {
          for (let t = 0; t < list[i].tabs.length; t++) {
            currentNode[i].children.push({
              label: list[i].tabs[t].title,
              children: [],
              disabled: true,
              id: Math.random().toString(36).slice(-8),
              setdataDisabled: true,
              remoteOptionDisabled: true,
              dialogDisabled: true
            })

            currentNode[i].children[t].children = this._getFormFields(list[i].tabs[t].list, group, curLabel)
          }
        }
        if (list[i].type == 'inline') {
          currentNode[i].children = this._getFormFields(list[i].list, group, curLabel)
        }
        if (list[i].type == 'table') {
          let curGroup = group ? group + '.' + list[i].model : list[i].model
          currentNode[i].children = this._getFormFields(list[i].tableColumns, curGroup, curLabel)
        }
        if (list[i].type == 'subform') {
          let curGroup = group ? group + '.' + list[i].model : list[i].model
          currentNode[i].children = this._getFormFields(list[i].list, curGroup, curLabel)
        }
        if (list[i].type == 'dialog') {
          let curGroup = group ? group + '.' + list[i].model : list[i].model
          currentNode[i].children = this._getFormFields(list[i].list, curGroup, curLabel)
        }
        if (list[i].type == 'card') {
          currentNode[i].children = this._getFormFields(list[i].list, group, curLabel)
        }
        if (list[i].type == 'group') {
          let curGroup = group ? group + '.' + list[i].model : list[i].model
          currentNode[i].children = this._getFormFields(list[i].list, curGroup, curLabel)
        }
      }

      return currentNode
    },
    getDataSourceArray () {
      return _.cloneDeep(this.dataSourceArray)
    }
  },
  watch: {
    '$i18n.locale': function (val) {
      this._loadComponents()
    },
    codeActiveName (val) {
      this.codeCopyValue = this.codeActiveName == 'vue' ? this.vueTemplate : this.htmlTemplate
    },
    widgetFormSelect (val) {
      val.key && this.$refs.outlineRef?.setCurrentKey(val.key, this.isScrollTo)

      this.isScrollTo = true

      this.modelNode = findModelNodeString(this.widgetForm.list, 'key', val.key)
    },
    activeLeft (val) {
      if (val == 'outline') {
        this.widgetFormSelect?.key && this.$refs.outlineRef?.setCurrentKey(this.widgetFormSelect.key, this.isScrollTo)
      }
    }
  }
}
</script>

<style lang="scss">
.widget-empty{
  background-position: 50%;
}
</style>
