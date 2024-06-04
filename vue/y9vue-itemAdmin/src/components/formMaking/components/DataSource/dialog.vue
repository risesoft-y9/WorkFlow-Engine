<template>
  <cus-dialog
    :visible="visible"
    @on-close="handleClose"
    ref="dataSourceDialog"
    width="1000px"
    form
    :title="$t('fm.datasource.config.title')"
    :action="false"
  >
    <el-container class="fm-data-source-dialog-container" style="height: 600px; ">
      <datasource-index v-model="dataSourceList"></datasource-index>
    </el-container>
    
  </cus-dialog>
</template>

<script>
import CusDialog from '../CusDialog.vue'
import DatasourceIndex from './index.vue'

export default {
  components: {
    CusDialog,
    DatasourceIndex
  },
  emits: ['dialog-close'],
  data: () => ({
    visible: false,
    dataSourceList: []
  }),
  methods: {
    open (list) {
      this.visible = true

      if (list) {
        this.dataSourceList = list
      }
    },

    handleClose () {
      this.$emit('dialog-close', this.dataSourceList)

      this.visible = false
    }
  }
}
</script>

<style lang="scss">
.fm-data-source-dialog-container{
  border: 1px solid var(--el-border-color-lighter);
}
</style>