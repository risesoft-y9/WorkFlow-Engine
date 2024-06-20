export const UpgradeData = (data) => {
  if (data.type == 'grid') {
    return {
      ...data,
      columns: data.columns.map(item => {

        if (item.key) {
          return item
        } else {
          return {
            ...item,
            key: Math.random().toString(36).slice(-8),
            type: 'col',
            options: {
              span: item.span,
              offset: 0,
              push: 0,
              pull: 0,
              xs: item.xs,
              sm: item.sm,
              md: item.md,
              lg: item.lg,
              xl: item.xl,
              customClass: ''
            },
            list: item.list.map(colItem => {
              return UpgradeData(colItem)
            })
          }
        }
      })
    }
  } else {
    return data
  }
}