<template>
  <div class="fm-formula-container">
    <el-row justify="space-between" >
      <el-col :span="24">
        <el-card :header="$t('fm.formula.header')" shadow="never" size="small" class="formula-card">
          <div :id="editorId" class="formula-editor"></div>
        </el-card>
      </el-col>
      
    </el-row>
    <el-row justify="space-between"  :gutter="10">
      <el-col :span="12">
        <el-card :header="$t('fm.formula.field')" shadow="never" size="small" class="formula-card formula-field">
          <el-scrollbar ref="scrollRef" noresize>
            <el-tree
              :data="modelsData"
              node-key="id"
              default-expand-all
              :expand-on-click-node="false"
              style="width: 100%;"
              ref="fieldTree"
            >
              <template #default="{ node }">
                <div style="display: flex; justify-content: space-between; width: 90%;" @click="handleNode(node.data)">
                  <span style=" padding: 0 5px; max-width: 50%;">{{ node.data.name }} {<span style="font-size: 12px;">{{node.data.id}}</span>}</span>
                  <el-tag  type="info">{{$t('fm.components.fields.' + node.data.type)}}</el-tag>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card :header="$t('fm.formula.event')" shadow="never" size="small" class="formula-card formula-method">
          <el-scrollbar noresize>
            <el-tree
              :data="argsData"
              node-key="id"
              default-expand-all
              :expand-on-click-node="false"
              style="width: 100%;"
            >
              <template #default="{ node }">
                <div style="display: flex; justify-content: space-between; width: 90%;" @click="handleArgsNode(node.data)">
                  <span style=" padding: 0 5px; max-width: 50%;">{{ node.data.name }}</span>
                  <span style="font-size: 12px;">{{node.data.id}}</span>
                </div>
              </template>
            </el-tree>
            <el-tree
              v-if="localVariables.length"
              :data="localVariables"
              node-key="id"
              default-expand-all
              :expand-on-click-node="false"
              style="width: 100%;"
            >
              <template #default="{ node }">
                <div style="display: flex; justify-content: space-between; width: 90%;" @click="handleVariableNode(node.data)">
                  <span style=" padding: 0 5px; max-width: 50%;">{{ node.data.name }}</span>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CodeMirror from 'codemirror/lib/codemirror.js'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/ayu-dark.css'
import 'codemirror/addon/edit/closebrackets.js'

