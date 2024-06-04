<template>
  <div class="fm-virtual-table">
    <div role="table" class="fm-virtual-table-main">
      <div class="fm-virtual-table__body">
        <div class="fm-virtual-table__body-wrapper" ref="bodyWrapper">
          <slot :rowWidthCalc="rowWidthCalc" :rowLeftWidthCalc="rowLeftWidthCalc" :rowRightWidthCalc="rowRightWidthCalc"></slot>
        </div>
      </div>
      <div class="fm-virtual-table__header">
        <div class="fm-virtual-table__header-content">
          <div class="fm-virtual-table__header-row"
            :style="{
              left: - (scrollLeft) + 'px',
              'min-width': `calc(${rowWidthCalc})`,
              width: `calc(${rowWidthCalc})`,
            }"
          >
            <div class="fm-virtual-table__header-cell"
              v-if="showControl"
              :style="{
                width: `50px`
              }"
            >
            </div>
            <div class="fm-virtual-table__header-cell"
              v-if="rowLeftWidthCalc != '0px'"
              :style="{
                width: `calc(${rowLeftWidthCalc})`
              }"
            >
            </div>
            <template v-for="column in columns">
              <div class="fm-virtual-table__header-cell"
                :key="column.key"
                v-if="columnDisplay(column.model) && !column.options.fixedColumn"
                :style="{
                  width: column.options.width,
                  
                }"
                :class="{
                  'is-require': column.options.required,
                  [column.options && column.options.customClass]: column.options.customClass ? true : false,
                }"
              >
                <div class="fm-virtual-table__header-cell-text" :title="column.options.hideLabel ? '' : column.name">
                  {{column.options.hideLabel ? '' : column.name}}
                </div>
              </div>
            </template>
            <div class="fm-virtual-table__header-cell"
              v-if="rowRightWidthCalc != '0px'"
              :style="{
                width: `calc(${rowRightWidthCalc})`
              }"
            >
            </div>
          </div>
        </div>
      </div>
    </div>
    <div role="table" class="fm-virtual-table-main fm-virtual-table-main__left">
      <div class="fm-virtual-table__body">
        <div>
          <slot name="left"></slot>
        </div>
      </div>
      <div class="fm-virtual-table__header">
        <div class="fm-virtual-table__header-content">
          <div class="fm-virtual-table__header-row">
            <div class="fm-virtual-table__header-cell"
              v-if="showControl"
              :style="{
                width: 50 + 'px'
              }"
            >
              <!-- Y9 -->
              <div class="fm-virtual-table__header-cell-text" style="margin: 0 auto;" title="Column">
                序号
              </div>
            </div>
            <template v-for="column in columns">
              <div class="fm-virtual-table__header-cell"
                :key="column.key"
                v-if="displayFields[column.model] && column.options.fixedColumn && column.options.fixedColumnPosition != 'right'"
                :style="{
                  width: column.options.width,
                }"
                :class="{
                  'is-require': column.options.required,
                  [column.options && column.options.customClass]: column.options.customClass ? true : false,
                }"
              >
                <div class="fm-virtual-table__header-cell-text" :title="column.options.hideLabel ? '' : column.name">
                  {{column.options.hideLabel ? '' : column.name}}
                </div>
              </div>
            </template>
            
          </div>
        </div>
      </div>
    </div>
    <div role="table" class="fm-virtual-table-main fm-virtual-table-main__right">
      <div class="fm-virtual-table__body">
        <div>
          <slot name="right"></slot>
        </div>
      </div>
      <div class="fm-virtual-table__header">
        <div class="fm-virtual-table__header-content">
          <div class="fm-virtual-table__header-row" >
            <template v-for="column in columns">
              <div class="fm-virtual-table__header-cell"
                :key="column.key"
                v-if="displayFields[column.model] && column.options.fixedColumn && column.options.fixedColumnPosition == 'right'"
                :style="{
                  width: column.options.width,
                }"
                :class="{
                  'is-require': column.options.required,
                  [column.options && column.options.customClass]: column.options.customClass ? true : false,
                }"
              >
                <div class="fm-virtual-table__header-cell-text" :title="column.options.hideLabel ? '' : column.name">
                  {{column.options.hideLabel ? '' : column.name}}
                </div>
              </div>
            </template>
            
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineAsyncComponent } from 'vue'
import _ from 'lodash'

