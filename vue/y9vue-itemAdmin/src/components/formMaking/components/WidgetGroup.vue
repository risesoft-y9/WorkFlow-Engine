<template>
  <div class="widget-group widget-view"
    :class="{
      active: select.key && select.key == element.key,
      'is_hidden': element.options.hidden
    }"
    @click.stop="handleSelectWidget(index)"
    @mouseover.stop="handleMouseover"
    @mouseout="handleMouseout"
    ref="widgetGroup"
  >

    <el-form-item 
      :label="element.options.hideLabel ? '' : element.name"
      :label-width="element.options.hideLabel ? '0px' : (element.options.isLabelWidth ? element.options.labelWidth + 'px' : '')"
      :class="{
        [element.options && element.options.customClass] : element.options.customClass ? true : false,
        'fm-label-wrap': element.options.labelWrap
      }"
    >
      <div v-if="element.options.tip" class="fm-item-tooltip" v-html="element.options.tip.replace(/\n/g, '<br/>')"></div>
      <div
        :style="{
          width: element.options.width,
        }"
      >
        <draggable
          v-model="element.list"
          v-bind="{group:{name: 'people', put: handlePut}, ghostClass: 'ghost',animation: 200, handle: '.drag-widget'}"
          :no-transition-on-drag="true"
          @add="handleWidgetGroupAdd($event, element)"
          @update="handleWidgetGroupUpdate"
          class="widget-col-list"
          :class="{
            [element.options && element.options.customClass]: element.options.customClass ? true : false
          }"
          item-key="key"
        >
          <template #item="{element:item, index}">
            <widget-tab-item
              v-if="item.type === 'tabs'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index" 
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-tab-item>

            <widget-collapse
              v-else-if="item.type === 'collapse'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index" 
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-collapse>

            <widget-table
              v-else-if="item.type === 'table'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index" 
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-table>

            <widget-sub-form
              v-else-if="item.type === 'subform'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index" 
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-sub-form>

            <widget-card
              v-else-if="item.type === 'card'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index" 
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-card>

            <widget-inline
              v-else-if="item.type === 'inline'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget"
              :index="index"
              :data="element"
              @select-change="handleSelectChange($event, item)"
              :platform="platform"
              :form-key="formKey"
            ></widget-inline>

            <widget-report
              v-else-if="item.type === 'report'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index"
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-report>

            <widget-group
              v-else-if="item.type === 'group'"
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index"
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform" 
              :form-key="formKey"
            >
            </widget-group>

            <widget-form-item 
              v-else-if="item.type !== 'grid'" 
              :key="item.key" 
              :element="item" 
              v-model:select="selectWidget" 
              :index="index" :data="element"
              @select-change="handleSelectChange($event, item)"
              :form-key="formKey"
            >
            </widget-form-item>

            <widget-col-item
              v-else
              :key="item.key" 
              :element="item"
              v-model:select="selectWidget" 
              :index="index"
              :data="element"
              @select-change="handleSelectChange($event, item)" 
              :platform="platform"
              :form-key="formKey"
            >
            </widget-col-item>
          </template>
        </draggable>
      </div>
    </el-form-item>

    <div class="widget-view-action widget-subform-action" v-if="select.key == element.key">
      <i class="fm-iconfont icon-icon_clone" @click.stop="handleWidgetClone(index)" :title="$t('fm.tooltip.clone')"></i>
      <i class="fm-iconfont icon-trash" @click.stop="handleWidgetDelete(index)" :title="$t('fm.tooltip.trash')"></i>
    </div>

    <div class="widget-view-drag widget-subform-drag" v-if="select.key == element.key">
      <i class="fm-iconfont icon-drag drag-widget"></i>
    </div>

    <div class="widget-view-model " :style="{'color': element.options.dataBind ? '' : '#666'}">
      <span>{{element.model}}</span>
    </div>

    <div class="widget-view-type ">
      <span>{{element.type ? this.$t('fm.components.fields.' + element.type) : ''}}</span>
    </div>
  </div>
</template>

<script>
import Draggable from 'vuedraggable/src/vuedraggable'
import WidgetFormItem from './WidgetFormItem.vue'
import WidgetInline from './WidgetInline.vue'
import { defineAsyncComponent } from 'vue'
import _ from 'lodash'
import { CloneLayout } from '../util/layout-clone.js'
import { EventBus } from '../util/event-bus.js'
import { generateKeyToTD, generateKeyToTH, generateKeyToCol, fixDraggbleList, addClass, removeClass } from '../util'

