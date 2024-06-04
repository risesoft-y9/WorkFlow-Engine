<template>
  <div class="fm-virtual-table__row"
    @mouseover="handleMouseover"
    @mouseout="handleMouseout"
    :ref="'tableRow'"
    :style="{
      'min-width': `calc(${rowWidthCalc})`,
      'min-height': rowHeight + 'px'
    }"
    :class="`row_${rowIndex} row_${tableKey}`"
  >
    <slot name="default" :rowHeight="rowHeight"></slot>
  </div>
</template>

<script>
import { addClass, removeClass } from '../../util'

export default{
  props: ['rowWidthCalc', 'rowIndex', 'tableKey'],
  data () {
    return {
      resizeObserver: null,
      eles: [],
      rowHeight: 0
    }
  },
  mounted () {
    this.eles = document.querySelectorAll(`.fm-virtual-table__row.row_${this.rowIndex}>div`)

    this.resizeObserver = new ResizeObserver((mutationList) => {
      this.rowHeight = this.clacRowHeight()
    })

    this.eles.forEach(ele => {
      this.resizeObserver.observe(ele)
    })
  },
  unmounted () {
    this.eles.forEach(ele => {
      this.resizeObserver.unobserve(ele)
    })
  },
  methods: {
    handleMouseover () {
      addClass(document.querySelectorAll('.fm-virtual-table__row.main.row_'+this.tableKey)[this.rowIndex], 'is-hover')
      addClass(document.querySelectorAll('.fm-virtual-table__row.left.row_'+this.tableKey)[this.rowIndex], 'is-hover')
      addClass(document.querySelectorAll('.fm-virtual-table__row.right.row_'+this.tableKey)[this.rowIndex], 'is-hover')
    },
    handleMouseout () {
      removeClass(document.querySelectorAll('.fm-virtual-table__row.main.row_'+this.tableKey)[this.rowIndex], 'is-hover')
      removeClass(document.querySelectorAll('.fm-virtual-table__row.left.row_'+this.tableKey)[this.rowIndex], 'is-hover')
      removeClass(document.querySelectorAll('.fm-virtual-table__row.right.row_'+this.tableKey)[this.rowIndex], 'is-hover')
    },
    clacRowHeight () {
      let curHeight = 0
      this.eles.forEach(ele => {
        if (curHeight < ele.offsetHeight + 0.5) {
          curHeight = ele.offsetHeight + 0.5
        }
      })

      return curHeight
    }
  }
}
</script>