## 本地运行需下载驱动软件（LibreOffice）：

https://vue.youshengyun.com/storage/s/1556326055835340800.7z  
下载后将其解压,并修改application.yml 的office.home, 改为解压后的 LibreOfficePortable/App/libreoffice 目录绝对路径。

# Linux下安装LibreOffice 和 字体安装
## 1.解压、安装
根据操作系统选择不同方式。
### 1.1 CentOS x86架构
1.下载并上传到服务器 /opt 目录下  
[libreoffice7.5.3.2 x86.rpm 安装包](https://vue.youshengyun.com/storage/s/1584241447769477120.gz)  
```shell
// 解压   
$ cd /opt  
$ tar -xzvf LibreOffice_7.5.3.2_Linux_x86-64_rpm.tar.gz  
// 安装主安装程序的所有rpm包 */  
$ sudo yum install ./LibreOffice_7.5.3.2_Linux_x86-64_rpm/RPMS/*.rpm  
$ mv libreoffice7.5 libreoffice7  
// 安装以下插件，否则libreoffice可能无法启动 */  
$ yum install cairo  
$ yum install cups-libs  
$ yum install libSM  
$ yum update nss  
```

### 1.2 国产系统 arm架构 
1.下载并上传到服务器 /opt 目录下  
[libreoffice-7.5.9.2 arm 安装包](https://vue.youshengyun.com/storage/s/1621472837644587008.xz)  
```shell
安装包放到服务器的 /opt 目录  
// 解压  
tar -xvf libreoffice-7.5.9.2-arm64-kylinv7.tar.xz   
// 改名  
mv instdir libreoffice7  
// 运行以下命令，验证是否成功  
/opt/libreoffice7/program/soffice.bin --headless --accept="socket,host=0.0.0.0,port=8100;urp;" --nofirststartwizard &  

// 当出现类似以下，且无报错代表部署成功。    
[1] 2803  
```

### 2.字体安装

[myFonts.tar.gz](https://vue.youshengyun.com/storage/s/1621470919820054528.gz)  
/* 将字体文件放入以下目录并解压，会生成chinese目录 */  
$ cd /usr/share/fonts
$ tar -zxvf myFonts.tar.gz
/* 更新字体缓存 */  
$ mkfontscale （如果提示 mkfontscale: command not found，需安装 # yum install mkfontscale ）  
$ mkfontdir
$ fc-cache -fv （如果提示 fc-cache: command not found，则需要安装# yum install fontconfig ）

### 3.替换字体配置

用以下文件，替换/opt/libreoffice7/share/fonts/truetype/fc_local.conf  
[fc_local.conf](https://vue.youshengyun.com/storage/s/1586775102685450240.conf)

### 4.后续若需添加新字体，参照并修改/opt/libreoffice7/usr/share/fonts/truetype/fc_local.conf 文件，如下：

![img.png](https://vue.youshengyun.com/storage/s/1586745686706098176.png)