<template>
  <van-uploader
    v-model="dataModel"
    v-bind="props"
    :after-read="afterReadFunc"
    :before-read="beforeReadFunc"
    :before-delete="beforeDeleteFunc"
  />
</template>

<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['update:modelValue'])

const props = defineProps({
  modelValue:  {
    type: Array,
    default: () => []
  },
  accept: {
    type: String,
    default: 'image/*'
  },
  name: [Number, String],
  previewSize: {
    type: [Number, String, Array],
    default: '80px'
  },
  previewImage: {
    type: Boolean,
    default: true
  },
  previewFullImage: {
    type: Boolean,
    default: true
  },
  previewOptions: Object,
  multiple: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  deletable: {
    type: Boolean,
    default: true
  },
  showUpload: {
    type: Boolean,
    default: true
  },
  lazyLoad: {
    type: Boolean,
    default: false
  },
  capture: String,
  afterRead: String,
  beforeRead: String,
  beforeDelete: String,
  maxSize: [Number, String],
  maxCount: [Number, String],
  resultType: {
    type: String,
    default: 'dataUrl'
  },
  uploadText: String,
  imageFit: {
    type: String,
    default: 'cover'
  },
  uploadIcon: {
    type: String,
    default: 'photograph'
  }
})

const dataModel = ref(props.modelValue)

// "afterRead": "console.log(file)"
const afterReadFunc = (file, detail) => {
  props.afterRead && new Function('file', 'detail', props.afterRead)(file, detail)
}

const beforeReadFunc = (file, detail) => {
  props.beforeRead && new Function('file', 'detail', props.beforeRead)(file, detail)
}

const beforeDeleteFunc = (file, detail) => {
  props.beforeDelete && new Function('file', 'detail', props.beforeDelete)(file, detail)
}

watch(dataModel, (val) => {
  emit('update:modelValue', val)
})
</script>
