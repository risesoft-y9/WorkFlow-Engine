<template>
  <el-dialog
    :class="customClass + ' cus-dialog-container fm-dialog'"
    :title="title"
    v-model="dialogVisible"
    :close-on-click-modal="closeOnClickModal"
    :close-on-press-escape="closeOnPressEscape"
    :append-to-body="true"
    center
    :width="width"
    ref="elDialog"
    :fullscreen="fullscreen"
    :show-close="showclose"
    top="10vh"
    draggable
    destroy-on-close	
    >
    <span v-if="show">
      <slot></slot>
    </span>
    <template #footer v-if="action">
      <span class="dialog-footer" v-loading="loading"
        :element-loading-text="loadingText">
        <slot name="action">
          <el-button @click="close">{{$t('fm.actions.cancel')}}</el-button>
          <el-button type="primary" @click="submit" >{{$t('fm.actions.confirm')}}</el-button>
        </slot>
      </span>
    </template>
  </el-dialog>
</template>

<script>
export default {
  props: {
    visible: Boolean,
    loadingText: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '600px'
    },
    form: {
      type: Boolean,
      default: true
    },
    action: {
      type: Boolean,
      default: true
    },
    fullscreen: {
      type: Boolean,
      default: false
    },
    closeOnClickModal: {
      type: Boolean,
      default: false
    },
    closeOnPressEscape: {
      type: Boolean,
      default: true
    },
    customClass: {
      type: String,
      default: ''
    },
    showclose: {//Y9
      type: Boolean,
      default: true
    }
  },
  emits: ['on-submit', 'on-close'],
  computed: {
    show () {
      if (this.form) {
        return this.showForm
      } else {
        return true
      }
    }
  },
  data () {
    return {
      loading: false,
      dialogVisible: this.visible,
      showForm: false
    }
  },
  methods: {
    close () {
      this.dialogVisible = false
    },
    submit () {
      this.loading = true

      this.$emit('on-submit')
    },
    end () {
      this.loading = false
    }
  },
  mounted () {
  },
  watch: {
    dialogVisible (val) {
      if (!val) {
        this.loading = false
        this.$emit('on-close')
        setTimeout(() => {
          this.showForm = false
        }, 300)
      } else {
        this.showForm = true
      }
    },
    visible (val) {
      this.dialogVisible = val
    }
  }
}
</script>

<style lang="scss">
.cus-dialog-container{
  padding: 0;

  > .el-dialog__header{
    background: var(--el-fill-color-light);
    padding: 10px;
    margin-right: 0;
  }

  .el-dialog__headerbtn{
    top:6px;
    right: 0;
    font-size: 20px;
    width: 40px;
    height: 40px;
  }

  &.notitle{
    .el-dialog__header{
      padding: 0;
    }
  }

  .el-dialog__body{
    padding: 20px;
    text-align: left;
  }

  .el-dialog__footer{
    text-align: center;
    position: relative;
    padding-bottom: var(--el-dialog-padding-primary);
    padding-top: 10px;

    .dialog-footer{
      display: block;

      .circular{
        display: inline-block;
        vertical-align: middle;
        margin-right: 5px;
        width: 24px;
        height: 24px;
      }

      .el-loading-text{
        display: inline-block;
        vertical-align: middle;
      }

      .el-loading-spinner{
        margin-top: -12px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .cus-dialog-container{
    .el-dialog{
      width: 100% !important;
    }
  }
}
</style>
