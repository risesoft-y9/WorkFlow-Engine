<template>
  <div :style="{width: formData.config && formData.config.width}" class="fm-form"
    :class="{
      ['fm-'+formStyleKey]: true,
      'is-print': printRead
    }"
  >
    <el-form :ref="formRef"
      :key="formKey"
      v-if="formShow"
      :class="{
        [formData.config && formData.config.customClass]:  (formData.config && formData.config.customClass) ? true : false,
        'no-label-form': formData.config && (formData.config.labelWidth == 0)
      }"
      :size="formData.config && formData.config.size"
      :model="models" 
      :rules="rules" 
      :label-position="formData.config && formData.config.labelPosition" 
      :disabled="!edit"
      :label-width="formData.config && formData.config.labelWidth + 'px'"
      :validate-on-rule-change="false"
      :label-suffix="formData.config?.labelSuffix ? ' : ' : ' '"
      >

      <template v-for="element in formData.list">
        <generate-col-item
          v-if="element.type == 'grid'"
          :key="element.key"
          :model="models"
          :rules="rules"
          :element="element"
          :remote="remote"
          :blanks="blanks"
          :display="displayFields"
          @input-change="onInputChange"
          :edit="edit"
          :remote-option="remoteOption"
          :platform="platform"
          :preview="preview"
          :container-key="containerKey"
          :data-source-value="dataSourceValue"
          :event-function="eventFunction"
          :print-read="printRead"
          :form-component="$refs[formRef]"
          :group="''"
          :field-node="''"
        >
          <template v-slot:[blank.name]="scope" v-for="blank in blanks">
            <slot :name="blank.name" :model="scope.model"></slot>
          </template>
        </generate-col-item>

        <generate-form-item
          v-else
          :key="element.key"
          :models="models"
          :rules="rules"
          :widget="element"
          :remote="remote"
          :blanks="blanks"
          :display="displayFields"
          @input-change="onInputChange"
          :edit="edit"
          :remote-option="remoteOption"
          :platform="platform"
          :preview="preview"
          :container-key="containerKey"
          :data-source-value="dataSourceValue"
          :event-function="eventFunction"
          :print-read="printRead"
          :form-component="$refs[formRef]"
          :group="''"
          :field-node="''"
        >
          <template v-slot:[blank.name]="scope" v-for="blank in blanks">
            <slot :name="blank.name" :model="scope.model"></slot>
          </template>
        </generate-form-item>
      </template>
    </el-form>
  </div>
</template>

