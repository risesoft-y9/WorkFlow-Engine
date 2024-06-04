<template>
  <div class="fm-virtual-table__row-cell"
    :style="{
      width: width,
      'min-height': (rowHeight - 0.5) + 'px',
    }"
  >
    <slot name="default"></slot>
  </div>
</template>

<script>
export default {
  props: ['width', 'rowHeight']
}
</script>