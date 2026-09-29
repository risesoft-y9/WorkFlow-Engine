<template>
  <div :id="id"></div>
</template>

<script>
import { Chart } from '@antv/g2';

export default {
  name: 'custom-chart',
  props: {
    modelValue: {
      type: Array,
      default: () => ([])
    },
    height: {
      type: Number,
      default: 300
    },
    width: {
      type: Number,
      default: 500
    }
  },
  data() {
    return {
      dataModel: this.modelValue,
      id: 'chart-' + Math.random().toString(36).slice(-8),
      chart: null
    }
  },
  mounted () {
    this.chart = new Chart({
      container: this.id,
      height: this.height,
      width: this.width
    });

    this.chart.data(this.dataModel);
    this.chart.scale('sales', {
      nice: true,
    });

    this.chart.tooltip({
      showMarkers: false
    });
    this.chart.interaction('active-region');

    this.chart.interval().position('year*sales');

    this.chart.render();
  },
  beforeUnmount () {
    this.chart.destroy()
  },
  watch: {
    modelValue (val) {
      this.dataModel = val

      this.chart.changeData(val)
    },
    dataModel (val) {
      this.$emit('update:modelValue', val)

      this.chart.changeData(val)
    }
  }
}
</script>