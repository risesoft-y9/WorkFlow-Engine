<script lang="ts" setup>
    import { onMounted, reactive, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';

    // 数据响应
    const settingStore = useSettingStore();
    let webSettingVisible = ref(false);
    const formLabelWidth = '15%';
    const form = reactive({
        webName: settingStore.getWebName,
        logoSvgName: settingStore.getLogoSvgName,
        webLanguage: settingStore.getWebLanguage,
        themeName: settingStore.getThemeName,
        menuStyle: settingStore.getMenuStyle,
        pcLayout: settingStore.getPcLayout,
        settingPageStyle: settingStore.getSettingPageStyle
    });

    // Confirm事件
    const submitFunc = () => {
        settingStore.$patch({
            webName: form.webName,
            logoSvgName: form.logoSvgName,
            webLanguage: form.webLanguage,
            themeName: form.themeName,
            menuStyle: form.menuStyle,
            pcLayout: form.pcLayout,
            settingPageStyle: form.settingPageStyle
        });
    };

    // reset事件
    const resetFunc = () => {
        form.webName = '有生集团';
        form.logoSvgName = '';
        form.webLanguage = 'zh';
        form.themeName = 'theme-default';
        form.menuStyle = 'Light';
        form.pcLayout = 'Y9Default';
        form.settingPageStyle = 'Dcat';
    };

    // 点击事件
    onMounted(() => {
        setTimeout(() => {
            document.getElementsByClassName('web-setting')[0].addEventListener('click', () => {
                if (!webSettingVisible.value) {
                    webSettingVisible.value = true;
                }
            });
        }, 500);
    });
</script>

<template>
    <!-- Form -->
    <el-dialog v-model="webSettingVisible" class="index-layout-setting-list" class="ddd" title="网站设置" width="65%">
        <el-form :model="form">
            <el-form-item
                :label-width="formLabelWidth"
                :rules="[
                    {
                        required: true
                    }
                ]"
                label="Name"
            >
                <el-input v-model="form.webName" autocomplete="off" width="100%">
                    <template #prepend>
                        <el-icon :size="16">
                            <i class="ri-pencil-line"></i>
                        </el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <div class="Tip"> <i class="ri-question-line"></i>&nbsp;网站名称 </div>
            <el-form-item
                :label-width="formLabelWidth"
                :rules="[
                    {
                        required: true
                    }
                ]"
                label="Logo"
            >
                <el-input v-model="form.logoSvgName" autocomplete="off">
                    <template #prepend>
                        <el-icon :size="16">
                            <i class="ri-pencil-line"></i>
                        </el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <div class="Tip"> <i class="ri-question-line"></i>&nbsp;logo设置 </div>
            <el-form-item
                :label-width="formLabelWidth"
                :rules="[
                    {
                        required: true
                    }
                ]"
                label="语言"
            >
                <el-radio v-model="form.webLanguage" label="zh" size="large">简体中文</el-radio>
                <el-radio v-model="form.webLanguage" label="en" size="large">English</el-radio>
            </el-form-item>
            <el-form-item
                :label-width="formLabelWidth"
                :rules="[
                    {
                        required: true
                    }
                ]"
                label="主题"
            >
                <el-radio v-model="form.themeName" label="theme-default" size="large">绿</el-radio>
                <el-radio v-model="form.themeName" label="blue" size="large">蓝</el-radio>
                <el-radio v-model="form.themeName" label="deepblue" size="large">深蓝</el-radio>
            </el-form-item>
            <el-form-item :label-width="formLabelWidth" label="设置版本">
                <el-radio v-model="form.settingPageStyle" label="Dcat" size="large">Dcat</el-radio>
                <el-radio v-model="form.settingPageStyle" label="Admin-plus" size="large">Admin-plus</el-radio>
            </el-form-item>
            <el-form-item :label-width="formLabelWidth" label="菜单样式">
                <el-radio v-model="form.menuStyle" label="Light" size="large">Light</el-radio>
                <el-radio v-model="form.menuStyle" label="Primary" size="large">Primary</el-radio>
            </el-form-item>
            <el-form-item :label-width="formLabelWidth" label="菜单布局">
                <el-radio v-model="form.pcLayout" label="Y9Default" size="large">左右</el-radio>
                <el-radio v-model="form.pcLayout" label="Y9Horizontal" size="large">上下</el-radio>
                <el-radio v-model="form.pcLayout" label="Y9Default sidebar-separate" size="large"
                    >sidebar-separate
                </el-radio>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="resetFunc()"> <i class="ri-refresh-line"></i> &nbsp;Reset </el-button>
                <el-button type="primary" @click="(webSettingVisible = false), submitFunc()">
                    <i class="ri-save-line"></i> &nbsp;Confirm
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
    .el-button--text {
        margin-right: 15px;
    }

    .el-input {
        width: 84%;
    }

    .Tip {
        display: flex;
        position: relative;
        height: 30px;
        left: 15%;
        top: -10px;
        color: var(--el-color-info);
    }

    .dialog-footer {
        margin: 0 auto;
        width: 70%;
        display: flex;
        justify-content: space-between;
    }
</style>
