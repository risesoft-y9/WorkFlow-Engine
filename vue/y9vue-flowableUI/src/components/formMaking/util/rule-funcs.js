const ruleToFuncString = (rule) => {
  const condition = rule.options?.isCondition ? ` if (${rule.options.condition || 'false'}) ` : ''

  if (rule.action == 'hide') {
    return  `${condition} this.hide(${JSON.stringify(rule.options.fields)});`
  }

  if (rule.action == 'display') {
    return  `${condition} this.display(${JSON.stringify(rule.options.fields)});`
  }

  if (rule.action == 'disabled') {
    return `${condition} this.disabled(${JSON.stringify(rule.options.fields)}, ${rule.options.disabled});`
  }

  if (rule.action == 'refresh') {
    return `${condition} await this.refresh();`
  }

  if (rule.action == 'reset') {
    return `${condition} this.reset();`
  }

  if (rule.action == 'setData') {
    const setField = rule.options.fields.map(item => {
      if (rule.options.valueTypes[item] == 'fx') {
        return `'${item}' : ${rule.options.values[item]}`
      } else if (rule.options.valueTypes[item] == 'boolean') {
        return `'${item}' : ${rule.options.values[item] ?? false}`
      } else if (rule.options.valueTypes[item] == 'number') {
        return `'${item}' : ${rule.options.values[item] || undefined}`
      } else {
        return `'${item}' : ${JSON.stringify(rule.options.values[item] ?? '')}`
      }
    })

    return `${condition} await this.setData({${setField.join(',')}});`
  }

  if (rule.action == 'validate') {
    if (rule.options.failSuspend) {
      return `${condition} await this.validate(${JSON.stringify(rule.options.fields)});`
    } else {
      return `${condition} this.validate(${JSON.stringify(rule.options.fields)});`
    }
  }

  if (rule.action == 'sendRequest') {
    if (!rule.options.dataSource) return ''
    let argsArray = Object.keys(rule.options.dataSource.args).map(argKey => {
      if (rule.options.valueTypes[argKey] == 'fx') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey]}`
      } else if (rule.options.valueTypes[argKey] == 'boolean') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey]}`
      } else if (rule.options.valueTypes[argKey] == 'number') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey] || undefined}`
      } else {
        return `'${argKey}' : ${JSON.stringify(rule.options.dataSource.args[argKey] ?? '')}`
      }
    })
    if (rule.options.responseVariable) {
      let responseStatement = ` let ${rule.options.responseVariable} = undefined;`
      return `${responseStatement} ${condition} ${rule.options.responseVariable} = await this.sendRequest('${rule.options.dataSource.label}', {${argsArray.join(',')}});`
    } else {
      return `${condition} await this.sendRequest('${rule.options.dataSource.label}', {${argsArray.join(',')}});`
    }
  }

  if (rule.action == 'refreshFieldDataSource') {
    if (!rule.options.field) return ''
    let argsArray = Object.keys(rule.options.dataSource.args).map(argKey => {
      if (rule.options.valueTypes[argKey] == 'fx') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey]}`
      } else if (rule.options.valueTypes[argKey] == 'boolean') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey]}`
      } else if (rule.options.valueTypes[argKey] == 'number') {
        return `'${argKey}' : ${rule.options.dataSource.args[argKey] || undefined}`
      } else {
        return `'${argKey}' : ${JSON.stringify(rule.options.dataSource.args[argKey] ?? '')}`
      }
    })
    return `${condition} await this.refreshFieldDataSource('${rule.options.field}', {${argsArray.join(',')}});`
  }

  if (rule.action == 'getFieldDataSource') {
    if (rule.options.localVariable) {
      let localStatement = ` let ${rule.options.localVariable} = undefined;`
      return `${localStatement} ${condition} ${rule.options.localVariable} = this.getFieldDataSource('${rule.options.field}');`
    }
  }

  if (rule.action == 'openDialog') {
    if (!rule.options.field) return ''
    return `${condition} this.openDialog('${rule.options.field}');`
  }

  if (rule.action == 'closeDialog') {
    if (!rule.options.field) return ''
    return `${condition} this.closeDialog('${rule.options.field}');`
  }

  if (rule.action == 'triggerEvent') {
    if (!rule.options.functionName) return ''
    let params = rule.options.functionParams.trim()

    if (params) {
      try{
        params = new Function('return ' + params)()
      } catch (error) {
        return `${condition} this.triggerEvent('${rule.options.functionName}', "${params}");`
      }
      return `${condition} this.triggerEvent('${rule.options.functionName}', ${JSON.stringify(params)});`
    } else {
      return `${condition} this.triggerEvent('${rule.options.functionName}');`
    }
  }

  if (rule.action == 'js') {
    return rule.options.func
  }
}

const ruleToFunction = (rules) => {
  if (!rules) return ''
  if (typeof rules == 'string') {
    rules = JSON.parse(rules)
  }
  let funcs = []

  rules.forEach(item => {
    funcs.push(ruleToFuncString(item))
  })

  return funcs.join('\n')
}

export {
  ruleToFunction
}