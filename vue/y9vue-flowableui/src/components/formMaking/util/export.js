const loadJspdf = () => {
  return new Promise((resolve, reject) => {
    if (window.FormMaking_OPTIONS.jsPDF) {
      resolve(window.FormMaking_OPTIONS.jsPDF)
    } else if (window.jspdf) {
      resolve(window.jspdf.jsPDF)
    } else {
      reject('jsPDF not found')
    }
  })
}

const loadHtml2canvas = () => {
  return new Promise((resolve, reject) => {
    if (window.FormMaking_OPTIONS.html2canvas) {
      resolve(window.FormMaking_OPTIONS.html2canvas)
    } else if (window.html2canvas) {
      resolve(window.html2canvas)
    } else {
      reject('html2canvas not found')
    }
  })
}

export const exportPDF = (content) => {
  return new Promise((resolve, reject) => {

    loadJspdf().then((jsPDF) => {
      loadHtml2canvas().then(html2canvas => {
        const pdf = new jsPDF('p', 'pt', 'a4')
      
        html2canvas(content, {
          dpi: 192,
          scale: 4,
          allowTaint: true,
          useCORS: true
        }).then((canvas) => {

          let context = canvas.getContext('2d')
          context.mozImageSmoothingEnabled = false
          context.webkitImageSmoothingEnabled = false
          context.msImageSmoothingEnabled = false
          context.imageSmoothingEnabled = false

          const marginLeft = 20;
          const marginRight = 20;
          const marginTop = 20;
          const marginBottom = 30;

          const pageWidth = pdf.internal.pageSize.getWidth();
          const pageHeight = pdf.internal.pageSize.getHeight();
          
          const contentWidth = pageWidth - marginLeft - marginRight;
          const contentHeight = pageHeight - marginTop - marginBottom;

          const imgWidth = contentWidth
          const imgHeight = (canvas.height * imgWidth) / canvas.width

          let leftHeight = imgHeight
          let position = marginTop
          let currentPage = 1

          if (leftHeight < contentHeight) {
            let imgData = canvas.toDataURL('image/jpeg')
            pdf.addImage(imgData, 'JPEG', marginLeft, marginRight, imgWidth, imgHeight)
          } else {
            while (leftHeight > 0) {
              let croppWidth = canvas.width
              let croppHeight = (croppWidth * contentHeight) / contentWidth

              const croppedCanvas = document.createElement('canvas');
              const ctx = croppedCanvas.getContext('2d');
              ctx.mozImageSmoothingEnabled = false
              ctx.webkitImageSmoothingEnabled = false
              ctx.msImageSmoothingEnabled = false
              ctx.imageSmoothingEnabled = false
              croppedCanvas.width = croppWidth;
              croppedCanvas.height = croppHeight;

              let remainHeight = canvas.height - (currentPage - 1) * croppHeight

              if (remainHeight < croppHeight) {
                croppedCanvas.height = remainHeight

                ctx.drawImage(canvas, 0, (currentPage - 1) * croppHeight, canvas.width, remainHeight, 0, 0, croppWidth, remainHeight)

                let imgData = croppedCanvas.toDataURL('image/jpeg')

                pdf.addImage(imgData, 'JPEG', marginLeft, marginTop, contentWidth, (contentHeight / croppHeight) * remainHeight)
              } else {
                ctx.drawImage(canvas, 0, (currentPage - 1) * croppHeight, canvas.width, croppHeight, 0, 0, croppWidth, croppHeight)

                let imgData = croppedCanvas.toDataURL('image/jpeg')

                pdf.addImage(imgData, 'JPEG', marginLeft, marginTop, contentWidth, contentHeight)
              }
              
              leftHeight -= contentHeight
              position -= contentHeight
              if (leftHeight > 0) {
                pdf.addPage()
                currentPage++
              }
            }
          }

          const footerText = 'Powered by FormMaking - https://form.making.link'

          const totalPages = pdf.internal.getNumberOfPages()

          for (let i = 1; i <= totalPages; i++) {
            pdf.setPage(i); // 设置当前页

            // 添加页脚
            pdf.setTextColor(144, 147, 153)
            pdf.setFontSize(10);
            pdf.text(footerText, pageWidth - 240, pageHeight - 10);
          }

          resolve(pdf.output('blob'))
        }).catch(() => {
          reject()
        })
      }).catch(error => reject(error))
    }).catch(error => reject(error))
  })
}