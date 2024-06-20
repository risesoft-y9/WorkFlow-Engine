import _ from 'lodash'

export const CloneLayout = (data) => {
  if (data.type === 'grid') {
    let key = Math.random().toString(36).slice(-8)
    return {
      ...data,
      key,
      model: data.type + '_' + key,
      columns: data.columns.map (item => {
        return {
          ...item,
          list: item.list.map(colItem => {
            return CloneLayout(colItem)
          }),
          key: Math.random().toString(36).slice(-8)
        }
      })
    }
  } else if (data.type === 'col') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      list: data.list.map(item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'tabs' || data.type === 'collapse') {
    let key = Math.random().toString(36).slice(-8)
    return {
      ...data,
      key,
      model: data.type + '_' + key,
      tabs: data.tabs.map (item => {
        return {
          ...item,
          list: item.list.map (tabItem => {
            return CloneLayout(tabItem)
          })
        }
      })
    }
  } else if (data.type === 'table') {
    let key = Math.random().toString(36).slice(-8)
    return {
      ...data,
      key,
      model: data.type + '_' + key,
      tableColumns: data.tableColumns.map (item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'subform') {
    let key = Math.random().toString(36).slice(-8)
    return {
      ...data,
      key,
      model: data.type + '_' + key,
      list: data.list.map (item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'report') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      model: data.type + '_' + key,
      rows: data.rows.map(r => {
        return {
          ...r,
          columns: r.columns.map(c => {
            return {
              ...c,
              list: c.list.map(cItem => {
                return CloneLayout(cItem)
              }),
              key: Math.random().toString(36).slice(-8)
            }
          })
        }
      })
    }
  } else if (data.type === 'inline') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      model: data.type + '_' + key,
      list: data.list.map(item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'dialog') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      model: data.type + '_' + key,
      list: data.list.map(item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'card') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      model: data.type + '_' + key,
      list: data.list.map(item => {
        return CloneLayout(item)
      })
    }
  } else if (data.type === 'group') {
    let key = Math.random().toString(36).slice(-8)

    return {
      ...data,
      key,
      model: data.type + '_' + key,
      list: data.list.map(item => {
        return CloneLayout(item)
      })
    }
  } else {
    let key = Math.random().toString(36).slice(-8)
    return {
      ...data,
      key,
      model: data.type + '_' + key,
    }
  }
}