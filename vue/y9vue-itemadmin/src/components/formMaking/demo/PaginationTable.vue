<template>
  <div>
    <div style="height: 400px">
      <el-auto-resizer>
        <template #default="{ height, width }">
          <el-table-v2
            :columns="columns"
            :data="data"
            :width="width"
            :height="height"
            fixed
          />
        </template>
      </el-auto-resizer>
    </div>
    <el-pagination 
      background 
      layout="prev, pager, next" 
      :total="1000" 
      v-model:current-page="currentPage"
      @current-change="loadPageData"
    />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'

const props = defineProps({
  modelValue:  {
    type: Array,
    default: () => []
  },
  columns: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'on-load'])

const data = ref(props.modelValue)

const currentPage = ref(1)

const loadPageData = (index) => {
  emit('on-load', index)
}

onMounted(() => {
  emit('on-load', currentPage.value)
})

watch(() => props.modelValue, (val) => {
  data.value = val
})
</script>
