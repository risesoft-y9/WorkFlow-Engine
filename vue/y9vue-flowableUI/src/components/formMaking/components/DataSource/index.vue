<template>
  <el-container class="data-source-container">
    <el-aside width="300px" class="data-source-aside">
      <el-container>
        <el-header height="42px">
          <el-button link type="primary" size="default" @click="handleAdd">
            <i class="fm-iconfont icon-plus" style="font-size: 12px; margin: 5px;"></i>
            {{$t('fm.datasource.config.add')}}
          </el-button>
        </el-header>
        <el-main>
          <el-scrollbar>
            <el-menu class="data-source-aside-menu" :default-active="selectKey" @select="handleSelect"> 
              <el-menu-item :index="item.key" v-for="(item, index) in list" :key="item.key" :disabled="!saved" @click="handleSelect(item.key)">
                <template #title>
                  <div>
                    <span class="data-source-menu-i" :class="item.method">{{item.method}}</span>
                    <div class="data-source-menu-label">{{item.name}}</div>
                    <div class="data-source-menu-action">
                      <i class="fm-iconfont icon-icon_clone" @click.stop="handleClone(index)" :title="$t('fm.tooltip.clone')"></i>
                      <i class="fm-iconfont icon-trash" @click.stop="handleRemove(index)" :title="$t('fm.tooltip.trash')"></i>
                    </div>
                  </div>
                </template>
              </el-menu-item>
            </el-menu>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-aside>
    <el-main class="data-source-main">
      <el-container v-if="selectIndex >= 0">
        <el-header height="42px">
          <div class="data-source-action">
            <el-button type="primary" size="default" @click="handleSave">{{$t('fm.datasource.config.save')}}</el-button>
            <el-button  size="default" @click="handleTest">{{$t('fm.datasource.config.test')}}</el-button>
            <el-button size="default" @click="handleCancal">{{$t('fm.datasource.config.cancel')}}</el-button>
          </div>
        </el-header>
        <el-main>
          <el-form ref="dataForm" :model="formData" :rules="formRules" :label-width="$i18n.locale == 'zh-cn' ? '79px' : '119px'" size="default" label-position="right" :key="selectKey">
            <el-form-item :label="$t('fm.datasource.edit.name')" prop="name">
              <el-input v-model="formData.name"></el-input>
            </el-form-item>
            <el-form-item :label="$t('fm.datasource.edit.url')" prop="url">
              <el-input type="textarea" :rows="1" v-model="formData.url" autosize></el-input>
            </el-form-item>
            <el-form-item :label="$t('fm.datasource.edit.method')" prop="method">
              <el-radio-group v-model="formData.method">
                <el-radio-button label="GET" value="GET" ></el-radio-button>
                <el-radio-button label="POST" value="POST"></el-radio-button>
                <el-radio-button label="PUT" value="PUT"></el-radio-button>
                <el-radio-button label="DELETE" value="DELETE"></el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('fm.datasource.edit.headers')">
              <array-dynamic v-model="formData.headers"></array-dynamic>
            </el-form-item>
            <el-form-item :label="$t('fm.datasource.edit.params')">
              <array-dynamic v-model="formData.params"></array-dynamic>
            </el-form-item>
            <el-form-item :label="$t('fm.datasource.edit.auto')" :label-width="$i18n.locale == 'zh-cn' ? '175px' : '340px'">
              <el-switch
                v-model="formData.auto">
              </el-switch>
            </el-form-item>

            <el-form-item :label="$t('fm.datasource.edit.response')">
              <el-collapse :modelValue="['1', '2', '3']">
                <el-collapse-item name="1" :title="$t('fm.datasource.edit.willRequest')">
                  <div class="code-desc" v-if="$i18n.locale == 'zh-cn'">// config: 发出请求的可用配置选项;</div>
                  <div class="code-desc" v-if="$i18n.locale == 'zh-cn'">// 通过 config.url 可以更改请求地址，通过 config.headers 可以更改请求头部</div>
                  <div class="code-desc" v-if="$i18n.locale == 'zh-cn'">// 通过 config.data 可以更改发送的数据，（GET 请求不适用，需要更改 config.params）</div>
                  <div class="code-line">(config, args) => {</div>
                  <code-editor v-model="formData.requestFunc" mode="javascript" height="150px"></code-editor>
                  <div class="code-line">}</div>
                </el-collapse-item>
                <el-collapse-item :title="$t('fm.datasource.edit.responseSuccess')" name="2">
                  <div class="code-desc" v-if="$i18n.locale == 'zh-cn'">// {{$t('fm.datasource.edit.annotation')}}</div>
                  <div class="code-line">(res, args) => {</div>
                  
                  <code-editor v-model="formData.responseFunc" mode="javascript" height="150px"></code-editor>
                  <div class="code-line">}</div>
                </el-collapse-item>
                <el-collapse-item :title="$t('fm.datasource.edit.requestError')" name="3">
                  <div class="code-line">(error) => {</div>
                  
                  <code-editor v-model="formData.errorFunc" mode="javascript" height="150px"></code-editor>
                  <div class="code-line">}</div>
                </el-collapse-item>
              </el-collapse>
            </el-form-item> 
          </el-form>
        </el-main>
      </el-container>
    </el-main>
  </el-container>
