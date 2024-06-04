<template>
  <el-input
    v-model="value"
    placeholder="点击编写表达式"
    class="input-with-select"
  >
    <template #append>
      <el-button size="small"><i class="fm-iconfont icon-editor-formula" style="font-size: 13px;"></i></el-button>
    </template>
  </el-input>

  <div :id="editorId"></div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/javascript/javascript.js'

export default {
  props: ['modelValue'],
  data () {
    return {
      value: this.modelValue,
      editorId: 'formula-' + Math.random().toString(36).slice(-8),
      editor: null
    }
  },
  mounted () {
    this.editor = CodeMirror(document.getElementById(this.editorId), {
      value: "var a = 1",
      lineNumbers: false,
      mode: 'javascript',
      lineWrapping: false
    })
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