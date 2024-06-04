<template>

  <div class="fm-outline-header">
    <el-input v-model="filterText" placeholder="Filter node" clearable />
  </div>

  <div class="fm-outline-content">
    <el-scrollbar ref="scrollRef">
      <el-tree
        ref="treeRef"
        class="filter-tree"
        :data="treeData"
        :props="defaultProps"
        default-expand-all
        highlight-current	
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        :indent="16"
        node-key="id"
        @node-click="onNodeClick"
        check-on-click-node	
      >
        <template #default="{ node }">
          <div class="custom-tree-node" :class="{'is-bind': node.data?.dataBind}">
            <span v-html="node.label"></span>
          </div>
        </template>
      </el-tree>
    </el-scrollbar>  
  </div>
</template>

<script>
export default {
  props: ['data', 'show'],
  inject: ['sizeObjInfo'],
  data () {
    return {
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'label',
        disabled: 'disabled'
      },
      treeData: [],
      currentKey: ''
    }
  },
  emits: ['select'],
  mounted () {
    this.treeData = this.loadNode(this.data)

    this.loadTreeWidthStyle()
  },
  methods: {
    filterNode (value, data) {
      if (!value) return true
      return data.label.includes(value)
    },

    loadNode (list) {
      let currentNode = []

      for (let i = 0; i < list.length; i++) {

        if (!list[i].type) continue

        currentNode.push({
          id: list[i].key,
          label: (list[i].icon ? `<i class="iconfont fm-iconfont ${list[i].icon}"></i>&nbsp;` : '')
            +`<span class="custom-tree-node-type">${list[i].type ? this.$t('fm.components.fields.' + list[i].type) : 'undefined'}</span>` 
            + (list[i].model ? `&nbsp;{ <span class="custom-tree-node-model">${list[i].model}</span> }` : ''),
          children: [],
          dataBind: list[i].options?.dataBind
        })

        if (list[i].type == 'grid') {
          currentNode[i].children = this.loadNode(list[i].columns)
        }
        if (list[i].type == 'col') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'report') {
          // 处理表格布局结构，便于大纲树展示
          let reportList = []

          for (let r = 0; r < list[i].rows.length; r++) {
            for (let c = 0; c < list[i].rows[r].columns.length; c++) {
              let td = list[i].rows[r].columns[c]

              if (!td.options.invisible) {
                reportList.push(td)
              }
            }
          }

          currentNode[i].children = this.loadNode(reportList)
        }
        if (list[i].type == 'td') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'tabs') {
          for (let t = 0; t < list[i].tabs.length; t++) {
            currentNode[i].children.push({
              label: list[i].tabs[t].label,
              children: [],
              disabled: true
            })

            currentNode[i].children[t].children = this.loadNode(list[i].tabs[t].list)
          }
        }
        if (list[i].type == 'collapse') {
          for (let t = 0; t < list[i].tabs.length; t++) {
            currentNode[i].children.push({
              label: list[i].tabs[t].title,
              children: [],
              disabled: true
            })

            currentNode[i].children[t].children = this.loadNode(list[i].tabs[t].list)
          }
        }
        if (list[i].type == 'inline') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'table') {
          currentNode[i].children = this.loadNode(list[i].tableColumns)
        }
        if (list[i].type == 'subform') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'dialog') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'card') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
        if (list[i].type == 'group') {
          currentNode[i].children = this.loadNode(list[i].list)
        }
      }

      return currentNode
    },
    loadTreeWidthStyle () {
      if (!this.show) return

      document.querySelector('.fm-outline-content .el-scrollbar__view').removeAttribute('style')

      let realWidth = document.querySelector('.fm-outline-content .el-scrollbar__wrap').scrollWidth

      document.querySelector('.fm-outline-content .el-scrollbar__view').setAttribute('style', `width: ${realWidth <= 250 ? realWidth : (realWidth + 5)}px`)
    },

    onNodeClick (data, node) {

      if (data.id) {
        this.$emit('select', data.id)
        this.currentKey = data.id
      } else {
        this.$refs.treeRef?.setCurrentKey(this.currentKey)
      }
    },

    setCurrentKey (key, isScrollTo) {
      this.currentKey = key

      this.$refs.treeRef?.setCurrentKey(key)

      setTimeout(() => {
        isScrollTo && this.scrollTo()
      }, 200)
    },
    scrollTo () {
      // 计算当前选中元素距离容器顶部距离
      let y = document.querySelector('.fm-outline-content .is-current')?.offsetTop

      let containerHeight = document.querySelector('.fm-outline-content').offsetHeight

      let containerTop = document.querySelector('.fm-outline-content').getBoundingClientRect().top
      let scorllTop = document.querySelector('.fm-outline-content .el-scrollbar__view').getBoundingClientRect().top

      let top = scorllTop - containerTop

      if (y > -top + containerHeight || y < -top) {
        this.$refs.scrollRef.setScrollTop(y)
      }
    }
  },
  watch: {
    filterText (val) {
      this.$refs.treeRef.filter(val)
    },
    data: {
      deep: true, 
      handler (val) {
        this.treeData = this.loadNode(val)

        this.loadTreeWidthStyle()

        this.$nextTick(() => {
          this.$refs.treeRef?.setCurrentKey(this.currentKey)

          this.$refs.treeRef.filter(this.filterText)
        })
      }
    }
  }
}
</script>

<style lang="scss">
.fm-outline-header{
  padding: 12px;
  height: 56px;

}
.fm-outline-content{
  height: calc(100% - 56px);

  .custom-tree-node{
    display: inline-block;
    font-size: v-bind('sizeObjInfo.smallFontSize');

    .fm-iconfont{
      vertical-align: top;
      font-size: v-bind('sizeObjInfo.baseFontSize');
    }

    &.is-bind{
      >span>.custom-tree-node-model{
        color: #67C23A;
      }
    }
  }

  .el-tree-node>.el-tree-node__children{
    overflow: unset;
  }

  .el-tree-node.is-current{

    >.el-tree-node__content{
      background: #c6e2ff;

      >.custom-tree-node{
        // background: #c6e2ff;
      }
    }
  }

  .filter-tree{
    margin-bottom: 10px;
  }
}

html.dark{
  .fm-outline-content{
    .el-tree-node.is-current{

      >.el-tree-node__content{
        background: #213d5b;
      }
    }
  }
}
</style>