</template>

<script>
import ArrayDynamic from './arrayDynamic.vue'
import CodeEditor from '../CodeEditor/index.vue'
import axios from 'axios'
import { ElMessageBox, ElMessage } from 'element-plus'
import { h } from 'vue'

export default {
  components: {
    ArrayDynamic,
    CodeEditor
  },
  props: {
    modelValue: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue'],
  data () {
    return {
      formData: {
        key: '',
        name: '',
        url: '',
        method: 'GET',
        auto: true,
        params: [],
        headers: [],
        responseFunc: `return res;`,
        requestFunc: `return config;`,
        errorFunc: ``
      },
      formRules: {
        name: [
          {required: true, message: ' '},
          { validator: (rule, value, callback) => {
            let currentItem = this.historyList.find(item => item.name ==value)

            if (currentItem && currentItem.key != this.selectKey) {
              callback(new Error(this.$t('fm.datasource.message.repeat')))
            } else {
              callback()
            }
          }}
        ],
        url: [
          {required: true, message: ' '}
        ]
      },
      list: [...this.modelValue],
      selectIndex: -1,
      selectKey: '',
      historyList: [...this.modelValue],
      saved: true,
      requestError: false
    }
  },
  methods: {
    _getAttributeFunction (funcStrs) {
      let matchReg1 = new RegExp("(?<=args.)\\w*(?=[\\s|,|;])", "g") // 匹配 args.a 形式的参数
      let matchReg2 = new RegExp("(?<=args\\[\\s*['|\"])\\w*(?=['|\"]\\s*\\])", "g") // 匹配 args[] 形式的参数
      let matchReg3 = new RegExp("(?<=\{)[\\s+\\w*\\s+,{0|1}]+(?=\}\\s+=\\s+args)", "g") // 匹配 {} = args 形式的参数

      let args = new Set()

      funcStrs.forEach(funcStr => {
        funcStr?.match(matchReg1)?.forEach(item => item && args.add(item.trim()))
        funcStr?.match(matchReg2)?.forEach(item => item && args.add(item.trim()))
        funcStr?.match(matchReg3)?.forEach(item => item && item.split(',').forEach(sitem => sitem && args.add(sitem.trim())))
      })

      return args
    },
    _getArgsObject (argSet) {
      const args = {}
      for (let arg of argSet) {
        args[arg] = ''
      }

      return args
    },
    handleTest () {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          const argSet = this._getAttributeFunction([this.formData.requestFunc, this.formData.responseFunc])
          if (argSet.size > 0) {
            let argsModel = this._getArgsObject(argSet)
            ElMessageBox({
              title: 'Data source arguments',
              customStyle: 'z-index: 3000',
              customClass: 'fm-messagebox',
              class: 'aaa',
              draggable: true,
              key: new Date().getTime(),
              message: h ('div', null, [

                  h(
                    CodeEditor,{
                      modelValue: argsModel,
                      mode: (typeof data == 'object' ? 'javascript' : 'xml'),
                      height: '200px',
                      key: new Date().getTime(),
                      'onUpdate:modelValue' : (value) => {
                        argsModel = value
                      }
                    }
                  )
                ]
              ),
              callback: (action) => {
                if (action == 'confirm') {
                  let args = {}
                  if (typeof argsModel == 'string') {
                    args =  Function('"use strict";return (' + argsModel + ')')()
                  } else {
                    args =  argsModel
                  }

                  this.requestTest(args)
                }
              }
            })

          } else {
            this.requestTest()
          }
          
        } else {
          ElMessage({
            message: this.$t('fm.datasource.message.settingError'),
            type: 'warning'
          })
        }
      })
    },

    requestTest (args = {}) {
      let options = {
        method: this.formData.method,
        url: this.formData.url,
        headers: (() => {
          let headObj = {}
          this.formData.headers.forEach(item => {
            if (item.key) {
              headObj[item.key] = item.value
            }
          })
          return headObj
        })()
      }

      options.params = (() => {
        let paramsObj = {}
        this.formData.params.forEach(item => {
          if (item.key) {
            paramsObj[item.key] = item.value
          }
        })
        return paramsObj
      })()

      this.requestError = false

      if (this.formData.requestFunc) {
        const requestDynamicFunc = new Function('config', 'args', this.formData.requestFunc)
        try {
          options = requestDynamicFunc(options, args)
        } catch(error) {

          this.requestError = true
        }
      }

      axios(options).then(res => {
        try {
          let data = new Function('res', 'args', this.formData.responseFunc)(res.data, args)

          ElMessageBox({
            title: 'Response',
            customStyle: 'z-index: 3000',
            customClass: 'fm-messagebox',
            class: 'aaa',
            draggable: true,
            key: new Date().getTime(),
            message: h ('div', null, [
                h('div', {style: this.requestError ? 'margin: 10px 0; ' : 'display: none;'}, [
                  'The request test does not support ',
                  h('b', {style: 'color: red;'}, 'this'),
                  ', has been ignored.'
                ]),

                h(
                  CodeEditor,{
                    modelValue: data,
                    mode: (typeof data == 'object' ? 'javascript' : 'xml'),
                    height: '300px',
                    key: new Date().getTime()
                  }
                )
              ]
            ),
          })
        } catch (error) {
          ElMessageBox({
            title: 'Response',
            customStyle: 'z-index: 3000',
            customClass: 'fm-messagebox',
            class: 'aaa',
            draggable: true,
            key: new Date().getTime(),
            message: h ('div', null, [
                h('div', {style: this.requestError ? 'margin: 10px 0; ' : 'display: none;'}, [
                  'The request test does not support ',
                  h('b', {style: 'color: red;'}, 'this'),
                  ', has been ignored.'
                ]),

                h(
                  CodeEditor,{
                    modelValue: res.data,
                    mode: (typeof res.data == 'object' ? 'javascript' : 'xml'),
                    height: '300px',
                    key: new Date().getTime()
                  }
                )
              ]
            ),
          })
        }
      }).catch(err => {
        ElMessageBox.alert(err.message)
      })
    },

    handleSave () {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          const argSet = this._getAttributeFunction([this.formData.requestFunc, this.formData.responseFunc])

          let options = {
            ...this.formData,
            params: (() => {
              let paramsObj = {}
              this.formData.params.forEach(item => {
                if (item.key) {
                  paramsObj[item.key] = item.value
                }
              })
              return paramsObj
            })(),
            headers: (() => {
              let headersObj = {}
              this.formData.headers.forEach(item => {
                if (item.key) {
                  headersObj[item.key] = item.value
                }
              })
              return headersObj
            })(),
            args: [...argSet]
          }

          this.list[this.list.findIndex(item => item.key === this.selectKey)] = options

          this.historyList = [...this.list]

          this.saved = true

          this.$emit('update:modelValue', this.historyList)

          ElMessage({
            message: this.$t('fm.datasource.message.saveSuccess'),
            type: 'success'
          })
        } else {
          ElMessage({
            message: this.$t('fm.datasource.message.settingError'),
            type: 'warning'
          })
        }
      })
    },

    handleAdd () {
      if (!this.saved) {
        ElMessage({
          message: this.$t('fm.datasource.message.saveError'),
          type: 'warning'
        })
        return;
      }

      let key = Math.random().toString(36).slice(-8)

      this.list.push({
        key: key,
        name: 'DataSource_'+key,
        url: '',
        method: 'GET',
        auto: false,
        params: {},
        headers: {},
        responseFunc: 'return res;',
        requestFunc: `return config;`,
        errorFunc: ``
      })
      this.selectKey = key

      this.saved = false
    },

    loadForm () {
      let currentData = this.list[this.selectIndex]

      this.formData = {
        ...currentData,
        params: currentData.params && Object.keys(currentData.params).map(item => ({key: item, value: currentData.params[item]})) || [],
        headers: currentData.headers && Object.keys(currentData.headers).map(item => ({key: item, value: currentData.headers[item]})) || [],
        requestFunc: currentData.requestFunc || `return config;`
      }
    },

    handleSelect (key) {
      if (key === this.selectKey) {
        return
      }
      if (!this.saved) {
        ElMessage({
          message: this.$t('fm.datasource.message.saveError'),
          type: 'warning'
        })
        return;
      }

      this.selectKey = key
    },

    handleCancal () {
      this.selectKey = ''

      this.list = [...this.historyList]

      this.saved = true
    },

    handleClone (index) {
      if (!this.saved) {
        ElMessage({
          message: this.$t('fm.datasource.message.saveError'),
          type: 'warning'
        })
        return;
      }
      let currentData = this.list[index]

      let key = Math.random().toString(36).slice(-8)

      let cloneData = {
        ...currentData,
        key,
        name: currentData.name + '_copy'
      }

      this.list.push(cloneData)

      this.selectKey = key

      this.saved = false
    },

    handleRemove (index) {
      let currentData = this.list[index]

      ElMessageBox.confirm(`${this.$t('fm.datasource.message.confirmRemove')} [${currentData.name}] ?`, '', {
        type: 'warning'
      }).then(() => {
        if (currentData.key === this.selectKey) {
          this.selectKey = ''
          this.saved = true
        }
        this.list.splice(index, 1)
        if (index < this.historyList.length) {
          this.historyList.splice(index, 1)
        }

        this.$emit('update:modelValue', this.historyList)
      }).catch(() => {})
    }
  },
  watch: {
    selectKey (val) {
      if (val) {
        this.selectIndex = this.list.findIndex(item => item.key === val)

        this.loadForm()
      } else {
        this.selectIndex = -1
      }
    },
    modelValue (val) {
      this.historyList = [...val]
      this.list = [...val]
    }
  }
}
</script>