<script>
import GenerateFormItem from './GenerateFormItem.vue'
import {loadJs, updateStyleSheets, splitStyleSheets, clearStyleSheets, consoleError, getBindModels } from '../util/index.js'
import { updateClassName } from '../util/reuse-methods.js'
import { EventBus } from '../util/event-bus.js'
import _ from 'lodash'
import axios from 'axios'
import { defineAsyncComponent } from 'vue'
import { exportPDF } from '../util/export.js'
import { ruleToFunction } from '../util/rule-funcs.js'
import GenerateColItem from './GenerateColItem.vue'
export default {
  name: 'fm-generate-form',
  components: {
    GenerateFormItem,
    GenerateColItem
  },
  props: {
    data: {
      type: Object,
      default: () => ({
        "list": [],
        "config": {
          "labelWidth": 100,
          "labelPosition": "right",
          "size": "small",
          "customClass": "",
          "ui": "element",
          "layout": "horizontal",
        }
      })
    },
    remote: {
      type: Object,
      default: () => ({})
    },
    value: {
      type: Object,
      default: () => ({})
    },
    edit: {
      type: Boolean,
      default: true
    },
    printRead: {
      type: Boolean,
      default: false
    },
    remoteOption: {
      type: Object,
      default: () => ({})
    },
    preview: {
      type: Boolean,
      default: false
    },
    platform: {
      type: String,
      default: 'pc'
    }
  },
  emits: ['on-change'],
  data () {
    return {
      formData: {},
      models: {},
      rules: {},
      blanks: [],
      displayFields: {},
      dataBindFields: {},
      generateShow: false,
      resetModels: {},
      formKey: Math.random().toString(36).slice(-8),
      formStyleKey: Math.random().toString(36).slice(-8),
      formValue: this.value,
      formShow: false,
      formRef: Math.random().toString(36).slice(-8) + 'Form',
      containerKey: Math.random().toString(36).slice(-8),
      dataSourceValue: [],
      eventFunction: {},
      instanceObject: {},
      dataSourceInterface: [],
      formHideFields: []
    }
  },
  created () {
    this._initForm()
  },
  mounted () {
    this.$nextTick(() => {
      this.eventFunction['mounted'] && this.eventFunction['mounted']()
    })
  },
  beforeUnmount () {
    let head = '.fm-' + this.formStyleKey + ' '
    clearStyleSheets(head)
  },
  provide() {
    return {
      'generateComponentInstance': this.generateComponentInstance,
      'deleteComponentInstance': this.deleteComponentInstance,
      'eventScriptConfig': this.getEventScriptConfig,
      'getFormComponent': this.getFormComponent,
      'onChange': this._changeFormValue,
      'onFormDisabled': this.disabled,
      'formHideFields': this.formHideFields,
      'onFormHide': this.hide,
      'onFormDisplay': this.display
    }
  },
  methods: {
    getFormComponent () {
      return this.$refs[this.formRef]
    },
    _changeFormValue (value, field) {
      this.models[field] = value

      this.$emit('on-change', field, value, this.models)
    },
    _initForm () {
      this.formShow = false
      this.formData = _.cloneDeep(this.data)

      this.models = {}
      this.rules = {}
      this.blanks = []
      this.displayFields = {}
      this.dataBindFields = {}
      this.resetModels = {}
      this.dataSourceValue = []
      this.eventFunction = []
      this.dataSourceInterface = []

      if (Object.keys(this.data).length) {
        this.generateModel(this.formData.list)
      } else {
        this.generateModel([])
      }

      this.resetModels = _.cloneDeep(this.models)

      this.models = {...this.models}

      return new Promise((resolve) => {
        this.$nextTick(() => {
          this.formShow = true

          if (this.formData.config && this.formData.config.styleSheets) {
            let head = '.fm-' + this.formStyleKey + ' '

            updateStyleSheets(splitStyleSheets(this.formData.config.styleSheets), head)
          }

          this.loadDataSource()

          this.loadEvents()

          this.$nextTick(() => {
            this.eventFunction['refresh'] && this.eventFunction['refresh']()

            resolve()
          })
        })
      })
    },
    getEventScriptConfig () {
      return this.data?.config?.eventScript ?? []
    },
    loadEvents () {
      if (this.formData.config && this.formData.config.eventScript) {
        for (let i = 0; i < this.formData.config.eventScript.length; i++) {
          let currentScript = this.formData.config.eventScript[i]

          if (currentScript.type === 'rule') {
            // 加载异步方法
            // console.log(ruleToFunction(currentScript.rules))
            const AsyncFunction = Object.getPrototypeOf(async function(){}).constructor
            this.eventFunction[currentScript.key] = new AsyncFunction(ruleToFunction(currentScript.rules)).bind(this)
          } else {
            this.eventFunction[currentScript.key] = Function(currentScript.func).bind(this)
          }
        }
      }
    },
    triggerEvent (eventName, args) {
      if (this.formData.config && this.formData.config.eventScript) {
        let eventScript = this.formData.config.eventScript.find(item => item.name == eventName)

        if (eventScript) {
          return this.eventFunction[eventScript.key](args)
        }
      }
    },
    loadDataSource () {
      for (let i = 0; i < this.dataSourceInterface.length; i++) {
        let curRequest = this.dataSourceInterface[i]
        let requestObj = this.formData.config.dataSource.find(item => item.key == curRequest.key)
        if (requestObj && requestObj.auto)
        requestObj.name && this.sendRequest(requestObj.name, curRequest.args).then(data => {
          curRequest.fields.forEach(field => {
            let curKey = field + '.' + curRequest.key
            let sourceValue = this.dataSourceValue.find(item => item.key === curKey)

            if (sourceValue) {
              sourceValue.value = data
            } else {
              this.dataSourceValue.push({
                key: curKey, 
                value: data
              })
            }
          })
        })
      }
      // 处理需要初始化请求但没有进行绑定的数据源
      if (this.formData.config?.dataSource?.length > 0) {
        for (let i = 0; i < this.formData.config.dataSource.length; i++) {
          let currentDataSource = this.formData.config.dataSource[i]

          if (currentDataSource.auto && this.dataSourceInterface.findIndex(item => item.key == currentDataSource.key) < 0) {
            this.sendRequest(currentDataSource.name, {})
          }
        }
      }
    },

    refreshFieldDataSource (field, args) {
      let curRequest = this.dataSourceInterface.find(item => item.fields.includes(field))

      return new Promise((resolve, reject) => {
        if (curRequest) {
          let requestName = this.formData.config.dataSource.find(item => item.key == curRequest.key)?.name
          requestName && this.sendRequest(requestName, {...curRequest.args, ...args}).then(data => {
            let curKey = field + '.' + curRequest.key
            let sourceValue = this.dataSourceValue.find(item => item.key === curKey)

            if (sourceValue) {
              sourceValue.value = data
            } else {
              this.dataSourceValue.push({
                key: curKey, 
                value: data
              })
            }

            resolve(data)
          }).catch(() => {
            reject()
          })
        } else {
          resolve()
        }
      })
    },

    getFieldDataSource (field) {
      let curRequest = this.dataSourceInterface.find(item => item.fields.includes(field))

      if (curRequest) {
        let curKey = field + '.' + curRequest.key

        let sourceValue = this.dataSourceValue.find(item => item.key === curKey)

        if (sourceValue) {
          return sourceValue.value
        }
      }

      return null
    },
    
    sendRequest(name, args = {}) {
      return new Promise((resolve, reject) => {
        let currentDataSource = this.formData.config.dataSource.find(item => item.name === name)

        if (currentDataSource) {
          let options = {
            method: currentDataSource.method,
            url: currentDataSource.url,
            headers: currentDataSource.headers,
            params: currentDataSource.params
          }

          //请求发送前处理函数
          if (currentDataSource.requestFunc) {
            const requestDynamicFunc = Function('config, args', currentDataSource.requestFunc).bind(this)

            options = requestDynamicFunc(options, args)
          }

          axios(options).then(res => {

            let data = res

            if (currentDataSource.responseFunc) {
              const dynamicFunc = Function('res, args', currentDataSource.responseFunc).bind(this)

              data = dynamicFunc(res.data, args)

              resolve(data)
            } else {
              resolve(res.data)
            }
          }).catch((error) => {
            //请求错误处理函数
            if (currentDataSource.errorFunc) {
              const errorDynamicFunc = Function('error', currentDataSource.errorFunc).bind(this)

              errorDynamicFunc(error)
            }

            reject(error)
          })
        }
      })
    },
    generateSubformModel (subName, genList, dataBindFields) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].options.hidden) {
          this.formHideFields.push(`${subName}.${genList[i].model}`)
        }

        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this.generateSubformModel(subName, item.list, dataBindFields)
          })
        } else {

          if (genList[i].type === 'blank') {
            this.blanks.push({
              name: genList[i].model
            })
          }

          if (genList[i].options.dataBind) {
            
              dataBindFields[genList[i].model] = true
            
          }

          genList[i].tableColumns && genList[i].tableColumns.length && genList[i].tableColumns.forEach(item => {
            if (item.type === 'blank') {
              this.blanks.push({
                name: item.model
              })
            }

            if (item.options.dataBind && genList[i].options.dataBind) {
              dataBindFields[genList[i].model][item.model] = true
            }

            if (item.options.hidden) {
              this.formHideFields.push(`${subName}.${genList[i].model}.${item.model}`)
            }

            // 处理 rules
            this._generateRules(`${subName}.${genList[i].model}.${item.model}`, item.rules)

            // 处理子表单中的DataSource
            this._generateDataSource(item, `${subName}.${genList[i].model}.${item.model}`)
          })

         
          this._generateRules(`${subName}.${genList[i].model}`, genList[i].rules)

          // 处理子表单中的DataSource
          this._generateDataSource(genList[i], `${subName}.${genList[i].model}`)
        }
      }
    },
    generateDialogModel (dialogName, genList, dataBindFields) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].options.hidden) {
          this.formHideFields.push(`${dialogName}.${genList[i].model}`)
        }
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this.generateDialogModel(dialogName, item.list, dataBindFields)
          })
        } else {

          if (genList[i].type === 'blank') {
            this.blanks.push({
              name: genList[i].model
            })
          }

          if (genList[i].options.dataBind) {
              dataBindFields[genList[i].model] = true
          }

         
          genList[i].tableColumns && genList[i].tableColumns.length && genList[i].tableColumns.forEach(item => {
            if (item.type === 'blank') {
              this.blanks.push({
                name: item.model
              })
            }

            if (item.options.dataBind && genList[i].options.dataBind) {
              dataBindFields[genList[i].model][item.model] = true
            }

            if (item.options.hidden) {
              this.formHideFields.push(`${dialogName}.${genList[i].model}.${item.model}`)
            }

            // 处理 rules
            this._generateRules(`${dialogName}.${genList[i].model}.${item.model}`, item.rules)

            // 处理子表单中的DataSource
            this._generateDataSource(item, `${dialogName}.${genList[i].model}.${item.model}`)
          })

          this._generateRules(`${dialogName}.${genList[i].model}`, genList[i].rules)

          // 处理弹框中的DataSource
          this._generateDataSource(genList[i], `${dialogName}.${genList[i].model}`)
        }
      }
    },
    generateGroupModel (groupName, genList, dataBindFields) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].options.hidden) {
          this.formHideFields.push(`${groupName}.${genList[i].model}`)
        }
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this.generateGroupModel(groupName, item.list, dataBindFields)
          })
        } else {

          if (genList[i].type === 'blank') {
            this.blanks.push({
              name: genList[i].model
            })
          }

          if (genList[i].options.dataBind) {
              dataBindFields[genList[i].model] = true
          }

          

          genList[i].tableColumns && genList[i].tableColumns.length && genList[i].tableColumns.forEach(item => {
            if (item.type === 'blank') {
              this.blanks.push({
                name: item.model
              })
            }

            if (item.options.dataBind && genList[i].options.dataBind) {
              dataBindFields[genList[i].model][item.model] = true
            }

            if (item.options.hidden) {
              this.formHideFields.push(`${groupName}.${genList[i].model}.${item.model}`)
            }

            // 处理 rules
            this._generateRules(`${groupName}.${genList[i].model}.${item.model}`, item.rules)

            // 处理子表单中的DataSource
            this._generateDataSource(item, `${groupName}.${genList[i].model}.${item.model}`)
          })

          this._generateRules(`${groupName}.${genList[i].model}`, genList[i].rules)

          // 处理弹框中的DataSource
          this._generateDataSource(genList[i], `${groupName}.${genList[i].model}`)
        }
      }
    },
    generateModel (genList) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].options.hidden) {
          this.formHideFields.push(genList[i].model)
        }
        if (genList[i].type === 'grid') {
          this.displayFields[genList[i].model] = !genList[i].options.hidden

          genList[i].columns.forEach(item => {
            this.generateModel(item.list)
          })
        } else {
          if (Object.keys(this.formValue).indexOf(genList[i].model) >= 0) {
            this.models[genList[i].model] = this.formValue[genList[i].model]
            
            this.displayFields[genList[i].model] = !genList[i].options.hidden

            if (genList[i].type === 'blank') {
              this.blanks.push({
                name: genList[i].model
              })
            }

          } else {
            if (genList[i].type === 'blank') {
              // bound the default value
              this.models[genList[i].model] = genList[i].options.defaultType === 'String' ? '' : (genList[i].options.defaultType === 'Object' ? {} : [])
              this.displayFields[genList[i].model] = !genList[i].options.hidden

              this.blanks.push({
                name: genList[i].model
              })
            } else {
              this.models[genList[i].model] = _.cloneDeep(genList[i].options.defaultValue)
              this.displayFields[genList[i].model] = !genList[i].options.hidden
            }
          }

          if ((Object.keys(genList[i].options).indexOf('dataBind') < 0 || genList[i].options.dataBind) && genList[i].key && genList[i].model) {
            
              this.dataBindFields[genList[i].model] = true
          }

         
          genList[i].tableColumns && genList[i].tableColumns.length && genList[i].tableColumns.forEach(item => {
            if (item.type === 'blank') {
              this.blanks.push({
                name: item.model
              })
            }

            if (item.options.dataBind && genList[i].options.dataBind) {
              this.dataBindFields[genList[i].model][item.model] = true
            }

            if (item.options.hidden) {
              this.formHideFields.push(`${genList[i].model}.${item.model}`)
            }

            // 处理 rules
            this._generateRules(`${genList[i].model}.${item.model}`, item.rules)

            // 处理子表单中的DataSource
            this._generateDataSource(item, `${genList[i].model}.${item.model}`)
          })

          this._generateRules(genList[i].model, genList[i].rules)

          // 处理DataSource
          this._generateDataSource(genList[i], genList[i].model)
        }
      }
    },
    _generateDataSource (element, model) {
      if (element.options.remoteType === 'datasource' && element.options.remoteDataSource) {
        this._setDataSourceInterface(model, element.options.remoteArgs, element.options.remoteDataSource)
      }
      if ((element.type == 'imgupload' || element.type == 'fileupload')
        && element.options.tokenType == 'datasource' && element.options.tokenDataSource
      ) {
        this._setDataSourceInterface(model, element.options.remoteArgs, element.options.tokenDataSource)
      }
    },
    _generateRules (model, rules) {
      if (this.rules[model]) {
        this.rules[model] = [
          ...this.rules[model],
          ...(rules ? rules.map(im => {
            if (im.pattern) {
              return {...im, pattern: eval(im.pattern)}
            } else if (im.func) {

              const validatorFunc = Function('rule', 'value', 'callback', im.func).bind(this)

              return {...im, validator: validatorFunc}
            } else {
              return {...im}
            }
          }) : [])
        ]
      } else {
        
        this.rules[model] = [
          ...(rules ? rules.map(im => {
            if (im.pattern) {
              return {...im, pattern: eval(im.pattern)}
            } else if (im.func) {
              const validatorFunc = Function('rule', 'value', 'callback', im.func).bind(this)

              return {...im, validator: validatorFunc}
            } else {
              return {...im}
            }
          }) : [])
        ]
      }
    },
    _setDataSourceInterface (field, args, key) {
      let argsObj
      if (typeof args == 'string') {
        argsObj =  (new Function('"use strict";return (' + (args || '{}') + ')').bind(this))()
      } else {
        argsObj =  args
      }

      let findCurInterfaceIndex = this.dataSourceInterface.findIndex(item => item.key == key && _.isEqual(item.args, argsObj))

      if (findCurInterfaceIndex >= 0) {
        this.dataSourceInterface[findCurInterfaceIndex].fields.push(field)
      } else {
        this.dataSourceInterface.push({
          key: key,
          args: argsObj,
          fields: [field]
        })
      }
    },
    _setSubDisabled (genList, fields, disabled) {
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._setSubDisabled(item.list, fields, disabled)
          })
        } else {
          if (fields.length === 1) {
            if (genList[i].model === fields[0]) {
              genList[i].options.disabled = disabled
            }
          } else {
            if (genList[i].model !== fields[0]) continue

            let newFields = [...fields]
            newFields.splice(0, 1)
            
          }
        }
      }
    },
    _setSubOptions (genList, fields, opts) {
      for (let i = 0; i < genList.length; i++) {
        if (fields.length === 1) {
          if (genList[i].model === fields[0]) {
            Object.keys(opts).forEach(key => {
              genList[i].options[key] = opts[key]
            })
          } else {
            if (genList[i].type === 'grid') {
              genList[i].columns.forEach(item => {
                this._setSubOptions(item.list, fields, opts)
              })
            }
          }
        } else {

          if (genList[i].type === 'grid') {
            genList[i].columns.forEach(item => {
              this._setSubOptions(item.list, fields, opts)
            })
          }else {
            let newFields = [...fields]
            newFields.splice(0, 1)
          }
        }
      }
    },
    validate (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      return new Promise((resolve, reject) => {
        if (fields) {
          this.$refs[this.formRef].validateField(fields, (valid, error) => {
            if (valid) {
              resolve()
            } else {
              reject(error)
            }
          })
        } else {
          this.$refs[this.formRef].validate((valid, error) => {
            if (valid) {
              resolve()
            } else {
              reject(error)
            }
          })
        }
      })
    },
    getData (isValidate = true) {
      return new Promise((resolve, reject) => {
        if (isValidate) {
          this.$refs[this.formRef].validate(valid => {
            if (valid) {
              
              resolve(getBindModels(this.models, this.dataBindFields))
            } else {
              reject(new Error(this.$t('fm.message.validError')).message)
            }
          })
        } else {
          resolve(getBindModels(this.models, this.dataBindFields))
        }
      })
    },
    reset () {
      return new Promise((resolve) => {
        this.setData(_.cloneDeep(this.resetModels)).then(() => {
          setTimeout(() => {
            this.$refs[this.formRef].clearValidate()
            resolve()
          })
        })
      })
    },
    onInputChange (value, field) {
      this.$emit('on-change', field, value, this.models)
      this.$emit(`on-${field}-change`, value)
    },
    display (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        if (this.formHideFields.includes(field)) {
          let index = this.formHideFields.findIndex(item => item == field)
          this.formHideFields.splice(index, 1)
        }
      })
    },
    hide (fields) {
      if (typeof fields === 'string') {
        fields = [fields]
      }

      fields.forEach(field => {
        if (!this.formHideFields.includes(field)) {
          this.formHideFields.push(field)
        }
      })
    },
    disabled (fields, disabled) {
      if (typeof fields === 'string') {
        fields = [fields]
      }

      fields.forEach(item => {
        let currentComponent = this.instanceObject[item]

        if (currentComponent) {
          if (Array.isArray(currentComponent)) {
            currentComponent.forEach(cc => {
              cc.$parent?.disabledElement(disabled)
            })
          } else {
            currentComponent.$parent?.disabledElement(disabled)
          }
        } else {
          this._setSubDisabled(this.formData.list, item.split('.'), disabled)
        }
      })
    },
    clearValidate (writeField) {//Y9移除writeField之外字段的必填验证
      this._clearValidate(this.formData.list,writeField)
    },
    _clearValidate (genList,writeField) {//Y9移除writeField之外字段的必填验证
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._clearValidate(item.list,writeField)
          })
        } else {
          if (writeField.indexOf(genList[i].model) < 0) {
            let field = [];
            field.push(genList[i].model);
            this.setRules (genList[i].model, [{required: false}]);
            this.setOptions (field, {required: false});
          }
        }
      }
    },
    clearAllValidate () {//Y9移除所有字段必填验证
      this._clearAllValidate(this.formData.list)
    },
    _clearAllValidate (genList) {//Y9移除所有字段必填验证
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._clearAllValidate(item.list)
          })
        }else {
          let field = [];
          field.push(genList[i].model);
          this.setRules (genList[i].model, [{required: false}]);
          this.setOptions (field, {required: false});
        }
      }
    },
    disabledAll(disabled){//Y9表单所有字段设置是否禁用
      this._disabledAll(this.formData.list,disabled);
    },  
    _disabledAll(genList,disabled){//Y9表单所有字段设置是否禁用
      for (let i = 0; i < genList.length; i++) {
        if (genList[i].type === 'grid') {
          genList[i].columns.forEach(item => {
            this._disabledAll(item.list, disabled)
          })
        } else {
          //this.$set(genList[i].options, 'disabled', disabled)
          genList[i].options.disabled = disabled;
        }
      }
    },
    addClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.formData.list, item.split('.'), className, 'add')
      })
    },
    removeClassName (fields, className) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        updateClassName(this.formData.list, item.split('.'), className, 'remove')
      })
    },
    async refresh () {
      await this._initForm()
    },
    setData (value) {
      const _setNestedValue = (currentObj, keys, value) => {
        const key = keys.shift()

        if (keys.length === 0) {
          currentObj[key] = value
        } else {
          if (key in currentObj) {
            if (Array.isArray(currentObj[key])) {
              if (Number.isNaN(Number(keys[0]))) {
                currentObj[key].forEach(cur => {
                  _setNestedValue(cur, [...keys], value)
                })
              } else {
                const indexKey = keys.shift()
                currentObj[key].length > Number(indexKey) && _setNestedValue(currentObj[key][Number(indexKey)], [...keys], value)
              }
            } else {
              _setNestedValue(currentObj[key], [...keys], value)
            }
          }
        }
      }

      return new Promise((resolve) => {
        this.$nextTick(() => {
          Object.keys(value).forEach(item => {
            _setNestedValue(this.models, item.split('.'), value[item])
          })
          resolve()
        })
      })
    },
    getComponent (name) {
      console.log(111,this.instanceObject)
      if (this.instanceObject[name]) {
        return this.instanceObject[name]
      } else {
        consoleError('No component instance found with ' + name)
        return null
      }
    },
    getValues () {
      return _.cloneDeep(this.models)
    },
    getValue (fieldName) {
      const resModels = _.cloneDeep(this.models)

      let fieldArray = fieldName.split('.')

      const _getFieldRes = (res, array) => {
        if (array.length === 1) {
          return res[array[0]]
        } else {
          let newRes = res[array[0]]

          let newFields = [...array]
          newFields.splice(0, 1)

          return _getFieldRes(newRes, newFields)
        }
      }

      return _getFieldRes(resModels, fieldArray)
    },
    setRules (field, rules) {

      this.rules[field] = [...rules]

      this.$nextTick(() => {
        if (field?.split('.').length > 1) {
          const tableRowLength = this.getValue(field.split('.')[0])?.length
          if (tableRowLength) {
            for (let i= 0; i < tableRowLength; i++) {
              this.$refs[this.formRef].validateField([`${field.split('.')[0]}.${i}.${field.split('.')[1]}`])
            }
          }
        } else {
          this.$refs[this.formRef].validateField([field])
        }
      })
    },
    setOptions (fields, options) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(item => {
        this._setSubOptions(this.formData.list, item.split('.'), options)
      })
    },
    generateComponentInstance (key, instance) {
      if (this.instanceObject[key]) {
        if (Array.isArray(this.instanceObject[key])) {
          this.instanceObject[key] = [...this.instanceObject[key], instance]
        } else {
          this.instanceObject[key] = [this.instanceObject[key], instance]
        }
      } else {
        this.instanceObject[key] = instance
      }
    },
    deleteComponentInstance (key) {
      if (this.instanceObject[key]) {
        delete this.instanceObject[key]
      }
    },
    setOptionData (fields, data) {
      if (typeof fields === 'string') {
        fields = [fields]
      }
      fields.forEach(field => {
        const curRef = this.instanceObject[field]
        curRef?.$parent?.loadOptions(data)
      })
    },
    exportPDF () {
      if (this.printRead) {
        return exportPDF(document.querySelector('.fm-'+this.formStyleKey))
      } else {
        return Promise.reject()
      }
    },
    openDialog (dialogField) {
      const dialogComponent = this.getComponent(dialogField)

      if (dialogComponent) {
        dialogComponent.open()
      }
    },
    closeDialog (dialogField) {
      const dialogComponent = this.getComponent(dialogField)

      if (dialogComponent) {
        dialogComponent.close()
      } 
    }
  }
}
</script>

<style lang="scss">
</style>
