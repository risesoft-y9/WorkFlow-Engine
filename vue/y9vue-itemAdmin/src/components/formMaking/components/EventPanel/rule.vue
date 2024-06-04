<template>
  <div class="fm-rules-config-container">
    <draggable
      :list="ruleList" 
      v-bind="{group:'peo2', ghostClass: 'ghost11',animation: 200, handle: '.drag-widget'}"
      :no-transition-on-drag="true"
      item-key="key"
      class="widget-form-list"
    >
      <template #item="{element, index}">
        <el-card class="rule-card" shadow="never" size="small">
          <template #header>
            <div class="card-header">
              <el-tag>{{$t('fm.rules.actions.'+element.action)}}</el-tag>
              <div>
                <el-popconfirm width="200" :title="$t('fm.rules.message.confirmDelete')" @confirm="handleActonRemove(index)">
                  <template #reference>
                    <div class="widget-view-delete widget-view-action">
                      <i class="fm-iconfont icon-delete"></i>
                    </div>
                  </template>
                </el-popconfirm>
                
                <div class="widget-view-drag widget-view-action">
                  <i class="fm-iconfont icon-icon_bars drag-widget"></i>
                </div>
              </div>
            </div>
          </template>
          <el-form label-width="100px" style="margin-right: 20px;" v-model="element.options" class="rule-card-form" >
            <!-- {{element}} -->
            <el-form-item :label="$t('fm.rules.label.field')" v-if="Object.keys(element.options).includes('fields')">
              <FieldsSelect :defaultExpand="true" :multiple="true" v-model="element.options.fields" :action="element.action"></FieldsSelect>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.field')" v-if="Object.keys(element.options).includes('field')">
              <FieldsSelect :defaultExpand="element.action != 'openDialog' && element.action != 'closeDialog'" :multiple="false" v-model="element.options.field" :action="element.action" @on-data-change="handleFieldDataChange($event, element)"></FieldsSelect>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.functionName')" v-if="Object.keys(element.options).includes('functionName')">
              <events-select v-model="element.options.functionName"></events-select>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.functionParams')" v-if="element.options.functionName && !['mounted', 'refresh'].includes(element.options.functionName) && Object.keys(element.options).includes('functionParams')">
              <el-input type="textarea" v-model="element.options.functionParams" :autosize="true"></el-input>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.datasource')" v-if="Object.keys(element.options).includes('dataSource') && element.action == 'sendRequest'">
              <data-source-select v-model="element.options.dataSource"></data-source-select>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.datasourceArgs')" v-if="element.options.dataSource && Object.keys(element.options.dataSource.args).length">
              <div v-for="(argKey) in Object.keys(element.options.dataSource.args)" :key="argKey" class="values-item">
                
                <el-input :value="argKey" readonly style="width: 200px;" :title="argKey" />
                <div>&nbsp;=&nbsp;</div>
                <el-select v-model="element.options.valueTypes[argKey]" :placeholder="$t('fm.rules.label.string')" style="width: 200px;">
                  <el-option :label="$t('fm.rules.label.string')" value="string" />
                  <el-option :label="$t('fm.rules.label.number')" value="number" />
                  <el-option :label="$t('fm.rules.label.boolean')" value="boolean" />
                  <el-option :label="$t('fm.rules.label.fx')" value="fx" />

                </el-select>
                <div class="values-value">
                  <el-input v-if="!element.options.valueTypes[argKey] || element.options.valueTypes[argKey] == 'string'" v-model="element.options.dataSource.args[argKey]" ></el-input>
                  <el-input v-if="element.options.valueTypes[argKey] == 'number'" type="number" v-model.number="element.options.dataSource.args[argKey]" ></el-input>
                  <el-switch v-if="element.options.valueTypes[argKey] == 'boolean'" :active-value="'true'" :inactive-value="'false'" v-model="element.options.dataSource.args[argKey]" style="margin-left: 5px;"></el-switch>
                  <el-input v-if="element.options.valueTypes[argKey] == 'fx'" readonly v-model="element.options.dataSource.args[argKey]" :placeholder="$t('fm.rules.message.editFx')" @click="handleOpenFx(element.options.dataSource.args[argKey], index, argKey, 'dataSource')"></el-input>

                </div>
              </div>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.responseVariable')" v-if="element.options.dataSource && Object.keys(element.options).includes('responseVariable')">
              <el-input v-model="element.options.responseVariable" clearable></el-input>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.localVariable')" v-if="element.options.field && Object.keys(element.options).includes('localVariable')">
              <el-input v-model="element.options.localVariable" clearable></el-input>
            </el-form-item>

            <el-form-item :label-width="$i18n.locale == 'zh-cn' ? '100px' : '120px'" :label="$t('fm.rules.label.enabled') + '/' + $t('fm.rules.label.disabled')" v-if="Object.keys(element.options).includes('disabled')">
              <el-radio-group v-model="element.options.disabled">
                <el-radio :label="false" :value="false">{{$t('fm.rules.label.enabled')}}</el-radio>
                <el-radio :label="true" :value="true">{{$t('fm.rules.label.disabled')}}</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.values')" v-if="Object.keys(element.options).includes('values') && element.options.fields.length">
              <div v-for="(field) in element.options.fields" :key="field" class="values-item">
                <el-input :value="field" readonly style="width: 200px; direction: rtl;" :title="field" />
                <div>&nbsp;=&nbsp;</div>
                <el-select v-model="element.options.valueTypes[field]" :placeholder="$t('fm.rules.label.string')" style="width: 200px;">
                  <el-option :label="$t('fm.rules.label.string')" value="string" />
                  <el-option :label="$t('fm.rules.label.number')" value="number" />
                  <el-option :label="$t('fm.rules.label.boolean')" value="boolean" />
                  <el-option :label="$t('fm.rules.label.fx')" value="fx" />

                </el-select>
                <div class="values-value">
                  <el-input v-if="!element.options.valueTypes[field] || element.options.valueTypes[field] == 'string'" v-model="element.options.values[field]" ></el-input>
                  <el-input v-if="element.options.valueTypes[field] == 'number'" type="number" v-model.number="element.options.values[field]" ></el-input>
                  <el-switch v-if="element.options.valueTypes[field] == 'boolean'" v-model="element.options.values[field]" style="margin-left: 5px;"></el-switch>
                  <el-input v-if="element.options.valueTypes[field] == 'fx'" readonly v-model="element.options.values[field]" :placeholder="$t('fm.rules.message.editFx')" @click="handleOpenFx(element.options.values[field], index, field)"></el-input>

                </div>
              </div>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.validateFail')" v-if="Object.keys(element.options).includes('failSuspend')">
              <el-radio-group v-model="element.options.failSuspend">
                <el-radio :label="false" :value="false">{{$t('fm.rules.label.validateFailContinue')}}</el-radio>
                <el-radio :label="true" :value="true">{{$t('fm.rules.label.validateFailSuspend')}}</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item :label="$t('fm.rules.label.condition')" v-if="Object.keys(element.options).includes('condition')">
              
                <el-switch v-model="element.options.isCondition"></el-switch>
                <el-input
                  v-model="element.options.condition"
                  :placeholder="$t('fm.rules.message.condition')"
                  class="input-with-select"
                  v-if="element.options.isCondition"
                  readonly
                >
                  <template #append>
                    <el-button size="small" @click="handleOpenCondition(element.options.condition, index)"><i class="fm-iconfont icon-editor-formula" style="font-size: 13px;"></i></el-button>
                  </template>
                </el-input>
            </el-form-item>

            <el-form-item label-width="0" style="margin-bottom: 0px;" v-if="Object.keys(element.options).includes('func')">
              <div class="code-line">Function () {</div>
                  
              <code-editor v-model="element.options.func" mode="tsx" :height="'200px'"></code-editor>
              <div class="code-line">}</div>
            </el-form-item>
          </el-form>
        </el-card>
      </template>
    </draggable>


    <el-dropdown @command="handleActionCommand" trigger="click" class="fm-add-rules-wrapper" max-height="400px">
      <div class="fm-add-rules-button">{{$t('fm.rules.actions.add')}}<i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i></div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="item in dialogActions" :key="item.action" :command="item">
            <div>{{$t('fm.rules.actions.'+item.action)}}</div>
          </el-dropdown-item>
          <el-dropdown-item v-for="(item, index) in formActions" :key="item.action" :command="item" :divided="index == 0">
            <div>{{$t('fm.rules.actions.'+item.action)}}</div>
          </el-dropdown-item>
          <el-dropdown-item v-for="(item, index) in requestActions" :key="item.action" :command="item" :divided="index == 0">
            <div>{{$t('fm.rules.actions.'+item.action)}}</div>
          </el-dropdown-item>
          <el-dropdown-item v-for="(item, index) in otherActions" :key="item.action" :command="item" :divided="index == 0">
            <div>{{$t('fm.rules.actions.'+item.action)}}</div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>

  <FormulaDialog ref="formulaDialog" @dialog-confirm="handleFormulaConfirm"></FormulaDialog>