<style lang="scss">
.data-source-container{
  .data-source-main{
    padding: 0;

    .el-form-item .el-form-item__content{
      display: block;
    }

    >.el-container{
      display: flex;
      height: 100%;

      >.el-header{
        padding: 5px;
        border-bottom: 1px solid var(--el-border-color-lighter);
        background: var(--el-border-color-extra-light);
      }
    }
    
    .data-source-action{
      text-align: right;
    }

    .code-line{
      font-size: 14px;
      color: var(--el-color-primary);
      font-weight: 500;
    }

    .code-desc{
      margin-left: 2px;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }

    .el-collapse{
      width: 100%;
    }

    .el-collapse-item{
      border: 1px solid var(--el-border-color-lighter);
    }

    .el-collapse-item__header{
      background: var(--el-border-color-lighter);
      // border: 0;
      height: 36px;
      line-height: 36px;
      padding: 5px;
    }

    .el-collapse-item__wrap{
      border: 0;

      .el-collapse-item__content{
        padding: 5px;
      }
    }
  }

  .data-source-aside{
    border-right: 1px solid var(--el-border-color-lighter);

    >.el-container{
      display: flex;
      height: 100%;

      >.el-main{
        margin: 0;
        padding: 0;
      }

      >.el-header{
        padding: 5px;
        border-bottom: 1px solid var(--el-border-color-lighter);
        background: var(--el-border-color-extra-light);
      }
    }

    .data-source-aside-menu{
      margin: 10px;
      border-right: 0;

      .el-menu-item{
        border: 1px solid var(--el-border-color);
        background: var(--el-bg-color);
        border-radius: 3px;
        padding: 10px !important;
        height: auto;
        line-height: 1;
        cursor: default;
        position: relative;

        &.is-disabled{
          opacity: 1;
          cursor: default;
          background: var(--el-bg-color);
        }

        &.is-active{
          background: var(--el-border-color-light);
          color: var(--el-text-color-primary);
        }

        &.is-active.is-disabled{
          background: var(--el-border-color-light) !important;
          color: var(--el-text-color-primary);
        }

        +.el-menu-item{
          margin-top: 6px;
        }
      }

      .data-source-menu-label{
        overflow:hidden;
        text-overflow:ellipsis;
        white-space:nowrap;
        display: inline-block;
        width: 180px;
        margin-left: 40px;
      }

      .data-source-menu-i{
        position: absolute;
        font-size: 12px;
        left: 2px;
        top: 13px;
        width: 45px;
        text-align: center;

        &.GET{
          color: #67C23A;
        }

        &.POST{
          color: #E6A23C;
        }

        &.PUT{
          color: #409EFF;
        }

        &.DELETE{
          color: #F56C6C;
        }
      }

      .data-source-menu-action{
        display: inline-block;
        padding-right: 10px;
        color: var(--el-text-color-regular);
        font-weight: 600;

        >i{
          cursor: pointer;
          margin-left: 5px;
        }
      }
    }
  }
}

</style>