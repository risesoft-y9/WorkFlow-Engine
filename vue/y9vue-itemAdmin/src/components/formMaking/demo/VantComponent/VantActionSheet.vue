<template>
  <van-cell :class="{'van-cell-disabled': props.disabled}" :clickable="!props.disabled" :is-link="!props.disabled" v-bind="attrs" :title="props.title" :value="dataModel" @click="onShow" />
  <van-action-sheet 
    v-model:show="show"
    v-bind="props"
    :before-close="beforeCloseFunc"
    @select="onSelect"
  />
</template>

<script setup>
import { ref, useAttrs } from 'vue'

const attrs = useAttrs()

const emit = defineEmits(['update:modelValue'])

const show = ref(false)

const props = defineProps({
  modelValue:  {
    type: String,
    default: ''
  },
  actions: {
    type: Array,
    default: () => []
  },
  title: String,
  cancelText: String,
  description: String,
  closeable: {
    type: Boolean,
    default: true
  },
  closeIcon: {
    type: String,
    default: 'cross'
  },
  duration: {
    type: [Number, String],
    default: 0.3
  },
  round: {
    type: Boolean,
    default: true
  },
  overlay: {
    type: Boolean,
    default: true
  },
  overlayClass: [String, Array, Object],
  overlayStyle: Object,
  lockScroll: {
    type: Boolean,
    default: true
  },
  lazyRender: {
    type: Boolean,
    default: true
  },
  closeOnPopstate: {
    type: Boolean,
    default: true
  },
  closeOnClickAction: {
    type: Boolean,
    default: false
  },
  closeOnClickOverlay: {
    type: Boolean,
    default: true
  },
  safeAreaInsetBottom: {
    type: Boolean,
    default: true
  },
  teleport: undefined,
  beforeClose: String,
  disabled: {
    type: Boolean,
    default: false
  }
})

const dataModel = ref(props.modelValue)

const onShow = () => {
  if (!props.disabled) {
    show.value = true
  }
}

const onSelect = (item) => {
  show.value = false
  dataModel.value = item.name
  emit('update:modelValue', item.name)
}

const beforeCloseFunc = (action) => {
  props.beforeClose && new Function('action', props.beforeClose)(action)
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
