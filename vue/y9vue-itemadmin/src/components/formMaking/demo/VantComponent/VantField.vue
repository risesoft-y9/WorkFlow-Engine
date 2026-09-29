<template>
  <van-cell-group inset>
    <van-field 
      v-model="dataModel" 
      v-bind="props"
      :formatter="fieldFormatter"
    />
  </van-cell-group>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue:  {
    type: [String, Number],
    default: ''
  },
  label: String,
  name: String,
  id: String,
  type: {
    default: 'text'
  },
  size: String,
  maxlength: [String, Number],
  placeholder: String,
  border: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  colon: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  center: {
    type: Boolean,
    default: false
  },
  clearable: {
    type: Boolean,
    default: false
  },
  clearIcon: {
    type: String,
    default: 'clear'
  },
  clearTrigger: {
    default: 'focus'
  },
  clickable: {
    type: Boolean,
    default: false
  },
  isLink: {
    type: Boolean,
    default: false
  },
  autofocus: {
    type: Boolean,
    default: false
  },
  showWordLimit: {
    type: Boolean,
    default: false
  },
  error: {
    type: Boolean,
    default: false
  },
  errorMessage: String,
  errorMessageAlign: {
    default: 'left'
  },
  formatter: String,
  formatTrigger: {
    default: 'onChange'
  },
  arrowDirection: {
    type: String,
    default: 'right'
  },
  labelClass: [String, Array, Object],
  labelWidth: [String, Number],
  labelAlign: {
    default: 'left'
  },
  inputAlign: {
    default: 'left'
  },
  autosize: {
    type: Boolean,
    default: false
  },
  leftIcon: String,
  rightIcon: String,
  iconPrefix: {
    type: String,
    default: 'van-icon'
  },
  rules: Array,
  autocomplete: String,
  enterkeyhint: String
})

const emit = defineEmits(['update:modelValue'])

const dataModel = ref(props.modelValue)

// 配置示例："formatter": "return val.replace(/\\d/g, '')"
const fieldFormatter = (val) => {
  return val && props.formatter ? new Function('val', props.formatter)(val) : val
}

watch(dataModel, (val) => {
  emit('update:modelValue', val)
})
</script>