export default {
  components: {
    GenerateElementItem: defineAsyncComponent(() => import('../GenerateElementItem.vue'))
  },
  props: ['columns', 'showControl', 'paging', 'pageSize', 'currentPage', 'displayFields', 'group', 'widget'],
  data () {
    return {
      scrollLeft: 0,
      tableData: _.cloneDeep(this.data)
    }
  },
  emits: ['remove-row', 'table-change', 'table-item-change'],
  inject: ['formHideFields'],
  mounted () {
    this.$refs['bodyWrapper'].addEventListener('scroll', () => {
      this.scrollLeft = this.$refs['bodyWrapper'].scrollLeft
    })
  },
  computed: {
    rowWidthCalc () {
      let width = this.showControl ? '50px' : '0px'

      this.columns.forEach(column => {
        if (this.displayFields[column.model]) {
          width += ' + ' + (column.options.width || '200px')
        }
      })

      return width
    },
    rowLeftWidthCalc () {
      let width = '0px'

      this.columns.forEach(column => {
        if (this.displayFields[column.model] && column.options.fixedColumn && column.options.fixedColumnPosition != 'right') {
          width += ' + ' + (column.options.width || '200px')
        }
      })

      return width
    },
    rowRightWidthCalc () {
      let width = '0px'

      this.columns.forEach(column => {
        if (this.displayFields[column.model] && column.options.fixedColumn && column.options.fixedColumnPosition == 'right') {
          width += ' + ' + (column.options.width || '200px')
        }
      })

      return width
    }
  },
  methods: {
    handleScroll ({scrollLeft, scrollTop}) {
      this.scrollLeft = scrollLeft
    },

    columnDisplay (model) {
      if (this.formHideFields.includes(this.group ? `${this.group}.${this.widget.model}.${model}` : `${this.widget.model}.${model}`)
      ) {
        return false
      } else {
        return true
      }
    },
  }
}
</script>

<style lang="scss">
.fm-virtual-table{
  width: 100%;
  position: relative;

  .el-scrollbar__bar{
    z-index: 3;
  }

  .fm-virtual-table-main__left{
    position: absolute;
    overflow: hidden;
    top: 0;
    left: 0;
    box-shadow: 2px 0 4px #0000000f;
    background: var(--el-bg-color);
    z-index: 2;
  }

  .fm-virtual-table-main__right{
    position: absolute;
    overflow: hidden;
    top: 0;
    right: 0;
    box-shadow: -2px 0 4px #0000000f;
    background: var(--el-bg-color);
    z-index: 2;
  }

  .fm-virtual-table-main{
    display: flex;
    flex-direction: column-reverse;
    overflow: hidden;

    &.fm-virtual-table-main__left{
      .fm-virtual-table__header{
        .fm-virtual-table__header-row{
          position: relative;
        }
      }
    }

    .fm-virtual-table__body{
      position: relative;

      .fm-virtual-table__body-wrapper{
        overflow: auto;
      }

      .fm-virtual-table__row{
        display: flex;
        align-items: center;
        border-bottom: 1px solid var(--el-border-color-lighter);
        transition: background-color 0.2s;
        white-space: nowrap;

        &.is-hover{
          background-color: var(--el-fill-color-light);
          transition: background-color 0.2s;

          .scope-index{
            display: none;
          }

          .scope-action{
            display: block;
          }
        }
      }

      .fm-virtual-table__row-cell{
        height: 100%;
        width: 100%;
        overflow: hidden;
        display: flex;
        align-items: center;
        padding: 0px 8px 0;//Y9
        flex: 0 0 auto;
        width: 200px;
        margin-bottom: -1px;

        .fm-form-item{
          width: 100%;
        }

        .ant-form-item{
          margin-bottom: 18px;

          .ant-form-item-explain{
            position: absolute;
            bottom: -24px;
            font-size: 12px;
          }
        }
      }
    }

    .fm-virtual-table__header{
      overflow: hidden;

      .fm-virtual-table__header-content{
        position: relative;
        overflow: hidden;
        height: 40px;
        border-bottom: 1px solid var(--el-border-color-lighter);
        background: var(--el-fill-color-light);
        width: 100%;
      }

      .fm-virtual-table__header-row{
        display: flex;
        position: absolute;
        height: 40px;

        .fm-virtual-table__header-cell{
          flex: 0 0 auto;
          width: 200px;
          padding: 0 8px;
          overflow: hidden;
          display: flex;
          align-items: center;
          height: 100%;
          user-select: none;
          font-weight: 700;
          border-bottom: 1px solid var(--el-border-color-lighter);

          &.is-require{
            >div::before{
              content: '*';
              color: #f56c6c;
              margin-right: 4px;
              background: transparent;
              vertical-align: top;
            }
          }
        }
      }
    }
  }
}

html.dark{
  .fm-virtual-table{

    .fm-virtual-table-main__left{
      box-shadow: 2px 0 4px #ffffff0f;
    }

    .fm-virtual-table-main__right{
      box-shadow: -2px 0 4px #ffffff0f;
    }
  }
}

</style>