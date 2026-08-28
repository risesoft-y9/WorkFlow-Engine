export const updateClassName = (genList, fields, className, updateType) => {
  for (let i = 0; i < genList.length; i++) {
    if (genList[i].type === 'grid') {
      genList[i].columns.forEach(item => {
        updateClassName(item.list, fields, className, updateType)
      })
    } else if (genList[i].type === 'tabs') {
      genList[i].tabs.forEach(item => {
        updateClassName(item.list, fields, className, updateType)
      })
    } else if (genList[i].type === 'collapse') {
      genList[i].tabs.forEach(item => {
        updateClassName(item.list, fields, className, updateType)
      })
    } else if (genList[i].type === 'report') {
      genList[i].rows.forEach(row => {
        row.columns.forEach(column => {
          updateClassName(column.list, fields, className, updateType)
        })
      })
    } else if (genList[i].type === 'inline') {
      updateClassName(genList[i].list, fields, className, updateType)
    } else if (genList[i].type === 'card') {
      updateClassName(genList[i].list, fields, className, updateType)
    } else {
      if (fields.length === 1) {
        if (genList[i].model === fields[0]) {
          if (updateType == 'add' && !genList[i].options.customClass.split(' ').includes(className)) {
        
            genList[i].options.customClass = [...genList[i].options.customClass.split(' '), className].join(' ')
          }
    
          if (updateType == 'remove' && genList[i].options.customClass.split(' ').includes(className)) {
            let originArray = genList[i].options.customClass.split(' ')
            originArray.splice(originArray.findIndex(item => item == className), 1)
            genList[i].options.customClass = originArray.join(' ')
          }
        }
      } else {
        if (genList[i].model === fields[0]) {
          let newFields = [...fields]
          newFields.splice(0, 1)

          if (genList[i].type === 'table') {
            updateClassName(genList[i].tableColumns, newFields, className, updateType)
          }
          if (genList[i].type === 'subform') {
            updateClassName(genList[i].list, newFields, className, updateType)
          }
          if (genList[i].type === 'group') {
            updateClassName(genList[i].list, newFields, className, updateType)
          }
          if (genList[i].type === 'dialog') {
            updateClassName(genList[i].list, newFields, className, updateType)
          }
        }
      }
    }
  }
}