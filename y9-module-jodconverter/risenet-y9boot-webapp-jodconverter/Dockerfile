FROM tomcat:9.0-jre11-temurin-jammy

ENV LANG=zh_CN.UTF-8 \
    LC_ALL=zh_CN.UTF-8 \
    TZ=Asia/Shanghai \
    DEBIAN_FRONTEND=noninteractive

# 修改 Ubuntu 源，可以尝试阿里源、清华源、中科大源等，注意需要 https
# 安装 libreoffice
# 内置一些常用的中文字体
RUN sed -i 's|http://ports.ubuntu.com|https://mirrors.aliyun.com|g' /etc/apt/sources.list &&\
    sed -i 's|http://security.ubuntu.com|https://mirrors.aliyun.com|g' /etc/apt/sources.list &&\
    sed -i 's|http://archive.ubuntu.com|https://mirrors.aliyun.com|g' /etc/apt/sources.list &&\
    apt-get update &&\
    apt-get install -y --no-install-recommends tzdata locales xfonts-utils fontconfig libreoffice &&\
    localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 &&\
    locale-gen zh_CN.UTF-8 &&\
    apt-get install -y --no-install-recommends ttf-mscorefonts-installer &&\
    apt-get install -y --no-install-recommends ttf-wqy-microhei ttf-wqy-zenhei xfonts-wqy &&\
    wget -O /usr/share/fonts/myFonts.tar.gz https://test.youshengyun.com/libreoffice/myFonts.tar.gz &&\
    cd /usr/share/fonts &&\
    tar -zxvf myFonts.tar.gz &&\
    cd /usr/share/fonts/chinese &&\
    mkfontscale &&\
    mkfontdir &&\
    fc-cache -fv &&\
    apt-get autoremove -y &&\
    apt-get clean &&\
    rm -rf /usr/share/fonts/myFonts.tar.gz /var/lib/apt/lists/* /tmp/* /var/tmp/*

CMD ["/bin/bash","/usr/local/tomcat/bin/catalina.sh", "run"]