</template>

<script>
import Draggable from 'vuedraggable/src/vuedraggable'
import FieldsSelect from './fieldsSelect.vue'
import DataSourceSelect from './dataSourceSelect.vue'
import EventsSelect from './eventSelect.vue'
import { formActions, requestActions, dialogActions, otherActions } from './actions.js'
import _ from 'lodash'
import FormulaDialog from '../FormulaPanel/dialog.vue'
import CodeEditor from '../CodeEditor/index.vue'

export default {
  components: {
    Draggable,
    FieldsSelect,
    DataSourceSelect,
    EventsSelect,
    FormulaDialog,
    CodeEditor
  },
  props: ['modelValue'],
  emits: ['update:modelValue'],
  data () {
    return {
      ruleList: this.modelValue || [],
      select: '',
      formActions: formActions,
      requestActions: requestActions,
      dialogActions: dialogActions,
      otherActions: otherActions,
      curIndex: -1,
      curOption: '',
      curField: ''
    }
  },
  provide () {
    return {
      'getResponseVariables': this.getResponseVariables,
      'getLocalVariables': this.getLocalVariables
    }
  },
  methods: {
    handleOpenCondition (condition, index) {
      this.$refs.formulaDialog.open(condition)

      this.curIndex = index
      this.curOption = 'condition'
    },

    handleOpenFx (fx, index, field, option = 'values') {
      this.$refs.formulaDialog.open(fx)

      this.curIndex = index
      this.curOption = option
      this.curField = field
    },

    handleFormulaConfirm (val) {
      if (this.curIndex >= 0 && this.curOption == 'condition') {
        this.ruleList[this.curIndex].options[this.curOption] = val
      }

      if (this.curIndex >= 0 && this.curOption == 'values') {
        this.ruleList[this.curIndex].options[this.curOption][this.curField] = val
      }

      if (this.curIndex >= 0 && this.curOption == 'dataSource') {
        this.ruleList[this.curIndex].options.dataSource['args'][this.curField] = val
      }
    },

    handleActionCommand (command) {
      let key = Math.random().toString(36).slice(-8)

      this.ruleList.push({
        key,
        ..._.cloneDeep(command)
      })
    },

    handleActonRemove (index) {
      this.ruleList.splice(index, 1)
    },

    handleFieldDataChange (fieldData, element) {
      if (fieldData) {
        element.options.dataSource = {
          args: typeof fieldData.options.remoteArgs == 'string' ? JSON.parse(fieldData.options.remoteArgs) : fieldData.options.remoteArgs
        }
      } else {
        element.options.dataSource = {}
      }
    },

    getResponseVariables () {
      let vars = []
      this.ruleList.forEach(item => {
        if (item.options.responseVariable) {
          vars.push(item.options.responseVariable)
        }
      })

      return vars
    },

    getLocalVariables () {
      let vars = []
      this.ruleList.forEach(item => {
        if (item.options.localVariable) {
          vars.push(item.options.localVariable)
        }
      })

      return vars
    }
  },
  watch: {
    modelValue (val) {
      this.ruleList = val
    },
    ruleList: {
      deep: true,
      handler (val) {
        this.$emit('update:modelValue', val)
      }
    }
  }
}
</script>

