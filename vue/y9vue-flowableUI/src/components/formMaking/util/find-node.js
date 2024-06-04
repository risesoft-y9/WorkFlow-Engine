const findChildren = (stack, data, key, value) => {
  let findItem = data.find(item => item[key] == value)

  if (findItem) {
    stack.push({
      type: findItem.type,
      key: findItem.key,
      model: findItem.model
    })
  } else {
    for (let i = 0; i < data.length; i++) {

      const item = data[i]

      if (item.type === 'grid') {
        for (let j = 0; j < item.columns.length; j++) {

          if (item.columns[j].list?.length) {
            stack.push({
              type: item.type,
              key: item.key,
              model: item.model
            })
  
            let res = findChildren([...stack], item.columns[j].list, key, value)
  
            if (res.length > stack.length) {
              return res
            }
  
            stack.pop()
          }
        }
      }
      if (item.type === 'table') {

        if (item.tableColumns.length) {
          stack.push({
            type: item.type,
            key: item.key,
            model: item.model
          })

          let res = findChildren([...stack], item.tableColumns, key, value)

          if (res.length > stack.length) {
            return res
          }

          stack.pop()
        }
      }
      if (item.type === 'subform' || item.type === 'inline' || item.type === 'dialog'
        || item.type === 'card' || item.type === 'group'
      ) {
        if (item.list.length) {
          stack.push({
            type: item.type,
            key: item.key,
            model: item.model
          })

          let res = findChildren([...stack], item.list, key, value)

          if (res.length > stack.length) {
            return res
          }

          stack.pop()
        }
      }
      if (item.type === 'tabs' || item.type === 'collapse') {
        for (let j = 0; j < item.tabs.length; j++) {
          if (item.tabs[j].list?.length) {
            stack.push({
              type: item.type,
              key: item.key,
              model: item.model
            })

            let res = findChildren([...stack], item.tabs[j].list, key, value)

            if (res.length > stack.length) {
              return res
            }

            stack.pop()
          }
        }
      }
      if (item.type === 'report') {

        for (let r = 0; r < item.rows.length; r++) {

          for (let c = 0; c < item.rows[r].columns.length; c++) {
            
            if (item.rows[r].columns[c].list?.length) {
              stack.push({
                type: item.type,
                key: item.key,
                model: item.model
              })
  
              let res = findChildren([...stack], item.rows[r].columns[c].list, key, value)
  
              if (res.length > stack.length) {
                return res
              }
  
              stack.pop()
            }
          }

        }
      }
    }
  }

  return stack
}

export const findModelNode = (data, key, value) => {
  let res = []

  let array = findChildren([], data, key, value)

  for (let i = 0; i < array.length; i++) {
    if (array[i].type !== 'grid'
      && array[i].type !== 'inline'
      && array[i].type !== 'card'
      && array[i].type !== 'tabs'
      && array[i].type !== 'collapse'
      && array[i].type !== 'report'
      || array.length === i + 1
    ) {
      res.push(array[i].model)
    }
  }

  return res
}

export const findModelNodeString = (data, key, value) => {
  let res = findModelNode(data, key, value)

  if (res.length > 1) {
    res.pop()
    return res.join('.') + '.'
  } else {
    return ''
  }
}