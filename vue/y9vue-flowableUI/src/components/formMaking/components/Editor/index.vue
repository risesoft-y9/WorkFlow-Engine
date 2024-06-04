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