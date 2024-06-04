<template>
<div
  class="widget-table-view" 
  :class="{
    active: selectWidget.key == element.key, 
    'is_req': element.options.required,
    'is_hidden': element.options.hidden,
    'mobile': platform == 'mobile'
  }"
  @click.stop="handleSelectWidget(index)"
  @mouseover.stop="handleMouseover"
  @mouseout="handleMouseout"
  ref="widgetTableItem"
  :style="{
    width: platform != 'mobile' ? (!element.options.width ? '200px' : element.options.width) : ''
  }"
>
  <el-table
    v-if="platform != 'mobile'"
    row-class-name="widget-table-row"
    :data="[{}]">
    <el-table-column :label="element.options.hideLabel ? '' : element.name" 
      :class-name="element.options?.customClass"
      :label-class-name="element.options.required ? 'required' : ''">
      <widget-element-item :element="element" :is-table="true"></widget-element-item>
    </el-table-column>
  </el-table>

  <el-form-item v-if="platform == 'mobile'"
    :label="element.options.hideLabel ? '' : element.name"
    :label-width="element.options.hideLabel ? '0px' : (element.options.isLabelWidth ? element.options.labelWidth + 'px' : '')"
    :class="{
      [element.options && element.options.customClass]: element.options.customClass ? true : false,
      'fm-label-wrap': element.options.labelWrap
    }"
  >
    <widget-element-item :element="element" :is-table="true"></widget-element-item>
  </el-form-item>

  <div class="widget-view-action" v-if="selectWidget.key == element.key">
    <i class="fm-iconfont icon-icon_clone" @click.stop="handleWidgetClone(index)" :title="$t('fm.tooltip.clone')"></i>
    <i class="fm-iconfont icon-trash" @click.stop="handleWidgetDelete(index)" :title="$t('fm.tooltip.trash')"></i>
  </div>
  <div class="widget-view-drag" v-if="selectWidget.key == element.key">
    <i class="fm-iconfont icon-drag drag-widget"></i>
  </div>

  <div class="widget-view-model" :style="{'color': element.options.dataBind ? '' : '#666'}">
    <span>{{element.model}}</span>
  </div>

  <div class="widget-view-type ">
    <span>{{element.type ? this.$t('fm.components.fields.' + element.type) : ''}}</span>
  </div>
</div>
</template>

<script>
import WidgetElementItem from './WidgetElementItem.vue'
import _ from 'lodash'
import { EventBus } from '../util/event-bus.js'
import { addClass, removeClass } from '../util'

export default {
  components: {
    WidgetElementItem
  },
  props: ['element', 'select', 'index', 'data', 'platform', 'formKey'],
  emits: ['select-change', 'update:select'],
  data () {
    return {
      selectWidget: this.select || {}
    }
  },
  methods: {
    handleMouseover (e) {
      addClass(this.$refs['widgetTableItem'], 'is-hover')
    },
    handleMouseout (e) {
      removeClass(this.$refs['widgetTableItem'], 'is-hover')
    },
    handleSelectWidget (index) {
      this.$emit('update:select', this.data[index])
    },
    handleWidgetDelete (index) {
      if (this.data.length == 1) {
        this.$emit('select-change', -1)
      } else {
        if (this.data.length - 1 == index) {
          this.$emit('select-change', index - 1)
        } else {
          this.$emit('select-change', index)
        }
      }

      this.$nextTick(() => {
        this.data.splice(index, 1)

        setTimeout(() => {
          EventBus.$emit('on-history-add-' + this.formKey)
        }, 20)
      })
    },
    handleWidgetClone (index) {
      const key = Math.random().toString(36).slice(-8)
      let cloneData = {
        ..._.cloneDeep(this.data[index]),
        key,
        model: this.data[index].type + '_' + key,
      }

      this.data.splice(index + 1, 0, cloneData)

      this.$nextTick(() => {
        this.$emit('update:select', this.data[index + 1])

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      })
    },
  },
  watch: {
    select (val) {
      this.selectWidget = val
    },
    selectWidget (val) {
      this.$emit('update:select', val)
    }
  }
}
</script>
