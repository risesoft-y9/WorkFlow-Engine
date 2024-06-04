<template>
  <cus-dialog
    :visible="templateVisible"
    @on-close="templateVisible = false"
    ref="codeDialog"
    :width="width"
    :title="title"
    @on-submit="handleSubmit"
    custom-class="code-dialog-container"
  >

    <code-editor :height="codeHeight" :mode="this.mode" v-model="templ"></code-editor>
    <div class="code-dialog-help" v-if="help">
      <el-button link type="primary" @click="handleHelp">帮助<i class="el-icon-question el-icon--right"></i></el-button>
    </div>
  </cus-dialog>
</template>

<script>
import CusDialog from './CusDialog.vue'
import CodeEditor from './CodeEditor/index.vue'

export default {
  components: {
    CusDialog,
    CodeEditor
  },
  props: {
    mode: {
      type: String,
      default: 'xml'
    },
    title: {
      type: String,
      default: ''
    },
    help: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '900px'
    },
    codeHeight: {
      type: String,
      default: '460px'
    }
  },
  emits: ['on-confirm'],
  data () {
    return {
      templateVisible: false,
      templ: ''
    }
  },
  methods: {
    handleSubmit () {
      this.$emit('on-confirm', this.templ)
    },

    open (val) {
      this.templ = val

      this.templateVisible = true
    },

    close () {
      this.templateVisible = false
    },

    end () {
      this.$refs['codeDialog'].end()
    },

    handleHelp () {
      window.open(this.help)
    }
  }
}
</script>

<style lang="scss">
@media screen and (max-width: 1000px) {
  .code-dialog-container{
    .el-dialog{
      width: 100% !important;
    }
  }
}
</style>