<style lang="scss">
.fm-rules-config-container{
  .rule-card{

    +.rule-card{
      margin-top: 10px;
    }

    &.ghost11{
      border: 2px solid var(--fm-drag-color);
      outline-width: 0;
    }

    .el-card__header{
      padding: 5px;
      
      .card-header{
        display: flex;
        justify-content: space-between;
        align-items: center;

        .el-tag__content{
          font-size: 13px;
        }
      }
    }

    .el-card__body{
      padding: 10px 10px 0 10px;
    }

    .widget-view-action{
      display: inline-block;
      margin-left: 10px;
    }

    .widget-view-drag{
      cursor: move;
    }

    .widget-view-delete{
      cursor: pointer;
    }

    .el-form-item__label,.el-radio__label{
      font-size: 13px;
    }

    input{
      font-size: 13px;
    }

    .rule-card-form{
      .values-item{
        display: flex;
        justify-content: flex-start;
        width: 100%;
        align-items: flex-end;

        &+.values-item{
          margin-top: 5px;
        }

        .values-value{
          display: inline-block;
          width: 400px;
        }
      }
    }
  }

  .fm-add-rules-wrapper{
    width: 100%;
    margin-top: 10px;
  }

  .fm-add-rules-button{
    background-color: var(--el-fill-color-blank);
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    box-sizing: border-box;
    text-align: center;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    height: 30px;
    width: 100%;
    line-height: 30px;

    &:hover{
      border-color: var(--el-color-primary);
    }
  }
}
</style>
