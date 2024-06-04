<template>
  <div
    class="widget-collapse widget-view"
    :class="{
      active: selectWidget.key && selectWidget.key == element.key,
      'is_hidden': element.options.hidden
    }"
    @click.stop="handleSelectWidget(index)"
    @mouseover.stop="handleMouseover"
    @mouseout="handleMouseout"
    ref="widgetCollapse"
  >
    <el-collapse v-model="tabActive"
      :accordion="element.options.accordion"
      :class="{
        [element.options && element.options.customClass]: element.options.customClass?true: false
      }"
    >
      <el-collapse-item
        :key="item.name" 
        :title="item.title" 
        :name="item.name" 
        v-for="(item, index) in element.tabs"
      >
        <draggable
          v-model="item.list"
          v-bind="{group:{name: 'people', put: handlePut}, ghostClass: 'ghost',animation: 200, handle: '.drag-widget'}"
          :no-transition-on-drag="true"
          @add="handleWidgetTabAdd($event, element, index)"
          @update="handleWidgetTabUpdate"
          class="widget-col-list"
          item-key="key"
        >
            <template #item="{element: tab, index: tabindex}">
                <widget-tab-item
                  v-if="tab.type === 'tabs'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)"  
                  :platform="platform"
                  :form-key="formKey"
                  :subform="subform"
                >
                </widget-tab-item>

                <widget-collapse
                  v-else-if="tab.type === 'collapse'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)"  
                  :platform="platform"
                  :form-key="formKey"
                  :subform="subform"
                >
                </widget-collapse>

                <widget-table
                  v-else-if="tab.type === 'table'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform"
                  :form-key="formKey"
                >
                </widget-table>

                <widget-sub-form
                  v-else-if="tab.type === 'subform'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform"
                  :form-key="formKey"
                >
                </widget-sub-form>

                <widget-card
                  v-else-if="tab.type === 'card'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform"
                  :form-key="formKey"
                >
                </widget-card>

                <widget-group
                  v-else-if="tab.type === 'group'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform"
                  :form-key="formKey"
                >
                </widget-group>

                <widget-inline
                  v-else-if="tab.type === 'inline'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform"
                  :form-key="formKey"
                ></widget-inline>

                <widget-report
                  v-else-if="tab.type === 'report'"
                  :key="tab.key"
                  :element="tab"
                  v-model:select="selectWidget" 
                  :index="tabindex" 
                  :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform" 
                  :form-key="formKey"
                  :subform="subform"
                >
                </widget-report>

                <widget-col-item
                  v-else-if="tab.type === 'grid'" 
                  :key="tab.key" 
                  :element="tab" 
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)" 
                  :platform="platform" 
                  :form-key="formKey"
                  :subform="subform"
                >
                </widget-col-item>

                <widget-form-item 
                  v-else
                  :key="tab.key" 
                  :element="tab" 
                  v-model:select="selectWidget" 
                  :index="tabindex" :data="item"
                  @select-change="handleSelectChange($event, item)"
                  :form-key="formKey"
                >
                </widget-form-item>

                
            </template>
          
        </draggable>
      </el-collapse-item>
    </el-collapse>

    <div class="widget-view-action widget-col-action" v-if="selectWidget.key == element.key">
      <i class="fm-iconfont icon-icon_clone" @click.stop="handleTabClone(index)" :title="$t('fm.tooltip.clone')"></i>
      <i class="fm-iconfont icon-trash" @click.stop="handleWidgetDelete(index)" :title="$t('fm.tooltip.trash')"></i>
    </div>

    <div class="widget-view-drag widget-col-drag" v-if="selectWidget.key == element.key">
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
import WidgetFormItem from './WidgetFormItem.vue'
import WidgetTable from './WidgetTable.vue'
import WidgetInline from './WidgetInline.vue'
import Draggable from 'vuedraggable/src/vuedraggable'
import _ from 'lodash'
import { CloneLayout } from '../util/layout-clone.js'
import { EventBus } from '../util/event-bus.js'
import { generateKeyToTD, generateKeyToTH, generateKeyToCol, fixDraggbleList, addClass, removeClass } from '../util'
import { defineAsyncComponent } from 'vue'

