FROM tomcat:9.0-jre11-temurin

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
# 安装 libreoffice 与字体
    wget -O /usr/share/fonts/myFonts.tar.gz https://vue.youshengyun.com/storage/s/1621470919820054528.gz &&\
    wget -O /opt/LibreOffice_7.5.3.2_Linux_x86-64_deb.tar.gz https://vue.youshengyun.com/storage/s/1623635062622785536.gz &&\
    wget -O /usr/share/fonts/fc_local.conf https://vue.youshengyun.com/storage/s/1586775102685450240.conf &&\
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
