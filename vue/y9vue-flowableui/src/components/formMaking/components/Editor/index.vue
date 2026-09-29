<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-06-11 17:47:08
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-12 09:55:54
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\components\formMaking\components\Editor\index.vue
-->
<template>
  <div :style="customStyle">
    <QuillEditor
      v-model:content="editorValue"
      :toolbar="toolbar"
      :enable="!disabled"
      :read-only="disabled"
      content-type="html"
      ref="quillEditor"
    />
  </div>
  
</template>

<script setup>
import { nextTick, ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  toolbar: {type: Array},
  disabled: {type: Boolean},
  customStyle: {type: Object}
})

const editorValue = ref(props.modelValue)

let newValue = props.modelValue

const emit = defineEmits(['update:modelValue'])

const quillEditor = ref(null)

watch(() => props.modelValue, (val) => {
  if (newValue === val) return

  quillEditor.value.setHTML(val)

  nextTick(() => {
    let q = quillEditor.value.getQuill()
    q.setSelection(val.length, 0, 'api')
    q.focus()
  })
})

watch(editorValue, (val) => {
  emit('update:modelValue', val)
  newValue = val
})
</script>