<template>
  <div :id="id" v-loading="loading" class="fm-code-editor" :style="{width: width, height: height}" :ref="id+'Ref'"></div>
</template>

<script>
import {loadJs} from '../../util'
import { ref, watch } from 'vue'

export default {
  name: 'code-editor',
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '100%'
    },
    mode: {
      type: String,
      default: 'xml'
    },
    modelValue: {
      type: [String, Object, Array]
    },
    fontSize: {
      type: Number,
      default: 13
    },
    wrap: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue'],
  data () {
    return {
      id: 'code_' + Math.random().toString(36).slice(-8),
      codeValue: this.modelValue,
      loading: true,
      observer: null,
      editor: null
    }
  },
  computed: {
    aceMode () {
      switch (this.mode) {
        case 'xml':
          return 'ace/mode/xml'
        case 'html':
          return 'ace/mode/html'
        case 'json':
          return 'ace/mode/json'
        case 'css':
          return 'ace/mode/css'
        case 'javascript':
          return 'ace/mode/javascript'
        case 'tsx':
          return 'ace/mode/tsx'
        default:
          return 'ace/mode/xml'
      }
    }
  },
  mounted () {
    
    setTimeout(() => {
      if (window.ace) {
        this.loadEditor()
      } else {
        loadJs(`${window.FormMaking_OPTIONS.aceurl}/ace.js`).then(() => {
          loadJs(`${window.FormMaking_OPTIONS.aceurl}/ext-language_tools.js`).then(() => {
            this.loadEditor()
          })
        })
      }


      this.observer = new MutationObserver((mutationsList, observer) => {

        this.loadEditor()
      })

      this.observer.observe(document.getElementsByTagName('html')[0], {attributes: true})
    }, 0)
  },
  methods: {
    loadEditor () {
      this.loading = false

      ace.require("ace/ext/language_tools")

      this.editor = ace.edit(this.$refs[this.id+'Ref'])

      this.editor.session.setMode(this.aceMode)
      this.editor.setFontSize(this.fontSize)
      this.editor.getSession().setTabSize(2)
      this.editor.setShowPrintMargin(false)
      this.wrap && this.editor.getSession().setUseWrapMode(true)
      if (document.querySelector('html').className.indexOf('dark')>-1) {
        this.editor.setTheme("ace/theme/nord_dark")
      } else {
        this.editor.setTheme("ace/theme/textmate")
      }

      this.editor.setOptions({
        // enableBasicAutoCompletion : true,
        enableSnippets : true,
        enableLiveAutocompletion : true
      });

      this.editor.commands.addCommand({

          name: 'myCommand',

          bindKey: {win: 'Ctrl-Enter',  mac: 'Command-Enter'},

          exec: function(editor) {

              const currentCursor = editor.selection.getCursor();

              editor.selection.moveCursorLineEnd()

              editor.selection.moveTo(editor.selection.getCursor().row, editor.selection.getCursor().column)

              editor.session.insert(editor.getCursorPosition(), '\n')
          }

      });

      this.editor.setValue(typeof this.codeValue === 'string' ? this.codeValue : JSON.stringify(this.codeValue, null, '\t'), -1)

      this.editor.on('change', (e) => {
        this.codeValue = this.editor.getValue()
      })

      this.editor.on('paste', (e) => {
        this.mode === 'json' && setTimeout(() => {
          this.editor.setValue(JSON.stringify(JSON.parse(e.text), null, 2), -1)
        })
      })
    },
    setValue (value) {
      this.editor.setValue(JSON.stringify(JSON.parse(value), null, 2), -1)
    }
  },
  beforeUnmount () {
    this.observer?.disconnect() 
    this.editor?.destroy()
    this.editor?.container.remove()
  },
  watch: {
    modelValue (val) {
      this.codeValue =  val
    },
    codeValue (val) {
      this.$emit('update:modelValue', val)
    }
  }
}
</script>

<style lang="scss">
.fm-code-editor{
  border: 1px solid var(--el-border-color);
}
</style>
