<template>
  <van-cell :class="{'van-cell-disabled': props.disabled}" :clickable="!props.disabled" :is-link="!props.disabled" v-bind="attrs" :title="props.title" :value="date" @click="onShow" />
  <van-calendar 
    v-model:show="show" 
    v-bind="props"
    @confirm="onConfirm"
  />
</template>

<script setup>
import { ref, useAttrs } from 'vue'

const attrs = useAttrs()

const emit = defineEmits(['update:modelValue'])

const show = ref(false)
const date = ref(props.modelValue)

const props = defineProps({
  modelValue:  {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'single'
  },
  title: {
    type: String,
    default: '日期选择'
  },
  color: {
    type: String,
    default: '#ee0a24'
  },
  minDate: undefined,
  maxDate: undefined,
  defaultDate: undefined,
  rowHeight: {
    type: [Number, String],
    default: 64
  },
  formatter: String,
  poppable: {
    type: Boolean,
    default: true
  },
  lazyRender: {
    type: Boolean,
    default: true
  },
  showMark: {
    type: Boolean,
    default: true
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  showSubtitle: {
    type: Boolean,
    default: true
  },
  showConfirm: {
    type: Boolean,
    default: true
  },
  readonly: {
    type: Boolean,
    default: false
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  confirmDisabledText: {
    type: String,
    default: '确定'
  },
  firstDayOfWeek: {
    default: 0
  },
  position: {
    type: String,
    default: 'bottom'
  },
  round: {
    type: Boolean,
    default: true
  },
  closeOnPopstate: {
    type: Boolean,
    default: true
  },
  closeOnClickOverlay: {
    type: Boolean,
    default: true
  },
  safeAreaInsetTop: {
    type: Boolean,
    default: false
  },
  safeAreaInsetBottom: {
    type: Boolean,
    default: true
  },
  teleport: undefined,
  maxRange: [Number, String],
  rangePrompt: String,
  showRangePrompt: {
    type: Boolean,
    default: true
  },
  allowSameDay: {
    type: Boolean,
    default: false
  },
  maxRange: [Number, String],
  rangePrompt: String,
  disabled: {
    type: Boolean,
    default: false
  }
})

const formatDate = (date) => `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`

const onShow = () => {
  if (!props.disabled) {
    show.value = true
  }
}

const onConfirm = (value) => {
  show.value = false
  if (props.type == 'single') {
    date.value = formatDate(value)
  }
  if (props.type == 'multiple') {
    date.value = value.map(item => formatDate(item)).join(',')
  }
  if (props.type == 'range') {
    const [start, end] = value
    date.value = `${formatDate(start)} - ${formatDate(end)}`
  }
  emit('update:modelValue', date.value)
}
</script>

<style>
.van-cell-disabled .van-cell__title span{
  color: #c8c9cc;
}
.van-cell-disabled .van-cell__value span{
  color: #c8c9cc;
}
</style>
