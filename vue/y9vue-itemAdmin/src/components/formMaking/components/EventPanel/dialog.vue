<template>
  <cus-dialog
    :visible="visible"
    @on-close="handleClose"
    ref="scriptDialog"
    width="1000px"
    form
    :title="$t('fm.eventscript.config.title')"
    :action="false"
  >
    <el-container style="height: 600px;" class="fm-event-panel-dialog-container">
      <event-script-index v-model="scriptList" ref="eventScriptIndex" @on-confirm-event="handleConfirmEvent"></event-script-index>
    </el-container>
    
  </cus-dialog>
</template>

<script>
import CusDialog from '../CusDialog.vue'
import EventScriptIndex from './index.vue'

export default {
  components: {
    CusDialog,
    EventScriptIndex
  },
  emits: ['dialog-close', 'dialog-confirm'],
  data: () => ({
    visible: false,
    scriptList: []
  }),
  methods: {
    open (list, eventName, eventKey) {
      this.visible = true

      if (list) {
        this.scriptList = list
      }

      this.$nextTick(() => {
        if (!eventKey && eventName) {
          this.$refs.eventScriptIndex.loadNewFunction(eventName)
        }

        if (eventKey && eventName) {
          this.$refs.eventScriptIndex.loadFunction(eventKey, eventName)
        }
      })
    },

    handleClose () {
      this.$emit('dialog-close', this.scriptList)

      this.visible = false
    },

    handleConfirmEvent (eventObj) {
      this.$emit('dialog-close', this.scriptList)

      this.$emit('dialog-confirm', eventObj)

      this.visible = false
    }
  }
}
</script>

<style lang="scss">
.fm-event-panel-dialog-container{
  border: 1px solid var(--el-border-color-lighter);
}
</style>