export default {
  name: 'widget-group',
  components: {
    Draggable,
    WidgetFormItem,
    WidgetInline,
    WidgetColItem: defineAsyncComponent(() => import('./WidgetColItem.vue')),
    WidgetTabItem: defineAsyncComponent(() => import('./WidgetTabItem.vue')),
    WidgetReport: defineAsyncComponent(() => import('./WidgetReport.vue')),
    WidgetCollapse: defineAsyncComponent(() => import('./WidgetCollapse.vue')),
    WidgetTable: defineAsyncComponent(() => import('./WidgetTable.vue')),
    WidgetSubForm: defineAsyncComponent(() => import('./WidgetSubForm.vue')),
    WidgetCard: defineAsyncComponent(() => import('./WidgetCard.vue'))
  },
  props: ['element', 'select', 'index', 'data', 'platform', 'formKey', 'subform'],
  emits: ['select-change', 'update:select'],
  data () {
    return {
      selectWidget: this.select || {}
    }
  },
  methods: {
    handleMouseover (e) {
      addClass(this.$refs['widgetGroup'], 'is-hover')
    },
    handleMouseout (e) {
      removeClass(this.$refs['widgetGroup'], 'is-hover')
    },
    handlePut (a, b, c) {
      if (
        c.className.split(' ').indexOf('widget-dialog') >= 0 ||
        c.className.split(' ').indexOf('dialog-put') >= 0 || 
        c.children[0].className.split(' ').indexOf('dialog-put') >= 0) {
        return false
      }
      return true
    },
    handleSelectWidget (index) {
      this.$emit('update:select', this.data.list[index])
    },
    handleSelectChange (index, item) {
      setTimeout(() => {
        index >=0 ? (
          this.$emit('update:select', this.element.list[index])
        ) : (
          this.$emit('update:select', this.data.list[this.index])
        )
      })
    },
    handleWidgetClone (index) {
      let cloneData = _.cloneDeep(this.data.list[index])

      this.data.list.splice(index + 1, 0, CloneLayout(cloneData))

      this.$nextTick(() => {
        this.$emit('update:select', this.data.list[index + 1])

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      })
    },
    handleWidgetDelete (index) {
      if (this.data.list.length == 1) {
        this.$emit('select-change', -1)
      } else {
        if (this.data.list.length - 1 == index) {
          this.$emit('select-change', index - 1)
        } else {
          this.$emit('select-change', index)
        }
      }
      
      this.data.list.splice(index, 1)

      setTimeout(() => {
        EventBus.$emit('on-history-add-' + this.formKey)
      }, 20)
    },
    handleWidgetGroupAdd ($event, element) {
      const newIndex = $event.newIndex

      const key = Math.random().toString(36).slice(-8)

      fixDraggbleList(element.list, newIndex)

      element.list[newIndex] = _.cloneDeep(element.list[newIndex])
      element.list[newIndex] = {
        ...element.list[newIndex],
        options: {
          ...element.list[newIndex].options,
          remoteFunc: element.list[newIndex].options.remoteFunc || 'func_'+key,
          remoteOption: element.list[newIndex].options.remoteOption || 'option_'+key,
          tableColumn: false,
          subform: false
        },
        key: element.list[newIndex].key ? element.list[newIndex].key : key,
        model: element.list[newIndex].model ? element.list[newIndex].model : element.list[newIndex].type + '_' + key,
        rules: element.list[newIndex].rules ? [...element.list[newIndex].rules] : []
      }

      if (element.list[newIndex].type == 'report') {
        element.list[newIndex].rows = generateKeyToTD(element.list[newIndex].rows)
        element.list[newIndex].headerRow = generateKeyToTH(element.list[newIndex].headerRow)
      }

      if (element.list[newIndex].type == 'grid') {
        element.list[newIndex].columns = generateKeyToCol(element.list[newIndex].columns)
      }

      this.$nextTick(() => {
        this.selectWidget = element.list[newIndex]
        EventBus.$emit('on-history-add-' + this.formKey) 
      })
    },
    handleWidgetGroupUpdate () {
      EventBus.$emit('on-history-add-' + this.formKey)
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