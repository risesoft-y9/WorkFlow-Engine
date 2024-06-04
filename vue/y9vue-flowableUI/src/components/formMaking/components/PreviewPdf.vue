<template>
  <cus-dialog
    :visible="previewVisible"
    @on-close="previewVisible = false"
    ref="pdfPreview"
    form
    :title="$t('fm.actions.pdfPreview')"
    :action="false"
    custom-class="fm-generate-preview-pdf"
    :close-on-press-escape="false"
    :fullscreen="false"
    width="1200px"
  >
    <iframe :src="iframeSrc" frameborder="0" width="100%" height="600px" style="margin-bottom: -6px;"></iframe>
  </cus-dialog>
</template>

<script>
import CusDialog from './CusDialog.vue'

export default {
  components: {
    CusDialog
  },
  data () {
    return {
      previewVisible: false,
      iframeSrc: ''
    }
  },
  methods: {
    open (pdfBlob) {

      // 创建一个URL对象
      var pdfUrl = URL.createObjectURL(pdfBlob);

      // 创建一个新窗口并加载PDF文件
      this.iframeSrc = pdfUrl

      this.previewVisible = true
    }
  }
}
</script>

<style lang="scss">
.fm-generate-preview-pdf{
  overflow: hidden !important;
  display: flex;
  flex-direction: column;
  .el-dialog__body{
    height: 100%;
    padding: 0;
  }
}
</style>