export default {
  props: ['modelValue'],
  inject: ['getFormModels', 'getResponseVariables', 'getLocalVariables'],
  data () {
    return {
      value: this.modelValue,
      editorId: 'formula-' + Math.random().toString(36).slice(-8),
      // editor: null, 此处不能定义，防止被Vue Proxy 包裹
      modelsData: [],
      argsData: [{
        id: 'arguments[0]',
        name: this.$t('fm.formula.argsData.name'),
        children: [
          {
            id: 'arguments[0].field',
            name: this.$t('fm.formula.argsData.field'),
          },
          {
            id: 'arguments[0].value',
            name: this.$t('fm.formula.argsData.value'),
          }
        ]
      }],
      responseVariables: [],
      localVariables: []
    }
  },
  mounted () {
    this.modelsData = this.getFormModels()

    let responseVariables =  this.getResponseVariables()
    let localVars = this.getLocalVariables()

    if (responseVariables.length || localVars.length) {
      this.localVariables = [{
        id: '',
        name: this.$t('fm.formula.argsData.variable'),
        children: [...responseVariables, ...localVars].map(item => ({
          id: item,
          name: item
        }))
      }]
    }

    setTimeout(() => {
      this.initEditor()
    })
  },
  methods: {
    initEditor () {
      let theme = 'default'
      if (document.querySelector('html').className.indexOf('dark')>-1) {
        theme = 'ayu-dark'
      }

      this.value = this.modelValue.replace(/\n/g, ' ')

      this.editor = CodeMirror(document.getElementById(this.editorId), {
        value: this.value,
        lineNumbers: false,
        mode: 'javascript',
        lineWrapping: true,
        autofocus: true,
        theme: theme,
        autoCloseBrackets: true
      })

      this.editor.on('change', cm => {
        this.value = cm.getValue()
      })

      this.replaceFieldContent()
      this.replaceArgsContent()

      this.editor.execCommand("goDocEnd")
    },

    replaceArgsContent () {
      const regex = /arguments\[0\]/g

      const matches = []

      let match
      while ((match = regex.exec(this.value)) !== null) {
        // 匹配到的内容
        const matchedValue = match[0]

        // 匹配到的内容在文本中的起始位置
        const startIndex = match.index

        // 将匹配结果存储到数组中
        matches.push({
          value: matchedValue,
          startIndex: startIndex,
        })
      }

      matches.forEach(item => {
        let widgetNode = document.createElement('span')
        widgetNode.className = 'cm-eventargs'
        widgetNode.textContent = item.value

        this.editor.markText({line: 0, ch: item.startIndex}, { line: 0, ch: item.startIndex + item.value.length }, {
          atomic: true,
          selectLeft: true,
          selectRight: true,
          inclusiveLeft: false,
          inclusiveRight: false,
          replacedWith: widgetNode,
          handleMouseEvents: true
        })
      })
    },

    replaceFieldContent () {
      // 定义正则表达式模式，匹配 this.getValue("xxx") 形式的内容
      const regex = /this\.getValue\("([^"]+)"\)/g

      const matches = []

      let match
      while ((match = regex.exec(this.value)) !== null) {
        // 匹配到的内容
        const matchedValue = match[0]
        const matchedId = match[1]

        // 匹配到的内容在文本中的起始位置
        const startIndex = match.index

        const matchNode = this.$refs.fieldTree.getNode(matchedId)

        if (matchNode) {
          // 将匹配结果存储到数组中
          matches.push({
            value: matchedValue,
            startIndex: startIndex,
            id: matchedId,
            data: matchNode.data
          })
        }
      }

      matches.forEach(item => {
        let widgetNode = document.createElement('span')
        widgetNode.className = 'cm-field'
        widgetNode.textContent = item.data.name || item.data.id
        widgetNode.title = item.data.id

        this.editor.markText({line: 0, ch: item.startIndex}, { line: 0, ch: item.startIndex + item.value.length }, {
          atomic: true,
          selectLeft: true,
          selectRight: true,
          inclusiveLeft: false,
          inclusiveRight: false,
          replacedWith: widgetNode,
          handleMouseEvents: true
        })
      })
    },

    handleNode(data) {
      let cursor = this.editor.getCursor()

      let widgetNode = document.createElement('span')
      widgetNode.className = 'cm-field'
      widgetNode.textContent = data.name || data.id
      widgetNode.title = data.id

      let text = `this.getValue("${data.id}")`

      this.editor.replaceRange(text, cursor)

      this.editor.markText({line: cursor.line, ch: cursor.ch}, { line: cursor.line, ch: cursor.ch + text.length }, {
        atomic: true,
        selectLeft: true,
        selectRight: true,
        inclusiveLeft: false,
        inclusiveRight: false,
        replacedWith: widgetNode,
        handleMouseEvents: true
      })

      this.editor.focus()
    },

    handleArgsNode (data) {

      let cursor = this.editor.getCursor()

      let widgetNode = document.createElement('span')
      widgetNode.className = 'cm-eventargs'
      widgetNode.textContent = 'arguments[0]'

      let text = data.id

      this.editor.replaceRange(text, cursor)

      this.editor.markText({line: cursor.line, ch: cursor.ch}, { line: cursor.line, ch: cursor.ch + 12 }, {
        atomic: true,
        selectLeft: true,
        selectRight: true,
        inclusiveLeft: false,
        inclusiveRight: false,
        replacedWith: widgetNode,
        handleMouseEvents: true
      })

      this.editor.focus()
    },

    handleVariableNode (data) {
      let cursor = this.editor.getCursor()
      let text = data.id

      this.editor.replaceRange(text, cursor)
      this.editor.focus()
    }
  },
  watch: {
    modelValue (val) {
      this.value = val
    },
    value (val) {
      this.$emit('update:modelValue', val)
    }
  }
}
</script>

<style lang="scss">
.fm-formula-container{
  .formula-card{
    margin-bottom: 10px;

    .el-card__header{
      padding: 8px;
      background: var(--el-fill-color-light);
    }

    .el-card__body{
      padding: 0 5px;
    }
  }

  .formula-editor{
    height: 160px;

    .CodeMirror{
      height: 100%;
    }

    .cm-field{
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      padding: 0 8px;
      border: 1px solid var(--el-color-primary-light-5);
      border-radius: 4px;
      display: inline-block;
      font-size: 13px;
      margin: 0 1px;
    }

    .cm-eventargs{
      background-color: var(--el-color-warning-light-9);
      color: var(--el-color-warning);
      padding: 0 2px;
      border-radius: 4px;
      display: inline-block;
    }
  }

  .el-tree-node{
    padding: 2px 0;
  }

  .formula-field,.formula-method{
    .el-card__body{
      height: 240px;
      padding: 0;
    }
  }
}
</style>