export default {
  name: 'widget-collapse',
  components: {
    WidgetFormItem,
    WidgetTable,
    WidgetInline,
    Draggable,
    WidgetColItem: defineAsyncComponent(() => import('./WidgetColItem.vue')),
    WidgetReport: defineAsyncComponent(() => import('./WidgetReport.vue')),
    WidgetSubForm: defineAsyncComponent(() => import('./WidgetSubForm.vue')),
    WidgetTabItem: defineAsyncComponent(() => import('./WidgetTabItem.vue')),
    WidgetCard: defineAsyncComponent(() => import('./WidgetCard.vue')),
    WidgetGroup: defineAsyncComponent(() => import('./WidgetGroup.vue'))
  },
  props: ['element', 'select', 'index', 'data', 'platform', 'formKey', 'subform'],
  emits: ['select-change', 'update:select'],
  data () {
    return {
      tabActive: this.element.tabs.map(t => t.name),
      selectWidget: this.select || {}
    }
  },
  methods: {
    handleMouseover (e) {
      addClass(this.$refs['widgetCollapse'], 'is-hover')
    },
    handleMouseout (e) {
      removeClass(this.$refs['widgetCollapse'], 'is-hover')
    },
    handlePut (a, b, c) {
      if (c.className.split(' ').indexOf('widget-dialog') >=0 || c.className.split(' ').indexOf('dialog-put') >= 0 || c.children[0].className.split(' ').indexOf('dialog-put') >= 0) {
        return false
      }
      return true
    },
    handleSelectWidget (index) {
      this.selectWidget = this.data.list[index]
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
    handleTabClone (index) {
      let cloneData = _.cloneDeep(this.data.list[index])

      this.data.list.splice(index + 1, 0, CloneLayout(cloneData))

      this.$nextTick(() => {
        this.selectWidget = this.data.list[index + 1]

        this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
      })
    },
    handleWidgetTabUpdate (evt) {
      this.$nextTick(() => {         EventBus.$emit('on-history-add-' + this.formKey)       })
    },
    handleWidgetTabAdd ($event, element, tabIndex) {
      const newIndex = $event.newIndex

      const key = Math.random().toString(36).slice(-8)

      fixDraggbleList(element.tabs[tabIndex].list, newIndex)

      element.tabs[tabIndex].list[newIndex] = _.cloneDeep(element.tabs[tabIndex].list[newIndex])
      element.tabs[tabIndex].list[newIndex] = {
        ...element.tabs[tabIndex].list[newIndex],
        options: {
          ...element.tabs[tabIndex].list[newIndex].options,
          remoteFunc: element.tabs[tabIndex].list[newIndex].options.remoteFunc || 'func_'+key,
          remoteOption: element.tabs[tabIndex].list[newIndex].options.remoteOption || 'option_'+key,
          tableColumn: false,
          subform: this.subform ? true : false
        },
        key: element.tabs[tabIndex].list[newIndex].key ? element.tabs[tabIndex].list[newIndex].key : key,
        model: element.tabs[tabIndex].list[newIndex].model ? element.tabs[tabIndex].list[newIndex].model : element.tabs[tabIndex].list[newIndex].type + '_' + key,
        rules: element.tabs[tabIndex].list[newIndex].rules ? [...element.tabs[tabIndex].list[newIndex].rules] : []
      }

      if (element.tabs[tabIndex].list[newIndex].type == 'report') {
        element.tabs[tabIndex].list[newIndex].rows = generateKeyToTD(element.tabs[tabIndex].list[newIndex].rows)
        element.tabs[tabIndex].list[newIndex].headerRow = generateKeyToTH(element.tabs[tabIndex].list[newIndex].headerRow)
      }

      if (element.tabs[tabIndex].list[newIndex].type == 'grid') {
        element.tabs[tabIndex].list[newIndex].columns = generateKeyToCol(element.tabs[tabIndex].list[newIndex].columns)
      }

      setTimeout(() => {
        this.selectWidget = element.tabs[tabIndex].list[newIndex]
        EventBus.$emit('on-history-add-' + this.formKey)   
      }, 50)
    },
    handleSelectChange (index, item) {
      setTimeout(() => {
        index >=0 ? (this.selectWidget = item.list[index]) : (this.selectWidget = {})
      })
    }
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
