FROM tomcat:9.0-jre11-temurin
MAINTAINER lizhiwen "lizhiwen@youshengyun.com"
# 内置一些常用的中文字体，避免普遍性乱码
COPY ./myFonts.tar.gz /usr/share/fonts/
COPY ./LibreOffice_7.5.3.2_Linux_x86-64_deb.tar.gz /opt/
COPY ./fc_local.conf /usr/share/fonts/

RUN apt-get clean && apt-get update &&\
	sed -i 's/http:\/\/archive.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list &&\
	sed -i 's/# deb/deb/g' /etc/apt/sources.list &&\
	apt-get install -y --reinstall ca-certificates &&\
	apt-get clean && apt-get update &&\
	apt-get install -y locales language-pack-zh-hans &&\
	localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8 && locale-gen zh_CN.UTF-8 &&\
    export DEBIAN_FRONTEND=noninteractive &&\
	apt-get install -y tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime &&\
	apt-get install -y fontconfig ttf-mscorefonts-installer ttf-wqy-microhei ttf-wqy-zenhei xfonts-wqy &&\
	apt-get install -y wget &&\
# 安装 libreoffice
    apt-get install -y libxrender1 libxinerama1 libxt6 libxext-dev libfreetype6-dev libcairo2 libcups2 libx11-xcb1 libnss3 &&\
    cd /opt && tar -zxvf ./LibreOffice_7.5.3.2_Linux_x86-64_deb.tar.gz  &&\
    dpkg -i ./LibreOffice_7.5.3.2_Linux_x86-64_deb/DEBS/*.deb &&\
    mv ./libreoffice7.5 ./libreoffice7 &&\
    cp -f /usr/share/fonts/fc_local.conf /opt/libreoffice7/share/fonts/truetype/ &&\
#  清理临时文件
    rm -rf /tmp/* && rm -rf /var/lib/apt/lists/* &&\
    cd /usr/share/fonts &&\
    tar -zxvf myFonts.tar.gz && cd ./chinese && rm -rf /usr/share/fonts/myFonts.tar.gz &&\
    rm -rf /opt/LibreOffice_7.5.3.2_Linux_x86-64_deb.tar.gz && rm -rf /opt/LibreOffice_7.5.3.2_Linux_x86-64_deb &&\
    mkfontscale &&\
    mkfontdir &&\
    fc-cache -fv

ENV LANG zh_CN.UTF-8
ENV LC_ALL zh_CN.UTF-8
CMD ["/bin/bash","/usr/local/tomcat/bin/catalina.sh", "run"]
