<template>
  <cus-dialog
    :visible="visible"
    @on-close="handleClose"
    width="1000px"
    form
    :title="$t('fm.actions.import')"
    :action="false"
  >
    <import-json-index :library-list="libraryList" @load-json="handleLoadJson"></import-json-index>
    
  </cus-dialog>
</template>

<script setup>
import {ref} from 'vue'
import CusDialog from '../CusDialog.vue'
import ImportJsonIndex from './index.vue'

const visible = ref(false)

const libraryList = ref([])

const emit = defineEmits(['load-json'])

const open = (list) => {
  visible.value = true

  libraryList.value = list
}

const handleClose = () => {
  visible.value = false
}

const handleLoadJson = (json) => {
  emit('load-json', json)

  visible.value = false
}

defineExpose({
  open
})
</script>